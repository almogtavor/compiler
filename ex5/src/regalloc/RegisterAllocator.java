package regalloc;

import ir.*;
import temp.*;
import java.util.*;

// Simplification-based graph-coloring register allocator (Chaitin's algorithm).
// Allocates IR temporaries to 10 physical registers ($t0-$t9).
// No spilling - if the graph can't be colored, compilation fails.
// Runs independently per function.
public class RegisterAllocator {
    private static final int NUM_REGISTERS = 10; // $t0-$t9
    private Map<Integer, Integer> tempToRegister = new HashMap<>();

    // Per-function pipeline: split IR -> collect temps -> liveness -> interference graph -> color
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

    // Split the flat IR list into per-function blocks (each starting with a function entry label)
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

    // Backward dataflow analysis to compute live-out sets for each IR command.
    // Fixed-point iteration: in[i] = use[i] U (out[i] - def[i]),
    //                        out[i] = U{ in[s] : s in successors(i) }
    // Iterates until no sets change (capped at 1000 iterations as safety).
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

    // Control flow successors: jump -> target only, cond branch -> target + fall-through,
    // return -> none (terminal), everything else -> fall-through to i+1
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

    // Build interference graph: two temps interfere if one is defined while the other is live-out.
    // An edge between t1 and t2 means they can't share a register.
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

    // Chaitin's simplification-based graph coloring:
    // Phase 1 (simplify): repeatedly remove a node with degree < K (=10) and push onto stack.
    //   If no such node exists, allocation fails (no spilling implemented).
    // Phase 2 (select): pop nodes from stack, assign the lowest available color
    //   that doesn't conflict with already-colored neighbors.
    private Map<Integer, Integer> colorGraph(Map<Integer, Set<Integer>> graph, Set<Integer> allTemps) {
        Map<Integer, Set<Integer>> workGraph = new HashMap<>();
        for (Map.Entry<Integer, Set<Integer>> e : graph.entrySet()) {
            workGraph.put(e.getKey(), new HashSet<>(e.getValue()));
        }
        for (int t : allTemps) workGraph.computeIfAbsent(t, k -> new HashSet<>());

        Deque<Integer> stack = new ArrayDeque<>();
        Set<Integer> removed = new HashSet<>();

        // Phase 1: simplify - find and remove nodes with degree < K
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
                throw new RegisterAllocationFailedException(); // would need spilling
            }
        }

        // Phase 2: select - pop and assign colors (register numbers 0-9 = $t0-$t9)
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
