package ir;

import java.util.*;
import temp.*;

public class IrCommandJumpIfEqToZero extends IrCommand {
    public Temp t;
    public String labelName;
    public IrCommandJumpIfEqToZero(Temp t, String labelName) {
        this.t = t;
        this.labelName = labelName;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(t); }
}
