package codegen;

import java.util.*;
import types.*;

public class ClassLayoutManager {
    private static ClassLayoutManager instance = null;

    public static class ClassInfo {
        public String className;
        public ClassInfo parent;
        public List<String> allFields = new ArrayList<>();
        public Map<String, Integer> fieldOffsets = new LinkedHashMap<>();
        public List<String[]> vtable = new ArrayList<>();
        public Map<String, Integer> methodIndices = new LinkedHashMap<>();
        public int objectSize;
    }

    private Map<String, ClassInfo> classInfos = new LinkedHashMap<>();
    private Map<String, Map<String, Object[]>> fieldDefaults = new LinkedHashMap<>();

    protected ClassLayoutManager() {}

    public static ClassLayoutManager getInstance() {
        if (instance == null) instance = new ClassLayoutManager();
        return instance;
    }

    public void addClass(String name, TypeClass typeClass) {
        if (classInfos.containsKey(name)) return;

        ClassInfo info = new ClassInfo();
        info.className = name;

        if (typeClass.father != null) {
            String parentName = typeClass.father.name;
            if (!classInfos.containsKey(parentName)) {
                addClass(parentName, typeClass.father);
            }
            info.parent = classInfos.get(parentName);
            info.allFields.addAll(info.parent.allFields);
            for (String[] entry : info.parent.vtable) {
                info.vtable.add(new String[]{entry[0], entry[1]});
            }
            info.methodIndices.putAll(info.parent.methodIndices);
        }

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
                        int idx = info.methodIndices.get(m.name);
                        info.vtable.set(idx, new String[]{m.name, funcLabel});
                    } else {
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
