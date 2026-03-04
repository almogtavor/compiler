# L Compiler Test Suite (Exercise 5) - 250 Tests

## Overview

This document describes the full test suite for the L compiler (Exercise 5), comprising 250 tests. The tests cover all compiler phases -- lexing, parsing, semantic analysis, register allocation, and MIPS code generation -- as well as runtime behavior under SPIM. Tests range from basic programs (printing primes, sorting) to stress tests for register allocation, saturation arithmetic edge cases, runtime error detection, and full integration programs that exercise every major language feature simultaneously.

Each test consists of an L source file in `tests/` and a corresponding expected output file in `expected_output/`. The expected output matches SPIM output format (including the standard SPIM header) unless the test is expected to fail at a compiler phase, in which case the expected output contains an error token such as `ERROR`, `ERROR(3)`, `ERROR(4)`, `ERROR(8)`, or `Register Allocation Failed`.

---

## Table of Contents

1. [Tests 01-26: Original / Foundational Tests](#tests-01-26-original--foundational-tests)
2. [Tests 27-56: Edge Cases](#tests-27-56-edge-cases)
3. [Tests 57-61: Saturation and Literal Edge Cases](#tests-57-61-saturation-and-literal-edge-cases)
4. [Tests 62-65: More Arithmetic](#tests-62-65-more-arithmetic)
5. [Tests 66-70: String Operations](#tests-66-70-string-operations)
6. [Tests 71-75: Array Operations](#tests-71-75-array-operations)
7. [Tests 76-80: Class Operations](#tests-76-80-class-operations)
8. [Tests 81-85: Control Flow](#tests-81-85-control-flow)
9. [Tests 86-88: Evaluation Order](#tests-86-88-evaluation-order)
10. [Tests 89-91: Global Variables](#tests-89-91-global-variables)
11. [Tests 92-96: Runtime Errors](#tests-92-96-runtime-errors)
12. [Tests 97-100: Compiler Error Cases](#tests-97-100-compiler-error-cases)
13. [Tests 101-112: Miscellaneous](#tests-101-112-miscellaneous)
14. [Tests 113-120: Arithmetic Edge Cases](#tests-113-120-arithmetic-edge-cases)
15. [Tests 121-130: String Operations (Extended)](#tests-121-130-string-operations-extended)
16. [Tests 131-140: Array Operations (Extended)](#tests-131-140-array-operations-extended)
17. [Tests 141-150: Class Features (Extended)](#tests-141-150-class-features-extended)
18. [Tests 151-160: Control Flow (Extended)](#tests-151-160-control-flow-extended)
19. [Tests 161-170: Function Features](#tests-161-170-function-features)
20. [Tests 171-180: Error Cases (Extended)](#tests-171-180-error-cases-extended)
21. [Tests 181-190: Global Variable Edge Cases](#tests-181-190-global-variable-edge-cases)
22. [Tests 191-200: Register Allocation Stress](#tests-191-200-register-allocation-stress)
23. [Tests 201-210: Evaluation Order (Extended)](#tests-201-210-evaluation-order-extended)
24. [Tests 211-220: Runtime Error Edge Cases](#tests-211-220-runtime-error-edge-cases)
25. [Tests 221-230: Complex Programs / Algorithms](#tests-221-230-complex-programs--algorithms)
26. [Tests 231-240: Inheritance and Polymorphism](#tests-231-240-inheritance-and-polymorphism)
27. [Tests 241-250: Full Integration](#tests-241-250-full-integration)
28. [Error and Failure Tests Summary](#error-and-failure-tests-summary)
29. [Runtime Error Tests Summary](#runtime-error-tests-summary)
30. [Notes on the L Language and Testing](#notes-on-the-l-language-and-testing)

---

## Tests 01-26: Original / Foundational Tests

These are the foundational tests covering core language features: basic I/O, sorting algorithms, data structures, classes, strings, arrays, operator precedence, recursion, overflow/saturation, register pressure, global variables, inheritance, and runtime errors.

| # | Name | Description | Expected Output |
|---|------|-------------|-----------------|
| 01 | Print_Primes | Prints prime numbers up to 100 | SPIM output |
| 02 | Bubble_Sort | Bubble sort implementation | SPIM output |
| 03 | Merge_Lists | Merge two sorted lists | SPIM output |
| 04 | Matrices | Matrix operations | SPIM output |
| 05 | Classes | Basic class features | SPIM output |
| 06 | Strings | String operations | SPIM output |
| 07 | Arrays | Array operations | SPIM output |
| 08 | Access_Violation | Array out of bounds | SPIM output (Access Violation) |
| 09 | Access_Violation | Another access violation case | SPIM output (Access Violation) |
| 10 | Tree | Binary tree operations | SPIM output |
| 11 | Precedence | Operator precedence | SPIM output |
| 12 | Fib | Fibonacci | SPIM output |
| 13 | Overflow | Integer overflow/saturation | SPIM output |
| 14 | Many_Local_Variables | Many local variables (register pressure) | SPIM output |
| 15 | Many_Data_Members | Many class data members | SPIM output |
| 16 | Classes | More class features | SPIM output |
| 17 | Global_Variables | Global variable init and access | SPIM output |
| 18 | (unnamed) | Function call returning constant | SPIM output |
| 19 | (unnamed) | Nested function calls | SPIM output |
| 20 | (unnamed) | Class instantiation and method call | SPIM output |
| 21 | (unnamed) | Class inheritance with method override | SPIM output |
| 22 | (unnamed) | Recursive function | SPIM output |
| 23 | (unnamed) | Multiple inheritance levels with overrides | SPIM output |
| 24 | (unnamed) | Division by zero in method | SPIM output (runtime error) |
| 25 | (unnamed) | Large negative number arithmetic | SPIM output |
| 26 | (unnamed) | 18-parameter function (too many for register allocation) | Register Allocation Failed |

---

## Tests 27-56: Edge Cases

These tests target edge cases in recursion, data structures, saturation, evaluation order, control flow, nil handling, reference aliasing, and register allocation pressure.

| # | Name | Description | Expected Output |
|---|------|-------------|-----------------|
| 27 | Deep_Recursion | Ackermann function | SPIM output |
| 28 | Chained_Assignments | `a := b := c := d := 42` (invalid L syntax) | ERROR(8) |
| 29 | Array_of_Arrays | 2D arrays with nested indexing | SPIM output |
| 30 | Method_Returns_Object | Linked list creation via method returns | SPIM output |
| 31 | Complex_Saturation | -32768 literal (lexer rejects 32768) | ERROR |
| 32 | Nested_Method_Calls | Method calls as arguments | SPIM output |
| 33 | Empty_Array | `new int[0]` zero-length array | SPIM output |
| 34 | Single_Element_Array_Bounds | arr[1] on size-1 array triggers Access Violation | SPIM output |
| 35 | Multiple_Classes_Interaction | Objects containing objects | SPIM output |
| 36 | Division_Edge_Cases | Division with negative numbers, floor division | SPIM output |
| 37 | Side_Effects_In_Expressions | Global mutations during eval, left-to-right order | SPIM output |
| 38 | Deep_Inheritance_Chain | 4-level inheritance A->B->C->D | SPIM output |
| 39 | Tricky_While_Conditions | while(i) where i counts to 0 | SPIM output |
| 40 | Nested_If_While | Triple-nested control structures | SPIM output |
| 41 | Array_Class_Mix | Arrays stored in classes | SPIM output |
| 42 | Zero_Arithmetic | All operations with zero operands | SPIM output |
| 43 | Negative_Numbers_Operations | Arithmetic with negative operands | SPIM output |
| 44 | String_Empty_Concatenation | Empty string edge cases | SPIM output |
| 45 | Chained_Array_Access | values[indices[i]] | SPIM output |
| 46 | Global_Array_Init | Global array initialized in main | SPIM output |
| 47 | Complex_Global_Init | Chain of dependent global inits | SPIM output |
| 48 | Nil_Comparisons | nil = nil, object = nil, etc. | SPIM output |
| 49 | Fibonacci_Iterative | Iterative Fibonacci with temps | SPIM output |
| 50 | Array_Copy_Edge_Cases | Reference aliasing | SPIM output |
| 51 | Return_In_Loop | Early return from while loop | SPIM output |
| 52 | Class_Self_Reference | Circular linked list | SPIM output |
| 53 | Many_Parameters | 8-parameter function | SPIM output |
| 54 | Mixed_Access_Violations | Valid accesses then out-of-bounds | SPIM output |
| 55 | Expression_Precedence_Complex | Multi-operator precedence | SPIM output |
| 56 | Large_Array_Operations | 100-element array sum | SPIM output |

---

## Tests 57-61: Saturation and Literal Edge Cases

These tests focus on saturation arithmetic triggered by computation (not by literal values), chained saturation, multiplication overflow, division edge cases, and exact boundary values.

| # | Name | Description |
|---|------|-------------|
| 57 | Saturation_Via_Computation | Overflow via computation (0 - 32767 - 1) |
| 58 | Saturation_Chain | Chained saturating operations |
| 59 | Multiplication_Overflow | Multiplication overflow scenarios |
| 60 | Division_Saturation | Division edge cases with saturation |
| 61 | Saturation_Boundary_Exact | Exact boundary values 32767 and -32768 |

---

## Tests 62-65: More Arithmetic

Additional arithmetic tests covering underflow, saturation recovery, floor division, and negative division.

| # | Name | Description |
|---|------|-------------|
| 62 | Subtraction_Underflow | Subtraction causing underflow |
| 63 | Saturate_Then_Continue | Saturate then recover |
| 64 | Floor_Division | Floor division behavior |
| 65 | Negative_Division | Division with negatives |

---

## Tests 66-70: String Operations

Tests for string content equality, multi-concatenation, equality after concatenation, multiple print calls, and empty string edge cases.

| # | Name | Description |
|---|------|-------------|
| 66 | String_Equality | String content equality |
| 67 | String_Concat_Multi | Multiple string concatenations |
| 68 | String_Equality_After_Concat | Compare concatenated strings |
| 69 | PrintString_Multiple | Multiple PrintString calls |
| 70 | Empty_String_Operations | Empty string edge cases |

---

## Tests 71-75: Array Operations

Tests for array reference equality, last-element access, negative index handling, index-at-length boundary, and single-element array operations.

| # | Name | Description |
|---|------|-------------|
| 71 | Array_Equality | Array reference equality |
| 72 | Array_Last_Element | Access last element |
| 73 | Array_Negative_Index | Negative index triggers Access Violation |
| 74 | Array_Index_At_Length | Index equal to length triggers Access Violation |
| 75 | Array_Size_One | Single element array operations |

---

## Tests 76-80: Class Operations

Tests for reference equality, nil field access (Invalid Pointer Dereference), inherited fields, three-level virtual dispatch, and methods with multiple parameters.

| # | Name | Description |
|---|------|-------------|
| 76 | Class_Equality | Reference equality for objects |
| 77 | Nil_Field_Access | Field access on nil triggers Invalid Pointer Dereference |
| 78 | Inheritance_Field | Accessing inherited fields |
| 79 | Virtual_Dispatch_Three_Levels | 3-level virtual dispatch |
| 80 | Class_Method_With_Params | Methods with multiple parameters |

---

## Tests 81-85: Control Flow

Tests for truthy non-zero values, zero-iteration loops, cascading if/else, nested while with early return, and countdown loops.

| # | Name | Description |
|---|------|-------------|
| 81 | If_NonZero_Truthy | Non-zero values as true |
| 82 | While_Zero_Iterations | While with false condition |
| 83 | If_Else_Chain | Cascading if/else |
| 84 | Nested_While_Break_Return | Nested while with early return |
| 85 | While_Countdown | Countdown loop |

---

## Tests 86-88: Evaluation Order

Tests verifying left-to-right evaluation order for binary operations and function arguments, based on the PDF specification.

| # | Name | Description |
|---|------|-------------|
| 86 | Eval_Order_PDF_Example | PDF Figure 1 example |
| 87 | Eval_Order_Binary | Binary operation order |
| 88 | Eval_Order_Args_Three | Three-argument evaluation order |

---

## Tests 89-91: Global Variables

Tests for global initialization order, global class instances, and global arrays.

| # | Name | Description |
|---|------|-------------|
| 89 | Global_Init_Order | Global initialization order |
| 90 | Global_Class_Instance | Global class instance |
| 91 | Global_Array | Global array variable |

---

## Tests 92-96: Runtime Errors

Tests that verify runtime error detection: division by zero, method call on nil, array access on nil, and output followed by a runtime error.

| # | Name | Description |
|---|------|-------------|
| 92 | Div_Zero_In_Expression | Division by zero in expression |
| 93 | Null_Method_Call | Method call on nil object |
| 94 | Array_Nil_Access | Access on nil array |
| 95 | Div_Zero_After_Output | Output then division by zero |
| 96 | Null_Deref_After_Output | Output then null dereference |

---

## Tests 97-100: Compiler Error Cases

Tests that expect compiler-phase errors: lexer errors, semantic errors, and register allocation failure.

| # | Name | Description | Expected Output |
|---|------|-------------|-----------------|
| 97 | Lexer_Error_Bad_Token | Invalid token | ERROR |
| 98 | Semantic_Type_Mismatch | Type mismatch | ERROR(3) |
| 99 | Semantic_Undeclared | Undeclared variable | ERROR(3) |
| 100 | RegAlloc_Fail | Too many variables for registers | Register Allocation Failed |

---

## Tests 101-112: Miscellaneous

A varied collection covering void returns, factorial, arrays of objects, linked lists, default field values, nested classes, multiple return paths, GCD, power functions, string building, complex inheritance, and a comprehensive integration test.

| # | Name | Description |
|---|------|-------------|
| 101 | Void_Function_Return | Void function with return |
| 102 | Recursive_Factorial | Factorial via recursion |
| 103 | Array_Of_Objects | Array holding objects |
| 104 | Linked_List | Linked list traversal |
| 105 | Class_Init_Defaults | Default field values |
| 106 | Nested_Classes | Classes containing class instances |
| 107 | Multiple_Returns | Multiple return paths |
| 108 | GCD | Greatest common divisor |
| 109 | Power_Function | Power via recursion |
| 110 | String_Builder | Building strings via concatenation |
| 111 | Complex_Inheritance | Complex inheritance hierarchy |
| 112 | Ultimate | Comprehensive test: 4 animal classes, inheritance, virtual dispatch, arrays, linked list, recursion, saturation, string ops, nil checks |

---

## Tests 113-120: Arithmetic Edge Cases

Focused tests for overflow chains, multiplication overflow cascades, division truncation toward zero, saturation recovery, mixed arithmetic expressions, chained subtractions, division by negative numbers, and arithmetic with function return values.

| # | Name | Description |
|---|------|-------------|
| 113 | Overflow_Chain | Repeated addition pushing past saturation |
| 114 | Multiply_Overflow_Cascade | Multiplication overflow: 200*200, 32767*2, etc. |
| 115 | Division_Truncation | Division truncation toward zero: 7/2, 1/3, 100/7 |
| 116 | Saturation_Recovery | Saturate to max/min, then recover with opposite operations |
| 117 | Mixed_Arith_Expressions | Function a*b+c with overflow cases |
| 118 | Subtraction_Chains | Chained subtractions including negation of -32768 |
| 119 | Division_By_Negative | Division by negative numbers |
| 120 | Arith_With_Function_Results | double, triple, square functions with nesting |

---

## Tests 121-130: String Operations (Extended)

Extended string tests covering equality with various lengths, chained concatenation with empty strings, comparing concatenated strings to literals, interleaved PrintString/PrintInt, strings as function parameters, global string variables, string fields in classes, arrays of strings, virtual dispatch returning strings, and empty string edge cases.

| # | Name | Description |
|---|------|-------------|
| 121 | String_Equality_Various | Same content, different lengths, empty strings |
| 122 | String_Concat_Chain | Chained concatenation a+b+c with empty strings |
| 123 | String_Equality_After_Concat | Comparing concatenated to literal strings |
| 124 | PrintString_Interleaved | Interleaved PrintString and PrintInt |
| 125 | String_As_Parameter | Strings as function parameters |
| 126 | String_Global_Vars | Global string variables with concatenation |
| 127 | String_In_Class | String field in class with getter/setter |
| 128 | String_Array | Array of strings with iteration |
| 129 | String_Return_From_Method | Virtual dispatch returning strings |
| 130 | Empty_String_Edge | Empty string equality and concatenation |

---

## Tests 131-140: Array Operations (Extended)

Extended array tests covering matrix multiplication, element-by-element copy, exact bounds access violation, negative index access violation, 3D arrays, swap/reverse, sum/average, aliasing, arrays of objects, and nil element access.

| # | Name | Description |
|---|------|-------------|
| 131 | Matrix_Multiply | 2x2 matrix multiplication via arrays |
| 132 | Array_Copy | Element-by-element copy, verify independence |
| 133 | Array_Bounds_Exact | Access at index = length triggers Access Violation |
| 134 | Array_Negative_Index | Negative index triggers Access Violation |
| 135 | ThreeD_Array | 3D array via nested typedefs |
| 136 | Array_Swap_Elements | Swap function to reverse array |
| 137 | Array_Sum_Average | Sum and average (integer division) |
| 138 | Array_Alias | Two vars pointing to same array |
| 139 | Array_Of_Objects | Array of Point objects, find closest to origin |
| 140 | Array_Nil_Element_Access | Field access on nil array element |

---

## Tests 141-150: Class Features (Extended)

Extended class tests covering 5-level inheritance, selective method override, field defaults through inheritance, method chaining via return values, reference equality and nil comparison, three-level nested classes, polymorphic arrays, counter with this-reference, graph data structure, and overridden getters in grandchildren.

| # | Name | Description |
|---|------|-------------|
| 141 | Deep_Inheritance_Five | 5-level inheritance with virtual dispatch |
| 142 | Override_Selective | Selective method override across levels |
| 143 | Field_Defaults_Inheritance | Inherited field defaults (Vehicle/Car/Truck) |
| 144 | Method_Chaining_Via_Return | Pair class with nested addPairs calls |
| 145 | Class_Equality_And_Nil | Reference equality, nil comparison, aliasing |
| 146 | Class_As_Field | Three-level nested classes (Deep/Outer/Inner) |
| 147 | Polymorphic_Array | Shape[] with Circle, Rect, Triangle |
| 148 | This_In_Method | Counter class with inc/dec/addN methods |
| 149 | Multiple_Classes_Graph | Graph with Edge and Vertex classes |
| 150 | Inherited_Field_Override | Overridden getter in GrandChild |

---

## Tests 151-160: Control Flow (Extended)

Extended control flow tests covering nested while computing triangular sums, cascading if-return classifiers, early return via i*i > target, complex conditions, converging variable loops, void functions with early return, multiple accumulators, nested countAbove, zero-iteration while, and the Collatz sequence.

| # | Name | Description |
|---|------|-------------|
| 151 | Nested_While_Triangle | Nested while computing triangular sums |
| 152 | If_Else_Cascade | Cascading if-return classifier |
| 153 | Early_Return | Find smallest i where i*i > target |
| 154 | Complex_Condition | Complex conditions: (a<b)=0, (a+b)=c |
| 155 | While_With_Multiple_Updates | Two variables converging in while |
| 156 | If_Return_Void | Void function with early return |
| 157 | Loop_Accumulator | Sum, factorial, alternating sign accumulators |
| 158 | Nested_If_While | countAbove: count elements above threshold |
| 159 | While_Zero_Iterations | while(0) and while(1) with return |
| 160 | Collatz_Sequence | Collatz from 27, count steps |

---

## Tests 161-170: Function Features

Tests for recursive sum, utility functions (isEven, abs, max, min), many parameters, void functions with side effects, recursive Fibonacci, nested function calls as arguments, recursive power, Tower of Hanoi, recursive linked list methods, and global init via function calls.

| # | Name | Description |
|---|------|-------------|
| 161 | Recursive_Sum | Recursive sum 1 to n |
| 162 | Mutual_Recursion_Classes | isEven, abs, max, min utility functions |
| 163 | Many_Parameters | Functions with 8 parameters |
| 164 | Void_Functions_Side_Effects | Void functions modifying global counter |
| 165 | Recursive_Fibonacci | Classic recursive fib for n=0,1,2,5,10,15 |
| 166 | Function_Calls_As_Args | Nested calls: add(inc(5), dbl(3)), dbl(dbl(dbl(1))) |
| 167 | Recursive_Power | Recursive power with various bases/exponents |
| 168 | Tower_Of_Hanoi | Hanoi move counter for n=1,3,5,10 |
| 169 | Method_With_Recursion | Recursive linked list length and sum |
| 170 | Global_Init_With_Functions | Globals initialized by function calls |

---

## Tests 171-180: Error Cases (Extended)

Tests for compiler-phase errors: lexer errors (oversized integer literal, underscore in identifier, space in string), semantic errors (undefined function, type mismatch, undeclared class, wrong argument type, nonexistent method, return type mismatch, duplicate variable).

| # | Name | Description | Expected Output |
|---|------|-------------|-----------------|
| 171 | Lexer_Bad_Integer | Integer literal 99999 exceeds 32767 | ERROR |
| 172 | Lexer_Underscore_Id | Identifier with underscore | ERROR |
| 173 | Lexer_Bad_String | String with space character | ERROR |
| 174 | Semantic_Undefined_Func | Call to undeclared function | ERROR(3) |
| 175 | Semantic_Type_Mismatch_Assign | Assigning int to string | ERROR(4) |
| 176 | Semantic_Undeclared_Class | Using undeclared class | ERROR(3) |
| 177 | Semantic_Wrong_Arg_Type | String arg where int expected | ERROR(8) |
| 178 | Semantic_No_Such_Method | Calling non-existent method | ERROR(8) |
| 179 | Semantic_Return_Type | Returning string from int function | ERROR(3) |
| 180 | Semantic_Duplicate_Var | Duplicate variable in same scope | ERROR(4) |

---

## Tests 181-190: Global Variable Edge Cases

Tests for global variable initialization by function calls, chained dependencies, array creation during init, class instance creation, multiple global objects with mutation, side effects during init, saturation during init, string concatenation during init, arrays of objects as globals, and nested function call expressions in globals.

| # | Name | Description |
|---|------|-------------|
| 181 | Global_Func_Init | Global variable initialized by function call |
| 182 | Global_Chain_Init | Chain of global init dependencies (x->y->z->w) |
| 183 | Global_Array_Fill | Global array created at init, filled by function |
| 184 | Global_Object_Init | Global class instance with default fields |
| 185 | Global_Multiple_Objs | Two global objects with mutation |
| 186 | Global_Func_Side_Effect | Global init calls nextId() modifying counter |
| 187 | Global_Saturation_Init | Global init triggers saturation (32000+32000) |
| 188 | Global_String_Concat | Global string concatenation at init |
| 189 | Global_Array_Of_Objs | Global array of class instances |
| 190 | Global_Nested_Expr | Globals with nested function call expressions |

---

## Tests 191-200: Register Allocation Stress

Tests designed to stress the register allocator with many local variables, long computation chains, nested calls with many arguments, functions with 10 parameters, deeply nested arithmetic expressions, multiple concurrent local arrays, recursive functions with many locals per frame, multiple return paths through nested ifs, interleaved function calls, and many temporary result variables.

| # | Name | Description |
|---|------|-------------|
| 191 | Many_Locals_Sum | 20 local variables summed |
| 192 | Complex_Expr_Chain | 10 chained local computations |
| 193 | Nested_Calls_Many_Args | Nested function calls as arguments |
| 194 | Many_Params_Func | Functions with 10 parameters |
| 195 | Deep_Nesting_Expr | Deeply nested arithmetic |
| 196 | Many_Local_Arrays | 4 local arrays used concurrently |
| 197 | Recursive_Many_Locals | Recursive function with 4 locals per frame |
| 198 | Multi_Return_Paths | 5 params, 6 return paths through nested ifs |
| 199 | Interleaved_Calls | Interleaved doubleIt, tripleIt, addTwo, square calls |
| 200 | Lots_Of_Temps | 8 result variables from complex expressions |

---

## Tests 201-210: Evaluation Order (Extended)

Tests verifying left-to-right evaluation order for addition, multiplication, three-argument function calls, subtraction, division, array index side effects, equality with side effects, method call arguments, nested binary expressions with precedence, and sequential assignment right-hand sides.

| # | Name | Description |
|---|------|-------------|
| 201 | Eval_Order_Add | Left-to-right in sideA() + sideB() |
| 202 | Eval_Order_Mul | Left-to-right in incG() * doubleG() |
| 203 | Eval_Order_Three_Args | 3-arg evaluation: process(bump(1), bump(10), bump(100)) |
| 204 | Eval_Order_Subtraction | Left-to-right in decG() - negG() |
| 205 | Eval_Order_Division | Left-to-right in halfG() / addTen() |
| 206 | Eval_Order_Array_Idx | Side effects in array index: arr[next()] + arr[next()] |
| 207 | Eval_Order_Equality | Side effects in equality: setG(5) = setG(10) |
| 208 | Eval_Order_Method_Args | Eval order for method call arguments |
| 209 | Eval_Order_Nested_Binary | Eval with precedence: tick() + tick() * tick() |
| 210 | Eval_Order_Assign_Rhs | Sequential getAndBump() calls |

---

## Tests 211-220: Runtime Error Edge Cases

Tests for runtime errors occurring in various contexts: division by zero inside if blocks, mid-loop, from function return values; null pointer dereference on field write, through chained nil references; negative array index; exact bounds violation; nil array access; division by zero inside methods; and null dereference through uninitialized array elements.

| # | Name | Description |
|---|------|-------------|
| 211 | Div_Zero_In_If | Division by zero inside if block |
| 212 | Div_Zero_In_While | Division by zero mid-loop |
| 213 | Div_Zero_Func_Return | Division by zero from function return value |
| 214 | Null_Field_Write | Null pointer dereference on field write |
| 215 | Null_Method_Deep | Null through chain (a.next is nil) |
| 216 | Array_Neg_Index | Negative array index |
| 217 | Array_Exact_Bound | Access at exactly array length |
| 218 | Null_Array_Access | Element access on nil array |
| 219 | Div_Zero_In_Method | Division by zero inside class method |
| 220 | Null_In_Array_Elem | Null dereference through uninitialized array element |

---

## Tests 221-230: Complex Programs / Algorithms

Tests implementing complete algorithms: selection sort, insertion sort, stack (push/pop/peek), queue via linked list, Fibonacci array, matrix multiplication, iterative power with saturation, sieve of Eratosthenes, binary search, and GCD/LCM.

| # | Name | Description |
|---|------|-------------|
| 221 | Selection_Sort | Selection sort on 8 elements |
| 222 | Insertion_Sort | Insertion sort on 6 elements |
| 223 | Stack_Class | Stack with push, pop, peek, isEmpty |
| 224 | Queue_Linked_List | Queue with enqueue, dequeue, isEmpty |
| 225 | Fibonacci_Array | First 20 Fibonacci numbers in array |
| 226 | Matrix_Multiply | 2x2 matrix multiplication using flat arrays |
| 227 | Power_Iterative | Iterative power with saturation |
| 228 | Sieve_Of_Primes | Sieve of Eratosthenes for primes under 50 |
| 229 | Binary_Search | Binary search on sorted array |
| 230 | GCD_LCM | GCD (Euclidean) and LCM |

---

## Tests 231-240: Inheritance and Polymorphism

Tests for three-level virtual dispatch, selective override, field inheritance across hierarchies, polymorphic arrays of shapes, inherited methods with overridden helpers, five-level chains with base reference reassignment, expression trees (Expr/Literal/Add/Mul), upcasting and aliasing, method override with field access in subclass, and multiple subtypes with findMaxPriority.

| # | Name | Description |
|---|------|-------------|
| 231 | Three_Level_Override | 3-level virtual dispatch (Base/Mid/Leaf) |
| 232 | Partial_Override | Some methods inherited, some overridden |
| 233 | Field_Inheritance | Field inheritance across Vehicle/Car/Truck |
| 234 | Polymorphic_Array | Shape array with Circle, Rectangle, Triangle |
| 235 | Method_Calls_Inherited | Inherited getCount() with overridden increment() |
| 236 | Deep_Chain_Five | 5-level chain with base reference reassignment |
| 237 | Override_With_Field | Expression tree (Expr/Literal/Add/Mul) with eval() |
| 238 | Upcast_Assign | Upcasting, aliasing, field modification through base |
| 239 | Method_Returns_Self_Type | Override with field access in subclass |
| 240 | Multiple_Subtypes | 3 subtypes in array with findMaxPriority |

---

## Tests 241-250: Full Integration

End-to-end integration tests that combine multiple language features into realistic programs: a zoo system with polymorphic arrays, bank accounts with inheritance and interest, full linked list operations, string array manipulation, binary search tree, saturation in complex scenarios, global objects with method calls during init, a matrix class wrapping a flat array, Collatz conjecture with complex branching, and a player/scoreboard system combining arrays, globals, strings, and saturation.

| # | Name | Description |
|---|------|-------------|
| 241 | Zoo_System | Zoo with Mammal/Reptile, polymorphic arrays, counting |
| 242 | Bank_Account | Bank accounts with inheritance, transfer, interest |
| 243 | Linked_List_Ops | Full linked list: prepend, length, sum, reverse, print |
| 244 | String_Array_Ops | String array with concatenation and equality |
| 245 | Recursive_Tree | BST: insert, inorder traversal, sum |
| 246 | Saturation_Complex | Saturation in functions, assignments, boundaries |
| 247 | Global_Obj_Methods | Global object with methods called during init |
| 248 | Nested_Array_Class | Matrix class wrapping flat array with get/set/trace |
| 249 | Complex_Control_Flow | Collatz conjecture with complex branching |
| 250 | Full_Integration | Player/Scoreboard, arrays, globals, strings, saturation |

---

## Error and Failure Tests Summary

The following tests are expected to produce a compiler-phase error (not SPIM output). They should be caught before code generation or during register allocation.

### Lexer Errors (Expected: ERROR)

| # | Name | Reason |
|---|------|--------|
| 31 | Complex_Saturation | -32768 literal (lexer rejects the token 32768) |
| 97 | Lexer_Error_Bad_Token | Invalid token |
| 171 | Lexer_Bad_Integer | Integer literal 99999 exceeds 32767 |
| 172 | Lexer_Underscore_Id | Underscore in identifier (not valid in L) |
| 173 | Lexer_Bad_String | String containing a space character |

### Semantic Errors (Expected: ERROR with error code)

| # | Name | Expected | Reason |
|---|------|----------|--------|
| 28 | Chained_Assignments | ERROR(8) | Chained assignment is invalid L syntax |
| 98 | Semantic_Type_Mismatch | ERROR(3) | Type mismatch |
| 99 | Semantic_Undeclared | ERROR(3) | Undeclared variable |
| 174 | Semantic_Undefined_Func | ERROR(3) | Call to undeclared function |
| 175 | Semantic_Type_Mismatch_Assign | ERROR(4) | Assigning int to string variable |
| 176 | Semantic_Undeclared_Class | ERROR(3) | Using an undeclared class type |
| 177 | Semantic_Wrong_Arg_Type | ERROR(8) | String argument where int expected |
| 178 | Semantic_No_Such_Method | ERROR(8) | Calling a method that does not exist |
| 179 | Semantic_Return_Type | ERROR(3) | Returning string from int function |
| 180 | Semantic_Duplicate_Var | ERROR(4) | Duplicate variable declaration in same scope |

### Register Allocation Failures (Expected: Register Allocation Failed)

| # | Name | Reason |
|---|------|--------|
| 26 | (unnamed) | 18-parameter function exceeds available registers |
| 100 | RegAlloc_Fail | Too many live variables for register allocation |

---

## Runtime Error Tests Summary

The following tests produce SPIM output that includes a runtime error message. The program may print some output before the error is triggered. The error message is part of the expected output.

### Access Violation (Array Out of Bounds)

| # | Name | Trigger |
|---|------|---------|
| 08 | Access_Violation | Array out of bounds |
| 09 | Access_Violation | Another access violation case |
| 34 | Single_Element_Array_Bounds | arr[1] on a size-1 array |
| 54 | Mixed_Access_Violations | Valid accesses followed by out-of-bounds |
| 73 | Array_Negative_Index | Negative array index |
| 74 | Array_Index_At_Length | Index equal to array length |
| 133 | Array_Bounds_Exact | Access at index = length |
| 134 | Array_Negative_Index | Negative index |
| 216 | Array_Neg_Index | Negative array index |
| 217 | Array_Exact_Bound | Access at exactly array length |

### Division by Zero

| # | Name | Trigger |
|---|------|---------|
| 24 | (unnamed) | Division by zero in method |
| 92 | Div_Zero_In_Expression | Division by zero in an expression |
| 95 | Div_Zero_After_Output | Output printed, then division by zero |
| 211 | Div_Zero_In_If | Division by zero inside if block |
| 212 | Div_Zero_In_While | Division by zero mid-loop |
| 213 | Div_Zero_Func_Return | Division by zero from function return value |
| 219 | Div_Zero_In_Method | Division by zero inside class method |

### Invalid Pointer Dereference (Null/Nil)

| # | Name | Trigger |
|---|------|---------|
| 77 | Nil_Field_Access | Field access on nil object |
| 93 | Null_Method_Call | Method call on nil object |
| 94 | Array_Nil_Access | Access on nil array |
| 96 | Null_Deref_After_Output | Output printed, then null dereference |
| 140 | Array_Nil_Element_Access | Field access on nil array element |
| 214 | Null_Field_Write | Null pointer dereference on field write |
| 215 | Null_Method_Deep | Null reference through chained field (a.next is nil) |
| 218 | Null_Array_Access | Element access on nil array |
| 220 | Null_In_Array_Elem | Null dereference through uninitialized array element |

---

## Notes on the L Language and Testing

### L Language Constraints

- **Integer range**: All integers are 16-bit signed, ranging from -32768 to 32767.
- **Saturation arithmetic**: Arithmetic operations that overflow or underflow saturate (clamp) to 32767 or -32768 respectively, rather than wrapping around.
- **Integer literals**: The lexer accepts integer literals in the range 0 to 32767. The value -32768 can only be produced by computation (e.g., 0 - 32767 - 1), not as a literal.
- **Division**: Integer division truncates toward zero (e.g., 7/2 = 3, -7/2 = -3).
- **Booleans**: 0 is false, any non-zero value is true. There is no dedicated boolean type.
- **Strings**: Compared by content equality (not reference). Concatenation with `+`.
- **Arrays**: Compared by reference equality. Out-of-bounds access triggers an Access Violation runtime error.
- **Classes**: Compared by reference equality. Field access or method call on nil triggers an Invalid Pointer Dereference runtime error.
- **Evaluation order**: Left-to-right for binary operators and function/method arguments.
- **Register allocation**: The compiler uses a fixed number of registers. Programs that require more simultaneously live variables than available registers will fail with "Register Allocation Failed".

### SPIM Output Format

All expected output files that represent successful execution include the standard SPIM header. Runtime errors (Access Violation, Division by Zero, Invalid Pointer Dereference) are printed as part of the SPIM output and cause immediate program termination.

### File Organization

- **Test source files**: `tests/TEST_<NN>_<Name>.txt`
- **Expected output files**: `expected_output/TEST_<NN>_<Name>_Expected_Output.txt`

### Running Tests

Tests are executed by compiling each L source file through all compiler phases (lexing, parsing, semantic analysis, IR generation, register allocation, MIPS code generation) and then running the resulting MIPS assembly through SPIM. The actual SPIM output is compared against the expected output file. For error tests, the compiler output is compared against the expected error string.
