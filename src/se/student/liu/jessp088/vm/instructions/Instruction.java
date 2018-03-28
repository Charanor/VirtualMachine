package se.student.liu.jessp088.vm.instructions;

import se.student.liu.jessp088.vm.Bytecode;
import se.student.liu.jessp088.vm.Stack;
import se.student.liu.jessp088.vm.Variables;
import se.student.liu.jessp088.vm.exceptions.StackException;

public abstract class Instruction {
	private Stack stack;
	private Bytecode bytecode;
	private Variables variables;

	public void process(final Stack stack, final Bytecode bytecode, final Variables variables)
			throws InstructionException {
		this.stack = stack;
		this.bytecode = bytecode;
		this.variables = variables;

		this.process();
	}

	protected abstract void process() throws InstructionException;

	public void push(final int value) throws InstructionException {
		try {
			stack.push(value);
		} catch (final StackException e) {
			throw error(e);
		}
	}

	public int pop() throws InstructionException {
		try {
			return stack.pop();
		} catch (final StackException e) {
			throw error(e);
		}
	}

	public int peek() throws InstructionException {
		try {
			final int value = stack.pop();
			stack.push(value);
			return value;
		} catch (final StackException e) {
			throw error(e);
		}
	}

	public int deepPeek() throws InstructionException {
		try {
			final int inTheWay = stack.pop();
			final int value = stack.pop();
			stack.push(value);
			stack.push(inTheWay);
			return value;
		} catch (final StackException e) {
			throw error(e);
		}
	}

	public void expandStack(final int size) throws InstructionException {
		try {
			stack.expand(size);
		} catch (final StackException e) {
			throw error(e);
		}
	}

	public void shrinkStack(final int size) throws InstructionException {
		try {
			stack.shrink(size);
		} catch (final StackException e) {
			throw error(e);
		}
	}

	public void sizeStackTo(final int size) throws InstructionException {
		try {
			stack.sizeTo(size);
		} catch (final StackException e) {
			throw error(e);
		}
	}

	public void storeVariable(final int idx, final int val) throws InstructionException {
		try {
			variables.store(idx, val);
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	public int loadVariable(final int idx) throws InstructionException {
		try {
			return variables.load(idx);
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	public int getPtr() {
		return bytecode.getPtr();
	}

	public void setPtr(final int ptr) {
		bytecode.setPtr(ptr);
	}

	private InstructionException error(final Throwable cause) throws InstructionException {
		return new InstructionException(
				"Error executing instruction " + this.getClass().getSimpleName(), cause);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().toUpperCase();
	}
}
