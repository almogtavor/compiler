package ast;

import temp.Temp;

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
        System.out.print("AST NODE UNKNOWN\n");
    }

    public Temp IRme() {
        return null;
    }
}
