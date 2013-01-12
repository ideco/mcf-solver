package nwsimplex.graph;

/**
 *
 * @author mfj
 */
public class Edge
{

    /**
     * the current flow of this edge
     */
    public int flow;
    /**
     * the cost of this edge
     */
    public final int cost;
    /**
     * the lower capacity of this edge
     */
    public final int lowerCapacity;
    /**
     * the upper capacity of this edge
     */
    public final int upperCapacity;
    /**
     * the vertiex from which this edge goes out
     */
    public final Vertex from;
    /**
     * the vertex from which this edge goes in
     */
    public final Vertex to;

    /**
     *
     * @param flow
     * @param cost
     * @param lowerCapacity
     * @param upperCapacity
     * @param from
     * @param to
     */
    public Edge(int flow, int cost, int lowerCapacity, int upperCapacity, Vertex from, Vertex to)
    {
        this.flow = flow;
        this.cost = cost;
        this.lowerCapacity = lowerCapacity;
        this.upperCapacity = upperCapacity;
        this.from = from;
        this.to = to;
    }

    /**
     * Indicates that the edge flow reaches the lower bound
     *
     * @return {@code flow == lowerCapacity}
     */
    public boolean hasLowerBound()
    {
        return flow == lowerCapacity;
    }

    /**
     * Indicates that the edge flow reaches the upper bound
     *
     * @return {@code flow == capacity}
     */
    public boolean hasUpperBound()
    {
        return flow == upperCapacity;
    }

    /**
     * Gets the reduced costs.
     *
     * @return  {@code cost - from.potential + to.potential}
     */
    public int reducedCost()
    {
        return cost - from.potential + to.potential;
    }
    
    public void moderatePotentials(Vertex changed, int change)
    {
        // red cost = cost - from.potential + to.potential
        if(changed == to)
            from.potential += change;
        else
            to.potential -= change;
    }
}
