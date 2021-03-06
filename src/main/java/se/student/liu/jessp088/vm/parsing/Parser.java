package se.student.liu.jessp088.vm.parsing;

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
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import se.student.liu.jessp088.vm.Bytecode;
import se.student.liu.jessp088.vm.VMInstruction;
import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.parsing.suppliers.DefaultInstructionSupplier;
import se.student.liu.jessp088.vm.parsing.suppliers.InstructionSupplier;
import se.student.liu.jessp088.vm.parsing.util.ForwardDeclaration;

/**
 * A parser that takes a list of {@link Token} and generates a {@link Bytecode} object that a
 * {@link se.student.liu.jessp088.vm.VirtualMachine} can process.
 */
public class Parser
{
	private static final int INVALID_INDEX = -1;

	private Deque<Token> tokens;
	private Token next;

	private Map<String, Integer> labels;
	private List<ForwardDeclaration> forwardDeclarations;

	private Map<String, Integer> definitions;

	private List<Instruction> instructions;
	private int instructionPtr;

	private Map<Integer, Integer> ptrToLine;
	private int lineNumber;

	private final InstructionSupplier supplier;

	public Parser() {
		this(new DefaultInstructionSupplier());
	}

	public Parser(final InstructionSupplier instructionSupplier) {
		this.supplier = instructionSupplier;
		this.labels = new HashMap<>();
		this.definitions = new HashMap<>();
		this.tokens = new LinkedList<>();
		this.instructions = new ArrayList<>();
		this.forwardDeclarations = new ArrayList<>();
		this.ptrToLine = new HashMap<>();
		this.next = null;
	}

	public Bytecode parse(final List<Token> input) throws ParserException {
		labels.clear();
		definitions.clear();
		tokens.clear();
		tokens.addAll(input);
		instructions.clear();
		forwardDeclarations.clear();
		ptrToLine.clear();

		instructionPtr = 0;
		lineNumber = 1; // Line numbers start at 1
		next();

		compilationUnit();

		return new Bytecode(instructions, ptrToLine);
	}

	private void compilationUnit() throws ParserException {
		definitions();
		expressions();

		ensureNextType(EOF);
	}

	private void definitions() throws ParserException {
		while (nextTypeIs(DEFTAG, NEXTOP)) {
			if (nextTypeIs(NEXTOP)) {
				next();
				continue;
			}
			definition();
			next();
		}
	}

	private void definition() throws ParserException {
		// DEFTAG DEFINE IDENTIFIER EQUALS NUMBER NEXTOP
		next(DEFINE); // DEFINE IDENTIFIER EQUALS NUMBER NEXTOP
		next(IDENTIFIER); // IDENTIFIER EQUALS NUMBER NEXTOP
		final String name = next.value;
		next(EQUALS); // EQUALS NUMBER NEXTOP
		next(NUMBER); // NUMBER NEXTOP
		final int value = Integer.decode(next.value);
		next(NEXTOP); // NEXTOP

		defineNew(name, value);
	}

	private void expressions() throws ParserException {
		while (nextTypeIs(IDENTIFIER, LEFTBRACKET, NEXTOP)) {
			if (nextTypeIs(NEXTOP)) {
				next();
				continue;
			}
			expression();
			next();
		}
	}

	private void expression() throws ParserException {
		if (nextTypeIs(IDENTIFIER)) {
			instruction();
			ensureNextType(NEXTOP);
		} else {
			label();
			next(NEXTOP);
		}
	}

	private void instruction() throws ParserException {
		// IDENTIFIER (IDENTIFIER | NUMBER | variable)*
		final VMInstruction instruction = VMInstruction.valueOf(next.value.toUpperCase());
		final List<Float> args = new ArrayList<>();
		next(); // (IDENTIFIER | NUMBER | variable)*
		while (nextTypeIs(IDENTIFIER, NUMBER, DEFTAG)) {
			if (nextTypeIs(IDENTIFIER)) {
				final String label = next.value;
				if (!labels.containsKey(label)) {
					forwardDeclarations.add(new ForwardDeclaration(instruction, instructionPtr, label));
				}
				final float instructionIdx = labels.getOrDefault(label, Integer.valueOf(INVALID_INDEX));
				args.add(Float.valueOf(instructionIdx));

			} else if (nextTypeIs(NUMBER)) {
				final float number = Integer.decode(next.value);
				args.add(Float.valueOf(number));
			} else {
				final float value = taggedId();
				args.add(Float.valueOf(value));
			}
			next();
		}

		final int[] array = new int[args.size()];
		for (int i = 0; i < array.length; i++) { array[i] = args.get(i).intValue(); }
		addInstruction(instruction, array);
		instructionProcessed();
	}

	private void label() throws ParserException {
		// LEFTBRACKET IDENTIFIER RIGHTBRACKET
		ensureNextType(LEFTBRACKET);
		next(IDENTIFIER); // IDENTIFIER RIGHTBRACKET
		final String label = next.value;
		next(RIGHTBRACKET); // RIGHTBRACKET
		createLabel(label);
		replaceForwardDeclarations(label);
	}

	private int taggedId() throws ParserException {
		// defRef
		return defRef();
	}

	private int defRef() throws ParserException {
		// DEFTAG IDENTIFIER
		next(IDENTIFIER);
		final String name = next.value;
		return getConstant(name);
	}

	private void replaceForwardDeclarations(final String forwardDeclaration) {
		// We must sort forward declarations to make sure that the
		// forward declarations are inserted in the correct order.
		Collections.sort(forwardDeclarations);
		final Iterator<ForwardDeclaration> it = forwardDeclarations.iterator();
		while (it.hasNext()) {
			final ForwardDeclaration fw = it.next();
			if (fw.getForwardDeclaration().equals(forwardDeclaration)) {
				// Matching forward declaration, replace instruction
				final VMInstruction type = fw.getInstructionType();
				final Instruction instruction = supplier.getInstruction(type, instructionPtr);
				instructions.set(fw.getInstrunctionPos(), instruction);
				it.remove();
			}
		}
	}

	private void addInstruction(final VMInstruction instruction, final int... args) throws ParserException {
		if (args.length != instruction.numArguments) {
			final String format = "Invalid number of arguments for instruction %s on line %s. Expected %s got %s";
			throw new ParserException(format, instruction, Integer.valueOf(lineNumber),
									  Integer.valueOf(instruction.numArguments), Integer.valueOf(args.length));
		}
		try {
			instructions.add(supplier.getInstruction(instruction, args));
		} catch (final IllegalArgumentException e) {
			// We catch it to re-throw to a more appropriate exception
			throw new ParserException("Error fetching instruction " + instruction, e);
		}
	}

	private void instructionProcessed() {
		ptrToLine.put(Integer.valueOf(instructionPtr), Integer.valueOf(lineNumber));
		instructionPtr++;
	}

	private void createLabel(final String label) throws ParserException {
		if (labels.containsKey(label)) throw new ParserException("Label %s on line %s already defined!", label,
																 Integer.valueOf(lineNumber));
		labels.put(label, Integer.valueOf(instructionPtr));
	}

	private void defineNew(final String name, final int value) throws ParserException {
		if (definitions.containsKey(name))
			throw new ParserException("Constant %s on line %s already defined!", name, Integer.valueOf(lineNumber));
		definitions.put(name, Integer.valueOf(value));
	}

	private int getConstant(final String name) throws ParserException {
		if (!definitions.containsKey(name))
			throw new ParserException("Constant %s on line %s has not been defined!", name, Integer.valueOf(lineNumber));
		return definitions.get(name);
	}

	/**
	 * Gets the next token of the token stream.
	 */
	private void next() {
		if (next != null && next.type == NEXTOP) lineNumber++;
		try {
			next = tokens.pop();
		} catch (final NoSuchElementException ignored) {
			System.err.println("Possible error in parsing - next is null. Inserting EOF token.");
			next = new Token(EOF, null);
		}
	}

	/**
	 * Gets the next token of the token stream and ensures its type.
	 *
	 * @param expectedToken the expected token to find.
	 *
	 * @throws ParserException if the expected token was not found
	 */
	private void next(final TokenType expectedToken) throws ParserException {
		next();
		ensureNextType(expectedToken);
	}

	/**
	 * Ensures that the token is a specific type.
	 *
	 * @param t the type
	 *
	 * @throws ParserException if the next token is not that type
	 */
	private void ensureNextType(final TokenType t) throws ParserException {
		if (next == null) throw new ParserException("Unexpected EOF! Expected token %s", t);
		if (!nextTypeIs(t))
			throw new ParserException("Expected token on line %s. Expected %s got %s", Integer.valueOf(lineNumber), t, next.type);
	}

	/**
	 * @param types the types to compare to
	 *
	 * @return if the next token is one of the supplied types
	 */
	private boolean nextTypeIs(final TokenType... types) {
		if (next == null) return false;
		for (final TokenType t : types) { if (next.type == t) return true; }
		return false;
	}
}
