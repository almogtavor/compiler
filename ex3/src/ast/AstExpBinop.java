package ast;

import Exceptions.SemanticException;
import types.*;
import symboltable.SymbolTable;

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
        System.out.println("AST EXP BINOP: " + opToString());

        if (left != null) left.printMe();
        if (right != null) right.printMe();

        AstGraphviz.getInstance().logNode(
                serialNumber,
                String.format("BINOP(%s)", opToString())
        );

        if (left != null)
            AstGraphviz.getInstance().logEdge(serialNumber, left.serialNumber);

        if (right != null)
            AstGraphviz.getInstance().logEdge(serialNumber, right.serialNumber);
    }

    @Override
    public Type SemantMe() {
        Type t1 = left.SemantMe();
        Type t2 = right.SemantMe();

        switch (op) {

            case OP_PLUS:
                if (t1 == TypeInt.getInstance() && t2 == TypeInt.getInstance())
                    return TypeInt.getInstance();
                if (t1 == TypeString.getInstance() && t2 == TypeString.getInstance())
                    return TypeString.getInstance();
                throw new SemanticException(lineNumber);

            case OP_MINUS:
            case OP_TIMES:
                if (t1 != TypeInt.getInstance() || t2 != TypeInt.getInstance())
                    throw new SemanticException(lineNumber);
                return TypeInt.getInstance();
            case OP_DIVIDE:
                if (t1 != TypeInt.getInstance() || t2 != TypeInt.getInstance())
                    throw new SemanticException(lineNumber);
                
                if (right instanceof AstExpInt) {
                    AstExpInt rightInt = (AstExpInt) right;
                    if (rightInt.value == 0)
                        throw new SemanticException(lineNumber);
                }
                
                return TypeInt.getInstance();
            case OP_LT:
            case OP_GT:
                if (t1 == TypeInt.getInstance() && t2 == TypeInt.getInstance())
                    return TypeInt.getInstance();
                throw new SemanticException(lineNumber);

            case OP_EQ:
                if (t1 == t2 &&
                (t1 == TypeInt.getInstance() || t1 == TypeString.getInstance()))
                    return TypeInt.getInstance();

                if (t1.isAssignableFrom(t2) || t2.isAssignableFrom(t1))
                    return TypeInt.getInstance();

                if ((t1 instanceof TypeArray && t2 == TypeNil.getInstance()) ||
                    (t2 instanceof TypeArray && t1 == TypeNil.getInstance()))
                    return TypeInt.getInstance();

                throw new SemanticException(lineNumber);

            default:
                throw new SemanticException(lineNumber);
        }
    }

}
