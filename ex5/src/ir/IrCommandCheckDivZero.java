package ir;

import java.util.*;
import temp.*;

public class IrCommandCheckDivZero extends IrCommand {
    public Temp divisor;
    public IrCommandCheckDivZero(Temp divisor) { this.divisor = divisor; }
    @Override public List<Temp> getUsedTemps() { return List.of(divisor); }
}
