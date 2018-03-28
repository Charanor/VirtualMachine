package se.student.liu.jessp088.vm.instructions.meta;

import se.student.liu.jessp088.vm.instructions.InstructionException;
import se.student.liu.jessp088.vm.instructions.OneArgInstruction;

public class Free extends OneArgInstruction {

	public Free(final int arg) {
		super(arg);
	}

	@Override
	protected void process() throws InstructionException {
		shrinkStack(arg);
	}
}
