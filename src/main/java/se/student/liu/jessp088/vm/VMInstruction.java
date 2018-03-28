package se.student.liu.jessp088.vm;

/** All instructions that the VM can process.
 *
 * @author Charanor */
public enum VMInstruction {
	// Arithmetic instructions
	LITERAL(1),
	ADD(0),
	SUB(0),
	DIV(0),
	MUL(0),
	MOD(0),

	// Control Flow Instructions
	CMP(0),
	JMP(1),
	JMPGT(1),
	JMPLT(1),
	JMPEQ(1),
	JMPNE(1),
	JMPGE(1),
	JMPLE(1),
	CALL(1),
	BREAK(0),
	RETURN(0),

	// Data Transfer Instructions
	DUP(0),
	SWAP(0),
	LOAD(1),
	STORE(1),
	DISCARD(0),

	// Meta Control Instructions
	MALLOC(1),
	FREE(1),
	SIZETO(1),;

	public final int numArguments;

	private VMInstruction(final int numArguments) {
		this.numArguments = numArguments;
	}
}
