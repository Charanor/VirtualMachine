package se.student.liu.jessp088.vm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.parsing.Lexer;
import se.student.liu.jessp088.vm.parsing.Parser;
import se.student.liu.jessp088.vm.parsing.Token;
import se.student.liu.jessp088.vm.parsing.exceptions.LexerException;
import se.student.liu.jessp088.vm.parsing.exceptions.ParserException;

public class TestExampleFile {
	private static final Path RESOURCE_DIRECTORY = Paths.get("src", "test", "resources");

	@Test
	public void test() {
		final Lexer lexer = new Lexer();
		final Parser parser = new Parser();
		final VirtualMachine vm = new VirtualMachine(16, 1);
		VirtualMachine.DEBUG = true;

		try {
			final String code = new String(
					Files.readAllBytes(RESOURCE_DIRECTORY.resolve("count_primes.vm")));
			final List<Token> tokens = lexer.tokenize(code);
			final List<Instruction> instructions = parser.parse(tokens);
			final boolean success = vm.execute(new Bytecode(instructions));
			assertTrue(vm.getError(), success);
			assertEquals(vm.getStack().pop(), 25); // 25 primes
		} catch (IOException | LexerException | ParserException e) {
			fail(e.toString());
		}
	}
}
