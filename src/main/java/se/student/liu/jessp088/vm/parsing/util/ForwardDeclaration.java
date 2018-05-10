package se.student.liu.jessp088.vm.parsing.util;

import se.student.liu.jessp088.vm.VMInstruction;

import java.util.Objects;

/**
 * Placeholder for a forward-declaration of a label (i.e. when a label is used before it is defined).
 */
public class ForwardDeclaration implements Comparable<ForwardDeclaration>
{
	private final VMInstruction instructionType;
	private final int instrunctionPos;
	private final String forwardDeclaration;

	public ForwardDeclaration(final VMInstruction instructionType, final int instrunctionPos, final String forwardDeclaration) {
		this.instructionType = instructionType;
		this.instrunctionPos = instrunctionPos;
		this.forwardDeclaration = forwardDeclaration;
	}

	public VMInstruction getInstructionType() {
		return instructionType;
	}

	public int getInstrunctionPos() {
		return instrunctionPos;
	}

	public String getForwardDeclaration() {
		return forwardDeclaration;
	}

	@Override
	public String toString() {
		return String.format("(%s %s @ %s)", instructionType, forwardDeclaration, Integer.valueOf(instrunctionPos));
	}

	@Override
	public int compareTo(final ForwardDeclaration o) {
		return Integer.compare(instrunctionPos, o.instrunctionPos);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || !Objects.equals(getClass(), o.getClass())) return false;
		final ForwardDeclaration that = (ForwardDeclaration) o;
		return instrunctionPos == that.instrunctionPos && instructionType == that.instructionType &&
			   Objects.equals(forwardDeclaration, that.forwardDeclaration);
	}

	@Override
	public int hashCode() {
		// Built-in standard way to get hash code, so it can't be that much overhead...
		return Objects.hash(instructionType, Integer.valueOf(instrunctionPos), forwardDeclaration);
	}
}
