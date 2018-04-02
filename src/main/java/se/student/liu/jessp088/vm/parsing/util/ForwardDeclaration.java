package se.student.liu.jessp088.vm.parsing.util;

import se.student.liu.jessp088.vm.VMInstruction;

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
		return String.format("(%s %s @ %s)", instructionType, forwardDeclaration, instrunctionPos);
	}

	@Override
	public int compareTo(final ForwardDeclaration o) {
		return Integer.compare(instrunctionPos, o.instrunctionPos);
	}
}
