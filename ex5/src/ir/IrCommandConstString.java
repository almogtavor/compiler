package ir;

import java.util.*;
import temp.*;

public class IrCommandConstString extends IrCommand {
    public Temp dst;
    public String label;
    public IrCommandConstString(Temp dst, String label) {
        this.dst = dst;
        this.label = label;
    }
    @Override public List<Temp> getDefinedTemps() { return List.of(dst); }
}
