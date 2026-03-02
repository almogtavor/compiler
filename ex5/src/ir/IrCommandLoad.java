package ir;

import java.util.*;
import temp.*;

public class IrCommandLoad extends IrCommand {
    public Temp dst;
    public String varName;
    public IrCommandLoad(Temp dst, String varName) {
        this.dst = dst;
        this.varName = varName;
    }
    @Override public List<Temp> getDefinedTemps() { return List.of(dst); }
}
