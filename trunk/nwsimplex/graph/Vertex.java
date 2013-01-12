package nwsimplex.graph;

import java.util.*;

/**
 * A vertex which can be used by the network simplex algorithm.
 *
 * @author mfj
 */
public class Vertex
{

    /**
     * a unique identifier which also marks the position in the vertex[] array
     * in the corresponding Graph instance.
     */
    public final int ID;
    /**
     * balance which is defined by the min costflow instance
     */
    protected int balance;
    /**
     * the potential used by the networksimplex algorithm
     */
    protected int potential;
    /**
     * the depht inside the root of a spanning tree
     */
    protected int depth = 1;
    /**
     * the predessor vertex within the spanning tree
     */
    protected Vertex parent;
    /**
     * used to form al linked list, for child nodetraversion
     */
    protected Vertex child, leftSibbling, rightSibbling;
    /**
     * the edge which points upwards to the predessesor within the spanning tree
     */
    protected Edge treeEdge;
    /**
     * outgoing edges
     */
    protected AdjacencyList outgoing = new AdjacencyList();
    /**
     * ingoing edges
     */
    protected AdjacencyList ingoing = new AdjacencyList();

    /**
     * Instantiates a new Vertex.
     *
     * @param ID the unique identifier used by this graph.
     * @param balance the balance specified by the min-cost-flow problem
     */
    public Vertex(int ID, int balance)
    {
        this.ID = ID;
        this.balance = balance;
    }

    /**
     * Gets the sum of all capacities of ingoing edges
     */
    public int lowerInCapacity()
    {
        int cap = 0;
        for (Edge e : ingoing)
            cap += e.lowerCapacity;
        return cap;
    }

    /**
     * Gets the sum of all capacities of outgoing edges
     */
    public int lowerOutCapacity()
    {
        int cap = 0;
        for (Edge e : outgoing)
            cap += e.lowerCapacity;
        return cap;
    }

    /**
     * Gets the netto balance.
     *
     * @return b + l(d+) - l(d-) {@code balance + lowerOutCapacity() - lowerInCapacity()}
     */
    public int nettoBalance()
    {
        return balance + lowerOutCapacity() - lowerInCapacity();
    }

    @Override
    public String toString()
    {
        return "ID=" + ID + ", parent=" + (parent == null ? null : parent.ID);
    }
}