package regalloc;

import ir.*;
import temp.*;
import java.util.*;

public class RegisterAllocator {
    private static final int NUM_REGISTERS = 10;
    private Map<Integer, Integer> tempToRegister = new HashMap<>();

    public Map<Integer, Integer> allocate(IrCommandList irList) {
        List<List<IrCommand>> functions = splitIntoFunctions(irList);

        for (List<IrCommand> func : functions) {
            Set<Integer> allTemps = collectTemps(func);
            if (allTemps.isEmpty()) continue;

            Map<Integer, Set<Integer>> liveOut = livenessAnalysis(func);
            Map<Integer, Set<Integer>> interferenceGraph = buildInterferenceGraph(func, liveOut, allTemps);
            Map<Integer, Integer> coloring = colorGraph(interferenceGraph, allTemps);
            tempToRegister.putAll(coloring);
        }
        return tempToRegister;
    }

    private List<List<IrCommand>> splitIntoFunctions(IrCommandList irList) {
        List<List<IrCommand>> functions = new ArrayList<>();
        List<IrCommand> current = new ArrayList<>();

        for (IrCommandList it = irList; it != null; it = it.tail) {
            IrCommand cmd = it.command;
            if (cmd instanceof IrCommandLabel && ((IrCommandLabel) cmd).isFunctionEntry) {
                if (!current.isEmpty()) functions.add(current);
                current = new ArrayList<>();
            }
            current.add(cmd);
        }
        if (!current.isEmpty()) functions.add(current);
        return functions;
    }

    private Set<Integer> collectTemps(List<IrCommand> func) {
        Set<Integer> temps = new HashSet<>();
        for (IrCommand cmd : func) {
            for (Temp t : cmd.getUsedTemps()) temps.add(t.getSerialNumber());
            for (Temp t : cmd.getDefinedTemps()) temps.add(t.getSerialNumber());
        }
        return temps;
    }

    private Map<Integer, Set<Integer>> livenessAnalysis(List<IrCommand> func) {
        int n = func.size();
        Map<String, List<Integer>> labelToIndices = new HashMap<>();
        for (int i = 0; i < n; i++) {
            IrCommand cmd = func.get(i);
            if (cmd instanceof IrCommandLabel) {
                labelToIndices.computeIfAbsent(((IrCommandLabel) cmd).labelName, k -> new ArrayList<>()).add(i);
            }
        }

        Set<Integer>[] liveIn = new HashSet[n];
        Set<Integer>[] liveOutArr = new HashSet[n];
        for (int i = 0; i < n; i++) {
            liveIn[i] = new HashSet<>();
            liveOutArr[i] = new HashSet<>();
        }

        boolean changed = true;
        int iterations = 0;
        while (changed && iterations < 1000) {
            changed = false;
            iterations++;
            for (int i = n - 1; i >= 0; i--) {
                IrCommand cmd = func.get(i);
                Set<Integer> oldIn = new HashSet<>(liveIn[i]);
                Set<Integer> oldOut = new HashSet<>(liveOutArr[i]);

                Set<Integer> newOut = new HashSet<>();
                List<Integer> successors = getSuccessors(i, func, labelToIndices);
                for (int s : successors) {
                    newOut.addAll(liveIn[s]);
                }
                liveOutArr[i] = newOut;

                Set<Integer> use = new HashSet<>();
                for (Temp t : cmd.getUsedTemps()) use.add(t.getSerialNumber());
                Set<Integer> def = new HashSet<>();
                for (Temp t : cmd.getDefinedTemps()) def.add(t.getSerialNumber());

                Set<Integer> newIn = new HashSet<>(newOut);
                newIn.removeAll(def);
                newIn.addAll(use);
                liveIn[i] = newIn;

                if (!oldIn.equals(newIn) || !oldOut.equals(newOut)) changed = true;
            }
        }

        Map<Integer, Set<Integer>> liveOutMap = new HashMap<>();
        for (int i = 0; i < n; i++) {
            liveOutMap.put(i, liveOutArr[i]);
        }
        return liveOutMap;
    }

    private List<Integer> getSuccessors(int i, List<IrCommand> func, Map<String, List<Integer>> labelToIndices) {
        List<Integer> succs = new ArrayList<>();
        IrCommand cmd = func.get(i);

        if (cmd instanceof IrCommandJumpLabel) {
            String target = ((IrCommandJumpLabel) cmd).labelName;
            List<Integer> targets = labelToIndices.get(target);
            if (targets != null) succs.addAll(targets);
            return succs;
        }

        if (cmd instanceof IrCommandJumpIfEqToZero) {
            String target = ((IrCommandJumpIfEqToZero) cmd).labelName;
            List<Integer> targets = labelToIndices.get(target);
            if (targets != null) succs.addAll(targets);
            if (i + 1 < func.size()) succs.add(i + 1);
            return succs;
        }

        if (cmd instanceof IrCommandReturn || cmd instanceof IrCommandReturnVoid) {
            return succs;
        }

        if (i + 1 < func.size()) succs.add(i + 1);
        return succs;
    }

    private Map<Integer, Set<Integer>> buildInterferenceGraph(
            List<IrCommand> func, Map<Integer, Set<Integer>> liveOut, Set<Integer> allTemps) {
        Map<Integer, Set<Integer>> graph = new HashMap<>();
        for (int t : allTemps) graph.put(t, new HashSet<>());

        for (int i = 0; i < func.size(); i++) {
            IrCommand cmd = func.get(i);
            Set<Integer> live = liveOut.get(i);
            if (live == null) live = new HashSet<>();

            for (Temp def : cmd.getDefinedTemps()) {
                int d = def.getSerialNumber();
                for (int l : live) {
                    if (d != l) {
                        graph.computeIfAbsent(d, k -> new HashSet<>()).add(l);
                        graph.computeIfAbsent(l, k -> new HashSet<>()).add(d);
                    }
                }
            }
        }
        return graph;
    }

    private Map<Integer, Integer> colorGraph(Map<Integer, Set<Integer>> graph, Set<Integer> allTemps) {
        Map<Integer, Set<Integer>> workGraph = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> e : graph.entrySet()) {
            workGraph.put(e.getKey(), new HashSet<>(e.getValue()));
        }
        for (int t : allTemps) workGraph.computeIfAbsent(t, k -> new HashSet<>());

        Deque<Integer> stack = new ArrayDeque<>();
        Set<Integer> removed = new HashSet<>();

        while (removed.size() < allTemps.size()) {
            boolean found = false;
            for (int t : allTemps) {
                if (removed.contains(t)) continue;
                int degree = 0;
                for (int neighbor : workGraph.getOrDefault(t, Collections.emptySet())) {
                    if (!removed.contains(neighbor)) degree++;
                }
                if (degree < NUM_REGISTERS) {
                    stack.push(t);
                    removed.add(t);
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw new RegisterAllocationFailedException();
            }
        }

        Map<Integer, Integer> coloring = new HashMap<>();
        while (!stack.isEmpty()) {
            int t = stack.pop();
            Set<Integer> usedColors = new HashSet<>();
            for (int neighbor : graph.getOrDefault(t, Collections.emptySet())) {
                if (coloring.containsKey(neighbor)) {
                    usedColors.add(coloring.get(neighbor));
                }
            }
            for (int c = 0; c < NUM_REGISTERS; c++) {
                if (!usedColors.contains(c)) {
                    coloring.put(t, c);
                    break;
                }
            }
            if (!coloring.containsKey(t)) {
                throw new RegisterAllocationFailedException();
            }
        }
        return coloring;
    }

    public String getRegister(Temp t) {
        Integer reg = tempToRegister.get(t.getSerialNumber());
        if (reg == null) return "$t0";
        return "$t" + reg;
    }
}
