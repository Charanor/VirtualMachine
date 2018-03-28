package se.student.liu.jessp088.vm.parsing.suppliers;

import se.student.liu.jessp088.vm.VMInstruction;
import se.student.liu.jessp088.vm.instructions.Instruction;

public interface InstructionSupplier {
	Instruction getInstruction(VMInstruction instruction, int... args)
			throws IllegalArgumentException;
}
