package se.student.liu.jessp088.vm.instructions.data;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Duplicates the top value of the stack.
 */
public class Dup extends Instruction
{
	@Override
	protected void process() throws InstructionException {
		push(peek());
	}
}
