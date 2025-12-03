package types;

public abstract class Type
{
	/******************************/
	/*  Every type has a name ... */
	/******************************/
	public String name;

	/*************/
	/* isClass() */
	/*************/
	public boolean isClass(){ return false;}

	/*************/
	/* isArray() */
	/*************/
	public boolean isArray(){ return false;}

	public boolean isCompatible(Type other) {
        return this == other;
    }
	public boolean isAssignableFrom(Type other) {
    return this == other;
}

}
