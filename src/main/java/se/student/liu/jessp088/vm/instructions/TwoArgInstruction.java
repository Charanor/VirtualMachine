package se.student.liu.jessp088.vm.instructions;

public abstract class TwoArgInstruction extends Instruction {
	protected final int left;
	protected final int right;

	protected TwoArgInstruction(final int left, final int right) {
		this.left = left;
		this.right = right;
	}

	public int getArg1() {
		return left;
	}

	public int getArg2() {
		return right;
	}

	@Override
	public String toString() {
		return super.toString() + " " + left + " " + right;
	}
}
