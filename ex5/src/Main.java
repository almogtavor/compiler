import java.io.*;
import java_cup.runtime.Symbol;
import ast.*;
import Exceptions.*;
import ir.*;
import regalloc.*;
import mips.*;
import codegen.*;
import java.util.*;

public class Main {
    public static void main(String argv[]) {
        Lexer l;
        Parser p = null;
        FileReader fileReader;
        PrintWriter fileWriter;
        String inputFileName = argv[0];
        String outputFileName = argv[1];
        try {
            fileReader = new FileReader(inputFileName);
            fileWriter = new PrintWriter(outputFileName);
            l = new Lexer(fileReader);
            p = new Parser(l);

            AstProgram ast = (AstProgram) p.parse().value;
            ast.SemantMe();
            ast.IRme();

            IrCommandList irList = Ir.getInstance().getList();

            RegisterAllocator regAlloc = new RegisterAllocator();
            Map<Integer, Integer> coloring = regAlloc.allocate(irList);

            MipsGenerator mipsGen = new MipsGenerator(fileWriter, regAlloc);
            mipsGen.generate(irList);

            fileWriter.close();
        } catch (LexerException le) {
            try {
                PrintWriter w = new PrintWriter(outputFileName);
                w.print("ERROR");
                w.close();
            } catch (Exception ignore) {}
        } catch (SyntaxException se) {
            try {
                PrintWriter w = new PrintWriter(outputFileName);
                w.print("ERROR(" + se.getLine() + ")");
                w.close();
            } catch (Exception ignore) {}
        } catch (SemanticException se) {
            try {
                PrintWriter w = new PrintWriter(outputFileName);
                w.print("ERROR(" + se.getLine() + ")");
                w.close();
            } catch (Exception ignore) {}
        } catch (RegisterAllocationFailedException raf) {
            try {
                PrintWriter w = new PrintWriter(outputFileName);
                w.print("Register Allocation Failed");
                w.close();
            } catch (Exception ignore) {}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
