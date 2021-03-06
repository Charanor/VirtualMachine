package se.student.liu.jessp088.vm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import se.student.liu.jessp088.vm.parsing.Lexer;
import se.student.liu.jessp088.vm.parsing.Parser;
import se.student.liu.jessp088.vm.parsing.Token;
import se.student.liu.jessp088.vm.parsing.LexerException;
import se.student.liu.jessp088.vm.parsing.ParserException;

/**
 * Tests the example file shipped with the program.
 */
public class ExampleFileTest
{
	private static final Path RESOURCE_DIRECTORY = Paths.get("src", "test", "resources");

	@Test
	public void test() {
		final Lexer lexer = new Lexer();
		final Parser parser = new Parser();

		try {
			final String code = new String(Files.readAllBytes(RESOURCE_DIRECTORY.resolve("count_primes.vm")));
			final List<Token> tokens = lexer.tokenize(code);
			final Bytecode result = parser.parse(tokens);
			final VirtualMachine vm = new DefaultVirtualMachine(16, 1);
			vm.execute(result);
			assertEquals(vm.getError(), VMState.END_SUCCESS, vm.getState());
			assertEquals(168, vm.getStack().peek()); // 168 primes
		} catch (IOException | LexerException | ParserException e) {
			fail(e.toString());
		}
	}
}
