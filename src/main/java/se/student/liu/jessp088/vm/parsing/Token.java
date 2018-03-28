package se.student.liu.jessp088.vm.parsing;

public class Token {
	public final TokenType type;
	public final String value;

	public Token(final TokenType type, final String value) {
		this.type = type;
		this.value = value;
	}

	@Override
	public String toString() {
		return String.format("(%s %s)", type, value);
	}
}
