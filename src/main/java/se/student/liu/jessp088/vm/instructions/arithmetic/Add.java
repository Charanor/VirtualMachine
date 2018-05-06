package se.student.liu.jessp088.vm.instructions.arithmetic;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Pushes sum of first 2 values in the stack.
 */
public class Add extends Instruction
{
	@Override
	public void process() throws InstructionException {
		final int right = pop();
		final int left = pop();
		push(left + right);
	}
}
