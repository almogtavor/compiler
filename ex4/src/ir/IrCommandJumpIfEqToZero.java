/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;
import temp.*;

public class IrCommandJumpIfEqToZero extends IrCommand
{
	public Temp t;
	public String labelName;
	
	public IrCommandJumpIfEqToZero(Temp t, String labelName)
	{
		this.t = t;
		this.labelName = labelName;
	}

	@Override
	public void transform() {
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
	}
}
