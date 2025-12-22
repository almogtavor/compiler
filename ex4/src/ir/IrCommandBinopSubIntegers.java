/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;
import temp.*;

public class IrCommandBinopSubIntegers extends IrCommand
{
	public Temp t1;
	public Temp t2;
	public Temp dst;
	
	public IrCommandBinopSubIntegers(Temp dst, Temp t1, Temp t2)
	{
		this.dst = dst;
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public void transform() {
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
		String t1Serial = String.valueOf(this.t1.getSerialNumber());
		String t2Serial = String.valueOf(this.t2.getSerialNumber());
		if (this.liveVarsIn.contains(t1Serial) && this.liveVarsIn.contains(t2Serial)) {
			this.liveVarsOut.add(String.valueOf(this.dst.getSerialNumber()));
		}
	}
}
