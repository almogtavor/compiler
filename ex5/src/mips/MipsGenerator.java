package mips;

import ir.*;
import temp.*;
import regalloc.*;
import codegen.*;
import java.io.*;
import java.util.*;

public class MipsGenerator {
    private static final int CALLER_TEMP_REG_COUNT = 10;
    private PrintWriter out;
    private RegisterAllocator regAlloc;
    private Map<String, Integer> localVarOffsets = new HashMap<>();
    private int currentFrameSize = 0;
    private String currentFunc = null;

    public MipsGenerator(PrintWriter out, RegisterAllocator regAlloc) {
        this.out = out;
        this.regAlloc = regAlloc;
    }

    public void generate(IrCommandList irList) {
        generateDataSection();
        generateTextSection(irList);
    }

    private void generateDataSection() {
        out.println(".data");
        CodeGenInfo info = CodeGenInfo.getInstance();

        out.println("str_access_violation: .asciiz \"Access Violation\"");
        out.println("str_illegal_div_zero: .asciiz \"Illegal Division By Zero\"");
        out.println("str_invalid_ptr: .asciiz \"Invalid Pointer Dereference\"");
        out.println("str_space: .asciiz \" \"");

        for (Map.Entry<String, String> e : info.getStringLiterals().entrySet()) {
            String escaped = e.getValue()
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
            out.println(e.getKey() + ": .asciiz \"" + escaped + "\"");
        }

        for (String gv : info.getGlobalVars()) {
            out.println(sanitizeLabel(gv) + ": .word 0");
        }

        ClassLayoutManager clm = ClassLayoutManager.getInstance();
        for (Map.Entry<String, ClassLayoutManager.ClassInfo> e : clm.getAllClassInfos().entrySet()) {
            ClassLayoutManager.ClassInfo ci = e.getValue();
            out.print("vtable_" + ci.className + ":");
            if (ci.vtable.isEmpty()) {
                out.println(" .word 0");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < ci.vtable.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(ci.vtable.get(i)[1]);
                }
                out.println(" .word " + sb);
            }
        }
        out.println();
    }

    private void generateTextSection(IrCommandList irList) {
        out.println(".text");
        out.println(".globl main");
        out.println();

        out.println("main:");
        generateGlobalInit(irList);
        out.println("  jal func_main");
        out.println("  li $v0, 10");
        out.println("  syscall");
        out.println();

        boolean inGlobalInit = true;
        for (IrCommandList it = irList; it != null; it = it.tail) {
            IrCommand cmd = it.command;
            if (cmd instanceof IrCommandLabel && ((IrCommandLabel) cmd).isFunctionEntry) {
                inGlobalInit = false;
            }
            if (!inGlobalInit) {
                generateCommand(cmd);
            }
        }
    }

    private void generateGlobalInit(IrCommandList irList) {
        for (IrCommandList it = irList; it != null; it = it.tail) {
            IrCommand cmd = it.command;
            if (cmd instanceof IrCommandLabel && ((IrCommandLabel) cmd).isFunctionEntry) break;
            if (cmd instanceof IrCommandAllocate) continue;
            generateCommand(cmd);
        }
    }

    private void setupFunctionFrame(String funcLabel) {
        currentFunc = funcLabel;
        localVarOffsets.clear();
        CodeGenInfo info = CodeGenInfo.getInstance();
        List<String> params = info.getParams(funcLabel);
        List<String> locals = info.getLocals(funcLabel);

        for (int i = 0; i < params.size(); i++) {
            localVarOffsets.put(params.get(i), 8 + i * 4);
        }
        int offset = -4;
        for (String local : locals) {
            localVarOffsets.put(local, offset);
            offset -= 4;
        }
        currentFrameSize = locals.size() * 4;
    }

    private void generateCommand(IrCommand cmd) {
        if (cmd instanceof IrCommandLabel) generateLabel((IrCommandLabel) cmd);
        else if (cmd instanceof IrCommandConstInt) generateConstInt((IrCommandConstInt) cmd);
        else if (cmd instanceof IrCommandConstString) generateConstString((IrCommandConstString) cmd);
        else if (cmd instanceof IrCommandLoad) generateLoad((IrCommandLoad) cmd);
        else if (cmd instanceof IrCommandStore) generateStore((IrCommandStore) cmd);
        else if (cmd instanceof IrCommandAllocate) {}
        else if (cmd instanceof IrCommandBinopAddIntegers) generateBinop((IrCommandBinopAddIntegers) cmd, "add");
        else if (cmd instanceof IrCommandBinopSubIntegers) generateBinop((IrCommandBinopSubIntegers) cmd, "sub");
        else if (cmd instanceof IrCommandBinopMulIntegers) generateMul((IrCommandBinopMulIntegers) cmd);
        else if (cmd instanceof IrCommandBinopDivIntegers) generateDiv((IrCommandBinopDivIntegers) cmd);
        else if (cmd instanceof IrCommandBinopLtIntegers) generateCmp((IrCommandBinopLtIntegers) cmd, "slt");
        else if (cmd instanceof IrCommandBinopGtIntegers) generateCmp((IrCommandBinopGtIntegers) cmd, "sgt");
        else if (cmd instanceof IrCommandBinopEqIntegers) generateEq((IrCommandBinopEqIntegers) cmd);
        else if (cmd instanceof IrCommandJumpLabel) generateJump((IrCommandJumpLabel) cmd);
        else if (cmd instanceof IrCommandJumpIfEqToZero) generateCondJump((IrCommandJumpIfEqToZero) cmd);
        else if (cmd instanceof IrCommandPrintInt) generatePrintInt((IrCommandPrintInt) cmd);
        else if (cmd instanceof IrCommandPrintString) generatePrintString((IrCommandPrintString) cmd);
        else if (cmd instanceof IrCommandMalloc) generateMalloc((IrCommandMalloc) cmd);
        else if (cmd instanceof IrCommandFieldGet) generateFieldGet((IrCommandFieldGet) cmd);
        else if (cmd instanceof IrCommandFieldSet) generateFieldSet((IrCommandFieldSet) cmd);
        else if (cmd instanceof IrCommandArrayGet) generateArrayGet((IrCommandArrayGet) cmd);
        else if (cmd instanceof IrCommandArraySet) generateArraySet((IrCommandArraySet) cmd);
        else if (cmd instanceof IrCommandCallFunc) generateCallFunc((IrCommandCallFunc) cmd);
        else if (cmd instanceof IrCommandVirtualCall) generateVirtualCall((IrCommandVirtualCall) cmd);
        else if (cmd instanceof IrCommandReturn) generateReturn((IrCommandReturn) cmd);
        else if (cmd instanceof IrCommandReturnVoid) generateReturnVoid();
        else if (cmd instanceof IrCommandStringConcat) generateStringConcat((IrCommandStringConcat) cmd);
        else if (cmd instanceof IrCommandStringEq) generateStringEq((IrCommandStringEq) cmd);
        else if (cmd instanceof IrCommandCheckNullPtr) generateCheckNull((IrCommandCheckNullPtr) cmd);
        else if (cmd instanceof IrCommandCheckBounds) generateCheckBounds((IrCommandCheckBounds) cmd);
        else if (cmd instanceof IrCommandCheckDivZero) generateCheckDivZero((IrCommandCheckDivZero) cmd);
    }

    private void generateLabel(IrCommandLabel cmd) {
        if (cmd.isFunctionEntry) {
            setupFunctionFrame(cmd.labelName);
            out.println(cmd.labelName + ":");
            out.println("  subu $sp, $sp, 8");
            out.println("  sw $ra, 4($sp)");
            out.println("  sw $fp, 0($sp)");
            out.println("  move $fp, $sp");
            if (currentFrameSize > 0) {
                out.println("  subu $sp, $sp, " + currentFrameSize);
            }
        } else {
            out.println(cmd.labelName + ":");
        }
    }

    private void generateConstInt(IrCommandConstInt cmd) {
        out.println("  li " + reg(cmd.t) + ", " + cmd.value);
    }

    private void generateConstString(IrCommandConstString cmd) {
        out.println("  la " + reg(cmd.dst) + ", " + cmd.label);
    }

    private void generateLoad(IrCommandLoad cmd) {
        String r = reg(cmd.dst);
        if (CodeGenInfo.getInstance().isGlobalVar(cmd.varName)) {
            out.println("  la $s0, " + sanitizeLabel(cmd.varName));
            out.println("  lw " + r + ", 0($s0)");
        } else {
            Integer offset = localVarOffsets.get(cmd.varName);
            if (offset != null) {
                out.println("  lw " + r + ", " + offset + "($fp)");
            } else {
                out.println("  li " + r + ", 0");
            }
        }
    }

    private void generateStore(IrCommandStore cmd) {
        String r = reg(cmd.src);
        if (CodeGenInfo.getInstance().isGlobalVar(cmd.varName)) {
            out.println("  la $s0, " + sanitizeLabel(cmd.varName));
            out.println("  sw " + r + ", 0($s0)");
        } else {
            Integer offset = localVarOffsets.get(cmd.varName);
            if (offset != null) {
                out.println("  sw " + r + ", " + offset + "($fp)");
            }
        }
    }

    private void generateBinop(IrCommand cmd, String op) {
        Temp dst, t1, t2;
        if (cmd instanceof IrCommandBinopAddIntegers) {
            IrCommandBinopAddIntegers c = (IrCommandBinopAddIntegers) cmd;
            dst = c.dst; t1 = c.t1; t2 = c.t2;
        } else {
            IrCommandBinopSubIntegers c = (IrCommandBinopSubIntegers) cmd;
            dst = c.dst; t1 = c.t1; t2 = c.t2;
        }
        out.println("  " + op + " " + reg(dst) + ", " + reg(t1) + ", " + reg(t2));
        saturate(reg(dst));
    }

    private void generateMul(IrCommandBinopMulIntegers cmd) {
        out.println("  mul " + reg(cmd.dst) + ", " + reg(cmd.t1) + ", " + reg(cmd.t2));
        saturate(reg(cmd.dst));
    }

    private void generateDiv(IrCommandBinopDivIntegers cmd) {
        out.println("  div " + reg(cmd.t1) + ", " + reg(cmd.t2));
        out.println("  mflo " + reg(cmd.dst));
        saturate(reg(cmd.dst));
    }

    private void generateCmp(IrCommand cmd, String op) {
        Temp dst, t1, t2;
        if (cmd instanceof IrCommandBinopLtIntegers) {
            IrCommandBinopLtIntegers c = (IrCommandBinopLtIntegers) cmd;
            dst = c.dst; t1 = c.t1; t2 = c.t2;
        } else {
            IrCommandBinopGtIntegers c = (IrCommandBinopGtIntegers) cmd;
            dst = c.dst; t1 = c.t1; t2 = c.t2;
        }
        out.println("  " + op + " " + reg(dst) + ", " + reg(t1) + ", " + reg(t2));
    }

    private void generateEq(IrCommandBinopEqIntegers cmd) {
        String lEq = IrCommand.getFreshLabel("eq_true");
        String lEnd = IrCommand.getFreshLabel("eq_end");
        out.println("  beq " + reg(cmd.t1) + ", " + reg(cmd.t2) + ", " + lEq);
        out.println("  li " + reg(cmd.dst) + ", 0");
        out.println("  j " + lEnd);
        out.println(lEq + ":");
        out.println("  li " + reg(cmd.dst) + ", 1");
        out.println(lEnd + ":");
    }

    private void generateJump(IrCommandJumpLabel cmd) {
        out.println("  j " + cmd.labelName);
    }

    private void generateCondJump(IrCommandJumpIfEqToZero cmd) {
        out.println("  beq " + reg(cmd.t) + ", $zero, " + cmd.labelName);
    }

    private void generatePrintInt(IrCommandPrintInt cmd) {
        out.println("  move $a0, " + reg(cmd.t));
        out.println("  li $v0, 1");
        out.println("  syscall");
        out.println("  la $a0, str_space");
        out.println("  li $v0, 4");
        out.println("  syscall");
    }

    private void generatePrintString(IrCommandPrintString cmd) {
        out.println("  move $a0, " + reg(cmd.t));
        out.println("  li $v0, 4");
        out.println("  syscall");
    }

    private void generateMalloc(IrCommandMalloc cmd) {
        out.println("  move $a0, " + reg(cmd.size));
        out.println("  li $v0, 9");
        out.println("  syscall");
        out.println("  move " + reg(cmd.dst) + ", $v0");
    }

    private void generateFieldGet(IrCommandFieldGet cmd) {
        int byteOffset = cmd.offset * 4;
        out.println("  lw " + reg(cmd.dst) + ", " + byteOffset + "(" + reg(cmd.obj) + ")");
    }

    private void generateFieldSet(IrCommandFieldSet cmd) {
        int byteOffset = cmd.offset * 4;
        out.println("  sw " + reg(cmd.src) + ", " + byteOffset + "(" + reg(cmd.obj) + ")");
    }

    private void generateArrayGet(IrCommandArrayGet cmd) {
        out.println("  sll $s1, " + reg(cmd.idx) + ", 2");
        out.println("  addu $s1, $s1, " + reg(cmd.arr));
        out.println("  lw " + reg(cmd.dst) + ", 4($s1)");
    }

    private void generateArraySet(IrCommandArraySet cmd) {
        out.println("  sll $s1, " + reg(cmd.idx) + ", 2");
        out.println("  addu $s1, $s1, " + reg(cmd.arr));
        out.println("  sw " + reg(cmd.src) + ", 4($s1)");
    }

    private void generateCallFunc(IrCommandCallFunc cmd) {
        int numArgs = cmd.args.size();
        saveCallerTemps();
        for (int i = numArgs - 1; i >= 0; i--) {
            out.println("  subu $sp, $sp, 4");
            out.println("  sw " + reg(cmd.args.get(i)) + ", 0($sp)");
        }
        out.println("  jal " + cmd.funcLabel);
        if (numArgs > 0) out.println("  addu $sp, $sp, " + (numArgs * 4));
        restoreCallerTemps();
        if (cmd.dst != null) out.println("  move " + reg(cmd.dst) + ", $v0");
    }

    private void generateVirtualCall(IrCommandVirtualCall cmd) {
        List<Temp> allArgs = new ArrayList<>();
        allArgs.add(cmd.obj);
        allArgs.addAll(cmd.args);
        int numArgs = allArgs.size();

        saveCallerTemps();
        for (int i = numArgs - 1; i >= 0; i--) {
            out.println("  subu $sp, $sp, 4");
            out.println("  sw " + reg(allArgs.get(i)) + ", 0($sp)");
        }

        out.println("  lw $s0, 0(" + reg(cmd.obj) + ")");
        int byteOffset = cmd.vtableOffset * 4;
        out.println("  lw $s0, " + byteOffset + "($s0)");
        out.println("  jalr $s0");

        if (numArgs > 0) out.println("  addu $sp, $sp, " + (numArgs * 4));
        restoreCallerTemps();
        if (cmd.dst != null) out.println("  move " + reg(cmd.dst) + ", $v0");
    }

    private void saveCallerTemps() {
        for (int i = 0; i < CALLER_TEMP_REG_COUNT; i++) {
            out.println("  subu $sp, $sp, 4");
            out.println("  sw $t" + i + ", 0($sp)");
        }
    }

    private void restoreCallerTemps() {
        for (int i = CALLER_TEMP_REG_COUNT - 1; i >= 0; i--) {
            out.println("  lw $t" + i + ", 0($sp)");
            out.println("  addu $sp, $sp, 4");
        }
    }

    private void generateReturn(IrCommandReturn cmd) {
        if (cmd.val != null) out.println("  move $v0, " + reg(cmd.val));
        out.println("  move $sp, $fp");
        out.println("  lw $fp, 0($sp)");
        out.println("  lw $ra, 4($sp)");
        out.println("  addu $sp, $sp, 8");
        out.println("  jr $ra");
    }

    private void generateReturnVoid() {
        out.println("  move $sp, $fp");
        out.println("  lw $fp, 0($sp)");
        out.println("  lw $ra, 4($sp)");
        out.println("  addu $sp, $sp, 8");
        out.println("  jr $ra");
    }

    private void generateStringConcat(IrCommandStringConcat cmd) {
        out.println("  move $s2, " + reg(cmd.s1));
        out.println("  move $s3, " + reg(cmd.s2));
        out.println("  move $s4, $zero");
        out.println("  move $s5, $s2");
        String l1 = IrCommand.getFreshLabel("strlen1");
        String l1e = IrCommand.getFreshLabel("strlen1_end");
        out.println(l1 + ":");
        out.println("  lb $s0, 0($s5)");
        out.println("  beq $s0, $zero, " + l1e);
        out.println("  addu $s4, $s4, 1");
        out.println("  addu $s5, $s5, 1");
        out.println("  j " + l1);
        out.println(l1e + ":");
        out.println("  move $s5, $s3");
        String l2 = IrCommand.getFreshLabel("strlen2");
        String l2e = IrCommand.getFreshLabel("strlen2_end");
        out.println(l2 + ":");
        out.println("  lb $s0, 0($s5)");
        out.println("  beq $s0, $zero, " + l2e);
        out.println("  addu $s4, $s4, 1");
        out.println("  addu $s5, $s5, 1");
        out.println("  j " + l2);
        out.println(l2e + ":");
        out.println("  addu $a0, $s4, 1");
        out.println("  li $v0, 9");
        out.println("  syscall");
        out.println("  move " + reg(cmd.dst) + ", $v0");
        out.println("  move $s5, $v0");
        out.println("  move $s6, $s2");
        String l3 = IrCommand.getFreshLabel("copy1");
        String l3e = IrCommand.getFreshLabel("copy1_end");
        out.println(l3 + ":");
        out.println("  lb $s0, 0($s6)");
        out.println("  beq $s0, $zero, " + l3e);
        out.println("  sb $s0, 0($s5)");
        out.println("  addu $s5, $s5, 1");
        out.println("  addu $s6, $s6, 1");
        out.println("  j " + l3);
        out.println(l3e + ":");
        out.println("  move $s6, $s3");
        String l4 = IrCommand.getFreshLabel("copy2");
        String l4e = IrCommand.getFreshLabel("copy2_end");
        out.println(l4 + ":");
        out.println("  lb $s0, 0($s6)");
        out.println("  beq $s0, $zero, " + l4e);
        out.println("  sb $s0, 0($s5)");
        out.println("  addu $s5, $s5, 1");
        out.println("  addu $s6, $s6, 1");
        out.println("  j " + l4);
        out.println(l4e + ":");
        out.println("  sb $zero, 0($s5)");
    }

    private void generateStringEq(IrCommandStringEq cmd) {
        out.println("  move $s2, " + reg(cmd.s1));
        out.println("  move $s3, " + reg(cmd.s2));
        String loop = IrCommand.getFreshLabel("streq_loop");
        String neq = IrCommand.getFreshLabel("streq_neq");
        String end = IrCommand.getFreshLabel("streq_end");
        out.println(loop + ":");
        out.println("  lb $s0, 0($s2)");
        out.println("  lb $s1, 0($s3)");
        out.println("  bne $s0, $s1, " + neq);
        out.println("  beq $s0, $zero, " + end);
        out.println("  addu $s2, $s2, 1");
        out.println("  addu $s3, $s3, 1");
        out.println("  j " + loop);
        out.println(neq + ":");
        out.println("  li " + reg(cmd.dst) + ", 0");
        String skip = IrCommand.getFreshLabel("streq_skip");
        out.println("  j " + skip);
        out.println(end + ":");
        out.println("  li " + reg(cmd.dst) + ", 1");
        out.println(skip + ":");
    }

    private void generateCheckNull(IrCommandCheckNullPtr cmd) {
        String ok = IrCommand.getFreshLabel("null_ok");
        out.println("  bne " + reg(cmd.ptr) + ", $zero, " + ok);
        out.println("  la $a0, str_invalid_ptr");
        out.println("  li $v0, 4");
        out.println("  syscall");
        out.println("  li $v0, 10");
        out.println("  syscall");
        out.println(ok + ":");
    }

    private void generateCheckBounds(IrCommandCheckBounds cmd) {
        String ok = IrCommand.getFreshLabel("bounds_ok");
        String fail = IrCommand.getFreshLabel("bounds_fail");
        out.println("  lw $s0, 0(" + reg(cmd.arr) + ")");
        out.println("  bltz " + reg(cmd.idx) + ", " + fail);
        out.println("  slt $s1, " + reg(cmd.idx) + ", $s0");
        out.println("  bne $s1, $zero, " + ok);
        out.println(fail + ":");
        out.println("  la $a0, str_access_violation");
        out.println("  li $v0, 4");
        out.println("  syscall");
        out.println("  li $v0, 10");
        out.println("  syscall");
        out.println(ok + ":");
    }

    private void generateCheckDivZero(IrCommandCheckDivZero cmd) {
        String ok = IrCommand.getFreshLabel("divzero_ok");
        out.println("  bne " + reg(cmd.divisor) + ", $zero, " + ok);
        out.println("  la $a0, str_illegal_div_zero");
        out.println("  li $v0, 4");
        out.println("  syscall");
        out.println("  li $v0, 10");
        out.println("  syscall");
        out.println(ok + ":");
    }

    private void saturate(String r) {
        String ok1 = IrCommand.getFreshLabel("sat_ok1");
        String ok2 = IrCommand.getFreshLabel("sat_ok2");
        out.println("  li $s0, 32767");
        out.println("  slt $s1, $s0, " + r);
        out.println("  beq $s1, $zero, " + ok1);
        out.println("  li " + r + ", 32767");
        out.println(ok1 + ":");
        out.println("  li $s0, -32768");
        out.println("  slt $s1, " + r + ", $s0");
        out.println("  beq $s1, $zero, " + ok2);
        out.println("  li " + r + ", -32768");
        out.println(ok2 + ":");
    }

    private String reg(Temp t) {
        return regAlloc.getRegister(t);
    }

    private String sanitizeLabel(String name) {
        return "global_" + name.replace("@", "_");
    }
}
