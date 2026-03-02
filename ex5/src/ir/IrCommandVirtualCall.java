package ir;

import java.util.*;
import temp.*;

public class IrCommandVirtualCall extends IrCommand {
    public Temp dst;
    public Temp obj;
    public int vtableOffset;
    public List<Temp> args;
    public IrCommandVirtualCall(Temp dst, Temp obj, int vtableOffset, List<Temp> args) {
        this.dst = dst;
        this.obj = obj;
        this.vtableOffset = vtableOffset;
        this.args = args != null ? args : new ArrayList<>();
    }
    @Override public List<Temp> getUsedTemps() {
        List<Temp> used = new ArrayList<>();
        used.add(obj);
        used.addAll(args);
        return used;
    }
    @Override public List<Temp> getDefinedTemps() {
        return dst != null ? List.of(dst) : Collections.emptyList();
    }
}
