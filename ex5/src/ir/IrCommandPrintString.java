package ir;

import java.util.*;
import temp.*;

public class IrCommandPrintString extends IrCommand {
    public Temp t;
    public IrCommandPrintString(Temp t) { this.t = t; }
    @Override public List<Temp> getUsedTemps() { return List.of(t); }
}
