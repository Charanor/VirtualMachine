package se.student.liu.jessp088.vm.instructions.data;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.InstructionException;

public class Discard extends Instruction {
	@Override
	protected void process() throws InstructionException {
		pop();
	}
}
