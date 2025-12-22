/***********/
/* PACKAGE */
/***********/
package ir;

public class IrCommandList
{
	public IrCommand head;
	public IrCommandList tail;

	public IrCommandList(IrCommand head, IrCommandList tail)
	{
		this.head = head;
		this.tail = tail;
	}
}
