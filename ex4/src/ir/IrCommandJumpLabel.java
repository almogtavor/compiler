/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;

public class IrCommandJumpLabel extends IrCommand
{
	public String labelName;
	
	public IrCommandJumpLabel(String labelName)
	{
		this.labelName = labelName;
	}

	@Override
	public void transform() {
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
	}
}
