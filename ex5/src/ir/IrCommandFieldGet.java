package ir;

import java.util.*;
import temp.*;

public class IrCommandFieldGet extends IrCommand {
    public Temp dst;
    public Temp obj;
    public int offset;
    public IrCommandFieldGet(Temp dst, Temp obj, int offset) {
        this.dst = dst;
        this.obj = obj;
        this.offset = offset;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(obj); }
    @Override public List<Temp> getDefinedTemps() { return List.of(dst); }
}
