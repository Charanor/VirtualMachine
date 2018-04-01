package se.student.liu.jessp088.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import se.student.liu.jessp088.vm.instructions.Instruction;

public class Bytecode {
	private final List<Instruction> instructions;
	private final Map<Integer, Integer> ptrToLine;
	private int ptr;

	public Bytecode(final List<Instruction> instructions, final Map<Integer, Integer> ptrToLine) {
		this.instructions = new ArrayList<>(instructions);
		this.ptrToLine = new HashMap<>(ptrToLine);
	}

	public boolean hasNext() {
		return ptr < instructions.size();
	}

	public Instruction next() {
		if (!hasNext()) throw new IllegalStateException("End of bytecode");
		return instructions.get(ptr++);
	}

	public int getPtr() {
		return ptr;
	}

	public void setPtr(final int ptr) {
		this.ptr = ptr;
	}

	public int getLineNumber() {
		return toLineNumber(getPtr());
	}

	public int toLineNumber(final int ptr) {
		final Integer val = ptrToLine.get(ptr);
		if (val == null)
			throw new IllegalArgumentException("No line number for instruction pointer " + ptr);
		return val;
	}

	public int toPointer(final int lineNumber) {
		for (final Entry<Integer, Integer> entry : ptrToLine.entrySet()) {
			if (entry.getValue() == lineNumber) return entry.getKey();
		}
		throw new IllegalArgumentException("No pointer for line number " + lineNumber);
	}
}
