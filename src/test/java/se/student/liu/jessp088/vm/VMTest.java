package se.student.liu.jessp088.vm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.student.liu.jessp088.vm.Bytecode;
import se.student.liu.jessp088.vm.Stack;
import se.student.liu.jessp088.vm.Variables;
import se.student.liu.jessp088.vm.VirtualMachine;
import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.Literal;
import se.student.liu.jessp088.vm.instructions.arithmetic.Sub;
import se.student.liu.jessp088.vm.instructions.control.Jmp;
import se.student.liu.jessp088.vm.instructions.data.Load;
import se.student.liu.jessp088.vm.instructions.data.Store;

public class VMTest {
	private VirtualMachine vm;

	@Before
	public void setUp() throws Exception {
		vm = new VirtualMachine(4, 128);
		VirtualMachine.DEBUG = true;
	}

	@Test
	public void testExecute() {
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

		final boolean success = vm.execute(new Bytecode(instructions));
		assertTrue(vm.getError(), success);

		final Stack s = vm.getStack();
		final Variables v = vm.getVariables();

		assertEquals(s.pop(), 1);
		assertEquals(v.load(0), 1);
		assertEquals(v.load(1), 0);
		assertEquals(v.load(2), 55);
	}

}
