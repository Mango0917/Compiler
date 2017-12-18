/**
 * /**
 * JUunit tests for the Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;

import static cop5556fa17.Scanner.Kind.*;

public class ScannerTest {

	//set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();


	//To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}

	/**
	 *Retrieves the next token and checks that it is an EOF token. 
	 *Also checks that this was the last token.
	 *
	 * @param scanner
	 * @return the Token that was retrieved
	 */
	
	Token checkNextIsEOF(Scanner scanner) {
		Scanner.Token token = scanner.nextToken();
		assertEquals(Scanner.Kind.EOF, token.kind);
		assertFalse(scanner.hasTokens());
		return token;
	}


	/**
	 * Retrieves the next token and checks that its kind, position, length, line, and position in line
	 * match the given parameters.
	 * 
	 * @param scanner
	 * @param kind
	 * @param pos
	 * @param length
	 * @param line
	 * @param pos_in_line
	 * @return  the Token that was retrieved
	 */
	Token checkNext(Scanner scanner, Scanner.Kind kind, int pos, int length, int line, int pos_in_line) {
		Token t = scanner.nextToken();
		assertEquals(scanner.new Token(kind, pos, length, line, pos_in_line), t);
		return t;
	}

	/**
	 * Retrieves the next token and checks that its kind and length match the given
	 * parameters.  The position, line, and position in line are ignored.
	 * 
	 * @param scanner
	 * @param kind
	 * @param length
	 * @return  the Token that was retrieved
	 */
	Token check(Scanner scanner, Scanner.Kind kind, int length) {
		Token t = scanner.nextToken();
		assertEquals(kind, t.kind);
		assertEquals(length, t.length);
		return t;
	}

	/**
	 * Simple test case with a (legal) empty program
	 *   
	 * @throws LexicalException
	 */
	//@Test
	/*public void testEmpty() throws LexicalException {
		String input = "";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}*/
	
	/**
	 * Test illustrating how to put a new line in the input program and how to
	 * check content of tokens.
	 * 
	 * Because we are using a Java String literal for input, we use \n for the
	 * end of line character. (We should also be able to handle \n, \r, and \r\n
	 * properly.)
	 * 
	 * Note that if we were reading the input from a file, as we will want to do 
	 * later, the end of line character would be inserted by the text editor.
	 * Showing the input will let you check your input is what you think it is.
	 * 
	 * @throws LexicalException
	 */
	/*@Test
	public void testSemi() throws LexicalException {
		String input = ";;\n;;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 4, 1, 2, 2);
		checkNextIsEOF(scanner);
	}*/
	
	/**
	 * This example shows how to test that your scanner is behaving when the
	 * input is illegal.  In this case, we are giving it a String literal
	 * that is missing the closing ".  
	 * 
	 * Note that the outer pair of quotation marks delineate the String literal
	 * in this test program that provides the input to our Scanner.  The quotation
	 * mark that is actually included in the input must be escaped, \".
	 * 
	 * The example shows catching the exception that is thrown by the scanner,
	 * looking at it, and checking its contents before rethrowing it.  If caught
	 * but not rethrown, then JUnit won't get the exception and the test will fail.  
	 * 
	 * The test will work without putting the try-catch block around 
	 * new Scanner(input).scan(); but then you won't be able to check 
	 * or display the thrown exception.
	 * 
	 * @throws LexicalException
	 */
	/*@Test
	public void failUnclosedStringLiteral() throws LexicalException {
		String input = "()";
		show(input);
		thrown.expect(LexicalException.class);  //Tell JUnit to expect a LexicalException
		try {
			new Scanner(input).scan();
		} catch (LexicalException e) {  //
			show(e);
			assertEquals(2,e.getPos());
			throw e;
		}
	}*/
	@Test
	public void test1() throws LexicalException {
		String input = "  //Aroushi\n  ";  //The input is the empty string.  This is legal
		show(input);        //Display the input 
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		checkNextIsEOF(scanner);  //Check that the only token is the EOF token.
	}

	@Test
	public void test2() throws LexicalException {
		String input = "879dsw//comments\n\tasw x";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.INTEGER_LITERAL, 0,3, 1, 1);
			checkNext(scanner, Kind.IDENTIFIER, 3,3, 1, 4);
			checkNext(scanner, Kind.IDENTIFIER, 18,3, 2, 2);
			checkNext(scanner, Kind.KW_x, 22,1, 2, 6);
			
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}

	@Test
	public void test3() throws LexicalException {
		String input="";
		input = "\" ty\\tiijinrs  \"xyza \"\"";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, STRING_LITERAL, 0, 16, 1, 1);
		checkNext(scanner, IDENTIFIER, 16, 4, 1, 17);
		checkNext(scanner, STRING_LITERAL, 21, 2, 1, 22);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test4() throws LexicalException {
		String input = "\"\ngavv\"";
		try{
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		}
		catch(LexicalException l){
			show(l);
			assertEquals(1, l.getPos());
		}
	}
	
	@Test
	public void test5() throws LexicalException {
		try {
			String input="\"abc\\gk\" ";
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner); 
		} catch (LexicalException e) {
			show(e);
			assertEquals(5, e.getPos());
		}
	}
	
	
	
	@Test
	public void test7() throws LexicalException {
		String input = "";
		try{
			input = "\"\\\\\\\\\\\\\\\\\\\\\"a";
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.STRING_LITERAL, 0, 12, 1, 1);
			checkNext(scanner, KW_a,12,1,1,13);
			checkNextIsEOF(scanner);
		}
		catch(LexicalException e){
			show(e);
			assertEquals(4, e.getPos());
		}
	}
	
	@Test
	public void test8() throws LexicalException {
		String input = " \" \\\" \" ";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
		} catch (LexicalException e) {
			show(e);
		}
	}


	@Test
	public void test9() throws LexicalException {
		String input = "\"\\\b\"";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.STRING_LITERAL, 0,4, 1, 1);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
		}
	}


	@Test
	public void test10() throws LexicalException {
		String input="";
		input = ":;\n://Suman/\r\n;";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, OP_COLON, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, OP_COLON, 3, 1, 2, 1);
		checkNext(scanner, SEMI, 14, 1, 3, 1);
		checkNextIsEOF(scanner);
	}

	@Test
	public void test11() throws LexicalException {
		String input = ";;\r\n[]():/ //";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, LSQUARE, 4, 1, 2, 1);
		checkNext(scanner, RSQUARE, 5, 1, 2, 2);
		checkNext(scanner, LPAREN, 6, 1, 2, 3);
		checkNext(scanner, RPAREN, 7, 1, 2, 4);
		checkNext(scanner, OP_COLON, 8, 1, 2, 5);
		checkNext(scanner, OP_DIV, 9, 1, 2, 6);
		checkNextIsEOF(scanner);
	}
	@Test
	public void test12() throws LexicalException {
		String input = "[()]= ==";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, LSQUARE, 0, 1, 1, 1);
		checkNext(scanner, LPAREN, 1, 1, 1, 2);
		checkNext(scanner, RPAREN, 2, 1,1, 3);
		checkNext(scanner, RSQUARE, 3, 1, 1, 4);
		checkNext(scanner, OP_ASSIGN, 4, 1, 1, 5);
		checkNext(scanner, OP_EQ, 6, 2, 1, 7);
		checkNextIsEOF(scanner);
		
	}
	
	@Test
	public void test13() throws LexicalException {
		String input = "+*,;==:-&@=";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,Kind.OP_PLUS , 0, 1, 1, 1);
		checkNext(scanner,Kind.OP_TIMES ,1, 1, 1,2);
		checkNext(scanner,Kind.COMMA , 2, 1, 1,3);
		checkNext(scanner,Kind.SEMI , 3, 1, 1,4);
		checkNext(scanner,Kind.OP_EQ , 4, 2, 1,5);
		checkNext(scanner,Kind.OP_COLON , 6, 1, 1,7);
		checkNext(scanner,Kind.OP_MINUS , 7, 1, 1,8);
		checkNext(scanner,Kind.OP_AND , 8, 1, 1,9);
		checkNext(scanner,Kind.OP_AT , 9, 1, 1,10);
		checkNext(scanner,Kind.OP_ASSIGN , 10, 1, 1,11);
		checkNextIsEOF(scanner);
	}

	@Test
	public void test14() throws LexicalException {
		String input = "\t==!=<=>=**-><-";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,Kind.OP_EQ , 1, 2, 1, 2);
		checkNext(scanner,Kind.OP_NEQ ,3, 2, 1,4);
		checkNext(scanner,Kind.OP_LE , 5, 2, 1,6);
		checkNext(scanner,Kind.OP_GE , 7, 2, 1,8);
		checkNext(scanner,Kind.OP_POWER , 9, 2, 1,10);
		checkNext(scanner,Kind.OP_RARROW , 11, 2, 1,12);
		checkNext(scanner,Kind.OP_LARROW , 13, 2, 1,14);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test15() throws LexicalException {
		String input = "0464\n\n 876543";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.INTEGER_LITERAL, 0, 1, 1, 1);
			checkNext(scanner, Kind.INTEGER_LITERAL, 1, 3, 1, 2);
			checkNext(scanner, Kind.INTEGER_LITERAL, 7, 6, 3, 2);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
		}
	}
	
	@Test
	public void test16() throws LexicalException {
		String input = "0\n1234566799999999999999999999999999999";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner); 
		} catch (LexicalException e) {
			show(e);
			assertEquals(39,e.getPos());
			
		}
	}
	
	
	
	@Test
	public void test17() throws LexicalException {
		String input = "0\n123abc000";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.INTEGER_LITERAL, 0, 1, 1, 1);
			checkNext(scanner, Kind.INTEGER_LITERAL, 2, 3, 2, 1);
			checkNext(scanner, Kind.IDENTIFIER, 5, 6, 2, 4);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void test18() throws LexicalException {
		String input = "DEF_Y+";
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner);
			checkNext(scanner, Kind.KW_DEF_Y, 0, 5, 1, 1);
			checkNext(scanner, Kind.OP_PLUS, 5, 1, 1, 6);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	
	@Test
	public void test19() throws LexicalException {
		String input = "\t\rraw ? true";
		System.out.println(input);
		try {
			Scanner scanner = new Scanner(input).scan();
			show(input);
			show(scanner); 
			
			checkNext(scanner, Kind.IDENTIFIER, 2,3, 2, 1);
			checkNext(scanner, Kind.OP_Q, 6,1, 2, 5);
			checkNext(scanner, Kind.BOOLEAN_LITERAL, 8,4, 2, 7);
			checkNextIsEOF(scanner);
		} catch (LexicalException e) {
			show(e);
			throw e;
		}
	}
	
		
	
	@Test
	public void test20() throws LexicalException {
		String input="";
		input = ";;\n://Awsef/\r\n12345;/!!==\n!=";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, OP_COLON, 3, 1, 2, 1);
		checkNext(scanner, INTEGER_LITERAL, 14, 5, 3, 1);
		checkNext(scanner, SEMI, 19, 1, 3, 6);
		checkNext(scanner, OP_DIV, 20, 1, 3, 7);
		checkNext(scanner, OP_EXCL, 21, 1, 3, 8);
		checkNext(scanner, OP_NEQ, 22, 2, 3, 9);
		checkNext(scanner, OP_ASSIGN, 24, 1, 3, 11);
		checkNext(scanner, OP_NEQ, 26, 2, 4, 1);
		checkNextIsEOF(scanner);
	}

	
	@Test
	public void test21() throws LexicalException {
		String input = ";;\n;12345;\n<= == = >= <- \n!==!*** ++ + @@ ? ::-/->";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, SEMI, 0, 1, 1, 1);
		checkNext(scanner, SEMI, 1, 1, 1, 2);
		checkNext(scanner, SEMI, 3, 1, 2, 1);
		checkNext(scanner, INTEGER_LITERAL, 4, 5, 2, 2);
		checkNext(scanner, SEMI, 9, 1, 2, 7);
		checkNext(scanner, OP_LE, 11, 2, 3, 1);
		checkNext(scanner, OP_EQ, 14, 2, 3, 4);
		checkNext(scanner, OP_ASSIGN, 17, 1, 3, 7);
		checkNext(scanner, OP_GE, 19, 2, 3, 9);
		checkNext(scanner, Kind.OP_LARROW, 22, 2, 3, 12);
		checkNext(scanner, OP_NEQ, 26, 2, 4, 1);
		checkNext(scanner, OP_ASSIGN, 28, 1, 4, 3);
		checkNext(scanner, OP_EXCL, 29, 1, 4, 4);
		checkNext(scanner, OP_POWER, 30, 2, 4, 5);
		checkNext(scanner, OP_TIMES, 32, 1, 4, 7);
		checkNext(scanner, OP_PLUS, 34, 1, 4, 9);
		checkNext(scanner, OP_PLUS, 35, 1, 4, 10);
		checkNext(scanner, OP_PLUS, 37, 1, 4, 12);
		checkNext(scanner, OP_AT, 39, 1, 4, 14);
		checkNext(scanner, OP_AT, 40, 1, 4, 15);
		checkNext(scanner, OP_Q, 42, 1, 4, 17);
		checkNext(scanner, OP_COLON, 44, 1, 4, 19);
		checkNext(scanner, OP_COLON, 45, 1, 4, 20);
		checkNext(scanner, OP_MINUS, 46, 1, 4, 21);
		checkNext(scanner, OP_DIV, 47, 1, 4, 22);
		checkNext(scanner, OP_RARROW, 48, 2, 4, 23);
		checkNextIsEOF(scanner);
	}
	
	@Test
	public void test22() throws LexicalException {
		String input="";
		input = "//abcd\n\"greeting@\\n\"  true  sinx xsin sin";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner,STRING_LITERAL,7,13,2,1);
		checkNext(scanner, BOOLEAN_LITERAL, 22, 4, 2, 16);
		checkNext(scanner, IDENTIFIER, 28, 4, 2, 22);
		checkNext(scanner, IDENTIFIER, 33, 4, 2, 27);
		checkNext(scanner, KW_sin, 38, 3, 2, 32);
		checkNextIsEOF(scanner);
	}
	
	
	
	@Test
	public void test23() throws LexicalException {
		String input="";
		input = "  a\t\fb";
		Scanner scanner = new Scanner(input).scan();
		show(input);
		show(scanner);
		checkNext(scanner, KW_a, 2, 1, 1, 3);
		checkNext(scanner, IDENTIFIER, 5, 1, 1, 6);
		}
}
