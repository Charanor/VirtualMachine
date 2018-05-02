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

		int idx = 0;
		final List<Token> tokens = lexer.tokenizeRaw(builder.toString());
		assertEquals(TokenType.WHITESPACE, tokens.get(idx++).type);
		assertEquals(TokenType.COMMENT, tokens.get(idx++).type);
		assertEquals(TokenType.NEXTOP, tokens.get(idx++).type);
		assertEquals(TokenType.DEFTAG, tokens.get(idx++).type);
		assertEquals(TokenType.DEFINE, tokens.get(idx++).type);
		assertEquals(TokenType.EQUALS, tokens.get(idx++).type);
		assertEquals(TokenType.NUMBER, tokens.get(idx++).type);
		assertEquals(TokenType.LEFTBRACKET, tokens.get(idx++).type);
		assertEquals(TokenType.RIGHTBRACKET, tokens.get(idx++).type);
		assertEquals(TokenType.IDENTIFIER, tokens.get(idx++).type);
		assertEquals(TokenType.WHITESPACE, tokens.get(idx++).type);
		assertEquals(TokenType.EOF, tokens.get(tokens.size() - 1).type);
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
