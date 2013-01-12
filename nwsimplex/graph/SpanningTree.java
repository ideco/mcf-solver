package nwsimplex.graph;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * A Datastructure that represents a Spanning Tree, which is a tree that
 * contains all vertices of a Graph. Since each SpanningTree is connected to
 * each node adding a new edge requires to remove another edge, to obtain a
 * cyclefree tree structure.
 *
 * Since this SpanningTree is used for the networksimplex algorithm. each edge
 * within the spanningtree satisfies has reduced costs of zero.
 *
 * @author mfj
 */
public class SpanningTree
{

    private Vertex root;
    private final TreeIterator treeIterator;
    /**
     * An instance of the CycleIterator which can be recycled by resetting.
     */
    public final CycleIterator CYCLE_ITERATOR_INSTANCE;

    /**
     * Instantiates an empty cycletree which only constists of a single root and
     * no edge.
     */
    public SpanningTree()
    {
        root = new Vertex(-1, 0);
        root.depth = 0;
        treeIterator = new TreeIterator(root);
        CYCLE_ITERATOR_INSTANCE = new CycleIterator(null, null);
    }

    /**
     * Gets the root vertex of this cycle tree.
     *
     * @return the root of this SpanningTree
     */
    public Vertex getRoot()
    {
        return root;
    }

    /**
     * Tests if the speciefied edge is connected to the root of the
     * spanningtree.
     *
     * @param e the edge which may be connected to the root
     * @return {@code e.to == root || e.from == root}
     */
    public boolean isConnectedToRoot(Edge e)
    {
        return e.to == root || e.from == root;
    }

    /**
     * Tests if the specified edge is upward pointing within the spanningtree
     * meaning that the target vertex is closer to the root then the origin.
     * Note that this ist not the case if this edge is not contained within this
     * spanning tree.
     *
     * @param e the edge to test
     * @return {@code e.to == e.from.parent}
     */
    public boolean isUpwardPointing(Edge e)
    {
        return e.to == e.from.parent;
    }

    /**
     * Tests if the specified edge is downward pointing within the spanningtree
     * meaning that the origin is closer to the root then the target vertex.
     * Note that this ist not the case if this edge is not contained within this
     * spanning tree.
     *
     * @param e the edge to test
     * @return {@code e.from == e.to.parent}
     */
    public boolean isDownwardPointing(Edge e)
    {
        return e.from == e.to.parent;
    }

    /**
     * Tests if the specified edge is contained within this tree.
     *
     * @param e the edge to test
     * @return {@code isUpwardPointing(e) || isDownwardPointing(e)}
     */
    public boolean isTreeEdge(Edge e)
    {
        return isUpwardPointing(e) || isDownwardPointing(e);
    }

    /**
     * Adds a new Edge which connects a vertex to the root node with a upward
     * pointing edge.
     *
     * @param flow the edge flow
     * @param cost the cost of the edge
     * @param lowerCapacity the lower capacity
     * @param upperCapacity the upper capacity
     * @param to the origin vertex
     * @return the Edge that was added
     */
    public Edge addEdgeFromRoot(int flow, int cost, int lowerCapacity, int upperCapacity, Vertex to)
    {
        Edge e = new Edge(flow, cost, lowerCapacity, upperCapacity, root, to);
        addToTree(to, e);
        return e;
    }

    /**
     * Adds a new Edge which connects a vertex to the root node with a downward
     * pointing edge.
     *
     * @param flow the edge flow
     * @param cost the cost of the edge
     * @param lowerCapacity the lower capacity
     * @param upperCapacity the upper capacity
     * @param from the origin vertex
     * @return the Edge that was added
     */
    public Edge addEdgeToRoot(int flow, int cost, int lowerCapacity, int upperCapacity, Vertex from)
    {
        Edge e = new Edge(flow, cost, lowerCapacity, upperCapacity, from, root);
        addToTree(from, e);
        return e;
    }

    /**
     * Adds a new edge to this spanning tree and removes the edge which is the
     * upward connecting arch of the lowerLeaving vertex.
     *
     * @param entering the entering arch
     * @param leaving the eaving arch
     * @param lowerEntering the lower vertex of the entering arch
     */
    public void addEdge(Edge entering, Edge leaving, Vertex lowerEntering)
    {
        // removes leaving edge from tree
        Vertex lowerLeaving = leaving.from.depth > leaving.to.depth ? leaving.from : leaving.to;
        removeFromTree(lowerLeaving);

        // reverse predessesor relations from leaving to entering arc
        Vertex next, prev = null, curr = lowerEntering;
        while (curr != null)
        {
            next = curr.parent;
            curr.parent = prev;
            prev = curr;
            curr = next;
        }


        // add entering arc
        addToTree(lowerEntering, entering);
        update(lowerEntering);
    }

    protected void removeFromTree(Vertex v)
    {
        Vertex parent = v.parent;
        v.treeEdge = null;
        v.parent = null;

        Vertex left = v.leftSibbling, right = v.rightSibbling;
        if (left != null)
            left.rightSibbling = right;
        if (right != null)
            right.leftSibbling = left;
        v.leftSibbling = v.rightSibbling = null;

        if (v == parent.child)
            parent.child = v.rightSibbling;
    }

    protected void addToTree(Vertex v, Edge e)
    {
        v.treeEdge = e;
        Vertex parent = e.from == v ? e.to : e.from;
        v.parent = parent;
        if (parent.child == null)
            parent.child = v;
        else
        {
            parent.child.leftSibbling = v;
            v.rightSibbling = parent.child;
            parent.child = v;
        }
    }

    private void update(Vertex lowerEntering)
    {
        treeIterator.setRoot(lowerEntering);
        Vertex v;
        while ((v = treeIterator.next()) != null)
        {
            Edge e = v.treeEdge;
            v.potential += isUpwardPointing(e)
                    ? e.reducedCost() - v.potential : v.potential - e.reducedCost();

            v.depth = v.parent.depth;
        }
    }

    /**
     * Gets an Iterator which traverses a cycle created by an imaginary edge
     * connecting two branches of this tree.
     *
     * @param left a vertex representing the left branch
     * @param right a vertex representing the right branch
     * @return a Cycleiterator
     */
    public CycleIterator cycleIterator(Vertex left, Vertex right)
    {
        return new CycleIterator(left, right);
    }

    /**
     * Gets an Iterator which traverses a subtree starting with a given root
     * vertex.
     *
     * @param root the root vertex of the subtree
     * @return
     */
    public TreeIterator treeIterator(Vertex root)
    {
        return new TreeIterator(root);
    }

    /**
     * An class which can be used to traverse all vertices within a subtree in
     * bfs order. This Iterator can be reset by defining a new root vertex.
     */
    public class TreeIterator implements Iterator<Vertex>
    {

        Vertex next;
        Queue<Vertex> queue;

        /**
         * Instantiates a new TreeIterator from the root of a subtree
         *
         * @param root the root vertex of the subtree.
         */
        public TreeIterator(Vertex root)
        {
            this.next = root;
            this.queue = new LinkedList<Vertex>();

            if (next.child != null)
                queue.add(next.child);
        }

        /**
         * Sets the root of the subtree. this method can be used to reset this
         * iterator.
         *
         * @param root the new root vertex of the subtree.
         */
        public void setRoot(Vertex root)
        {
            this.next = root;
            queue.clear();

            if (next.child != null)
                queue.add(next.child);
        }

        @Override
        public boolean hasNext()
        {
            return next != null;
        }

        @Override
        public Vertex next()
        {
            Vertex result = next;

            next = queue.poll();
            if (next != null)
            {
                if (next.child != null)
                    queue.add(next.child);

                Vertex neigh = next;
                while ((neigh = neigh.rightSibbling) != null)
                    queue.add(neigh);
            }

            return result;
        }

        /*
         * Not supported.
         */
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

    /**
     * An Iterator which traverses all Edges within a cycle if two branches of
     * this tree are connected by an imaginary edge between two specified nodes.
     */
    public class CycleIterator implements Iterator<Edge>
    {

        private Branch currBranch = Branch.Undefined;
        private Edge current;
        private Vertex left;
        private Vertex right;

        /**
         * Note that the orientation of this cycle is in direction left to right
         * of the imaginary edge.
         *
         * @param left
         * @param right
         */
        public CycleIterator(Vertex left, Vertex right)
        {
            this.left = left;
            this.right = right;
        }

        /**
         * Gets the branch of the last returned edge, which may be either an
         * element of the left direction or the right direction.
         *
         * @return the direction of the last returned edge or undefined if no
         * traversion step was performed.
         */
        public Branch getCurrentBranch()
        {
            return currBranch;
        }

        /**
         * Determines if the last returned edge is inorientation of this cycle.
         *
         * @return true if the edge is in direction left to right, false
         * otherwise @throw IllegalStateException if no iteration was performed
         */
        public boolean isInOrientation()
        {
            switch (currBranch)
            {
                case Left:
                    return SpanningTree.this.isDownwardPointing(current);
                case Right:
                    return SpanningTree.this.isUpwardPointing(current);
                case Undefined:
                default:
                    throw new IllegalStateException();
            }
        }

        /**
         * Gets the cycleroot if the end of the traversion is reached.
         *
         * @return the cycle root or null if there exists a next element.
         */
        public Vertex getCycleRoot()
        {
            return hasNext() ? null : left;
        }

        /**
         * Resets the iterator to its initial state.
         *
         * @param left
         * @param right
         */
        public void resetVertices(Vertex left, Vertex right)
        {
            this.left = left;
            this.right = right;
            this.currBranch = Branch.Undefined;
            this.current = null;
        }

        @Override
        public boolean hasNext()
        {
            return left != right;
        }

        @Override
        public Edge next()
        {
            if (left.depth >= right.depth)
            {
                current = left.treeEdge;
                left = left.parent;
                currBranch = Branch.Left;
            }
            else
            {
                current = right.treeEdge;
                right = right.parent;
                currBranch = Branch.Right;
            }

            return current;
        }

        /*
         * Not supported.
         */
        @Override
        public void remove()
        {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

    /**
     * The branch used by the CycleIterator
     */
    public enum Branch
    {

        Left, Right, Undefined
    }
}