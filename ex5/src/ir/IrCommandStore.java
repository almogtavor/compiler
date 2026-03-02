package ir;

import java.util.*;
import temp.*;

public class IrCommandStore extends IrCommand {
    public String varName;
    public Temp src;
    public IrCommandStore(String varName, Temp src) {
        this.src = src;
        this.varName = varName;
    }
    @Override public List<Temp> getUsedTemps() { return List.of(src); }
}
