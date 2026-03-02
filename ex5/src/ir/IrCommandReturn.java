package ir;

import java.util.*;
import temp.*;

public class IrCommandReturn extends IrCommand {
    public Temp val;
    public IrCommandReturn(Temp val) { this.val = val; }
    @Override public List<Temp> getUsedTemps() {
        return val != null ? List.of(val) : Collections.emptyList();
    }
}
