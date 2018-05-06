package se.student.liu.jessp088.vm.instructions;

import se.student.liu.jessp088.vm.Bytecode;
import se.student.liu.jessp088.vm.Stack;
import se.student.liu.jessp088.vm.Variables;
import se.student.liu.jessp088.vm.VirtualMachine;

/**
 * An instruction that can be processed by a {@link VirtualMachine}.
 *
 * @author Charanor
 */
public abstract class Instruction
{
	private Stack stack;
	private Bytecode bytecode;
	private Variables variables;

	protected Instruction() {
		this.stack = null;
		this.bytecode = null;
		this.variables = null;
	}

	/**
	 * Processes this instruction.
	 *
	 * @param stack     the stack to process on
	 * @param bytecode  the code to process on
	 * @param variables the variables to process on
	 *
	 * @throws InstructionException if this instruction could not properly process.
	 */
	public void process(final Stack stack, final Bytecode bytecode, final Variables variables) throws InstructionException {
		this.stack = stack;
		this.bytecode = bytecode;
		this.variables = variables;

		this.process();
	}


	protected abstract void process() throws InstructionException;

	/**
	 * Push a value to the {@link Stack}
	 *
	 * @param value the value to push
	 *
	 * @throws InstructionException if the stack is full
	 * @see Stack#push(int)
	 */
	protected void push(final int value) throws InstructionException {
		try {
			stack.push(value);
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	/**
	 * Pop a value from the {@link Stack}.
	 *
	 * @return the poped value
	 * @throws InstructionException if the stack is empty
	 * @see Stack#pop()
	 */
	protected int pop() throws InstructionException {
		try {
			return stack.pop();
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	/**
	 * Peek from the {@link Stack}.
	 *
	 * @return the peeked value
	 * @throws InstructionException if the stack is empty
	 * @see Stack#peek()
	 */
	protected int peek() throws InstructionException {
		try {
			return stack.peek();
		} catch (final IndexOutOfBoundsException e) {
			throw error(e);
		}
	}

	/**
	 * Expands the size of the stack.
	 *
	 * @param size the size to expand
	 *
	 * @see Stack#expand(int)
	 */
	protected void expandStack(final int size) {
		stack.expand(size);
	}

	/**
	 * Shrinks the size of the stack.
	 *
	 * @param size the size to shrink by
	 *
	 * @see Stack#shrink(int)
	 */
	protected void shrinkStack(final int size) {
		stack.shrink(size);
	}

	/**
	 * Resizes the stack to the specified size.
	 *
	 * @param size the size to size to
	 *
	 * @see Stack#sizeTo(int)
	 */
	protected void sizeStackTo(final int size) {
		stack.sizeTo(size);
	}

	/**
	 * Stores a variable.
	 *
	 * @param idx the index of the variable
	 * @param val the value to store in the variable
	 *
	 * @throws InstructionException if something goes wrong.
	 * @see Variables#store(int, int)
	 */
	protected void storeVariable(final int idx, final int val) throws InstructionException {
		try {
			variables.store(idx, val);
		} catch (final IndexOutOfBoundsException e) {
			// We want to throw our own error to make sure that
			// the virtual machine understands that something
			// went wrong while executing an instruction.
			throw error(e);
		}
	}

	/**
	 * Loads a variable.
	 *
	 * @param idx the index of the variable
	 *
	 * @return the value the variable has right now
	 * @throws InstructionException if something goes wrong.
	 * @see Variables#load(int)
	 */
	protected int loadVariable(final int idx) throws InstructionException {
		try {
			return variables.load(idx);
		} catch (final IndexOutOfBoundsException e) {
			// We want to throw our own error to make sure that
			// the virtual machine understands that something
			// went wrong while executing an instruction.
			throw error(e);
		}
	}

	/**
	 * Returns the current pointer to the instruction.
	 *
	 * @return the pointer
	 * @see Bytecode#getPtr()
	 */
	protected int getPtr() {
		return bytecode.getPtr();
	}

	/**
	 * Sets the pointer.
	 *
	 * @param ptr the new pointer
	 *
	 * @see Bytecode#setPtr(int)
	 */
	protected void setPtr(final int ptr) {
		bytecode.setPtr(ptr);
	}

	/**
	 * Re-throws an exception as an {@link InstructionException} to signal that something went wring while executing an instruction.
	 *
	 * @param cause the throwable to re-throw
	 *
	 * @return the new {@link InstructionException}
	 */
	private InstructionException error(final Throwable cause) {
		return new InstructionException("Error executing instruction %s. Cause: %s", this.getClass().getSimpleName(), cause);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName().toUpperCase();
	}
}
