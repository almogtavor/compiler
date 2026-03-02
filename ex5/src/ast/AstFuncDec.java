package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;
import ir.*;
import codegen.CodeGenInfo;

public class AstFuncDec extends AstDec {
    public AstType retType;
    public String name;
    public AstArgList args;
    public AstStmtList body;
    public String funcLabel;

    public AstFuncDec(AstType retType, String name, AstArgList args, AstStmtList body, int lineNumber) {
        super(lineNumber);
        this.retType = retType;
        this.name = name;
        this.args = args;
        this.body = body;
    }

    @Override
    public void printMe() {
        if (retType != null) retType.printMe();
        if (args != null) args.printMe();
        if (body != null) body.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "FUNC(" + name + ")");
        if (retType != null) AstGraphviz.getInstance().logEdge(serialNumber, retType.serialNumber);
        if (args != null) AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);
        if (body != null) AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }

    @Override
    public Type SemantMe() {
        if (SymbolTable.isReservedKeyword(name))
            throw new SemanticException(lineNumber);
        Type returnType = retType.SemantMe();
        TypeList paramsList = null;
        if (args != null) paramsList = args.buildTypeList();
        if (SymbolTable.getInstance().findInCurrentScope(name) != null)
            throw new SemanticException(lineNumber);
        TypeFunction funcType = new TypeFunction(returnType, name, paramsList);
        SymbolTable.getInstance().enter(name, funcType);
        SymbolTable.getInstance().beginScope();
        SymbolTable.getInstance().enter("__RET_TYPE__", returnType);
        if (args != null) args.SemantMe();
        if (body != null) body.SemantMe();
        SymbolTable.getInstance().endScope();
        return funcType;
    }

    @Override
    public Temp IRme() {
        return IRmeWithLabel(this.funcLabel != null ? this.funcLabel : "func_" + name);
    }

    public Temp IRmeWithLabel(String label) {
        this.funcLabel = label;
        CodeGenInfo info = CodeGenInfo.getInstance();
        String prevFunc = info.getCurrentFunction();
        info.setCurrentFunction(label);

        IrCommandLabel lbl = new IrCommandLabel(label);
        lbl.isFunctionEntry = true;
        Ir.getInstance().AddIrCommand(lbl);

        if (args != null) args.IRme();
        if (body != null) body.IRme();

        Type rt = retType.SemantMe();
        if (rt == TypeVoid.getInstance()) {
            Ir.getInstance().AddIrCommand(new IrCommandReturnVoid());
        } else {
            Temp zero = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandConstInt(zero, 0));
            Ir.getInstance().AddIrCommand(new IrCommandReturn(zero));
        }

        info.setCurrentFunction(prevFunc);
        return null;
    }
}
