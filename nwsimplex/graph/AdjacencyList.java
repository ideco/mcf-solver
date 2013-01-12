package nwsimplex.graph;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A class that represents a simple adjacency lists.
 * @author mfj
 */
public class AdjacencyList implements Iterator<Edge>, Iterable<Edge>
{

    private Entry root, next;

    /**
     * Adds a new Edge to this list.
     * @param e the edge to add.
     */
    public void add(Edge e)
    {
        root = root == null ? new Entry(e, null) : new Entry(e, root);
    }

    @Override
    public boolean hasNext()
    {
        return next != null;
    }

    @Override
    public Edge next()
    {
        try
        {
            Edge key = next.edge;
            next = next.next;
            return key;
        }
        catch (NullPointerException npe)
        {
            throw new NoSuchElementException();
        }
    }

    /**
     * Not supported.
     */
    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Not yet.");
    }

    /**
     * Resets the iterator.
     */
    public void reset()
    {
        next = root;
    }

    @Override
    public Iterator<Edge> iterator()
    {
        reset();
        return this;
    }
    
    // a simple entry class
    private class Entry
    {
        Edge edge;
    
        Entry next;

        Entry(Edge edge, Entry next)
        {
            this.edge = edge;
            this.next = next;
        }
    }
}
