package se.student.liu.jessp088.vm.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lexes an input string into a list of {@link Token}s to be used by a {@link Parser}. The Lexer uses rexex capture groups to
 * find and identify the different types of tokens and pair them with their values.
 */
public class Lexer
{
	/**
	 * Tokenizes the string input into a list of {@link Token}s.
	 *
	 * @param input            the string input to tokenize
	 * @param addIgnoredValues if ignored token types should be included or not.
	 *
	 * @return the stream of tokens
	 * @throws LexerException if tokenizing failed
	 */
	public List<Token> tokenize(final String input, final boolean addIgnoredValues) throws LexerException {
		final List<Token> tokens = new ArrayList<>();

		final String allTokens = concatAllTokens();
		final Pattern patterns = Pattern.compile(allTokens);
		final Matcher matcher = patterns.matcher(input);
		while (matcher.find()) {
			for (final TokenType value : TokenType.values()) {
				if (!addIgnoredValues && value.ignore) continue;

				final String group = matcher.group(value.name());
				if (group != null) {
					if (value == TokenType.ERROR) throw new LexerException("Invalid token " + matcher.group(value.name()));
					tokens.add(new Token(value, group));
				}
			}
		}
		return tokens;
	}

	/**
	 * Tokenizes the input ignoring any ignored token types.
	 *
	 * @param input the input to tokenize
	 *
	 * @return the stream of tokens
	 * @throws LexerException if tokenizing failed.
	 */
	public List<Token> tokenize(final String input) throws LexerException {
		return tokenize(input, false);
	}

	private String concatAllTokens() {
		final StringBuilder sb = new StringBuilder();
		for (final TokenType value : TokenType.values()) {
			// Named capture group
			final String format = "|(?<%s>%s)";
			final String pattern = String.format(format, value, value.pattern);
			sb.append(pattern);
		}
		// Substring 1 so we don't include the first "|"
		return sb.substring(1);
	}
}
