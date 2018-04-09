package se.student.liu.jessp088.vm.parsing.suppliers;

import java.lang.reflect.InvocationTargetException;

import se.student.liu.jessp088.vm.VMInstruction;
import se.student.liu.jessp088.vm.instructions.Instruction;

public class ReflectionSupplier implements InstructionSupplier {
	@Override
	public Instruction getInstruction(final VMInstruction instruction, final int... inputArgs)
			throws IllegalArgumentException {
		final Class<?>[] paramTypes = new Class<?>[instruction.numArguments];
		for (int i = 0; i < paramTypes.length; i++)
			paramTypes[i] = int.class;

		// Must be of type Object[] because int[] will cause an argument mismatch.
		final Object[] args = new Object[instruction.numArguments];
		for (int i = 0; i < args.length; i++)
			args[i] = inputArgs[i];

		try {
			return instruction.instructionClass.getConstructor(paramTypes).newInstance(args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Could not create instruction " + instruction, e);
		}
	}
}
