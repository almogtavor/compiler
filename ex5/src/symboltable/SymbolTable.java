package symboltable;

import types.*;

public class SymbolTable {
    private int hashArraySize = 13;
    private TypeClass currentClass = null;
    private SymbolTableEntry[] table = new SymbolTableEntry[hashArraySize];
    private SymbolTableEntry top;
    private int topIndex = 0;
    private int irVarIdCounter = 0;

    private int hash(String s) {
        if (s.charAt(0) == 'l') return 1;
        if (s.charAt(0) == 'm') return 1;
        if (s.charAt(0) == 'r') return 3;
        if (s.charAt(0) == 'i') return 6;
        if (s.charAt(0) == 'd') return 6;
        if (s.charAt(0) == 'k') return 6;
        if (s.charAt(0) == 'f') return 6;
        if (s.charAt(0) == 'S') return 6;
        return 12;
    }

    public void setCurrentClass(TypeClass cls) { this.currentClass = cls; }
    public TypeClass getCurrentClass() { return this.currentClass; }

    public void enter(String name, Type t) {
        int hashValue = hash(name);
        SymbolTableEntry next = table[hashValue];
        SymbolTableEntry e = new SymbolTableEntry(name, t, hashValue, next, top, topIndex++);
        top = e;
        table[hashValue] = e;
    }

    private Type findInClassHierarchy(TypeClass cls, String name) {
        TypeClass curr = cls;
        while (curr != null) {
            if (curr.dataMembers != null) {
                Type found = curr.dataMembers.find(name);
                if (found != null) return found;
            }
            curr = curr.father;
        }
        return null;
    }

    public Type find(String name) {
        SymbolTableEntry cur = top;
        while (cur != null) {
            if (cur.name.equals("SCOPE-BOUNDARY")) break;
            if (cur.name.equals(name)) return cur.type;
            cur = cur.prevtop;
        }
        if (currentClass != null) {
            Type found = findInClassHierarchy(currentClass, name);
            if (found != null) return found;
        }
        SymbolTableEntry e;
        for (e = table[hash(name)]; e != null; e = e.next) {
            if (name.equals(e.name)) return e.type;
        }
        return null;
    }

    public void beginScope() {
        enter("SCOPE-BOUNDARY", new TypeForScopeBoundaries("NONE"));
    }

    public void endScope() {
        while (!top.name.equals("SCOPE-BOUNDARY")) {
            table[top.index] = top.next;
            topIndex--;
            top = top.prevtop;
        }
        table[top.index] = top.next;
        topIndex--;
        top = top.prevtop;
    }

    private static SymbolTable instance = null;
    protected SymbolTable() {}

    public static SymbolTable getInstance() {
        if (instance == null) {
            instance = new SymbolTable();
            instance.enter("int", TypeInt.getInstance());
            instance.enter("string", TypeString.getInstance());
            instance.enter("void", TypeVoid.getInstance());
            instance.enter("PrintInt", new TypeFunction(TypeVoid.getInstance(), "PrintInt",
                new TypeList(TypeInt.getInstance(), null)));
            instance.enter("PrintString", new TypeFunction(TypeVoid.getInstance(), "PrintString",
                new TypeList(TypeString.getInstance(), null)));
        }
        return instance;
    }

    public Type findInCurrentScope(String name) {
        SymbolTableEntry cur = top;
        while (cur != null) {
            if (cur.name.equals("SCOPE-BOUNDARY")) return null;
            if (cur.name.equals(name)) return cur.type;
            cur = cur.prevtop;
        }
        return null;
    }

    public static boolean isReservedKeyword(String name) {
        return name.equals("int") || name.equals("string") || name.equals("void");
    }

    public String enterVar(String name, Type t) {
        String irName = name + "@" + irVarIdCounter++;
        int hashValue = hash(name);
        SymbolTableEntry next = table[hashValue];
        SymbolTableEntry e = new SymbolTableEntry(name, t, hashValue, next, top, topIndex++);
        e.irName = irName;
        top = e;
        table[hashValue] = e;
        return irName;
    }

    public String getIrName(String name) {
        SymbolTableEntry cur = top;
        while (cur != null) {
            if (cur.name.equals(name) && cur.irName != null) return cur.irName;
            cur = cur.prevtop;
        }
        return null;
    }
}
