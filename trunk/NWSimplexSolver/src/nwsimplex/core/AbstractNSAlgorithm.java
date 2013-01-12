package nwsimplex.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import nwsimplex.graph.Edge;
import nwsimplex.graph.Graph;
import nwsimplex.graph.SpanningTree;
import nwsimplex.graph.SpanningTree.Branch;
import nwsimplex.graph.SpanningTree.CycleIterator;
import nwsimplex.graph.Vertex;

/**
 * A class which performs the
 *
 * @author mfj
 */
public abstract class AbstractNSAlgorithm implements Runnable
{

    private int minDelta;
    private Edge leaving;
    private SpanningTree.Branch branchOfLeavingEdge;
    private LinkedList<Edge> forwardEdges = new LinkedList<Edge>();
    private LinkedList<Edge> backwardEdges = new LinkedList<Edge>();
    private Graph graph;
    private SpanningTree spanningTree;

    public AbstractNSAlgorithm(Graph graph, SpanningTree spanningTree)
    {
        this.graph = graph;
        this.spanningTree = spanningTree;
    }

    /**
     * Gets the collection of the upperbound edges.
     *
     * @return
     */
    public abstract Collection<Edge> getLowerBoundEdges();

    /**
     * Gets the collection of the upperbound edges.
     *
     * @return
     */
    public abstract Collection<Edge> getUpperBoundEdges();

    /**
     * Gets the next entering arc.
     *
     * @return
     */
    public abstract Edge getNextEntering();

    /**
     * Initializes the Spanningtree from the given vertices within the graph.
     */
    protected void initializeSpanningTree()
    {
        Collection<Edge> lowerBoundEdges = getLowerBoundEdges();
        Iterator<Vertex> iter = graph.vertexIterator();
        while (iter.hasNext())
        {
            Vertex v = iter.next();
            if (v.nettoBalance() > 0)
            {
                // TODO implement this method
            }
            else
            {
                // TODO implement this method
            }
        }
    }

    /**
     * Runs the networksimplex algorithm.
     */
    @Override
    public void run()
    {
        // initialize the spanning tree from the given graph
        initializeSpanningTree();

        Edge entering;
        while ((entering = getNextEntering()) != null)
        {
            reduceCosts(entering);
            if (leaving.hasUpperBound())
                getUpperBoundEdges().add(leaving);
            else
                getLowerBoundEdges().add(leaving);
        }
    }

    private void reduceCosts(Edge entering)
    {
        forwardEdges.clear();
        backwardEdges.clear();
        
        if (entering.hasUpperBound())
        {
            forwardEdges.add(entering);
            minDelta = entering.upperCapacity - entering.flow;
            identifyCycle(entering.from, entering.to, SpanningTree.Branch.Left);
            augment();
            
            if (branchOfLeavingEdge == SpanningTree.Branch.Left)
                spanningTree.addEdge(entering, leaving, entering.to);
            else
                spanningTree.addEdge(entering, leaving, entering.from);
        }
        else
        {
            backwardEdges.add(entering);
            minDelta = entering.flow - entering.upperCapacity;
            identifyCycle(entering.to, entering.from, SpanningTree.Branch.Right);
            augment();
            
            if (branchOfLeavingEdge == SpanningTree.Branch.Left)
                spanningTree.addEdge(entering, leaving, entering.from);
            else
                spanningTree.addEdge(entering, leaving, entering.to);
        }
    }

    private void identifyCycle(Vertex left, Vertex right, SpanningTree.Branch preferedLeaving)
    {
        SpanningTree.CycleIterator iter = spanningTree.CYCLE_ITERATOR_INSTANCE;
        iter.resetVertices(left, right);

        while (iter.hasNext())
        {
            Edge e = iter.next();

            if (iter.isInOrientation())
            {
                forwardEdges.add(e);
                updateMinDelta(e.upperCapacity - e.flow, e, iter, preferedLeaving);
            }
            else
            {
                backwardEdges.add(e);
                updateMinDelta(e.flow - e.lowerCapacity, e, iter, preferedLeaving);
            }
        }
    }

    private void updateMinDelta(int delta, Edge e, CycleIterator iter, Branch preferedLeaving)
    {
        if (delta < minDelta || (delta == minDelta && iter.getCurrentBranch() == preferedLeaving))
        {
            minDelta = delta;
            leaving = e;
            branchOfLeavingEdge = iter.getCurrentBranch();
        }
    }

    private void augment()
    {
        for (Edge edge : forwardEdges)
            edge.flow -= minDelta;
        for (Edge edge : backwardEdges)
            edge.flow += minDelta;
    }

}