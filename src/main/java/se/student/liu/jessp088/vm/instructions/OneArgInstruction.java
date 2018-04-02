package se.student.liu.jessp088.vm.instructions;

public abstract class OneArgInstruction extends Instruction {
	protected final int arg;

	protected OneArgInstruction(final int arg) {
		this.arg = arg;
	}

	@Override
	public String toString() {
		return super.toString() + " " + arg;
	}
}
