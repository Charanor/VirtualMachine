package se.student.liu.jessp088.vm;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.student.liu.jessp088.vm.parsing.TokenType.DEFINE;
import static se.student.liu.jessp088.vm.parsing.TokenType.DEFTAG;
import static se.student.liu.jessp088.vm.parsing.TokenType.EOF;
import static se.student.liu.jessp088.vm.parsing.TokenType.EQUALS;
import static se.student.liu.jessp088.vm.parsing.TokenType.IDENTIFIER;
import static se.student.liu.jessp088.vm.parsing.TokenType.LEFTBRACKET;
import static se.student.liu.jessp088.vm.parsing.TokenType.NEXTOP;
import static se.student.liu.jessp088.vm.parsing.TokenType.NUMBER;
import static se.student.liu.jessp088.vm.parsing.TokenType.RIGHTBRACKET;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.Literal;
import se.student.liu.jessp088.vm.instructions.control.Jmp;
import se.student.liu.jessp088.vm.instructions.data.Load;
import se.student.liu.jessp088.vm.parsing.Parser;
import se.student.liu.jessp088.vm.parsing.Token;
import se.student.liu.jessp088.vm.parsing.TokenType;
import se.student.liu.jessp088.vm.parsing.exceptions.ParserException;

public class ParserTest {
	private Parser parser;

	@Before
	public void setUp() throws Exception {
		parser = new Parser();
	}

	@Test
	public void test() {
		final List<Token> tokens = new ArrayList<>();
		tokens.add(token(DEFTAG, "#"));
		tokens.add(token(DEFINE, "define"));
		tokens.add(token(IDENTIFIER, "i"));
		tokens.add(token(EQUALS, "="));
		tokens.add(token(NUMBER, "0x03"));
		tokens.add(next());

		tokens.add(token(IDENTIFIER, "LITERAL"));
		tokens.add(token(NUMBER, "5"));
		tokens.add(next());

		tokens.add(token(IDENTIFIER, "LOAD"));
		tokens.add(token(DEFTAG, "#"));
		tokens.add(token(IDENTIFIER, "i"));
		tokens.add(next());

		tokens.add(left());
		tokens.add(token(IDENTIFIER, "label"));
		tokens.add(right());
		tokens.add(next());

		tokens.add(token(IDENTIFIER, "JMP"));
		tokens.add(token(IDENTIFIER, "label"));
		tokens.add(next());
		tokens.add(eof());

		List<Instruction> instructions = null;
		try {
			instructions = parser.parse(tokens);
		} catch (final ParserException e) {
			fail("Parsing of tokens " + tokens + " failed! Reason: " + e.getMessage());
			return;
		}

		int idx = 0;
		assertTrue(instructions.get(idx++) instanceof Literal);
		assertTrue(instructions.get(idx++) instanceof Load);
		assertTrue(instructions.get(idx++) instanceof Jmp);
	}

	private static Token token(final TokenType t, final String v) {
		return new Token(t, v);
	}

	private static Token left() {
		return token(LEFTBRACKET, "[");
	}

	private static Token right() {
		return token(RIGHTBRACKET, "]");
	}

	private static Token next() {
		return token(NEXTOP, "\n");
	}

	private static Token eof() {
		return token(EOF, "");
	}
}
