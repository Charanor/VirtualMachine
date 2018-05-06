package se.student.liu.jessp088.vm.instructions.data;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Swap the top two values of the stack.
 */
public class Swap extends Instruction
{
	@Override
	protected void process() throws InstructionException {
		final int shallow = pop();
		final int deep = pop();
		push(shallow);
		push(deep);
	}
}
