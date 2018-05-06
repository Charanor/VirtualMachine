package se.student.liu.jessp088.vm.instructions.data;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Discards the top value of the stack.
 */
public class Discard extends Instruction
{
	@Override
	protected void process() throws InstructionException {
		pop();
	}
}
