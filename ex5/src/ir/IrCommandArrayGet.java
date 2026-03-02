package ir;

import java.util.*;
import temp.*;

public class IrCommandArrayGet extends IrCommand {
    public Temp dst;
    public Temp arr;
    public Temp idx;
    public IrCommandArrayGet(Temp dst, Temp arr, Temp idx) {
        this.dst = dst;
        this.arr = arr;
        this.idx = idx;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(arr, idx); }
    @Override public List<Temp> getDefinedTemps() { return List.of(dst); }
}
