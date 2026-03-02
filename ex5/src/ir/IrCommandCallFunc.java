package ir;

import java.util.*;
import temp.*;

public class IrCommandCallFunc extends IrCommand {
    public Temp dst;
    public String funcLabel;
    public List<Temp> args;
    public IrCommandCallFunc(Temp dst, String funcLabel, List<Temp> args) {
        this.dst = dst;
        this.funcLabel = funcLabel;
        this.args = args != null ? args : new ArrayList<>();
    }
    @Override public List<Temp> getUsedTemps() { return new ArrayList<>(args); }
    @Override public List<Temp> getDefinedTemps() {
        return dst != null ? List.of(dst) : Collections.emptyList();
    }
}
