package se.student.liu.jessp088.vm.instructions.control;

/**
 * Jumps to a specific instruction pointer.
 */
public class Jmp extends JumpInstruction
{

	public Jmp(final int arg) {
		super(arg);
	}

	@Override
	protected boolean shouldJump() {
		return true;
	}
}
