/***********/
/* PACKAGE */
/***********/
package ir;

import java.util.HashSet;

public abstract class IrCommand
{
	protected static int labelCounter=0;
	public boolean isLeader = false;
	public HashSet<String> liveVarsIn = new HashSet<>();
	public HashSet<String> liveVarsOut = new HashSet<>();

	public static String getFreshLabel(String msg)
	{
		return String.format("Label_%d_%s",labelCounter++,msg);
	}

	public void transform(){
		this.liveVarsOut = new HashSet<>(this.liveVarsIn);
	}
}
