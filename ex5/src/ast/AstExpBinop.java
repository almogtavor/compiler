package ast;

import Exceptions.SemanticException;
import types.*;
import symboltable.SymbolTable;
import temp.*;
import ir.*;

// AST node for binary operations: arithmetic (+,-,*,/), comparison (<,>,=).
// Type rules vary by operator - see SemantMe().
// All integer results use saturation arithmetic (clamped in MIPS codegen).
public class AstExpBinop extends AstExp {

    public static final int OP_PLUS   = 1;
    public static final int OP_MINUS  = 2;
    public static final int OP_TIMES  = 3;
    public static final int OP_DIVIDE = 4;
    public static final int OP_LT     = 5;
    public static final int OP_GT     = 6;
    public static final int OP_EQ     = 7;

    public AstExp left;
    public AstExp right;
    public int op;
    private Type leftType;
    private Type rightType;

    public AstExpBinop(AstExp left, AstExp right, int op, int lineNumber) {
        super(lineNumber);
        this.left = left;
        this.right = right;
        this.op = op;
    }

    private String opToString() {
        return switch (op) {
            case OP_PLUS   -> "+";
            case OP_MINUS  -> "-";
            case OP_TIMES  -> "*";
            case OP_DIVIDE -> "/";
            case OP_LT     -> "<";
            case OP_GT     -> ">";
            case OP_EQ     -> "=";
            default        -> "?";
        };
    }

    @Override
    public void printMe() {
        if (left != null) left.printMe();
        if (right != null) right.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("BINOP(%s)", opToString()));
        if (left != null) AstGraphviz.getInstance().logEdge(serialNumber, left.serialNumber);
        if (right != null) AstGraphviz.getInstance().logEdge(serialNumber, right.serialNumber);
    }

    // Type checking rules per operator:
    //   +     : int+int -> int, string+string -> string (concatenation)
    //   -,*   : int only
    //   /     : int only, static check for literal 0
    //   <, >  : int only, result is int (0 or 1)
    //   =     : int=int, string=string (content eq), class/array (address eq),
    //           nil can be compared with any class or array type
    @Override
    public Type SemantMe() {
        leftType = left.SemantMe();
        rightType = right.SemantMe();

        switch (op) {
            case OP_PLUS:
                if (leftType == TypeInt.getInstance() && rightType == TypeInt.getInstance())
                    return TypeInt.getInstance();
                if (leftType == TypeString.getInstance() && rightType == TypeString.getInstance())
                    return TypeString.getInstance();
                throw new SemanticException(lineNumber);
            case OP_MINUS:
            case OP_TIMES:
                if (leftType != TypeInt.getInstance() || rightType != TypeInt.getInstance())
                    throw new SemanticException(lineNumber);
                return TypeInt.getInstance();
            case OP_DIVIDE:
                if (leftType != TypeInt.getInstance() || rightType != TypeInt.getInstance())
                    throw new SemanticException(lineNumber);
                if (right instanceof AstExpInt && ((AstExpInt) right).value == 0)
                    throw new SemanticException(lineNumber);
                return TypeInt.getInstance();
            case OP_LT:
            case OP_GT:
                if (leftType == TypeInt.getInstance() && rightType == TypeInt.getInstance())
                    return TypeInt.getInstance();
                throw new SemanticException(lineNumber);
            case OP_EQ:
                if (leftType == rightType &&
                    (leftType == TypeInt.getInstance() || leftType == TypeString.getInstance()))
                    return TypeInt.getInstance();
                if (leftType.isAssignableFrom(rightType) || rightType.isAssignableFrom(leftType))
                    return TypeInt.getInstance();
                if ((leftType instanceof TypeArray && rightType == TypeNil.getInstance()) ||
                    (rightType instanceof TypeArray && leftType == TypeNil.getInstance()))
                    return TypeInt.getInstance();
                throw new SemanticException(lineNumber);
            default:
                throw new SemanticException(lineNumber);
        }
    }

    // IR generation: left is evaluated first (L spec), then right, then the operation.
    // String + dispatches to concat; string = dispatches to content comparison.
    // Division emits a div-zero runtime check before the actual div.
    // Integer equality for classes/arrays compares addresses (same IR as int eq).
    @Override
    public Temp IRme() {
        Temp t1 = left.IRme();  // left evaluated first per spec
        Temp t2 = right.IRme();
        Temp dst = TempFactory.getInstance().getFreshTemp();

        switch (op) {
            case OP_PLUS:
                if (leftType == TypeString.getInstance()) {
                    Ir.getInstance().AddIrCommand(new IrCommandStringConcat(dst, t1, t2));
                } else {
                    Ir.getInstance().AddIrCommand(new IrCommandBinopAddIntegers(dst, t1, t2));
                }
                break;
            case OP_MINUS:
                Ir.getInstance().AddIrCommand(new IrCommandBinopSubIntegers(dst, t1, t2));
                break;
            case OP_TIMES:
                Ir.getInstance().AddIrCommand(new IrCommandBinopMulIntegers(dst, t1, t2));
                break;
            case OP_DIVIDE:
                Ir.getInstance().AddIrCommand(new IrCommandCheckDivZero(t2));
                Ir.getInstance().AddIrCommand(new IrCommandBinopDivIntegers(dst, t1, t2));
                break;
            case OP_LT:
                Ir.getInstance().AddIrCommand(new IrCommandBinopLtIntegers(dst, t1, t2));
                break;
            case OP_GT:
                Ir.getInstance().AddIrCommand(new IrCommandBinopGtIntegers(dst, t1, t2));
                break;
            case OP_EQ:
                if (leftType == TypeString.getInstance() && rightType == TypeString.getInstance()) {
                    Ir.getInstance().AddIrCommand(new IrCommandStringEq(dst, t1, t2));
                } else {
                    Ir.getInstance().AddIrCommand(new IrCommandBinopEqIntegers(dst, t1, t2));
                }
                break;
        }
        return dst;
    }
}
