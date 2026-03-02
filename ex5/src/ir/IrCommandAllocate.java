package ir;

public class IrCommandAllocate extends IrCommand {
    public String varName;
    public IrCommandAllocate(String varName) {
        this.varName = varName;
    }
}
