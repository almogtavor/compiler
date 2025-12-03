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
                // בנה TypeFunction עבור המתודה
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

        // 1. האם השם כבר תפוס?
        if (sym.findInCurrentScope(name) != null)
            throw new SemanticException(lineNumber);

        // 2. בדיקת parent
        TypeClass parent = null;
        if (parentName != null) {
            Type p = sym.find(parentName);
            if (!(p instanceof TypeClass))
                throw new SemanticException(lineNumber);
            parent = (TypeClass) p;
        }

        // 3. בדיקת שמות כפולים בתוך המחלקה (overloading + shadowing פנימי)
        checkNoDuplicateNamesInClass();

        // 4. בדיקת shadowing והורשה
        if (parent != null)
            checkInheritanceRules(parent);

        // 5. צור מחלקה ריקה והכנס לטבלה מיד (לצורך self-reference)
        TypeClass thisClass = new TypeClass(name, parent, null);
        sym.enter(name, thisClass);

        // 6. פתח סקופ של מחלקה
        sym.beginScope();

        // 6.5. *** הוסף! *** שמור ועדכן את ה-class הנוכחי
        TypeClass prevClass = sym.getCurrentClass();
        sym.setCurrentClass(thisClass);

        // 7. סמנטי שדות ומתודות
        if (fields != null)
            fields.SemantMe();

        // 7.5. *** הוסף! *** החזר את ה-class הקודם
        sym.setCurrentClass(prevClass);

        // 8. סגור סקופ
        sym.endScope();

        // 9. בנה רשימת שדות (רק עכשיו!)
        TypeClassVarDecList members = buildMemberList(fields);

        // 10. עדכן את המחלקה
        thisClass.dataMembers = members;

        return thisClass;
    }

    // מתודה חדשה: בדיקת שמות כפולים בתוך המחלקה
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

    // מתודה חדשה: בדיקת כללי הורשה
    private void checkInheritanceRules(TypeClass parent) {
        if (fields == null) return;

        for (AstCFieldList it = fields; it != null; it = it.tail) {
            AstDec dec = it.head.dec;
            
            if (dec instanceof AstVarDec) {
                AstVarDec vd = (AstVarDec) dec;
                // אסור shadowing של field
                if (findInParentChain(parent, vd.name) != null)
                    throw new SemanticException(vd.lineNumber);
            }
            else if (dec instanceof AstFuncDec) {
                AstFuncDec fd = (AstFuncDec) dec;
                Type parentMember = findInParentChain(parent, fd.name);
                
                if (parentMember != null) {
                    // אם ה-parent יש field באותו שם - ERROR (shadowing)
                    if (!(parentMember instanceof TypeFunction))
                        throw new SemanticException(fd.lineNumber);
                    
                    // אם זה method overriding - בדוק חתימה זהה
                    TypeFunction parentFunc = (TypeFunction) parentMember;
                    
                    // בנה את הטיפוס של המתודה הנוכחית
                    Type retType = fd.retType.SemantMe();
                    TypeList params = (fd.args != null ? fd.args.buildTypeList() : null);
                    
                    // בדוק התאמה
                    if (!signaturesMatch(parentFunc, retType, params))
                        throw new SemanticException(fd.lineNumber);
                }
            }
        }
    }

    // מתודת עזר: חיפוש בשרשרת ההורים
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

    // מתודת עזר: בדיקת התאמת חתימות
    private boolean signaturesMatch(TypeFunction parentFunc, Type retType, TypeList params) {
        // בדוק return type
        if (parentFunc.returnType != retType)
            return false;
        
        // בדוק parameters
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

    // מתודת עזר: קבלת שם member
    private String getMemberName(AstDec dec) {
        if (dec instanceof AstVarDec)
            return ((AstVarDec) dec).name;
        if (dec instanceof AstFuncDec)
            return ((AstFuncDec) dec).name;
        return null;
    }


}
