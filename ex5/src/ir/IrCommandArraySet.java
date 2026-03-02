package ir;

import java.util.*;
import temp.*;

public class IrCommandArraySet extends IrCommand {
    public Temp arr;
    public Temp idx;
    public Temp src;
    public IrCommandArraySet(Temp arr, Temp idx, Temp src) {
        this.arr = arr;
        this.idx = idx;
        this.src = src;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(arr, idx, src); }
}
