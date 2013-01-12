package nwsimplex.graph;

import java.util.*;
import nwsimplex.util.VertexUtils;

/**
 * A Graph structure suitable for solving an instance of the mincostflow
 * problem. This Graph allows adding and removing Vertices and Edges in constnt
 * time, as well as Iterating over the set of all nodes.
 *
 * vertices can be identified by a unique id ranging from zero to the specified
 * domainsize of the graph.
 *
 * @author mfj
 */
public class Graph
{
    // the last added vertex id

    private int lastVertexId = -1;
    // the number of vertices
    private int numberOfVertices = 0;
    // the maximum absolut cost value: cost max |c(u,v)|
    private int maxAbsCost = 0;
    // an array that stores all vertices
    private Vertex[] vertices;
    // a linked list of edges, that stores all added edges
    private LinkedList<Edge> edges = new LinkedList<Edge>();

    /**
     * @param vertices
     * @param edges
     */
    public Graph(final Collection<Vertex> vertices, final Collection<Edge> edges)
    {
        Collection<Vertex> sorted = VertexUtils.sortByID(vertices);
        this.vertices = sorted.toArray(new Vertex[sorted.size()]);
        this.edges = new LinkedList<>(edges);
    }

    public Graph(int domainsize)
    {
        this.vertices = new Vertex[domainsize + 1];
    }

    /**
     * Gets the total amount of edges.
     *
     * @return the total amount of edges.
     */
    public int getNumberOfEdges()
    {
        return edges.size();
    }

    /**
     * Gets the maximum absolut cos of alle edges within this graph.
     *
     * @return
     */
    public int getMaxAbsoluteCost()
    {
        return maxAbsCost;
    }

    /**
     * Gets the number of vertices.
     *
     * @return the total amount of vertices.
     */
    public int getNumberOfVertices()
    {
        return numberOfVertices;
    }

    /**
     * Reduces the domainsize to its requred minimum. and frees up space.
     */
    public void trimVertexDomainSize()
    {
        this.vertices = Arrays.copyOf(vertices, lastVertexId + 1);
    }

    /**
     * Increases the domainsize of this graph by the specified size.
     *
     * @param size the new domainsize
     * @throws IllegalArgumentException if {@code size < numberOfVertices}
     */
    public void growVertexDomainSize(int size)
    {
        if (size < numberOfVertices)
            throw new IllegalArgumentException();

        this.vertices = Arrays.copyOf(vertices, size);
    }

    /**
     * Gets the current domainsize of this graph, which determines the range of
     * the vertex id's. Note that the domain is increases automatically if
     * nesessary.
     *
     * @return
     */
    public int getVertexDomainSize()
    {
        return vertices.length;
    }

    // grows the domain
    private void grow(int minSize)
    {
        int l = vertices.length;
        l = l < Integer.MAX_VALUE >> 4 ? l << 1 : (int) (1.5 * l);
        l = Math.max(l, minSize);
        if (l < 0)
            throw new OutOfMemoryError();

        vertices = Arrays.copyOf(vertices, l);
    }

    /**
     * Adds a new node witch the specified balance to this graph if a node with
     * the specified id already exists, the balance of this node is simply
     * changed changed.
     *
     * @param balance
     */
    public void addVertex(int id, int balance)
    {
        if (id >= vertices.length)
            grow(id + 1);
        else if (vertices[id] != null)
            vertices[id].balance = balance;
        else
        {
            numberOfVertices++;
            vertices[id] = new Vertex(id, balance);
            lastVertexId = Math.max(id, lastVertexId);
        }
    }

    /**
     * Adds a new edge connecting two vertices specified by their id's. if any
     * of these vertices does not exist a vertex with a balance of zero is
     * created.
     *
     * @param flow
     * @param cost
     * @param lowerCapacity
     * @param upperCapacity
     * @param fromId
     * @param toId
     * @return
     */
    public Edge addEdge(int flow, int cost, int lowerCapacity, int upperCapacity, int fromId, int toId)
    {
        if (getVertex(fromId) == null)
            addVertex(fromId, 0);
        if (getVertex(toId) == null)
            addVertex(toId, 0);

        Vertex from = getVertex(fromId);
        Vertex to = getVertex(toId);

        maxAbsCost = Math.max(maxAbsCost, Math.abs(cost));

        Edge edge = new Edge(flow, cost, lowerCapacity, upperCapacity, from, to);
        from.outgoing.add(edge);
        to.ingoing.add(edge);
        edges.add(edge);
        return edge;
    }

    /**
     * Adds a new edge connecting two vertices.
     *
     * @param flow
     * @param cost
     * @param lowerCapacity
     * @param upperCapacity
     * @param from
     * @param to
     * @return
     */
    public Edge addEdge(int flow, int cost, int lowerCapacity, int upperCapacity, Vertex from, Vertex to)
    {
        maxAbsCost = Math.max(maxAbsCost, Math.abs(cost));

        Edge edge = new Edge(flow, cost, lowerCapacity, upperCapacity, from, to);
        from.outgoing.add(edge);
        to.ingoing.add(edge);
        edges.add(edge);
        return edge;
    }

    /**
     * Gets the vertex which is specified by its id.
     *
     * @param id
     * @return the specified vertex or null if no such vertex exists.
     * @throws IllegalArgumentException if {@code id < 0}
     */
    public Vertex getVertex(int id)
    {
        if (id < 0)
            throw new IllegalArgumentException();
        if (id >= getVertexDomainSize())
            return null;
        return vertices[id];
    }

    /**
     * Gets an Iterator which traverses over all vertices.
     * @return 
     */
    public Iterator<Vertex> vertexIterator()
    {
        return new Iterator<Vertex>()
        {

            int currPos = -1;

            @Override
            public boolean hasNext()
            {
                while (currPos < vertices.length - 1)
                {
                    if (vertices[currPos + 1] != null)
                        return true;
                    currPos++;
                }

                return false;
            }

            @Override
            public Vertex next()
            {
                if (!hasNext())
                    throw new NoSuchElementException();

                return vertices[++currPos];
            }

            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Not supported.");
            }

        };
    }

    /**
     * Gets an iterator which traverses over all edges.
     * @return 
     */
    public Iterator<Edge> edgeIterator()
    {
        return edges.iterator();
    }

}
