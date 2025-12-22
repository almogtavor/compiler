/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;

public class IrCommandAllocate extends IrCommand
{
	public String varName;
	
	public IrCommandAllocate(String varName)
	{
		this.varName = varName;
	}

	@Override
	public void transform() {
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
	}
}
