package ir;

import java.util.*;
import temp.*;

public class IrCommandFieldSet extends IrCommand {
    public Temp obj;
    public int offset;
    public Temp src;
    public IrCommandFieldSet(Temp obj, int offset, Temp src) {
        this.obj = obj;
        this.offset = offset;
        this.src = src;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(obj, src); }
}
