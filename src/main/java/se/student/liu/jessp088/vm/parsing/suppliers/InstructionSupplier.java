package se.student.liu.jessp088.vm.parsing.suppliers;

import se.student.liu.jessp088.vm.VMInstruction;
import se.student.liu.jessp088.vm.instructions.Instruction;

/**
 * Supplies instructions to a {@link se.student.liu.jessp088.vm.parsing.Parser}.
 */
public interface InstructionSupplier
{
	/**
	 * Fetches an instruction.
	 *
	 * @param instruction the type of instruction to fetch
	 * @param args        the arguments to the instruction
	 *
	 * @return the instruction
	 * @throws IllegalArgumentException if an instruction could not be fetched (wrong number of arguments etc.)
	 */
	Instruction getInstruction(VMInstruction instruction, int... args) throws IllegalArgumentException;
}
