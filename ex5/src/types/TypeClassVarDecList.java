package types;

public class TypeClassVarDecList
{
	public TypeClassVarDec head;
	public TypeClassVarDecList tail;
	
	public TypeClassVarDecList(TypeClassVarDec head, TypeClassVarDecList tail)
	{
		this.head = head;
		this.tail = tail;
	}	
	
	public Type find(String name) {
        for (TypeClassVarDecList it = this; it != null; it = it.tail) {
            if (it.head.name.equals(name))
                return it.head.t;
        }
        return null;
    }
}
