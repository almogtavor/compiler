package ir;

import java.util.*;
import temp.*;

public class IrCommandConstInt extends IrCommand {
    public Temp t;
    public int value;
    public IrCommandConstInt(Temp t, int value) {
        this.t = t;
        this.value = value;
    }
    @Override public List<Temp> getDefinedTemps() { return List.of(t); }
}
