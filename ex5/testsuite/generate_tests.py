#!/usr/bin/env python3
from __future__ import annotations

from pathlib import Path
import shutil

ROOT = Path(__file__).resolve().parent
OUT = ROOT / "generated"


def clamp(value: int) -> int:
    if value > 32767:
        return 32767
    if value < -32768:
        return -32768
    return value


def write_case(category: str, index: int, program: str, expected: str) -> None:
    tests_dir = OUT / category / "tests"
    expected_dir = OUT / category / "expected_output"
    stem = f"{category.upper()}_{index:03d}"

    test_path = tests_dir / f"{stem}.txt"
    expected_path = expected_dir / f"{stem}_Expected_Output.txt"

    test_path.write_text(program.rstrip() + "\n", encoding="utf-8")
    expected_path.write_text(expected, encoding="utf-8")


def reset_dirs() -> None:
    if OUT.exists():
        shutil.rmtree(OUT)

    for category in ("unit", "integration", "e2e"):
        (OUT / category / "tests").mkdir(parents=True, exist_ok=True)
        (OUT / category / "expected_output").mkdir(parents=True, exist_ok=True)


def generate_unit() -> int:
    idx = 1

    # 50 arithmetic addition cases (including saturation checks)
    for i in range(50):
        a = (i * 631) % 32000
        b = (i * 977) % 32000
        expected = f"{clamp(a + b)} "
        program = (
            "void main()\n"
            "{\n"
            f"    PrintInt({a}+{b});\n"
            "}\n"
        )
        write_case("unit", idx, program, expected)
        idx += 1

    # 50 arithmetic subtraction cases
    for i in range(50):
        a = (i * 997 + 123) % 32000
        b = (i * 571 + 77) % 32000
        expected = f"{clamp(a - b)} "
        program = (
            "void main()\n"
            "{\n"
            f"    PrintInt({a}-{b});\n"
            "}\n"
        )
        write_case("unit", idx, program, expected)
        idx += 1

    # 40 arithmetic multiplication cases
    for i in range(40):
        a = (i * 37 + 5) % 400
        b = (i * 53 + 9) % 400
        expected = f"{clamp(a * b)} "
        program = (
            "void main()\n"
            "{\n"
            f"    PrintInt({a}*{b});\n"
            "}\n"
        )
        write_case("unit", idx, program, expected)
        idx += 1

    # 30 arithmetic division cases
    for i in range(30):
        a = (i * 1234 + 1) % 32000 + 1
        b = (i * 29 + 3) % 97 + 1
        expected = f"{clamp(a // b)} "
        program = (
            "void main()\n"
            "{\n"
            f"    PrintInt({a}/{b});\n"
            "}\n"
        )
        write_case("unit", idx, program, expected)
        idx += 1

    # 30 comparison/branching cases
    for i in range(30):
        a = (i * 173 + 11) % 200
        b = (i * 91 + 7) % 200
        expected = "1 " if a < b else "0 "
        program = (
            "void main()\n"
            "{\n"
            f"    if ({a}<{b})\n"
            "    {\n"
            "        PrintInt(1);\n"
            "    }\n"
            "    else\n"
            "    {\n"
            "        PrintInt(0);\n"
            "    }\n"
            "}\n"
        )
        write_case("unit", idx, program, expected)
        idx += 1

    return idx - 1


def generate_integration() -> int:
    idx = 1

    # 50 function + loop integration cases
    for i in range(50):
        n = i + 1
        expected = f"{clamp(n * (n - 1) // 2)} "
        program = (
            "int sumTo(int n)\n"
            "{\n"
            "    int i := 0;\n"
            "    int s := 0;\n"
            "    while (i<n)\n"
            "    {\n"
            "        s := s+i;\n"
            "        i := i+1;\n"
            "    }\n"
            "    return s;\n"
            "}\n"
            "\n"
            "void main()\n"
            "{\n"
            f"    PrintInt(sumTo({n}));\n"
            "}\n"
        )
        write_case("integration", idx, program, expected)
        idx += 1

    # 50 array + function integration cases
    for i in range(50):
        n = (i % 10) + 2
        base = (i * 17 + 5) % 200
        expected_sum = sum(base + j for j in range(n))
        expected = f"{clamp(expected_sum)} "
        program = (
            "array IntArray = int[];\n"
            "\n"
            "int sumArr(IntArray a, int n)\n"
            "{\n"
            "    int i := 0;\n"
            "    int s := 0;\n"
            "    while (i<n)\n"
            "    {\n"
            "        s := s+a[i];\n"
            "        i := i+1;\n"
            "    }\n"
            "    return s;\n"
            "}\n"
            "\n"
            "void main()\n"
            "{\n"
            f"    int n := {n};\n"
            "    IntArray a := new int[n];\n"
            "    int i := 0;\n"
            "    while (i<n)\n"
            "    {\n"
            f"        a[i] := {base}+i;\n"
            "        i := i+1;\n"
            "    }\n"
            "    PrintInt(sumArr(a,n));\n"
            "}\n"
        )
        write_case("integration", idx, program, expected)
        idx += 1

    # 50 class + loop + method integration cases
    for i in range(50):
        init = (i * 19 + 3) % 200
        step = (i % 7) + 1
        count = (i % 8) + 1
        expected_val = clamp(init + step * count)
        expected = f"{expected_val} "
        program = (
            "class Counter\n"
            "{\n"
            f"    int x := {init};\n"
            f"    int step := {step};\n"
            "\n"
            "    int tick()\n"
            "    {\n"
            "        x := x+step;\n"
            "        return x;\n"
            "    }\n"
            "\n"
            "    int peek()\n"
            "    {\n"
            "        return x;\n"
            "    }\n"
            "}\n"
            "\n"
            "void main()\n"
            "{\n"
            "    Counter c := new Counter;\n"
            "    int i := 0;\n"
            f"    while (i<{count})\n"
            "    {\n"
            "        c.tick();\n"
            "        i := i+1;\n"
            "    }\n"
            "    PrintInt(c.peek());\n"
            "}\n"
        )
        write_case("integration", idx, program, expected)
        idx += 1

    # 50 global variable + function composition integration cases
    for i in range(50):
        g = (i * 29 + 7) % 200
        x = (i * 41 + 11) % 200
        bump_val = clamp(x + g)
        expected_val = clamp(bump_val + bump_val)
        expected = f"{expected_val} "
        program = (
            f"int g := {g};\n"
            "\n"
            "int bump(int x)\n"
            "{\n"
            "    return x+g;\n"
            "}\n"
            "\n"
            "int twice(int y)\n"
            "{\n"
            "    return bump(y)+bump(y);\n"
            "}\n"
            "\n"
            "void main()\n"
            "{\n"
            f"    PrintInt(twice({x}));\n"
            "}\n"
        )
        write_case("integration", idx, program, expected)
        idx += 1

    return idx - 1


def fib(n: int) -> int:
    if n < 2:
        return n
    return fib(n - 1) + fib(n - 2)


def generate_e2e() -> int:
    idx = 1

    # 25 inheritance + virtual dispatch + string/int output cases
    for i in range(25):
        v = (i * 7 + 3) % 50
        bonus = (i * 11 + 5) % 50
        value = clamp(v + bonus)
        expected = f"B{value} "
        program = (
            "class A\n"
            "{\n"
            f"    int v := {v};\n"
            "\n"
            "    string tag()\n"
            "    {\n"
            "        return \"A\";\n"
            "    }\n"
            "\n"
            "    int value()\n"
            "    {\n"
            "        return v;\n"
            "    }\n"
            "}\n"
            "\n"
            "class B extends A\n"
            "{\n"
            f"    int bonus := {bonus};\n"
            "\n"
            "    string tag()\n"
            "    {\n"
            "        return \"B\";\n"
            "    }\n"
            "\n"
            "    int value()\n"
            "    {\n"
            "        return v+bonus;\n"
            "    }\n"
            "}\n"
            "\n"
            "void main()\n"
            "{\n"
            "    A x := new B;\n"
            "    PrintString(x.tag());\n"
            "    PrintInt(x.value());\n"
            "}\n"
        )
        write_case("e2e", idx, program, expected)
        idx += 1

    # 25 recursion + arithmetic cases
    for i in range(25):
        n = (i % 9) + 2
        expected = f"{fib(n)} "
        program = (
            "int fib(int n)\n"
            "{\n"
            "    if (n<2)\n"
            "    {\n"
            "        return n;\n"
            "    }\n"
            "    return fib(n-1)+fib(n-2);\n"
            "}\n"
            "\n"
            "void main()\n"
            "{\n"
            f"    PrintInt(fib({n}));\n"
            "}\n"
        )
        write_case("e2e", idx, program, expected)
        idx += 1

    # 25 arrays of objects + virtual calls cases
    for i in range(25):
        base_id = (i * 13 + 7) % 80
        extra = (i * 17 + 9) % 40
        derived_value = clamp(base_id + extra)
        expected = f"{base_id} {derived_value} "
        program = (
            "class Base\n"
            "{\n"
            f"    int id := {base_id};\n"
            "\n"
            "    int get()\n"
            "    {\n"
            "        return id;\n"
            "    }\n"
            "}\n"
            "\n"
            "class Child extends Base\n"
            "{\n"
            f"    int extra := {extra};\n"
            "\n"
            "    int get()\n"
            "    {\n"
            "        return id+extra;\n"
            "    }\n"
            "}\n"
            "\n"
            "array BaseArray = Base[];\n"
            "\n"
            "void main()\n"
            "{\n"
            "    BaseArray arr := new Base[2];\n"
            "    arr[0] := new Base;\n"
            "    arr[1] := new Child;\n"
            "    PrintInt(arr[0].get());\n"
            "    PrintInt(arr[1].get());\n"
            "}\n"
        )
        write_case("e2e", idx, program, expected)
        idx += 1

    # 25 runtime-check cases (division by zero, null dereference, bounds)
    for i in range(25):
        mode = i % 3
        if mode == 0:
            program = (
                "void main()\n"
                "{\n"
                "    int z := 0;\n"
                "    PrintInt(10/z);\n"
                "}\n"
            )
            expected = "Illegal Division By Zero"
        elif mode == 1:
            program = (
                "class A\n"
                "{\n"
                "    int x;\n"
                "}\n"
                "\n"
                "void main()\n"
                "{\n"
                "    A a := nil;\n"
                "    PrintInt(a.x);\n"
                "}\n"
            )
            expected = "Invalid Pointer Dereference"
        else:
            n = (i % 5) + 1
            program = (
                "array IntArray = int[];\n"
                "\n"
                "void main()\n"
                "{\n"
                f"    IntArray a := new int[{n}];\n"
                f"    PrintInt(a[{n}]);\n"
                "}\n"
            )
            expected = "Access Violation"

        write_case("e2e", idx, program, expected)
        idx += 1

    return idx - 1


def main() -> None:
    reset_dirs()

    unit_count = generate_unit()
    integration_count = generate_integration()
    e2e_count = generate_e2e()
    total = unit_count + integration_count + e2e_count

    manifest = (
        f"unit={unit_count}\n"
        f"integration={integration_count}\n"
        f"e2e={e2e_count}\n"
        f"total={total}\n"
    )
    (OUT / "MANIFEST.txt").write_text(manifest, encoding="utf-8")

    print(f"Generated {total} tests")
    print(f"  unit: {unit_count}")
    print(f"  integration: {integration_count}")
    print(f"  e2e: {e2e_count}")


if __name__ == "__main__":
    main()
