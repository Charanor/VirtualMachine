package se.student.liu.jessp088.vm.instructions.control;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

public class Cmp extends Instruction {
	@Override
	public void process() throws InstructionException {
		final int right = pop();
		final int left = pop();
		push(left - right);
	}
}
