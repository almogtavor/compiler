package types;

public class TypeClass extends Type {

    public TypeClass father;
    public TypeClassVarDecList dataMembers;

    public TypeClass(String name, TypeClass father, TypeClassVarDecList dataMembers) {
        this.name = name;
        this.father = father;
        this.dataMembers = dataMembers;
    }

    @Override
    public boolean isClass() {
        return true;
    }

    public Type findMember(String name) {
        for (TypeClassVarDecList it = dataMembers; it != null; it = it.tail) {
            if (it.head.name.equals(name))
                return it.head.t;
        }
        if (father != null)
            return father.findMember(name);
        return null;
    }

    @Override
    public boolean isAssignableFrom(Type other) {
        if (other == TypeNil.getInstance()) return true;

        if (!(other instanceof TypeClass)) return false;

        TypeClass c = (TypeClass) other;

        while (c != null) {
            if (c.name.equals(this.name)) return true;
            c = c.father;
        }
        return false;
    }
}
