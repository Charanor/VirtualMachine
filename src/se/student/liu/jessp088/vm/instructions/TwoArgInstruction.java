package se.student.liu.jessp088.vm.instructions;

public abstract class TwoArgInstruction extends Instruction {
	protected final int left;
	protected final int right;

	public TwoArgInstruction(final int left, final int right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public String toString() {
		return super.toString() + " " + left + " " + right;
	}
}
