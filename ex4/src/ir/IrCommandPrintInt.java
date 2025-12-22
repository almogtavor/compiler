/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;
import temp.*;

public class IrCommandPrintInt extends IrCommand
{
	public Temp t;
	
	public IrCommandPrintInt(Temp t)
	{
		this.t = t;
	}

	@Override
	public void transform() {
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
	}
}
