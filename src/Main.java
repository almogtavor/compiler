import java.io.*;
import java_cup.runtime.Symbol;
import ast.*;

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
            p.parse();   // אם יש שגיאה, הפונקציה תזרוק חריגה

            /***************************************/
            /* [6] No error → write OK to the file */
            /***************************************/
            fileWriter.print("OK");
            fileWriter.close();
		
    	}
        catch (Exception e) {
            try {
                PrintWriter w = new PrintWriter(outputFileName);
                if (p != null)
                    w.print("ERROR(" + p.errorLine + ")");
                else 
                    w.print("ERROR");
                w.close();
            } catch (Exception ignore) {}
        }
    }
}
