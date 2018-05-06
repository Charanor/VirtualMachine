package se.student.liu.jessp088.vm;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.Literal;
import se.student.liu.jessp088.vm.instructions.arithmetic.Sub;
import se.student.liu.jessp088.vm.instructions.control.Jmp;
import se.student.liu.jessp088.vm.instructions.data.Load;
import se.student.liu.jessp088.vm.instructions.data.Store;

/**
 * Tests the {@link VirtualMachine} class.
 */
public class VirtualMachineTest {
	private Bytecode code = null;
	private VirtualMachine vm = null;

	@Before
	public void beforeClass() {
		final List<Instruction> instructions = new ArrayList<>();
		instructions.add(new Literal(5));
		instructions.add(new Literal(4));
		instructions.add(new Sub());
		instructions.add(new Store(0));
		instructions.add(new Load(0));
		instructions.add(new Jmp(8));
		instructions.add(new Literal(99));
		instructions.add(new Store(1));
		instructions.add(new Literal(55)); // 8
		instructions.add(new Store(2));

		final Map<Integer, Integer> map = new HashMap<>();
		for (int i = 0; i < instructions.size(); i++)
			map.put(i, i);
		code = new Bytecode(instructions, map);
		vm = new DefaultVirtualMachine(4, 128);
	}

	@Test
	public void testExecute() {
		vm.execute(code);
		assertEquals(vm.getError(), VMState.END_SUCCESS, vm.getState());

		final Stack s = vm.getStack();
		final Variables v = vm.getVariables();

		assertEquals(1, s.pop());
		assertEquals(1, v.load(0));
		assertEquals(0, v.load(1));
		assertEquals(55, v.load(2));
	}

	@Test
	public void testPause() {
		final DebugListener listener = new DebugListener() {
			@Override
			public void onStateChanged(final VirtualMachine vm) {
			}

			@Override
			public void beforeInstruction(final VirtualMachine vm) {
				vm.pauseExecution();
			}

			@Override
			public void beforeExecution(final VirtualMachine vm) {
			}

			@Override
			public void afterInstruction(final VirtualMachine vm) {
			}

			@Override
			public void afterExecution(final VirtualMachine vm) {
			}
		};

		vm.addDebugListener(listener);
		vm.execute(code);

		assertEquals(VMState.PAUSE_USER, vm.getState());
		vm.removeDebugListener(listener);
		vm.resumeExecution();
		assertEquals(vm.getError(), VMState.END_SUCCESS, vm.getState());
	}

	@Test
	public void testStop() {
		final DebugListener listener = new DebugListener() {
			@Override
			public void onStateChanged(final VirtualMachine vm) {
			}

			@Override
			public void beforeInstruction(final VirtualMachine vm) {
				vm.stopExecution();
			}

			@Override
			public void beforeExecution(final VirtualMachine vm) {
			}

			@Override
			public void afterInstruction(final VirtualMachine vm) {
			}

			@Override
			public void afterExecution(final VirtualMachine vm) {
			}
		};

		vm.addDebugListener(listener);
		vm.execute(code);

		assertEquals(VMState.END_USER, vm.getState());
		vm.removeDebugListener(listener); // Don't stop next time
		vm.resumeExecution(); // Make sure resume does not actually resume
		assertEquals(vm.getError(), VMState.END_USER, vm.getState());
	}

	@Test
	public void testBreakpoint() {
		vm.addBreakpoint(2);
		vm.addBreakpoint(3);

		vm.debug(code);

		assertEquals(vm.getError(), VMState.PAUSE_BREAKPOINT, vm.getState());
		final int shouldBe4 = vm.getStack().pop();
		vm.getStack().push(shouldBe4);
		assertEquals(4, shouldBe4);

		vm.resumeExecution();
		assertEquals(vm.getError(), VMState.PAUSE_BREAKPOINT, vm.getState());
		final int shouldBe1 = vm.getStack().pop();
		vm.getStack().push(shouldBe1);
		assertEquals(1, shouldBe1);

		vm.resumeExecution();
		assertEquals(vm.getError(), VMState.END_SUCCESS, vm.getState());
	}

	@Test
	public void testStopWhenPaused() {
		vm.addDebugListener(new DebugListener() {

			@Override
			public void onStateChanged(final VirtualMachine vm) {
			}

			@Override
			public void beforeInstruction(final VirtualMachine vm) {
				vm.pauseExecution();
			}

			@Override
			public void beforeExecution(final VirtualMachine vm) {
			}

			@Override
			public void afterInstruction(final VirtualMachine vm) {
			}

			@Override
			public void afterExecution(final VirtualMachine vm) {
			}
		});

		vm.execute(code);
		assertEquals(vm.getError(), VMState.PAUSE_USER, vm.getState());
		vm.stopExecution();
		assertEquals(vm.getError(), VMState.END_USER, vm.getState());
	}

}
