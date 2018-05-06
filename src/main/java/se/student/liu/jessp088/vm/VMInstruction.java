package se.student.liu.jessp088.vm;

import se.student.liu.jessp088.vm.instructions.Instruction;
import se.student.liu.jessp088.vm.instructions.Literal;
import se.student.liu.jessp088.vm.instructions.arithmetic.Add;
import se.student.liu.jessp088.vm.instructions.arithmetic.Div;
import se.student.liu.jessp088.vm.instructions.arithmetic.Mod;
import se.student.liu.jessp088.vm.instructions.arithmetic.Mul;
import se.student.liu.jessp088.vm.instructions.arithmetic.Sub;
import se.student.liu.jessp088.vm.instructions.control.Cmp;
import se.student.liu.jessp088.vm.instructions.control.Jmp;
import se.student.liu.jessp088.vm.instructions.control.JmpEQ;
import se.student.liu.jessp088.vm.instructions.control.JmpGE;
import se.student.liu.jessp088.vm.instructions.control.JmpGT;
import se.student.liu.jessp088.vm.instructions.control.JmpLE;
import se.student.liu.jessp088.vm.instructions.control.JmpLT;
import se.student.liu.jessp088.vm.instructions.control.JmpNE;
import se.student.liu.jessp088.vm.instructions.control.subroutine.Break;
import se.student.liu.jessp088.vm.instructions.control.subroutine.Call;
import se.student.liu.jessp088.vm.instructions.control.subroutine.Return;
import se.student.liu.jessp088.vm.instructions.data.Discard;
import se.student.liu.jessp088.vm.instructions.data.Dup;
import se.student.liu.jessp088.vm.instructions.data.Load;
import se.student.liu.jessp088.vm.instructions.data.Store;
import se.student.liu.jessp088.vm.instructions.data.Swap;
import se.student.liu.jessp088.vm.instructions.meta.Free;
import se.student.liu.jessp088.vm.instructions.meta.Malloc;
import se.student.liu.jessp088.vm.instructions.meta.SizeTo;

/** All instructions that the VM can process.
 *
 * @author Charanor */
public enum VMInstruction {
	// Arithmetic instructions
	LITERAL(1, Literal.class),
	ADD(0, Add.class),
	SUB(0, Sub.class),
	DIV(0, Div.class),
	MUL(0, Mul.class),
	MOD(0, Mod.class),

	// Control Flow Instructions
	CMP(0, Cmp.class),
	JMP(1, Jmp.class),
	JMPGT(1, JmpGT.class),
	JMPLT(1, JmpLT.class),
	JMPEQ(1, JmpEQ.class),
	JMPNE(1, JmpNE.class),
	JMPGE(1, JmpGE.class),
	JMPLE(1, JmpLE.class),
	CALL(1, Call.class),
	BREAK(0, Break.class),
	RETURN(0, Return.class),

	// Data Transfer Instructions
	DUP(0, Dup.class),
	SWAP(0, Swap.class),
	LOAD(1, Load.class),
	STORE(1, Store.class),
	DISCARD(0, Discard.class),

	// Meta Control Instructions
	MALLOC(1, Malloc.class),
	FREE(1, Free.class),
	SIZETO(1, SizeTo.class),;

	/**
	 * The number of arguments the instruction takes.
	 */
	public final int numArguments;
	/**
	 * The class object that represents the instruction.
	 * Used by {@link se.student.liu.jessp088.vm.parsing.suppliers.ReflectionSupplier}.
	 */
	public final Class<? extends Instruction> instructionClass;

	private VMInstruction(final int numArguments,
			final Class<? extends Instruction> instructionClass) {
		this.numArguments = numArguments;
		this.instructionClass = instructionClass;
	}
}
