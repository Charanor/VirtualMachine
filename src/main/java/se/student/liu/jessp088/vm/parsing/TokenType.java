package se.student.liu.jessp088.vm.parsing;

public enum TokenType {
	WHITESPACE("[ \t]+", true),
	COMMENT("//[^\r\n\f]*", true),
	NEXTOP("[\r\n\f;]+"),
	DEFTAG("#"),
	DEFINE("define"),
	EQUALS("="),
	LEFTBRACKET("\\["),
	RIGHTBRACKET("\\]"),
	IDENTIFIER("[a-zA-Z_][a-zA-Z0-9_]*"),
	NUMBER("-?(0[xX][a-fA-F0-9]+|[0-9]+(\\.[0-9]+)?)"),
	EOF("\\Z"),

	// ERROR MUST ALWAYS ALWAYS ALWAYS BE LAST
	ERROR(".+"),;

	public final String pattern;
	public final boolean ignore;

	private TokenType(final String pattern) {
		this(pattern, false);
	}

	private TokenType(final String pattern, final boolean ignore) {
		this.pattern = pattern;
		this.ignore = ignore;
	}
}
