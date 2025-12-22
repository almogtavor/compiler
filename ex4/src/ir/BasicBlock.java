/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BasicBlock {

    public static ArrayList<BasicBlock> allBlocks = new ArrayList<BasicBlock>();
    public static BasicBlock currentBlock = null;
    public static HashMap<String, BasicBlock> labels = new HashMap<String, BasicBlock>();

    public ArrayList<BasicBlock> blockInputs;
    public ArrayList<BasicBlock> blockOutputs;
    public ArrayList<IrCommand> body;
    public BasicBlock nextBlock;

    protected BasicBlock() {
        this.blockInputs = new ArrayList<BasicBlock>();
        this.blockOutputs = new ArrayList<BasicBlock>();
        this.body = new ArrayList<IrCommand>();
        this.nextBlock = null;
    }

    public static void GenerateNewCurrentBasicBlock() {
        allBlocks.add(new BasicBlock());
        if (currentBlock != null) {
            currentBlock.nextBlock = allBlocks.get(allBlocks.size() - 1);
        }
        currentBlock = allBlocks.get(allBlocks.size() - 1);
    }

    public static void AddIrCommand(IrCommand cmd) {
        currentBlock.body.add(cmd);
    }

    public HashSet<String> getLiveOut() {
        if (body.isEmpty()) return new HashSet<>();
        return this.body.get(this.body.size() - 1).liveVarsOut;
    }

    public HashSet<String> getLiveIn() {
        if (body.isEmpty()) return new HashSet<>();
        return this.body.get(0).liveVarsIn;
    }
}
