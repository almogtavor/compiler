import java.io.*;
import java.util.HashSet;
import java.util.TreeSet;

import java_cup.runtime.Symbol;
import ast.*;
import Exceptions.*;
import ir.*;

public class Main {
    public static void main(String argv[]) {
        Lexer l;
        Parser p = null;
        FileReader fileReader;
        PrintWriter fileWriter;
        String inputFileName = argv[0];
        String outputFileName = argv[1];
        try
        {
            fileReader = new FileReader(inputFileName);
            fileWriter = new PrintWriter(outputFileName);
            l = new Lexer(fileReader);
            p = new Parser(l);

            AstProgram ast = (AstProgram) p.parse().value;
            
            ast.SemantMe();
            
            ast.IRme();
            
            IrCommandList list = Ir.getInstance().getList();
            BasicBlock first = CFG.generateCFG(list);
            
            UninitializedVariableAnalysis.analyze(list, first);
            
            HashSet<String> uninitializedVars = UninitializedVariableAnalysis.findUninitializedAccesses(list);
            TreeSet<String> sorted = new TreeSet<>(uninitializedVars);
            
            if (!sorted.isEmpty()) {
                String result = String.join(System.lineSeparator(), sorted);
                fileWriter.print(result); 
            } else {
                fileWriter.print("!OK");
            }
            
            fileWriter.close();
        }
        catch (LexerException le) {
            try{
                PrintWriter w = new PrintWriter(outputFileName);
                w.print("ERROR");
                w.close();
            }
            catch (Exception ignore) {}
        }
        catch (SyntaxException se) {
            try{
                PrintWriter w = new PrintWriter(outputFileName);
                w.print("ERROR(" + se.getLine() + ")");
                w.close();
            }
            catch (Exception ignore) {}
        }
        catch (SemanticException se) {
            try{
                PrintWriter w = new PrintWriter(outputFileName);
                w.print("ERROR(" + se.getLine() + ")");
                w.close();
            }
            catch (Exception ignore) {}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
