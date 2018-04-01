package se.student.liu.jessp088.vm.instructions;

import se.student.liu.jessp088.vm.Bytecode;
import se.student.liu.jessp088.vm.Stack;
import se.student.liu.jessp088.vm.Variables;
import se.student.liu.jessp088.vm.VirtualMachine;

/** An instruction that can be processed by a {@link VirtualMachine}.
 *
 * @author Charanor */
public abstract class Instruction {
	private Stack stack;
	private Bytecode bytecode;
	private Variables variables;

	/** Processes this instruction.
	 *
	 * @param stack
	 *            the stack to process on
	 * @param bytecode
	 *            the code to process on
	 * @param variables
	 *            the variables to process on
	 * @throws InstructionException
	 *             if this instruction could not properly process. */
	public void process(final Stack stack, final Bytecode bytecode, final Variables variables)
			throws InstructionException {
		this.stack = stack;
		this.bytecode = bytecode;
		this.variables = variables;

		this.process();
	}

	protected abstract void process() throws InstructionException;

	protected void push(final int value) throws InstructionException {
		try {
			stack.push(value);
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	protected int pop() throws InstructionException {
		try {
			return stack.pop();
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	protected int peek() throws InstructionException {
		try {
			return stack.peek();
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	protected int deepPeek() throws InstructionException {
		try {
			return stack.deepPeek();
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	protected void expandStack(final int size) {
		stack.expand(size);
	}

	protected void shrinkStack(final int size) {
		stack.shrink(size);
	}

	protected void sizeStackTo(final int size) {
		stack.sizeTo(size);
	}

	protected void storeVariable(final int idx, final int val) throws InstructionException {
		try {
			variables.store(idx, val);
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	protected int loadVariable(final int idx) throws InstructionException {
		try {
			return variables.load(idx);
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	protected int getPtr() {
		return bytecode.getPtr();
	}

	protected void setPtr(final int ptr) {
		bytecode.setPtr(ptr);
	}

	private InstructionException error(final Throwable cause) throws InstructionException {
		return new InstructionException("Error executing instruction %s. Cause: %s",
				this.getClass().getSimpleName(), cause);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().toUpperCase();
	}
}
