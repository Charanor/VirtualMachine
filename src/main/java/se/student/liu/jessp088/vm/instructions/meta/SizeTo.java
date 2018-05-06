package se.student.liu.jessp088.vm.instructions.meta;

import se.student.liu.jessp088.vm.instructions.OneArgInstruction;

/**
 * Sets the size of the stack to an absolute value.
 */
public class SizeTo extends OneArgInstruction
{
	public SizeTo(final int arg) {
		super(arg);
	}

	@Override
	protected void process() {
		sizeStackTo(arg);
	}
}
