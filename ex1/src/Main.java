import java.io.*;
import java.io.PrintWriter;

import java_cup.runtime.Symbol;
   
public class Main
{
	static public void main(String argv[])
	{
		Lexer l;
		Symbol s;
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

			/***********************/
			/* [4] Read next token */
			/***********************/
			s = l.next_token();
			StringBuilder st = new StringBuilder();
			/********************************/
			/* [5] Main reading tokens loop */
			/********************************/
			while (s.sym != TokenNames.EOF)
			{
				if (s.sym == TokenNames.ERROR) {
        			st = new StringBuilder("ERROR");
					break;
				}
				/************************/
				/* [6] Print to console */
				/************************/
				st.append(s.value).append("[").append(l.getLine()).append(",").append(l.getTokenStartPosition()).append("]");
				/***********************/
				/* [8] Read next token */
				/***********************/
				s = l.next_token();
				if(s.sym!=TokenNames.EOF){st.append("\n");}
			}
			fileWriter.print(st.toString());			
			/******************************/
			/* [9] Close lexer input file */
			/******************************/
			l.yyclose();

			/**************************/
			/* [10] Close output file */
			/**************************/
			fileWriter.close();
    	}
			     
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}


