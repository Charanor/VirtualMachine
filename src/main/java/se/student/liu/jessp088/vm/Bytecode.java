package se.student.liu.jessp088.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import se.student.liu.jessp088.vm.instructions.Instruction;

/**
 * Class that holds all {@link Instruction}s to be executed. It also keeps track of what line each instruction is located at.
 */
public class Bytecode
{
	private final List<Instruction> instructions;
	private final Map<Integer, Integer> ptrToLine;
	private int ptr;

	public Bytecode(final List<Instruction> instructions, final Map<Integer, Integer> ptrToLine) {
		this.instructions = new ArrayList<>(instructions);
		this.ptrToLine = new HashMap<>(ptrToLine);
	}

	/**
	 * @return if there are more instructions to be processed.
	 */
	public boolean hasNext() {
		return ptr < instructions.size();
	}

	/**
	 * @return the next instruction to execute
	 * @throws NoSuchElementException if there are no more instructions.
	 */
	public Instruction next() throws NoSuchElementException {
		if (!hasNext()) throw new NoSuchElementException("End of bytecode");
		return instructions.get(ptr++);
	}

	/**
	 * @return the int pointer to the current instruction
	 */
	public int getPtr() {
		return ptr;
	}

	/**
	 * Sets the pointer (current executing instruction). Used by Jump instructions.
	 *
	 * @param ptr the pointer
	 */
	public void setPtr(final int ptr) {
		this.ptr = ptr;
	}

	/**
	 * @return the line number for the currently eecuting instruction
	 */
	public int getLineNumber() {
		return toLineNumber(ptr);
	}

	/**
	 * Converts the pointer to the line number for the instruction.
	 *
	 * @param ptr the pointer
	 *
	 * @return the line number
	 */
	public int toLineNumber(final int ptr) {
		final Integer val = ptrToLine.get(ptr);
		if (val == null) throw new IllegalArgumentException("No line number for instruction pointer " + ptr);
		return val;
	}

	/**
	 * Converts a line number to the pointer for the instruction.
	 *
	 * @param lineNumber the line number
	 *
	 * @return the pointer
	 */
	public int toPointer(final int lineNumber) {
		for (final Entry<Integer, Integer> entry : ptrToLine.entrySet()) {
			if (entry.getValue() == lineNumber) return entry.getKey();
		}
		throw new IllegalArgumentException("No pointer for line number " + lineNumber);
	}

	/**
	 * @return string representation of the list of instructions
	 */
	@Override
	public String toString() {
		return instructions.toString();
	}
}
