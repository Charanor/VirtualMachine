package se.student.liu.jessp088.vm.instructions.arithmetic;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

/**
 * Push product of first two values in stack.
 */
public class Mul extends Instruction
{
	@Override
	public void process() throws InstructionException {
		final int right = pop();
		final int left = pop();
		push(left * right);
	}
}
