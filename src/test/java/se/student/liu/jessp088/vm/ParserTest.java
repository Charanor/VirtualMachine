package se.student.liu.jessp088.vm;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.student.liu.jessp088.vm.parsing.TokenType.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.student.liu.jessp088.vm.instructions.Literal;
import se.student.liu.jessp088.vm.instructions.control.Jmp;
import se.student.liu.jessp088.vm.instructions.data.Load;
import se.student.liu.jessp088.vm.parsing.Parser;
import se.student.liu.jessp088.vm.parsing.Token;
import se.student.liu.jessp088.vm.parsing.TokenType;
import se.student.liu.jessp088.vm.parsing.exceptions.ParserException;
import se.student.liu.jessp088.vm.parsing.suppliers.ReflectionSupplier;

public class ParserTest {
	private Parser parser;

	@Before
	public void setUp() {
		parser = new Parser(new ReflectionSupplier());
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
		tokens.add(next());
		tokens.add(next());
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

		Bytecode code;
		try {
			code = parser.parse(tokens);
		} catch (final ParserException e) {
			fail("Parsing of tokens " + tokens + " failed! Reason: " + e);
			e.printStackTrace();
			return;
		}

		assertTrue(code.next() instanceof Literal);
		assertTrue(code.next() instanceof Load);
		assertTrue(code.next() instanceof Jmp);
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
