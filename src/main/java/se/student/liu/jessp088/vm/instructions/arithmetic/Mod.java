package se.student.liu.jessp088.vm.instructions.arithmetic;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Push remainder of first two values in stack.
 */
public class Mod extends Instruction
{
	@Override
	protected void process() throws InstructionException {
		final int right = pop();
		final int left = pop();
		push(left % right);
	}
}
