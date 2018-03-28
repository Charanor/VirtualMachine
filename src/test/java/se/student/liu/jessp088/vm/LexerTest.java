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
import se.student.liu.jessp088.vm.parsing.exceptions.LexerException;

public class LexerTest {
	private static Lexer lexer;

	@BeforeClass
	public static void setUp() throws Exception {
		lexer = new Lexer();
	}

	@Test
	public void testAllTokens() {
		final StringBuilder builder = new StringBuilder();
		builder.append(" \t");
		builder.append("//asdasd");
		builder.append("\n");
		builder.append("#");
		builder.append("define");
		builder.append("=");
		builder.append("-0.51");
		builder.append("[");
		builder.append("]");
		builder.append("someText");
		builder.append(" "); // Separate text and number
		// Implicit EOF

		int idx = 0;
		final List<Token> tokens = lexer.tokenizeRaw(builder.toString());
		assertEquals(tokens.get(idx++).type, TokenType.WHITESPACE);
		assertEquals(tokens.get(idx++).type, TokenType.COMMENT);
		assertEquals(tokens.get(idx++).type, TokenType.NEXTOP);
		assertEquals(tokens.get(idx++).type, TokenType.DEFTAG);
		assertEquals(tokens.get(idx++).type, TokenType.DEFINE);
		assertEquals(tokens.get(idx++).type, TokenType.EQUALS);
		assertEquals(tokens.get(idx++).type, TokenType.NUMBER);
		assertEquals(tokens.get(idx++).type, TokenType.LEFTBRACKET);
		assertEquals(tokens.get(idx++).type, TokenType.RIGHTBRACKET);
		assertEquals(tokens.get(idx++).type, TokenType.IDENTIFIER);
		assertEquals(tokens.get(idx++).type, TokenType.WHITESPACE);
		assertEquals(tokens.get(tokens.size() - 1).type, TokenType.EOF);
	}

	@Test
	public void testTokensProperlyIgnored() {
		final StringBuilder builder = new StringBuilder();
		builder.append("hello  world 		2018 //Wow, some guy you are!");
		List<Token> tokens = null;
		try {
			tokens = lexer.tokenize(builder.toString());
		} catch (final LexerException e) {
			fail("Lexing of code " + builder.toString() + " failed! Reason: " + e.getMessage());
		}
		if (tokens != null) tokens.forEach(t -> assertFalse(t.type.ignore));
	}

}
