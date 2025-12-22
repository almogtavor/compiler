/***********/
/* PACKAGE */
/***********/
package ir;

public class Ir
{
	private IrCommandList list=null;

	public void AddIrCommand(IrCommand cmd)
	{
		if (this.list == null)
		{
			this.list = new IrCommandList(cmd, null);
		}
		else
		{
			IrCommandList it = this.list;
			while ((it != null) && (it.tail != null))
			{
				it = it.tail;
			}
			it.tail = new IrCommandList(cmd,null);
		}
	}
	
	public IrCommandList getList()
	{
		return list;
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static Ir instance = null;

	protected Ir() {}

	public static Ir getInstance()
	{
		if (instance == null)
		{
			instance = new Ir();
		}
		return instance;
	}
}
