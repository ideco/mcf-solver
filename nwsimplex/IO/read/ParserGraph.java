package nwsimplex.IO.read;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import nwsimplex.graph.Graph;

/**
 * An abstract implmentation of a graph parser. Handles the file streaming and
 * reads raw data from file.
 *
 * @author Kseniya
 */
public abstract class ParserGraph implements Parser<Graph> {

    private List<String> lines;

    @Override
    public Graph parse(Path filePath) throws IOException, FileFormatException {
        // do the raw data parsing
        lines = new LinkedList<>(Files.readAllLines(filePath, Charset.defaultCharset()));
        // handle empty files
        if (lines.isEmpty()) {
            throw new FileFormatException("File %s is empty.", filePath.getFileName().toString());
        }
        // delegate the actual parsing to the line types
        parseLines();
        // initialise the graph
        return initGraph();
    }

    /**
     * For every line in file, an appropriate line type is found. This handles
     * the correct format, extracts the data and converts it to a needed 
     * instance.
     */
    private void parseLines() {
        // get specified line types
        Collection<LineType> lineTypes = getLineTypes();
        // iterate over all lines
        Iterator<String> linesIterator = lines.iterator();
        while (linesIterator.hasNext()) {
            String nextLine = linesIterator.next();
            // find appropriate line type
            for (LineType lineType : lineTypes) {
                // line type found
                if (lineType.matches(nextLine)) {
                    // delegate the parsing task
                    lineType.parse(nextLine, this);
                    // once the line is parsed, we don't need it anymore
                    linesIterator.remove();
                }
            }
        }
    }
    
    /**
     * Specifies the line types that are known in this parser implementation.
     * @return a Collection of {@link LineType LineTypes}
     */
    protected abstract Collection<LineType> getLineTypes();

    /**
     * Initialises the {@link Graph} once the raw data is parsed.
     * @return parsed graph instance
     */
    protected abstract Graph initGraph();
}
