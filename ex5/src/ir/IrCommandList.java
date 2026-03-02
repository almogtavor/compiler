package ir;

public class IrCommandList {
    public IrCommand command;
    public IrCommandList tail;
    public IrCommandList(IrCommand command, IrCommandList tail) {
        this.command = command;
        this.tail = tail;
    }
}
