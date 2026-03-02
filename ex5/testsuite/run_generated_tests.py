#!/usr/bin/env python3
from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import argparse
import subprocess
import sys
import tempfile


ROOT = Path(__file__).resolve().parent
EX5_DIR = ROOT.parent
GENERATED_DIR = ROOT / "generated"


@dataclass
class TestResult:
    category: str
    stem: str
    ok: bool
    reason: str = ""


def run_cmd(cmd: list[str], cwd: Path | None = None, timeout: int = 30) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        cmd,
        cwd=str(cwd) if cwd else None,
        text=True,
        capture_output=True,
        timeout=timeout,
        check=False,
    )


def extract_payload(spim_stdout: str) -> str:
    lines = spim_stdout.splitlines(keepends=True)
    for i, line in enumerate(lines):
        if line.startswith("Loaded: "):
            return "".join(lines[i + 1 :]).rstrip("\n")
    return spim_stdout.rstrip("\n")


def run_single_test(test_file: Path, expected_file: Path, tmp_dir: Path) -> tuple[bool, str]:
    asm_file = tmp_dir / f"{test_file.stem}.s"

    compile_proc = run_cmd(
        ["java", "-jar", str(EX5_DIR / "COMPILER"), str(test_file), str(asm_file)],
        timeout=30,
    )
    if compile_proc.returncode != 0:
        return False, f"compiler failed: {compile_proc.stderr.strip()}"

    actual_compiler_output = asm_file.read_text(encoding="utf-8")
    expected_output = expected_file.read_text(encoding="utf-8")

    if actual_compiler_output == "Register Allocation Failed":
        actual_payload = actual_compiler_output
    elif actual_compiler_output.startswith("ERROR"):
        actual_payload = actual_compiler_output
    else:
        spim_proc = run_cmd(["spim", "-file", str(asm_file)], timeout=30)
        if spim_proc.returncode != 0:
            return False, f"spim failed: {spim_proc.stderr.strip()}"
        actual_payload = extract_payload(spim_proc.stdout)

    if actual_payload == expected_output:
        return True, ""

    return False, f"expected={expected_output!r}, actual={actual_payload!r}"


def collect_tests(categories: list[str]) -> list[tuple[str, Path, Path]]:
    collected: list[tuple[str, Path, Path]] = []
    for category in categories:
        tests_dir = GENERATED_DIR / category / "tests"
        expected_dir = GENERATED_DIR / category / "expected_output"
        for test_file in sorted(tests_dir.glob("*.txt")):
            expected_file = expected_dir / f"{test_file.stem}_Expected_Output.txt"
            collected.append((category, test_file, expected_file))
    return collected


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--category",
        action="append",
        choices=["unit", "integration", "e2e"],
        help="Run only selected categories (can be repeated)",
    )
    parser.add_argument("--skip-make", action="store_true", help="Skip make before tests")
    args = parser.parse_args()

    categories = args.category if args.category else ["unit", "integration", "e2e"]

    if not GENERATED_DIR.exists():
        print("Generated tests not found. Run generate_tests.py first.", file=sys.stderr)
        return 2

    if not args.skip_make:
        make_proc = run_cmd(["make"], cwd=EX5_DIR, timeout=120)
        if make_proc.returncode != 0:
            print("make failed", file=sys.stderr)
            print(make_proc.stdout, file=sys.stderr)
            print(make_proc.stderr, file=sys.stderr)
            return 2

    tests = collect_tests(categories)
    total = len(tests)
    if total == 0:
        print("No tests found")
        return 2

    print(f"Running {total} generated tests...")

    results: list[TestResult] = []
    with tempfile.TemporaryDirectory(prefix="ex5_generated_tests_") as td:
        tmp_dir = Path(td)
        for i, (category, test_file, expected_file) in enumerate(tests, start=1):
            ok, reason = run_single_test(test_file, expected_file, tmp_dir)
            results.append(TestResult(category=category, stem=test_file.stem, ok=ok, reason=reason))
            status = "OK" if ok else "FAIL"
            print(f"[{i:03d}/{total:03d}] {category}/{test_file.stem}: {status}")

    failures = [r for r in results if not r.ok]
    print()
    print(f"Total: {total}")
    print(f"Passed: {total - len(failures)}")
    print(f"Failed: {len(failures)}")

    if failures:
        print("\nFailure details:")
        for f in failures:
            print(f"- {f.category}/{f.stem}: {f.reason}")
        return 1

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
