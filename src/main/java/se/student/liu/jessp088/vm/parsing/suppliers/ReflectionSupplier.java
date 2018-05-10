package se.student.liu.jessp088.vm.parsing.suppliers;

import java.lang.reflect.InvocationTargetException;

import se.student.liu.jessp088.vm.VMInstruction;
import se.student.liu.jessp088.vm.instructions.Instruction;

/**
 * Custom {@link InstructionSupplier}. Uses reflection and the class supplied to the {@link VMInstruction} object to create a new Instruction through reflection.
 */
public class ReflectionSupplier implements InstructionSupplier
{
	@Override
	public Instruction getInstruction(final VMInstruction instruction, final int... args) throws IllegalArgumentException {
		final Class<?>[] paramTypes = new Class<?>[instruction.numArguments];
		for (int i = 0; i < paramTypes.length; i++) { paramTypes[i] = int.class; }

		// Must be of type Object[] because int[] will cause an argument mismatch.
		final Object[] objectArgs = new Object[instruction.numArguments];
		for (int i = 0; i < objectArgs.length; i++) { objectArgs[i] = Integer.valueOf(args[i]); }

		try {
			return instruction.instructionClass.getConstructor(paramTypes).newInstance(objectArgs);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Could not create instruction " + instruction, e);
		}
	}
}
