package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;

public class AstClassDec extends AstDec {

    public String name;
    public String parentName;
    public AstCFieldList fields;

    public AstClassDec(String name, String parentName, AstCFieldList fields, int lineNumber) {
        super(lineNumber);
        this.name = name;
        this.parentName = parentName;
        this.fields = fields;
    }

    private TypeClassVarDecList buildMemberList(AstCFieldList fields) {
        TypeClassVarDecList result = null;

        for (AstCFieldList it = fields; it != null; it = it.tail) {
            AstDec dec = it.head.dec;

            if (dec instanceof AstVarDec) {
                AstVarDec vd = (AstVarDec) dec;
                Type t = vd.type.SemantMe();
                result = new TypeClassVarDecList(new TypeClassVarDec(t, vd.name), result);
            }
            else if (dec instanceof AstFuncDec) {
                AstFuncDec fd = (AstFuncDec) dec;
                Type retType = fd.retType.SemantMe();
                TypeList params = (fd.args != null ? fd.args.buildTypeList() : null);
                TypeFunction funcType = new TypeFunction(retType, fd.name, params);
                result = new TypeClassVarDecList(new TypeClassVarDec(funcType, fd.name), result);
            }
        }
        return result;
    }

    @Override
    public void printMe() {

        if (fields != null)
            fields.printMe();

        String label = (parentName != null)
                ? String.format("CLASS(%s EXTENDS %s)", name, parentName)
                : String.format("CLASS(%s)", name);

        AstGraphviz.getInstance().logNode(serialNumber, label);

        if (fields != null)
            AstGraphviz.getInstance().logEdge(serialNumber, fields.serialNumber);
    }

    @Override
    public Type SemantMe() {
        System.out.println("SemantMe: " + this.getClass().getSimpleName());

        SymbolTable sym = SymbolTable.getInstance();

        if (SymbolTable.isReservedKeyword(name))
            throw new SemanticException(lineNumber);

        if (sym.findInCurrentScope(name) != null)
            throw new SemanticException(lineNumber);

        TypeClass parent = null;
        if (parentName != null) {
            Type p = sym.find(parentName);
            if (!(p instanceof TypeClass))
                throw new SemanticException(lineNumber);
            parent = (TypeClass) p;
        }

        checkNoDuplicateNamesInClass();

        if (parent != null)
            checkInheritanceRules(parent);

        TypeClass thisClass = new TypeClass(name, parent, null);
        sym.enter(name, thisClass);

        sym.beginScope();

        TypeClass prevClass = sym.getCurrentClass();
        sym.setCurrentClass(thisClass);

        processMembersInOrder();

        sym.setCurrentClass(prevClass);

        sym.endScope();

        TypeClassVarDecList members = buildMemberList(fields);

        thisClass.dataMembers = members;

        return thisClass;
    }

    private void processMembersInOrder() {
        if (fields == null) return;

        SymbolTable sym = SymbolTable.getInstance();

        for (AstCFieldList it = fields; it != null; it = it.tail) {
            AstDec dec = it.head.dec;

            if (dec instanceof AstVarDec) {
                AstVarDec vd = (AstVarDec) dec;
                
                if (SymbolTable.isReservedKeyword(vd.name))
                    throw new SemanticException(vd.lineNumber);
                
                Type t = vd.type.SemantMe();
                if (t == TypeVoid.getInstance())
                    throw new SemanticException(vd.lineNumber);

                if (sym.findInCurrentScope(vd.name) != null)
                    throw new SemanticException(vd.lineNumber);
                
                sym.enter(vd.name, t);

                if (vd.init != null) {
                    Type initType = vd.init.SemantMe();

                    if (initType == TypeNil.getInstance()) {
                        if (!(t instanceof TypeClass) && !(t instanceof TypeArray))
                            throw new SemanticException(vd.lineNumber);
                    }

                    if (!t.isAssignableFrom(initType))
                        throw new SemanticException(vd.lineNumber);
                }
            }
            else if (dec instanceof AstFuncDec) {
                AstFuncDec fd = (AstFuncDec) dec;
                
                if (SymbolTable.isReservedKeyword(fd.name))
                    throw new SemanticException(fd.lineNumber);
                
                Type returnType = fd.retType.SemantMe();
                TypeList paramsList = (fd.args != null ? fd.args.buildTypeList() : null);

                if (sym.findInCurrentScope(fd.name) != null)
                    throw new SemanticException(fd.lineNumber);

                TypeFunction funcType = new TypeFunction(returnType, fd.name, paramsList);
                sym.enter(fd.name, funcType);

                sym.beginScope();
                sym.enter("__RET_TYPE__", returnType);

                if (fd.args != null)
                    fd.args.SemantMe();

                if (fd.body != null)
                    fd.body.SemantMe();

                sym.endScope();
            }
        }
    }

    private void checkNoDuplicateNamesInClass() {
        if (fields == null) return;
        
        java.util.Set<String> names = new java.util.HashSet<>();
        
        for (AstCFieldList it = fields; it != null; it = it.tail) {
            String memberName = getMemberName(it.head.dec);
            if (memberName != null) {
                if (names.contains(memberName))
                    throw new SemanticException(it.head.dec.lineNumber);
                names.add(memberName);
            }
        }
    }

    private void checkInheritanceRules(TypeClass parent) {
        if (fields == null) return;

        for (AstCFieldList it = fields; it != null; it = it.tail) {
            AstDec dec = it.head.dec;
            
            if (dec instanceof AstVarDec) {
                AstVarDec vd = (AstVarDec) dec;
                if (findInParentChain(parent, vd.name) != null)
                    throw new SemanticException(vd.lineNumber);
            }
            else if (dec instanceof AstFuncDec) {
                AstFuncDec fd = (AstFuncDec) dec;
                Type parentMember = findInParentChain(parent, fd.name);
                
                if (parentMember != null) {
                    if (!(parentMember instanceof TypeFunction))
                        throw new SemanticException(fd.lineNumber);
                    
                    TypeFunction parentFunc = (TypeFunction) parentMember;
                    
                    Type retType = fd.retType.SemantMe();
                    TypeList params = (fd.args != null ? fd.args.buildTypeList() : null);
                    
                    if (!signaturesMatch(parentFunc, retType, params))
                        throw new SemanticException(fd.lineNumber);
                }
            }
        }
    }

    private Type findInParentChain(TypeClass parent, String name) {
        TypeClass curr = parent;
        while (curr != null) {
            if (curr.dataMembers != null) {
                Type found = curr.dataMembers.find(name);
                if (found != null)
                    return found;
            }
            curr = curr.father;
        }
        return null;
    }

    private boolean signaturesMatch(TypeFunction parentFunc, Type retType, TypeList params) {
        if (parentFunc.returnType != retType)
            return false;
        
        TypeList p1 = parentFunc.params;
        TypeList p2 = params;
        
        while (p1 != null && p2 != null) {
            if (p1.head != p2.head)
                return false;
            p1 = p1.tail;
            p2 = p2.tail;
        }
        
        return p1 == null && p2 == null;
    }

    private String getMemberName(AstDec dec) {
        if (dec instanceof AstVarDec)
            return ((AstVarDec) dec).name;
        if (dec instanceof AstFuncDec)
            return ((AstFuncDec) dec).name;
        return null;
    }
}