package codegen;

import java.util.*;

public class CodeGenInfo {
    private static CodeGenInfo instance = null;

    private Set<String> globalVars = new LinkedHashSet<>();
    private Map<String, List<String>> functionParams = new LinkedHashMap<>();
    private Map<String, List<String>> functionLocals = new LinkedHashMap<>();
    private Map<String, String> stringLiterals = new LinkedHashMap<>();
    private int stringCounter = 0;
    private String currentFunction = null;
    private String currentClassName = null;
    private String currentThisParam = null;

    protected CodeGenInfo() {}

    public static CodeGenInfo getInstance() {
        if (instance == null) instance = new CodeGenInfo();
        return instance;
    }

    public void registerGlobalVar(String irName) {
        globalVars.add(irName);
    }

    public boolean isGlobalVar(String irName) {
        return globalVars.contains(irName);
    }

    public Set<String> getGlobalVars() { return globalVars; }

    public void setCurrentFunction(String funcLabel) {
        this.currentFunction = funcLabel;
        if (funcLabel != null && !functionParams.containsKey(funcLabel)) {
            functionParams.put(funcLabel, new ArrayList<>());
            functionLocals.put(funcLabel, new ArrayList<>());
        }
    }

    public String getCurrentFunction() { return currentFunction; }

    public void addParam(String irName) {
        if (currentFunction != null)
            functionParams.get(currentFunction).add(irName);
    }

    public void addLocal(String irName) {
        if (currentFunction != null)
            functionLocals.get(currentFunction).add(irName);
        else
            registerGlobalVar(irName);
    }

    public List<String> getParams(String funcLabel) {
        return functionParams.getOrDefault(funcLabel, new ArrayList<>());
    }

    public List<String> getLocals(String funcLabel) {
        return functionLocals.getOrDefault(funcLabel, new ArrayList<>());
    }

    public String addStringLiteral(String value) {
        for (Map.Entry<String, String> e : stringLiterals.entrySet()) {
            if (e.getValue().equals(value)) return e.getKey();
        }
        String label = "str_const_" + stringCounter++;
        stringLiterals.put(label, value);
        return label;
    }

    public Map<String, String> getStringLiterals() { return stringLiterals; }

    public void setCurrentClassName(String name) { this.currentClassName = name; }
    public String getCurrentClassName() { return currentClassName; }

    public void setCurrentThisParam(String irName) { this.currentThisParam = irName; }
    public String getCurrentThisParam() { return currentThisParam; }

    public Map<String, List<String>> getAllFunctionParams() { return functionParams; }
    public Map<String, List<String>> getAllFunctionLocals() { return functionLocals; }
}
