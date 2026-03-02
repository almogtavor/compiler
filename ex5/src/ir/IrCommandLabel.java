package ir;

import java.util.*;
import temp.*;

public class IrCommandLabel extends IrCommand {
    public String labelName;
    public boolean isFunctionEntry = false;

    public IrCommandLabel(String labelName) {
        this.labelName = labelName;
        this.isLeader = true;
    }
}
