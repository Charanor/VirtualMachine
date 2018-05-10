package se.student.liu.jessp088.vm.parsing.suppliers;

import se.student.liu.jessp088.vm.VMInstruction;
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

/**
 * The default {@link InstructionSupplier}. Uses a long switch statement to manuallt decide what {@link Instruction} to return.
 */
public class DefaultInstructionSupplier implements InstructionSupplier
{
	// Complex but pretty clear to me.
	// Also overly coupled but that's why we have
	// ReflectionSupplier :)
	@Override
	public Instruction getInstruction(final VMInstruction instruction, final int... args) throws IllegalArgumentException
	{
		switch (instruction) {
			case LITERAL:
				return new Literal(args[0]);
			case ADD:
				return new Add();
			case SUB:
				return new Sub();
			case DIV:
				return new Div();
			case MUL:
				return new Mul();
			case MOD:
				return new Mod();
			case CMP:
				return new Cmp();
			case JMP:
				return new Jmp(args[0]);
			case JMPGT:
				return new JmpGT(args[0]);
			case JMPLT:
				return new JmpLT(args[0]);
			case JMPEQ:
				return new JmpEQ(args[0]);
			case JMPNE:
				return new JmpNE(args[0]);
			case JMPGE:
				return new JmpGE(args[0]);
			case JMPLE:
				return new JmpLE(args[0]);
			case DUP:
				return new Dup();
			case SWAP:
				return new Swap();
			case LOAD:
				return new Load(args[0]);
			case STORE:
				return new Store(args[0]);
			case DISCARD:
				return new Discard();
			case MALLOC:
				return new Malloc(args[0]);
			case FREE:
				return new Free(args[0]);
			case SIZETO:
				return new SizeTo(args[0]);
			case CALL:
				return new Call(args[0]);
			case BREAK:
				return new Break();
			case RETURN:
				return new Return();
			default:
				throw new IllegalArgumentException("Unimplemented instruction " + instruction);
		}
	}
}
