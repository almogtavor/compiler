package ir;

import java.util.*;
import temp.*;

public class IrCommandCheckBounds extends IrCommand {
    public Temp arr;
    public Temp idx;
    public IrCommandCheckBounds(Temp arr, Temp idx) {
        this.arr = arr;
        this.idx = idx;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(arr, idx); }
}
