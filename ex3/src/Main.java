import java.io.*;
import java_cup.runtime.Symbol;
import ast.*;
import Exceptions.*;

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
			/********************************/
			/* [1] Initialize a file reader */
			/********************************/
			fileReader = new FileReader(inputFileName);

			/********************************/
			/* [2] Initialize a file writer */
			/********************************/
			fileWriter = new PrintWriter(outputFileName);
			
			/******************************/
			/* [3] Initialize a new lexer */
			/******************************/
			l = new Lexer(fileReader);

            /*******************************/
            /* [4] Initialize a new parser */
            /*******************************/
            p = new Parser(l);

            /***********************************/
            /* [5] Parse !!! */
            /***********************************/
            AstProgram ast = (AstProgram) p.parse().value;
            //ast.printMe();
            ast.SemantMe();
            /***************************************/
            /* [6] No error → write OK to the file */
            /***************************************/
            fileWriter.print("OK");
            fileWriter.close();
            //AstGraphviz.getInstance().finalizeFile();
		
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
            try {
                PrintWriter w = new PrintWriter(outputFileName);
                w.close();
            } catch (Exception ignore) {}}
    }
}
