package ir;

import java.util.*;
import temp.*;

public class IrCommandStringConcat extends IrCommand {
    public Temp dst;
    public Temp s1;
    public Temp s2;
    public IrCommandStringConcat(Temp dst, Temp s1, Temp s2) {
        this.dst = dst;
        this.s1 = s1;
        this.s2 = s2;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(s1, s2); }
    @Override public List<Temp> getDefinedTemps() { return List.of(dst); }
}
