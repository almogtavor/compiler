package ir;

import java.util.*;
import temp.*;

public class IrCommandCheckNullPtr extends IrCommand {
    public Temp ptr;
    public IrCommandCheckNullPtr(Temp ptr) { this.ptr = ptr; }
    @Override public List<Temp> getUsedTemps() { return List.of(ptr); }
}
