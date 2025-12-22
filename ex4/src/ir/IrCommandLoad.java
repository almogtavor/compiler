/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;
import temp.*;

public class IrCommandLoad extends IrCommand
{
	public Temp dst;
	public String varName;
	
	public IrCommandLoad(Temp dst, String varName)
	{
		this.dst = dst;
		this.varName = varName;
	}

	@Override
	public void transform(){
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
		if (this.varName != null && this.liveVarsIn.contains(this.varName)){
			this.liveVarsOut.add(String.valueOf(this.dst.getSerialNumber()));
		}
	}
}
