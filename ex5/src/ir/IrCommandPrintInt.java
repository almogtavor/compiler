package ir;

import java.util.*;
import temp.*;

public class IrCommandPrintInt extends IrCommand {
    public Temp t;
    public IrCommandPrintInt(Temp t) { this.t = t; }
    @Override public List<Temp> getUsedTemps() { return List.of(t); }
}
