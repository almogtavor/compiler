package types;

public class TypeArray extends Type {
    public Type elementType;

    public TypeArray(String name, Type elementType) {
        this.name = name;
        this.elementType = elementType;
    }

    @Override
    public boolean isArray() {
        return true;
    }
    
    @Override
    public boolean isAssignableFrom(Type other) {
        if (other == TypeNil.getInstance())
            return true;
        
        if (!(other instanceof TypeArray))
            return false;
        
        TypeArray o = (TypeArray) other;
        
        if (this.name.equals(o.name))
        return true;
    
        if (o.name.equals(this.elementType.name))
            return true;
        
        return false;
    }

}
