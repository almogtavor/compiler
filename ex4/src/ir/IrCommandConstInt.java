/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;
import temp.*;

public class IrCommandConstInt extends IrCommand
{
	public Temp t;
	public int value;
	
	public IrCommandConstInt(Temp t, int value)
	{
		this.t = t;
		this.value = value;
	}

	@Override
	public void transform(){
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
		this.liveVarsOut.add(String.valueOf(this.t.getSerialNumber()));
	}
}
