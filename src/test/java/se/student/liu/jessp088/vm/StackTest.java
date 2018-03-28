package se.student.liu.jessp088.vm;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import se.student.liu.jessp088.vm.Stack;
import se.student.liu.jessp088.vm.exceptions.StackException;

public class StackTest {
	private static final int MAX_STACK_SIZE = 3;

	private Stack stack;

	@Before
	public void setUp() {
		stack = new Stack(MAX_STACK_SIZE);
	}

	@Test
	public void testPush() {
		stack.push(10);
		stack.push(3);
	}

	@Test
	public void testPop() {
		stack.push(10);
		stack.push(3);

		assertEquals(3, stack.pop());
		assertEquals(10, stack.pop());
	}

	@Test(expected = StackException.class)
	public void testPopException() {
		stack.pop();
	}

	@Test(expected = StackException.class)
	public void testPushException() {
		for (int i = 0; i <= MAX_STACK_SIZE; i++)
			stack.push(0);
	}

	@Test
	public void testClear() {
		assertEquals(0, stack.getStackPtr());

		stack.clear();
		assertEquals(0, stack.getStackPtr());

		stack.push(0);
		stack.clear();
		assertEquals(0, stack.getStackPtr());
	}

	@Test
	public void testGetCurrentSize() {
		assertEquals(0, stack.getStackPtr());
		stack.push(1);
		assertEquals(1, stack.getStackPtr());
		stack.push(3);
		assertEquals(2, stack.getStackPtr());
		stack.pop();
		assertEquals(1, stack.getStackPtr());
		stack.clear();
		assertEquals(0, stack.getStackPtr());
	}

}