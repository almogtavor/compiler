/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;

public class IrCommandLabel extends IrCommand
{
	public String labelName;
	
	public IrCommandLabel(String labelName)
	{
		this.labelName = labelName;
		this.isLeader = true;
	}

	@Override
	public void transform() {
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
	}
}
