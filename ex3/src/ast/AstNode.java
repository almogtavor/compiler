package ast;

public abstract class AstNode {

    public int serialNumber;
    public int lineNumber;

    public AstNode(int lineNumber) {
        this.serialNumber = AstNodeSerialNumber.getFresh();
        this.lineNumber = lineNumber;
    }
    
    public int getLine() {
        return this.lineNumber;
    }

    public void printMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        System.out.print("AST NODE UNKNOWN\n");
    }
}
