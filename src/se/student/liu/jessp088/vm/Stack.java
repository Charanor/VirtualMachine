package se.student.liu.jessp088.vm;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import se.student.liu.jessp088.vm.exceptions.StackException;

public class Stack {
	private final int maxStackSize;

	private int[] stack;
	private int stackPtr;

	public Stack(final int maxStackSize) {
		this(16, maxStackSize);
	}

	public Stack(final int startSize, final int maxStackSize) {
		this.maxStackSize = maxStackSize;
		stack = new int[startSize];
	}

	public void push(final int value) {
		if (stackPtr >= maxStackSize)
			throw new StackException("Trying to push past max stack size!");
		if (stackPtr >= getCurrentSize()) expand(1);
		stack[stackPtr++] = value;
	}

	public int pop() {
		if (stackPtr <= 0) throw new StackException("Trying to pop from an empty stack!");
		return stack[--stackPtr];
	}

	public void expand(final int size) {
		final int newSize = getCurrentSize() + size;
		if (newSize < 0) throw new StackException("Cannot resize to negative stack size!");
		if (newSize > maxStackSize) throw new StackException("Cannot resize above max stack size!");

		final int[] newStack = new int[newSize];
		final int len = Math.min(newStack.length, getCurrentSize());
		System.arraycopy(stack, 0, newStack, 0, len);
		stack = newStack;
	}

	public void shrink(final int size) {
		expand(-size);
	}

	public void sizeTo(final int size) {
		if (size < 0) throw new StackException("Cannot resize to negative stack size!");
		if (size > maxStackSize) throw new StackException("Cannot resize above max stack size!");

		final int delta = size - getCurrentSize();
		if (delta == 0) return;
		if (delta > 0) {
			// expand
			expand(delta);
		} else {
			// shrink
			shrink(delta);
		}
	}

	public void clear() {
		stackPtr = 0;
	}

	public int getStackPtr() {
		return stackPtr;
	}

	public int getCurrentSize() {
		return stack.length;
	}

	public int getMaxSize() {
		return maxStackSize;
	}

	@Override
	public String toString() {
		final IntStream stream = IntStream.of(stack);
		return stream.limit(stackPtr).mapToObj(v -> String.format("0x%02X", v))
				.collect(Collectors.toList()).toString();
	}
}
