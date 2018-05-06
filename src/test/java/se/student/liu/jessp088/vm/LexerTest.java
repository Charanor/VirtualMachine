package se.student.liu.jessp088.vm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import se.student.liu.jessp088.vm.parsing.Lexer;
import se.student.liu.jessp088.vm.parsing.Token;
import se.student.liu.jessp088.vm.parsing.TokenType;
import se.student.liu.jessp088.vm.parsing.LexerException;

/**
 * Tests the {@link Lexer} class.
 */
public class LexerTest
{
	private static Lexer lexer = null;

	@BeforeClass
	public static void setUp() {
		lexer = new Lexer();
	}

	@Test
	public void testAllTokens() {
		final StringBuilder builder = new StringBuilder();
		builder.append(" \t");
		builder.append("//asdasd");
		builder.append("\r\n");
		builder.append("#");
		builder.append("define");
		builder.append("=");
		builder.append("-0.51");
		builder.append("[");
		builder.append("]");
		builder.append("someText");
		builder.append(" "); // Separate text and number
		// Implicit EOF

		try {
			int idx = 0;
			final List<Token> tokens = lexer.tokenize(builder.toString());
			assertEquals("Expected WHITESPACE got " + tokens.get(idx), TokenType.WHITESPACE, tokens.get(idx++).type);
			assertEquals("Expected COMMENT got " + tokens.get(idx), TokenType.COMMENT, tokens.get(idx++).type);
			assertEquals("Expected NEXTOP got " + tokens.get(idx), TokenType.NEXTOP, tokens.get(idx++).type);
			assertEquals("Expected DEFTAG got " + tokens.get(idx), TokenType.DEFTAG, tokens.get(idx++).type);
			assertEquals("Expected DEFINE got " + tokens.get(idx), TokenType.DEFINE, tokens.get(idx++).type);
			assertEquals("Expected EQUALS got " + tokens.get(idx), TokenType.EQUALS, tokens.get(idx++).type);
			assertEquals("Expected NUMBER got " + tokens.get(idx), TokenType.NUMBER, tokens.get(idx++).type);
			assertEquals("Expected LEFTBRACKET got " + tokens.get(idx), TokenType.LEFTBRACKET, tokens.get(idx++).type);
			assertEquals("Expected RIGHTBRACKET got " + tokens.get(idx), TokenType.RIGHTBRACKET, tokens.get(idx++).type);
			assertEquals("Expected IDENTIFIER got " + tokens.get(idx), TokenType.IDENTIFIER, tokens.get(idx++).type);
			assertEquals("Expected WHITESPACE got " + tokens.get(idx), TokenType.WHITESPACE, tokens.get(idx++).type);
			assertEquals("Expected EOF got " + tokens.get(idx), TokenType.EOF, tokens.get(tokens.size() - 1).type);
		} catch (LexerException e) {fail(e.toString());}
	}

	@Test
	public void testTokensProperlyIgnored() {
		final StringBuilder builder = new StringBuilder();
		builder.append("hello  world 		2018 //Wow, some guy you are!");
		List<Token> tokens = null;
		try {
			tokens = lexer.tokenize(builder.toString());
		} catch (final LexerException e) {
			fail("Lexing of code " + builder + " failed! Reason: " + e.getMessage());
		}
		if (tokens != null) tokens.forEach(t -> assertFalse(t.type.ignore));
	}

}
