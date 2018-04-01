package se.student.liu.jessp088.vm;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A stack of integers.
 *
 * @author Charanor */
public class Stack {
	private int[] stack;
	private int stackPtr;

	/** Creates a new Stack object with the specified max stack size.
	 *
	 * @param maxStackSize */
	public Stack(final int maxStackSize) {
		stack = new int[maxStackSize];
	}

	/** Creates a new Stack object that is an exact copy (all elements and pointer are the same) of
	 * the supplied Stack parameter.
	 *
	 * @param stack
	 *            the stack to copy. */
	public Stack(final Stack stack) {
		this.stack = new int[stack.getMaxSize()];
		System.arraycopy(stack.stack, 0, this.stack, 0, stack.getMaxSize());
		this.stackPtr = stack.stackPtr;
	}

	/** Push a value to the top of the stack.
	 *
	 * @param value
	 *            the value
	 * @throws IndexOutOfBoundsException
	 *             if the stack is full */
	public void push(final int value) throws IndexOutOfBoundsException {
		if (stackPtr >= stack.length)
			throw new IndexOutOfBoundsException("Trying to push past max stack size!");
		stack[stackPtr++] = value;
	}

	/** Remove and return the top value of the stack.
	 *
	 * @return the value
	 * @throws IndexOutOfBoundsException
	 *             if the stack is empty. */
	public int pop() throws IndexOutOfBoundsException {
		if (stackPtr <= 0)
			throw new IndexOutOfBoundsException("Trying to pop from an empty stack!");
		return stack[--stackPtr];
	}

	/** Returns the top value of the stack without removing it.
	 *
	 * @return the value */
	public int peek() throws IndexOutOfBoundsException {
		if (stackPtr < 1)
			throw new IndexOutOfBoundsException("Trying to peek from an empty stack!");
		return stack[stackPtr - 1];
	}

	/** Returns the first value beneath the top of the stack.
	 *
	 * @return the value
	 * @throws IndexOutOfBoundsException
	 *             if the stack has fewer than 2 elements. */
	public int deepPeek() throws IndexOutOfBoundsException {
		if (stackPtr < 1) throw new IndexOutOfBoundsException(
				"Cannot deep peek a stack with fewer than 2 elements!");
		return stack[stackPtr - 1];
	}

	/** Clears the stack setting the stack pointer to 0. */
	public void clear() {
		stackPtr = 0;
	}

	/** Increases the max size of the stack by <code>size</code>.
	 *
	 * @param size
	 *            the size */
	public void expand(final int size) {
		sizeTo(getMaxSize() + size);
	}

	/** Reduces the max size of the stack by <code>size</code>.
	 *
	 * @param size
	 *            the size */
	public void shrink(final int size) {
		sizeTo(getMaxSize() - size);
	}

	/** Sets the max size of the stack to <code>size</code>
	 *
	 * @param size
	 *            the size */
	public void sizeTo(final int size) {
		final int[] tmp = new int[size];
		System.arraycopy(stack, 0, tmp, 0, Math.min(size, stack.length));
		stack = tmp;
	}

	/** @return the number of elements in the stack */
	public int getSize() {
		return stackPtr;
	}

	/** @return the max stack size */
	public int getMaxSize() {
		return stack.length;
	}

	/** @return if the stack is empty or not */
	public boolean isEmpty() {
		return stackPtr <= 0;
	}

	@Override
	public String toString() {
		final IntStream stream = IntStream.of(stack);
		final String hexFormat = "0x%02X";
		final String decFormat = "%s";
		return stream.limit(stackPtr).mapToObj(v -> String.format(decFormat, v))
				.collect(Collectors.toList()).toString();
	}
}
