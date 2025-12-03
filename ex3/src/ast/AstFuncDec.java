package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;

public class AstFuncDec extends AstDec {

    public AstType retType;
    public String name;
    public AstArgList args;
    public AstStmtList body;

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

        if (retType != null)
            AstGraphviz.getInstance().logEdge(serialNumber, retType.serialNumber);
        if (args != null)
            AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);
        if (body != null)
            AstGraphviz.getInstance().logEdge(serialNumber, body.serialNumber);
    }

    @Override
    public Type SemantMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        // 1. tipos hachzara
        Type returnType = retType.SemantMe();

        // 2. build parameter type list ONLY (do NOT declare names yet)
        TypeList paramsList = null;
        if (args != null)
            paramsList = args.buildTypeList();

        // 3. enter function into global scope BEFORE body semantic analysis
        if (SymbolTable.getInstance().findInCurrentScope(name) != null)
            throw new SemanticException(lineNumber);

        TypeFunction funcType = new TypeFunction(returnType, name, paramsList);
        SymbolTable.getInstance().enter(name, funcType);

        // 4. open function scope
        SymbolTable.getInstance().beginScope();

        // 4.5. *** הוסף את זה! *** הכנס את טיפוס ההחזרה למעקב
        SymbolTable.getInstance().enter("__RET_TYPE__", returnType);

        // 5. now declare parameters inside the scope
        if (args != null)
            args.SemantMe();   // HERE we insert arg names

        // 6. check body
        if (body != null)
            body.SemantMe();

        // 7. close scope
        SymbolTable.getInstance().endScope();

        return funcType;
    }


}
