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
        
        // arrays compatible if element types match
        return this.elementType == o.elementType;
    }

}
