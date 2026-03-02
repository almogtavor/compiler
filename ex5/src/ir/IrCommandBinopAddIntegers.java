package ir;

import java.util.*;
import temp.*;

public class IrCommandBinopAddIntegers extends IrCommand {
    public Temp t1;
    public Temp t2;
    public Temp dst;
    public IrCommandBinopAddIntegers(Temp dst, Temp t1, Temp t2) {
        this.dst = dst;
        this.t1 = t1;
        this.t2 = t2;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(t1, t2); }
    @Override public List<Temp> getDefinedTemps() { return List.of(dst); }
}
