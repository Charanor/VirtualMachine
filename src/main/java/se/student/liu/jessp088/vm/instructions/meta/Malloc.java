package se.student.liu.jessp088.vm.instructions.meta;

import se.student.liu.jessp088.vm.instructions.OneArgInstruction;

/**
 * Expands the stack.
 */
public class Malloc extends OneArgInstruction
{
	public Malloc(final int arg) {
		super(arg);
	}

	@Override
	protected void process() {
		expandStack(arg);
	}
}
