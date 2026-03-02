package ir;

import java.util.*;
import temp.*;

public abstract class IrCommand {
    protected static int labelCounter = 0;
    public boolean isLeader = false;

    public static String getFreshLabel(String msg) {
        return String.format("Label_%d_%s", labelCounter++, msg);
    }

    public List<Temp> getUsedTemps() { return Collections.emptyList(); }
    public List<Temp> getDefinedTemps() { return Collections.emptyList(); }
}
