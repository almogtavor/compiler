package ir;

import java.util.*;
import temp.*;

public class IrCommandMalloc extends IrCommand {
    public Temp dst;
    public Temp size;
    public IrCommandMalloc(Temp dst, Temp size) {
        this.dst = dst;
        this.size = size;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(size); }
    @Override public List<Temp> getDefinedTemps() { return List.of(dst); }
}
