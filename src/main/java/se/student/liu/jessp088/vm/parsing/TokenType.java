package se.student.liu.jessp088.vm.parsing;

/**
 * The different types of tokens recognized by the {@link Lexer}.
 */
public enum TokenType {
	WHITESPACE("[ \t]+", true),
	COMMENT("//[^\r\n\f]*", true),
	NEXTOP("(\r\n|\r|\n)"),
	DEFTAG("#"),
	DEFINE("define"),
	EQUALS("="),
	LEFTBRACKET("\\["),
	RIGHTBRACKET("\\]"),
	IDENTIFIER("[a-zA-Z_][a-zA-Z0-9_]*"),
	NUMBER("-?(0[xX][a-fA-F0-9]+|[0-9]+(\\.[0-9]+)?)"),
	EOF("\\Z"),

	// ERROR MUST ALWAYS ALWAYS ALWAYS BE LAST
	ERROR(".+");

	/**
	 * The regex pattern the token matches.
	 */
	public final String pattern;
	/**
	 * If the token should be ignored by the lexer when found.
	 */
	public final boolean ignore;

	private TokenType(final String pattern) {
		this(pattern, false);
	}

	private TokenType(final String pattern, final boolean ignore) {
		this.pattern = pattern;
		this.ignore = ignore;
	}
}
