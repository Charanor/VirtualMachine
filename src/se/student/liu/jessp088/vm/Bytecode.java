package se.student.liu.jessp088.vm;

import java.util.ArrayList;
import java.util.List;

import se.student.liu.jessp088.vm.instructions.Instruction;

public class Bytecode {
	private final List<Instruction> instructions;
	private int ptr;

	public Bytecode(final List<Instruction> instructions) {
		this.instructions = new ArrayList<>(instructions);
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
}
