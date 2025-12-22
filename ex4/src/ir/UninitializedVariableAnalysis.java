/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;
import java.util.ArrayList;

public class UninitializedVariableAnalysis {
    
    public static void analyze(IrCommandList list, BasicBlock first) {
        HashSet<String> allVars = collectAllVariables(list);
        
        for (BasicBlock block : BasicBlock.allBlocks) {
            for (IrCommand cmd : block.body) {
                cmd.liveVarsIn = new HashSet<>(allVars);
                cmd.liveVarsOut = new HashSet<>(allVars);
            }
        }
        
        for (BasicBlock block : BasicBlock.allBlocks) {
            if (block.blockInputs.isEmpty() && !block.body.isEmpty()) {
                block.body.get(0).liveVarsIn = new HashSet<>();
            }
        }
        
        boolean globalChanged = true;
        int maxIterations = 1000;
        int iteration = 0;
        
        while (globalChanged && iteration < maxIterations) {
            globalChanged = false;
            iteration++;
            
            for (BasicBlock block : BasicBlock.allBlocks) {
                if (block.body.isEmpty()) continue;
                
                for (int i = 0; i < block.body.size(); i++) {
                    IrCommand command = block.body.get(i);
                    HashSet<String> oldLiveOut = new HashSet<>(command.liveVarsOut);
                    
                    HashSet<String> newLiveIn;
                    
                    if (i == 0) {
                        if (block.blockInputs.isEmpty()) {
                            newLiveIn = new HashSet<>();
                        } else {
                            newLiveIn = new HashSet<>(allVars);
                            for (BasicBlock pred : block.blockInputs) {
                                if (!pred.body.isEmpty()) {
                                    HashSet<String> predOut = pred.getLiveOut();
                                    newLiveIn.retainAll(predOut);
                                }
                            }
                        }
                    } else {
                        newLiveIn = new HashSet<>(block.body.get(i - 1).liveVarsOut);
                    }
                    
                    command.liveVarsIn = newLiveIn;
                    command.transform();
                    
                    if (!oldLiveOut.equals(command.liveVarsOut)) {
                        globalChanged = true;
                    }
                }
            }
        }
    }
    
    private static HashSet<String> collectAllVariables(IrCommandList list) {
        HashSet<String> vars = new HashSet<>();
        IrCommandList it = list;
        while (it != null) {
            if (it.head instanceof IrCommandAllocate) {
                vars.add(((IrCommandAllocate) it.head).varName);
            }
            if (it.head instanceof IrCommandStore) {
                vars.add(((IrCommandStore) it.head).varName);
            }
            if (it.head instanceof IrCommandLoad) {
                IrCommandLoad load = (IrCommandLoad) it.head;
                if (load.varName != null) {
                    vars.add(load.varName);
                }
                vars.add(String.valueOf(load.dst.getSerialNumber()));
            }
            if (it.head instanceof IrCommandConstInt) {
                vars.add(String.valueOf(((IrCommandConstInt) it.head).t.getSerialNumber()));
            }
            if (it.head instanceof IrCommandBinopAddIntegers) {
                vars.add(String.valueOf(((IrCommandBinopAddIntegers) it.head).dst.getSerialNumber()));
            }
            if (it.head instanceof IrCommandBinopSubIntegers) {
                vars.add(String.valueOf(((IrCommandBinopSubIntegers) it.head).dst.getSerialNumber()));
            }
            if (it.head instanceof IrCommandBinopMulIntegers) {
                vars.add(String.valueOf(((IrCommandBinopMulIntegers) it.head).dst.getSerialNumber()));
            }
            if (it.head instanceof IrCommandBinopDivIntegers) {
                vars.add(String.valueOf(((IrCommandBinopDivIntegers) it.head).dst.getSerialNumber()));
            }
            if (it.head instanceof IrCommandBinopLtIntegers) {
                vars.add(String.valueOf(((IrCommandBinopLtIntegers) it.head).dst.getSerialNumber()));
            }
            if (it.head instanceof IrCommandBinopGtIntegers) {
                vars.add(String.valueOf(((IrCommandBinopGtIntegers) it.head).dst.getSerialNumber()));
            }
            if (it.head instanceof IrCommandBinopEqIntegers) {
                vars.add(String.valueOf(((IrCommandBinopEqIntegers) it.head).dst.getSerialNumber()));
            }
            it = it.tail;
        }
        return vars;
    }

    public static HashSet<String> findUninitializedAccesses(IrCommandList list) {
        HashSet<String> uninitializedVars = new HashSet<String>();
        IrCommandList it = list;
        while (it != null) {
            if (it.head instanceof IrCommandLoad) {
                IrCommandLoad load = (IrCommandLoad) it.head;
                if (load.varName != null && !load.liveVarsIn.contains(load.varName)) {
                    String[] name_id = load.varName.split("@");
                    String baseVarName = name_id[0];
                    uninitializedVars.add(baseVarName);
                }
            }
            it = it.tail;
        }
        return uninitializedVars;
    }
}
