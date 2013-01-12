package nwsimplex.IO.read;

import java.util.regex.Pattern;

/**
 * A line type skeleton for parsing. In combination with a Parser, describes a
 * Visitor pattern: the Parser (Visitable) handles the file streaming, and 
 * every line is being handled by the appropriate line type (Visitor).
 * 
 * @author Kseniya
 */
public abstract class LineType<P extends Parser> {
    
    /**
     * Decides if this line type can parse the given line.
     * @param line data source
     * @return true if the line is of the line type described by this class.
     */
    public boolean matches(String line) {
        return getLinePattern().matcher(line).matches();
    }

    /**
     * The visit method in the visitor pattern. Does the parsing and calls the
     * appropriate data handling method in the parser.
     * @param line data source
     * @param parser the parser that delegates the line parsing job
     */
    public abstract void parse(String line, P parser);

    /**
     * Describes how the line should look like for successful parsing.
     * @return a Pattern that can match an entire line for matching this line type.
     */
    protected abstract Pattern getLinePattern();
}
