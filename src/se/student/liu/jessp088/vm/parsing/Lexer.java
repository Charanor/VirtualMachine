package se.student.liu.jessp088.vm.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.student.liu.jessp088.vm.parsing.exceptions.LexerException;

//TODO: Remove duplicate code in lex() and lexRaw()
public class Lexer {
	public List<Token> tokenize(final String input) throws LexerException {
		final List<Token> tokens = new ArrayList<>();

		final String allTokens = concatAllTokens();
		final Pattern patterns = Pattern.compile(allTokens);
		final Matcher matcher = patterns.matcher(input);
		while (matcher.find()) {
			for (final TokenType value : TokenType.values()) {
				if (value.ignore)
					continue;

				final String group = matcher.group(value.name());
				if (group != null) {
					if (value == TokenType.ERROR)
						throw new LexerException("Invalid token " + matcher.group(value.name()));
					tokens.add(new Token(value, group));
				}
			}
		}

		return removeMultipleNextop(tokens);
	}

	public List<Token> tokenizeRaw(final String input) {
		final List<Token> tokens = new ArrayList<>();

		final String allTokens = concatAllTokens();
		final Pattern patterns = Pattern.compile(allTokens);
		final Matcher matcher = patterns.matcher(input);
		while (matcher.find()) {
			for (final TokenType value : TokenType.values()) {
				final String group = matcher.group(value.name());
				if (group != null) {
					tokens.add(new Token(value, group));
				}
			}
		}

		return tokens;
	}

	private String concatAllTokens() {
		final StringBuffer sb = new StringBuffer();
		for (final TokenType value : TokenType.values()) {
			// Named capture group
			final String format = "|(?<%s>%s)";
			final String pattern = String.format(format, value, value.pattern);
			sb.append(pattern);
		}
		// Substring 1 so we don't include the first "|"
		return sb.substring(1);
	}

	private List<Token> removeMultipleNextop(final List<Token> tokens) {
		final List<Token> onlyOneNextop = new ArrayList<>();
		for (int i = 0; i < tokens.size(); i++) {
			final Token t = tokens.get(i);
			if (t.type == TokenType.NEXTOP
					&& (i == 0 || tokens.get(i - 1).type == TokenType.NEXTOP))
				continue;
			onlyOneNextop.add(t);
		}
		return onlyOneNextop;
	}
}
