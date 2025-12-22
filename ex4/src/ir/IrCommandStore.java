/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;
import temp.*;

public class IrCommandStore extends IrCommand
{
	public String varName;
	public Temp src;
	
	public IrCommandStore(String varName, Temp src)
	{
		this.src = src;
		this.varName = varName;
	}
	
	@Override
	public void transform() {
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
		if (this.liveVarsIn.contains(String.valueOf(this.src.getSerialNumber()))){
			this.liveVarsOut.add(this.varName);
		}
	}
}
