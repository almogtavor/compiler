package ir;

public class IrCommandJumpLabel extends IrCommand {
    public String labelName;
    public IrCommandJumpLabel(String labelName) {
        this.labelName = labelName;
    }
}
