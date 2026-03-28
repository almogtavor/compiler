package ast;

import Exceptions.SemanticException;
import symboltable.SymbolTable;
import types.*;
import temp.*;
import ir.*;
import codegen.*;
import java.util.*;

// AST node for function/method call expressions: `foo(x)` or `obj.foo(x)`.
// Handles three call forms:
//   1. Global function call: `foo(x)` where foo is a top-level function
//   2. Implicit this call: `foo(x)` inside a class, where foo is a method of the current class
//   3. Explicit receiver call: `obj.foo(x)` - virtual method dispatch
public class AstCallExp extends AstExp {
    public AstVar object;          // receiver object (null for global/implicit this calls)
    public String name;            // function/method name
    public AstExpList args;
    private boolean isMethodCall = false;    // true if resolved as a class method (cases 2 & 3)
    private String resolvedClassName = null; // class to look up vtable from

    public AstCallExp(AstVar object, String name, AstExpList args, int lineNumber) {
        super(lineNumber);
        this.object = object;
        this.name = name;
        this.args = args;
    }

    @Override
    public void printMe() {
        if (args != null) args.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, String.format("CALL(%s)", name));
        if (args != null) AstGraphviz.getInstance().logEdge(serialNumber, args.serialNumber);
    }

    // Resolve the call target and type-check arguments.
    // No receiver (object==null): look up name in scope. If inside a class and
    //   name isn't in the local scope, search the class hierarchy - if found,
    //   mark as implicit `this` method call.
    // With receiver: resolve receiver type, search its class hierarchy for method.
    @Override
    public Type SemantMe() {
        TypeFunction func;
        TypeList expectedParams;
        TypeList givenParams = (args != null ? args.SemantMe() : null);

        if (object == null) {
            // No explicit receiver - could be global function or implicit this.method()
            Type t = SymbolTable.getInstance().find(name);
            if (!(t instanceof TypeFunction))
                throw new SemanticException(lineNumber);
            func = (TypeFunction) t;
            expectedParams = func.params;

            // Check if this is actually a method of the enclosing class (implicit this)
            TypeClass currentClass = SymbolTable.getInstance().getCurrentClass();
            if (currentClass != null) {
                Type directLookup = SymbolTable.getInstance().findInCurrentScope(name);
                if (directLookup == null) {
                    // Not a local var/param - walk the class hierarchy
                    TypeClass curr = currentClass;
                    while (curr != null) {
                        Type found = (curr.dataMembers != null ? curr.dataMembers.find(name) : null);
                        if (found instanceof TypeFunction) {
                            isMethodCall = true;
                            resolvedClassName = currentClass.name;
                            break;
                        }
                        curr = curr.father;
                    }
                }
            }
        } else {
            Type base = object.SemantMe();
            if (!(base instanceof TypeClass))
                throw new SemanticException(lineNumber);

            TypeClass cls = (TypeClass) base;
            resolvedClassName = cls.name;
            func = null;
            TypeClass curr = cls;
            while (curr != null) {
                Type found = (curr.dataMembers != null ? curr.dataMembers.find(name) : null);
                if (found instanceof TypeFunction) {
                    func = (TypeFunction) found;
                    break;
                }
                curr = curr.father;
            }
            if (func == null)
                throw new SemanticException(lineNumber);
            expectedParams = func.params;
            isMethodCall = true;
        }

        TypeList e = expectedParams;
        TypeList g = givenParams;
        while (e != null && g != null) {
            if (!e.head.isAssignableFrom(g.head))
                throw new SemanticException(lineNumber);
            e = e.tail;
            g = g.tail;
        }
        if (e != null || g != null)
            throw new SemanticException(lineNumber);

        return func.returnType;
    }

    // IR generation for calls:
    // - PrintInt/PrintString: special-cased as direct MIPS syscalls
    // - Explicit receiver (obj.method): null-check obj, then virtual call via vtable
    // - Implicit this (isMethodCall, no receiver): load this param, then virtual call
    // - Global function: direct call via label
    @Override
    public Temp IRme() {
        if (name.equals("PrintInt")) {
            Temp argTemp = null;
            if (args != null && args.head != null)
                argTemp = args.head.IRme();
            if (argTemp != null)
                Ir.getInstance().AddIrCommand(new IrCommandPrintInt(argTemp));
            Temp ret = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandConstInt(ret, 0));
            return ret;
        }

        if (name.equals("PrintString")) {
            Temp argTemp = null;
            if (args != null && args.head != null)
                argTemp = args.head.IRme();
            if (argTemp != null)
                Ir.getInstance().AddIrCommand(new IrCommandPrintString(argTemp));
            Temp ret = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandConstInt(ret, 0));
            return ret;
        }

        List<Temp> argTemps = new ArrayList<>();
        if (args != null) {
            AstExpList curr = args;
            while (curr != null) {
                if (curr.head != null) argTemps.add(curr.head.IRme());
                curr = curr.tail;
            }
        }

        Temp dst = TempFactory.getInstance().getFreshTemp();

        if (object != null) {
            Temp objTemp = object.IRme();
            Ir.getInstance().AddIrCommand(new IrCommandCheckNullPtr(objTemp));
            int methodIndex = ClassLayoutManager.getInstance().getMethodIndex(resolvedClassName, name);
            Ir.getInstance().AddIrCommand(new IrCommandVirtualCall(dst, objTemp, methodIndex, argTemps));
        } else if (isMethodCall) {
            String thisParam = CodeGenInfo.getInstance().getCurrentThisParam();
            Temp thisTemp = TempFactory.getInstance().getFreshTemp();
            Ir.getInstance().AddIrCommand(new IrCommandLoad(thisTemp, thisParam));
            int methodIndex = ClassLayoutManager.getInstance().getMethodIndex(resolvedClassName, name);
            Ir.getInstance().AddIrCommand(new IrCommandVirtualCall(dst, thisTemp, methodIndex, argTemps));
        } else {
            String funcLabel = "func_" + name;
            Ir.getInstance().AddIrCommand(new IrCommandCallFunc(dst, funcLabel, argTemps));
        }

        return dst;
    }
}
