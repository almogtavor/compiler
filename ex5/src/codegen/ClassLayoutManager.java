package codegen;

import java.util.*;
import types.*;

// Manages the memory layout of class objects and their virtual method tables.
//
// Object memory layout: [vtable_ptr | field0 | field1 | ...]
//   - Word 0: pointer to the class's vtable (array of function pointers)
//   - Words 1..N: data fields (inherited fields first, then own fields)
//
// Vtable: array of function labels, indexed by method index.
//   - Inherited methods keep their parent's index; overrides replace the slot.
public class ClassLayoutManager {
    private static ClassLayoutManager instance = null;

    public static class ClassInfo {
        public String className;
        public ClassInfo parent;
        public List<String> allFields = new ArrayList<>();       // all fields including inherited
        public Map<String, Integer> fieldOffsets = new LinkedHashMap<>();  // field name -> word offset (1-based)
        public List<String[]> vtable = new ArrayList<>();        // [methodName, funcLabel] per slot
        public Map<String, Integer> methodIndices = new LinkedHashMap<>(); // method name -> vtable index
        public int objectSize;  // total bytes = (1 + numFields) * 4
    }

    private Map<String, ClassInfo> classInfos = new LinkedHashMap<>();
    private Map<String, Map<String, Object[]>> fieldDefaults = new LinkedHashMap<>(); // class -> field -> [type, value]

    protected ClassLayoutManager() {}

    public static ClassLayoutManager getInstance() {
        if (instance == null) instance = new ClassLayoutManager();
        return instance;
    }

    // Register a class: inherit parent's fields and vtable, then add own members.
    // Methods override existing vtable slots if the name matches (polymorphism),
    // otherwise get appended as a new slot.
    public void addClass(String name, TypeClass typeClass) {
        if (classInfos.containsKey(name)) return;

        ClassInfo info = new ClassInfo();
        info.className = name;

        // Inherit from parent: copy fields list and vtable (deep copy vtable entries)
        if (typeClass.father != null) {
            String parentName = typeClass.father.name;
            if (!classInfos.containsKey(parentName)) {
                addClass(parentName, typeClass.father); // ensure parent is registered first
            }
            info.parent = classInfos.get(parentName);
            info.allFields.addAll(info.parent.allFields);
            for (String[] entry : info.parent.vtable) {
                info.vtable.add(new String[]{entry[0], entry[1]});
            }
            info.methodIndices.putAll(info.parent.methodIndices);
        }

        // Process own members (reversed because buildMemberList builds in reverse order)
        if (typeClass.dataMembers != null) {
            List<TypeClassVarDec> members = new ArrayList<>();
            for (TypeClassVarDecList it = typeClass.dataMembers; it != null; it = it.tail) {
                members.add(it.head);
            }
            Collections.reverse(members);

            for (TypeClassVarDec m : members) {
                if (m.t instanceof TypeFunction) {
                    String funcLabel = "func_" + name + "_" + m.name;
                    if (info.methodIndices.containsKey(m.name)) {
                        // Override: replace parent's vtable slot with child's implementation
                        int idx = info.methodIndices.get(m.name);
                        info.vtable.set(idx, new String[]{m.name, funcLabel});
                    } else {
                        // New method: append to vtable
                        int idx = info.vtable.size();
                        info.vtable.add(new String[]{m.name, funcLabel});
                        info.methodIndices.put(m.name, idx);
                    }
                } else {
                    if (!info.allFields.contains(m.name)) {
                        info.allFields.add(m.name);
                    }
                }
            }
        }

        // Field offsets start at 1 (word 0 is vtable pointer)
        int offset = 1;
        for (String field : info.allFields) {
            info.fieldOffsets.put(field, offset++);
        }
        info.objectSize = (1 + info.allFields.size()) * 4;

        classInfos.put(name, info);
    }

    public ClassInfo getClassInfo(String className) {
        return classInfos.get(className);
    }

    public int getFieldOffset(String className, String fieldName) {
        ClassInfo info = classInfos.get(className);
        if (info == null) return -1;
        Integer offset = info.fieldOffsets.get(fieldName);
        return offset != null ? offset : -1;
    }

    public int getMethodIndex(String className, String methodName) {
        ClassInfo info = classInfos.get(className);
        if (info == null) return -1;
        Integer idx = info.methodIndices.get(methodName);
        return idx != null ? idx : -1;
    }

    public String getMethodLabel(String className, String methodName) {
        ClassInfo info = classInfos.get(className);
        if (info == null) return null;
        Integer idx = info.methodIndices.get(methodName);
        if (idx == null) return null;
        return info.vtable.get(idx)[1];
    }

    public Map<String, ClassInfo> getAllClassInfos() { return classInfos; }

    public void setFieldDefault(String className, String fieldName, Object[] defaultVal) {
        fieldDefaults.computeIfAbsent(className, k -> new LinkedHashMap<>()).put(fieldName, defaultVal);
    }

    // Collect field defaults for object construction, merging parent defaults first.
    // Child defaults override parent defaults for the same field name.
    public Map<String, Object[]> getFieldDefaults(String className) {
        Map<String, Object[]> result = new LinkedHashMap<>();
        ClassInfo info = classInfos.get(className);
        if (info != null && info.parent != null) {
            Map<String, Object[]> parentDefaults = getFieldDefaults(info.parent.className);
            result.putAll(parentDefaults);
        }
        Map<String, Object[]> own = fieldDefaults.getOrDefault(className, Collections.emptyMap());
        result.putAll(own);
        return result;
    }
}
