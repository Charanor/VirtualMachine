package se.student.liu.jessp088.vm;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A stack of integers.
 *
 * @author Charanor */
public class Stack {
	private int[] stack;
	private int size;

	/** Creates a new Stack object with the specified max stack size.
	 *
	 * @param maxStackSize the maximum stack size*/
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
		this.size = stack.size;
	}

	/** Push a value to the top of the stack.
	 *
	 * @param value
	 *            the value
	 *
	 * @throws IndexOutOfBoundsException
	 *             if the stack is full */
	public void push(final int value) throws IndexOutOfBoundsException {
		if (size >= stack.length)
			throw new IndexOutOfBoundsException("Trying to push past max stack size!");
		stack[size++] = value; // Set value then increment
	}

	/** Remove and return the top value of the stack.
	 *
	 * @return the value
	 * @throws IndexOutOfBoundsException
	 *             if the stack is empty. */
	public int pop() throws IndexOutOfBoundsException {
		if (size <= 0)
			throw new IndexOutOfBoundsException("Trying to pop from an empty stack!");
		return stack[--size]; // Decrement then return value
	}

	/** Returns the top value of the stack without removing it.
	 *
	 * @return the value */
	public int peek() throws IndexOutOfBoundsException {
		if (size < 1)
			throw new IndexOutOfBoundsException("Trying to peek from an empty stack!");
		return stack[size - 1];
	}

	/** Clears the stack setting the stack pointer to 0. */
	public void clear() {
		size = 0;
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

	/** Sets the max size of the stack to <code>size</code>.
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
		return size;
	}

	/** @return the max stack size */
	public int getMaxSize() {
		return stack.length;
	}

	/** @return if the stack is empty or not */
	public boolean isEmpty() {
		return size <= 0;
	}

	/** Returns the contents of the stack. Identical to {@link Stack#toDecimalString()}.
	 *
	 * @return the string representation. */
	@Override
	public String toString() {
		return toDecimalString();
	}

	/** Returns the contents of the stack represented as decimal numbers. Example:
	 * <code>[5, 10, 252]</code>.
	 *
	 * @return the string representation. */
	public String toDecimalString() {
		return IntStream.of(stack).limit(size).boxed().collect(Collectors.toList()).toString();
	}

	/** Returns the contents of the stack represented as hexadecimal numbers. Example:
	 * <code>[0x05, 0x0A, 0xFC]</code>.
	 *
	 * @return the string representation. */
	// Unused. Can be useful in debugging or if you want a different representation.
	public String toHexString() {
		return IntStream.of(stack).limit(size).mapToObj(v -> String.format("0x%02X", Integer.valueOf(v)))
				.collect(Collectors.toList()).toString();
	}
}
