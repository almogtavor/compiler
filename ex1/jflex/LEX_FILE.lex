/***************************/
/* FILE NAME: LEX_FILE.lex */
/***************************/

/*************/
/* USER CODE */
/*************/

import java_cup.runtime.*;

/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/

%%

/************************************/
/* OPTIONS AND DECLARATIONS SECTION */
/************************************/
   
/*****************************************************/ 
/* Lexer is the name of the class JFlex will create. */
/* The code will be written to the file Lexer.java.  */
/*****************************************************/ 
%class Lexer

/********************************************************************/
/* The current line number can be accessed with the variable yyline */
/* and the current column number with the variable yycolumn.        */
/********************************************************************/
%line
%column

/*******************************************************************************/
/* Note that this has to be the EXACT same name of the class the CUP generates */
/*******************************************************************************/
%cupsym TokenNames

/******************************************************************/
/* CUP compatibility mode interfaces with a CUP generated parser. */
/******************************************************************/
%cup

/****************/
/* DECLARATIONS */
/****************/
/*****************************************************************************/   
/* Code between %{ and %}, both of which must be at the beginning of a line, */
/* will be copied verbatim (letter to letter) into the Lexer class code.     */
/* Here you declare member variables and functions that are used inside the  */
/* scanner actions.                                                          */  
/*****************************************************************************/   
%{
	/*********************************************************************************/
	/* Create a new java_cup.runtime.Symbol with information about the current token */
	/*********************************************************************************/
	private Symbol symbol(int type)               {return new Symbol(type, yyline, yycolumn);}
	private Symbol symbol(int type, Object value) {return new Symbol(type, yyline, yycolumn, value);}

	/*******************************************/
	/* Enable line number extraction from main */
	/*******************************************/
	public int getLine() { return yyline + 1; } 

	/**********************************************/
	/* Enable token position extraction from main */
	/**********************************************/
	public int getTokenStartPosition() { return yycolumn + 1; }
%}

/***********************/
/* MACRO DECLARATIONS */
/***********************/
LineTerminator	= \r|\n|\r\n
Space 			= [ \t\f]
WhiteSpace		= {LineTerminator} | {Space}
INTEGER			= 0 | [1-9][0-9]*
ID				= [a-zA-Z][a-zA-Z0-9]*

LETTERS = [a-zA-Z]+
DIGIT = [0-9]+
CMT = [a-zA-Z0-9\(\)\[\]\{\}\?\!\+\-\*\/\.\;]
LETTERS_ = [a-zA-Z]*
DIGIT_ = [0-9]*

STRING = \"{LETTERS_}\"
COMMENT_TYPE_1 = "//"({CMT}|{Space})*{LineTerminator}?
COMMENT_TYPE_2 = "/*"({CMT}| {LineTerminator}|{WhiteSpace})*"*/"


LPAREN      = "("
RPAREN 		= ")" 
RBRACK      = "]"
LBRACK      = "["
LBRACE      = "{"
RBRACE      = "}"
PLUS        = "+"
MINUS       = "-"
TIMES       = "*"
DIVIDE      = "/"
COMMA       = ","
DOT         = "."
SEMICOLON   = ";"
TYPE_INT    = "int"
TYPE_STRING = "string"
TYPE_VOID   = "void"
ASSIGN      = ":="
EQ          = "="
LT          = "<"
GT          = ">"
ARRAY       = "array"
CLASS       = "class"
RETURN      = "return"
WHILE       = "while"
IF          = "if"
ELSE        = "else"
NEW         = "new"
EXTENDS		= "extends"
NIL         = "nil"
ERROR 		= .
/******************************/
/* DOLLAR DOLLAR - DON'T TOUCH! */
/******************************/

%%

/************************************************************/
/* LEXER matches regular expressions to actions (Java code) */
/************************************************************/

/**************************************************************/
/* YYINITIAL is the state at which the lexer begins scanning. */
/* So these regular expressions will only be matched if the   */
/* scanner is in the start state YYINITIAL.                   */
/**************************************************************/

<YYINITIAL> {

{ARRAY}             { return symbol(TokenNames.ARRAY, "ARRAY"); }
{CLASS}             { return symbol(TokenNames.CLASS, "CLASS"); }
{ELSE}              { return symbol(TokenNames.ELSE, "ELSE"); }
{IF}                { return symbol(TokenNames.IF, "IF"); }
{NEW}               { return symbol(TokenNames.NEW, "NEW"); }
{NIL}               { return symbol(TokenNames.NIL, "NIL"); }
{RETURN}            { return symbol(TokenNames.RETURN, "RETURN"); }
{WHILE}             { return symbol(TokenNames.WHILE, "WHILE"); }
{TYPE_INT}          { return symbol(TokenNames.TYPE_INT, "TYPE_INT"); }
{TYPE_STRING}       { return symbol(TokenNames.TYPE_STRING, "TYPE_STRING"); }
{TYPE_VOID}         { return symbol(TokenNames.TYPE_VOID, "TYPE_VOID"); }
{EXTENDS}           { return symbol(TokenNames.EXTENDS, "EXTENDS"); }
{ASSIGN}            { return symbol(TokenNames.ASSIGN, "ASSIGN"); }
{EQ}                { return symbol(TokenNames.EQ, "EQ"); }
{LT}                { return symbol(TokenNames.LT, "LT"); }
{GT}                { return symbol(TokenNames.GT, "GT"); }
{PLUS}              { return symbol(TokenNames.PLUS, "PLUS"); }
{MINUS}             { return symbol(TokenNames.MINUS, "MINUS"); }
{TIMES}             { return symbol(TokenNames.TIMES, "TIMES"); }
{DIVIDE}            { return symbol(TokenNames.DIVIDE, "DIVIDE"); }
{LPAREN}            { return symbol(TokenNames.LPAREN, "LPAREN"); }
{RPAREN}            { return symbol(TokenNames.RPAREN, "RPAREN"); }
{LBRACK}            { return symbol(TokenNames.LBRACK, "LBRACK"); }
{RBRACK}            { return symbol(TokenNames.RBRACK, "RBRACK"); }
{LBRACE}            { return symbol(TokenNames.LBRACE, "LBRACE"); }
{RBRACE}            { return symbol(TokenNames.RBRACE, "RBRACE"); }
{COMMA}             { return symbol(TokenNames.COMMA, "COMMA"); }
{DOT}               { return symbol(TokenNames.DOT, "DOT"); }
{SEMICOLON}         { return symbol(TokenNames.SEMICOLON, "SEMICOLON"); }
{WhiteSpace}		{/*ignore*/}
{COMMENT_TYPE_1}	{/*ignore*/}
{COMMENT_TYPE_2}	{/*ignore*/}
{STRING} {return symbol(TokenNames.STRING,"STRING("+yytext()+")");}
{INTEGER} {
  int val = Integer.parseInt(yytext());
  if (val > 32767)
    return symbol(TokenNames.ERROR, "ERROR");
  else
    return symbol(TokenNames.INT, "INT(" + Integer.valueOf(val) + ")");
}
{ID}                { return symbol(TokenNames.ID, "ID("+yytext()+")"); }
<<EOF>>				{ return symbol(TokenNames.EOF);}
{ERROR} 			{return symbol(TokenNames.ERROR,"ERROR");}
}
