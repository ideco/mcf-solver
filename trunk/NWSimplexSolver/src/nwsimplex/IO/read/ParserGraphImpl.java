package nwsimplex.IO.read;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nwsimplex.graph.Edge;
import nwsimplex.graph.Graph;
import nwsimplex.graph.Vertex;
import nwsimplex.util.ParserConstants;
import nwsimplex.util.VertexUtils;

/**
 * A concrete parser implementation that is capable of handling .net file format
 * with comment lines, problem line, edge and vertex lines. Does not require the
 * monotoneous order of lines (first problem line, than node lines, last edge
 * lines), can read the lines in any order, then checks their validity.
 *
 * @author Kseniya
 */
public class ParserGraphImpl extends ParserGraph {

    private final Pattern patternDigit = Pattern.compile(ParserConstants.DIGIT_PATTERN);
    
    // here we store vertices to initialise graph later
    private final List<Vertex> vertices;
    // raw edge data will be stored, then converted to edges and validated
    private final List<RawEdgeData> rawEdges;
    // problem line data
    private ProblemDescription stats;

    public ParserGraphImpl() {
        vertices = new LinkedList<>();
        rawEdges = new LinkedList<>();
    }

    @Override
    protected Graph initGraph() {
        checkDataConsistency();
        return new Graph(vertices, initEdgesFromRawEdgeData());
    }

    /**
     * Checks if the data in given file was valid. If not, throws an exception.
     *
     * @throws FileFormatException if the data is not parseable.
     */
    protected void checkDataConsistency() throws FileFormatException {
        // the problem line must be always present
        if (stats == null) {
            throw new FileFormatException("File does not contain a valid problem line.");
        }
        if (vertices.size() != stats.getNumberOfVertices()) {
            throw new FileFormatException("Number of vertices does not match the amount of stored ones.");
        }
        if (rawEdges.size() != stats.getNumberOfEdges()) {
            throw new FileFormatException("Number of edges does not match the amount of stored ones.");
        }
    }

    /**
     * Once all vertices and edges data is parsed, we can construct the edges to
     * create a graph out of. This is being done here.
     *
     * @return a Collection of edges if all edges were correctly stored in the
     * file.
     * @throws FileFormatException otherwise
     */
    private Collection<Edge> initEdgesFromRawEdgeData() throws FileFormatException {
        // prepare edges container
        List<Edge> edges = new ArrayList<>(rawEdges.size());
        Edge edge;
        try {
            for (RawEdgeData rawEdgeData : rawEdges) {
                // find source vertex
                Vertex srcVertex = VertexUtils.findByID(vertices, rawEdgeData.srcVertexID);
                // find target vertex
                Vertex targetVertex = VertexUtils.findByID(vertices, rawEdgeData.targetVertexID);
                // add edge if both vertices found
                edge = new Edge(
                        rawEdgeData.lowerCapacity,
                        rawEdgeData.cost,
                        rawEdgeData.lowerCapacity,
                        rawEdgeData.upperCapacity,
                        srcVertex,
                        targetVertex);
                edges.add(edge);
            }
        } catch (NoSuchElementException ex) {
            // rethrow the error when a referenced vertex was not found
            // with a more detailed message.
            throw new FileFormatException(String.format("An edge stored in file "
                    + "is not valid: %s", ex.getMessage()));
        }
        return edges;
    }

    @Override
    protected Collection<LineType> getLineTypes() {
        return Arrays.asList(new LineType[]{
                    new LineTypeComment(),
                    new LineTypeVertex(),
                    new LineTypeEdge(),
                    new LineTypeProblem()});
    }

    /**
     * Data that is read out of the problem line. Nothing special.
     */
    public class ProblemDescription implements RawLineData {

        private int numberOfVertices;
        private int numberOfEdges;

        public ProblemDescription(int numberOfVertices, int numberOfEdges) {
            this.numberOfVertices = numberOfVertices;
            this.numberOfEdges = numberOfEdges;
        }

        public int getNumberOfVertices() {
            return numberOfVertices;
        }

        public int getNumberOfEdges() {
            return numberOfEdges;
        }
    }

    /**
     * Data that is read out of an edge line, will be converted to a real edge
     * after all file data is read.
     */
    protected class RawEdgeData implements RawLineData {

        public final int srcVertexID, targetVertexID, cost, lowerCapacity, upperCapacity;

        public RawEdgeData(int srcVertexID, int targetVertexID, int cost, int lowerCapacity, int upperCapacity) {
            this.srcVertexID = srcVertexID;
            this.targetVertexID = targetVertexID;
            this.cost = cost;
            this.lowerCapacity = lowerCapacity;
            this.upperCapacity = upperCapacity;
        }
    }
    
        //<editor-fold defaultstate="collapsed" desc="Line types implementation">
    protected class LineTypeComment extends LineType {

        @Override
        public void parse(String line, Parser parser) {
            // simpy do nothing since we do not want to obtain any data
            // out of comment lines in this parser implementation.
        }

        @Override
        protected Pattern getLinePattern() {
            return Pattern.compile("c *");
        }
    }

    protected class LineTypeVertex extends LineType {

        @Override
        public void parse(String line, Parser parser) {
            int id = -1, balance = -1;
            Matcher matcherDigit = patternDigit.matcher(line);
            // read id
            if (matcherDigit.find()) {
                id = Integer.parseInt(matcherDigit.group());
            }
            // read balance
            if (matcherDigit.find()) {
                balance = Integer.parseInt(matcherDigit.group());
            }
            vertices.add(new Vertex(id, balance));
        }

        @Override
        protected Pattern getLinePattern() {
            return Pattern.compile("n " + ParserConstants.DIGIT_PATTERN + " " + ParserConstants.DIGIT_PATTERN);
        }
    }

    protected class LineTypeEdge extends LineType {

        @Override
        public void parse(String line, Parser parser) {
            int srcID = -1, targetID = -1, cost = -1, lowerCapacity = -1, upperCapacity = -1;
            Matcher matcherDigit = patternDigit.matcher(line);
            // read source vertex
            if (matcherDigit.find()) {
                srcID = Integer.parseInt(matcherDigit.group());
            }
            // read target vertex
            if (matcherDigit.find()) {
                targetID = Integer.parseInt(matcherDigit.group());
            }
            // read cost
            if (matcherDigit.find()) {
                lowerCapacity = Integer.parseInt(matcherDigit.group());
            }
            // read cost
            if (matcherDigit.find()) {
                upperCapacity = Integer.parseInt(matcherDigit.group());
            }
            // read cost
            if (matcherDigit.find()) {
                cost = Integer.parseInt(matcherDigit.group());
            }
            rawEdges.add(new RawEdgeData(srcID, targetID, cost, lowerCapacity, upperCapacity));
        }

        @Override
        protected Pattern getLinePattern() {
            return Pattern.compile("a " + ParserConstants.DIGIT_PATTERN + " "
                    + ParserConstants.DIGIT_PATTERN + " "
                    + ParserConstants.DIGIT_PATTERN + " "
                    + ParserConstants.DIGIT_PATTERN + " "
                    + ParserConstants.DIGIT_PATTERN);
        }
    }

    protected class LineTypeProblem extends LineType {

        @Override
        public void parse(String line, Parser parser) {
            int numberOfVertices = -1, numberOfEdges = -1;
            Matcher matcherDigit = patternDigit.matcher(line);
            // read number of vertices
            if (matcherDigit.find()) {
                numberOfVertices = Integer.parseInt(matcherDigit.group());
            }
            // read number of edges
            if (matcherDigit.find()) {
                numberOfEdges = Integer.parseInt(matcherDigit.group());
            }
            stats = new ProblemDescription(numberOfVertices, numberOfEdges);
        }

        @Override
        protected Pattern getLinePattern() {
            return Pattern.compile("p min " + ParserConstants.DIGIT_PATTERN + " " + ParserConstants.DIGIT_PATTERN);
        }
    }
    //</editor-fold>

}
