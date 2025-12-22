/***********/
/* PACKAGE */
/***********/
package ir;

public class CFG {

    public static void markLeaders(IrCommandList list) {
        IrCommandList it = list;
        boolean markNextAsLeader = false;
        while (it != null) {
            if (markNextAsLeader) {
                it.head.isLeader = true;
                markNextAsLeader = false;
            }

            if (it.head instanceof IrCommandJumpIfEqToZero || it.head instanceof IrCommandJumpLabel) {
                markNextAsLeader = true;
            }

            it = it.tail;
        }
    }

    public static BasicBlock generateBasicBlockCFG(IrCommandList list) {
        IrCommandList it = list;
        BasicBlock.GenerateNewCurrentBasicBlock();
        BasicBlock first = BasicBlock.currentBlock;

        while (it != null) {
            if (it.head.isLeader && BasicBlock.currentBlock.body.size() > 0) {
                BasicBlock.GenerateNewCurrentBasicBlock();
            }

            if (it.head instanceof IrCommandLabel) {
                BasicBlock.labels.put(((IrCommandLabel) it.head).labelName, BasicBlock.currentBlock);
            }
            
            BasicBlock.AddIrCommand(it.head);
            it = it.tail;
        }

        return first;
    }

    public static BasicBlock connectBasicBlocks(BasicBlock first) {
        BasicBlock it = first;
        while (it != null) {
            if (it.body.size() > 0) {
                IrCommand last = it.body.get(it.body.size() - 1);
                if (last instanceof IrCommandJumpLabel) {
                    connectBasicBlockPair(it, BasicBlock.labels.get(((IrCommandJumpLabel) last).labelName));
                }
                else if (last instanceof IrCommandJumpIfEqToZero) {
                    connectBasicBlockPair(it, BasicBlock.labels.get(((IrCommandJumpIfEqToZero) last).labelName));
                    if (it.nextBlock != null && !isFunctionEntry(it.nextBlock)) {
                        connectBasicBlockPair(it, it.nextBlock);
                    }
                }
                else {
                    if (it.nextBlock != null) {
                        boolean currentIsFunction = isFunctionEntry(it);
                        boolean nextIsFunction = isFunctionEntry(it.nextBlock);
                        
                        if (!currentIsFunction || !nextIsFunction) {
                            connectBasicBlockPair(it, it.nextBlock);
                        }
                    }
                }
            }
            it = it.nextBlock;
        }

        return first;
    }

    private static boolean isFunctionEntry(BasicBlock block) {
        if (block.body.isEmpty()) return false;
        IrCommand first = block.body.get(0);
        if (first instanceof IrCommandLabel) {
            String labelName = ((IrCommandLabel) first).labelName;
            return !labelName.startsWith("Label_");
        }
        return false;
    }

    public static void connectBasicBlockPair(BasicBlock from, BasicBlock to) {
        from.blockOutputs.add(to);
        to.blockInputs.add(from);
    }

    public static BasicBlock generateCFG(IrCommandList list) {
        markLeaders(list);
        BasicBlock first = generateBasicBlockCFG(list);
        connectBasicBlocks(first);

        return first;
    }
}
