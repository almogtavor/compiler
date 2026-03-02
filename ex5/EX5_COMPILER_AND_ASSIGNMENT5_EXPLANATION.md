# Exercise 5 Compiler Solution - Architecture and Implementation Notes

## What this compiler does

This project compiles the **L language** into **MIPS assembly** executable by SPIM.

The ex5 pipeline is:

1. Parse source code into AST.
2. Semantic analysis (type checking, scope, inheritance rules, etc.).
3. AST to IR lowering.
4. Register allocation on IR (liveness, interference graph, coloring to 10 temp registers).
5. IR to MIPS translation.

Possible outputs are:

- `ERROR` (lexer)
- `ERROR(<line>)` (syntax/semantic)
- `Register Allocation Failed`
- Valid MIPS assembly

## Ex5 implementation summary

### New backend infrastructure

- `src/codegen/CodeGenInfo.java`
  - Tracks globals, function params/locals, current function/class context, string literals.
- `src/codegen/ClassLayoutManager.java`
  - Computes object field offsets and class vtables, including inheritance/overrides.

### Register allocation

- `src/regalloc/RegisterAllocator.java`
  - Performs per-function liveness analysis.
  - Builds interference graph from live sets.
  - Simplification-based graph coloring for 10 registers (`$t0-$t9`).
  - Throws `RegisterAllocationFailedException` when coloring is impossible.

### MIPS generation

- `src/mips/MipsGenerator.java`
  - Emits `.data` section: runtime error strings, string literals, globals, vtables.
  - Emits `.text` section: global init, `main`, and all function bodies.
  - Handles:
    - arithmetic + saturation to `[-32768, 32767]`
    - conditions/jumps
    - stack frames + function prologue/epilogue
    - normal and virtual calls (`jal` / `jalr`)
    - arrays (length in slot 0)
    - objects (vtable pointer in slot 0)
    - string concat and string equality
    - runtime checks (null, bounds, division by zero)

### AST/IR extensions

Added/updated IR commands for:

- strings (`ConstString`, `PrintString`, concat, equality)
- memory/object operations (`Malloc`, `FieldGet/Set`, `ArrayGet/Set`)
- calls/returns (`CallFunc`, `VirtualCall`, `Return`, `ReturnVoid`)
- runtime checks (`CheckNullPtr`, `CheckBounds`, `CheckDivZero`)

Updated AST nodes generate IR for all required ex5 language features.

### Main driver

- `src/Main.java` now runs the full ex5 pipeline:
  - parse -> semant -> IR -> regalloc -> MIPS emission
  - catches allocation failure and emits `Register Allocation Failed`

## Important correctness fixes made during debugging

1. **Caller temp preservation around calls**
   - Bug: values in `$t*` were being reused after `jal/jalr` without save/restore.
   - Fix: save/restore caller temp registers around regular and virtual calls.
   - Impact: fixed recursion and virtual-call related wrong outputs/crashes.

2. **Assignment evaluation order (LHS before RHS side effects)**
   - Bug: assignment RHS was evaluated before some LHS parts (`array[index] := ...`), breaking cases like `birthday()` side effects.
   - Fix: in assignment IR emission, evaluate field/array LHS access path first, then RHS, then store.

3. **String literal quoting**
   - Bug: printed strings included surrounding source quotes.
   - Fix: strip token quotes in `AstExpString` before storing string literals.

## Runtime model used

- **Objects**
  - word 0: vtable pointer
  - words 1..N: fields
- **Arrays**
  - word 0: length
  - words 1..N: elements
- **Function frame**
  - saved `$fp` / `$ra`
  - params at positive offsets from `$fp` (starting at `+8`)
  - locals at negative offsets from `$fp`
- **Global vars**
  - allocated in `.data`, initialized before `func_main` call

## Testing

## Official self-check (26 tests)

- Local run: passed behaviorally (normalizing SPIM banner/version differences).
- `nova` run: passed exactly against expected outputs.

Remote (`nova`) run result summary:

- `TEST_01` .. `TEST_26`: all `OK`
- `FAILED_COUNT=0`

## Additional 500 tests (unit/integration/e2e)

Added generated suite under:

- `ex5/testsuite/generate_tests.py`
- `ex5/testsuite/run_generated_tests.py`
- `ex5/testsuite/generated/...`

Counts:

- unit: 200
- integration: 200
- e2e: 100
- total: 500

Execution result:

- Total: 500
- Passed: 500
- Failed: 0

## How to run

Build compiler:

```bash
cd ex5
make
```

Run official 26 tests locally (example style):

```bash
java -jar COMPILER ../self-check-ex5-contents/tests/TEST_18.txt /tmp/out.s
spim -file /tmp/out.s
```

Run 500 generated tests:

```bash
cd ex5/testsuite
./generate_tests.py
./run_generated_tests.py
```

## Final status

The ex5 backend is implemented end-to-end, passes the official suite on `nova`, and passes an additional 500-test suite spanning unit, integration, and e2e coverage.
