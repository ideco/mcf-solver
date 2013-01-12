/*
 #  * To change this template, choose Tools | Templates
 #  * and open the template in the editor.
 */
package nwsimplex.graph;

import java.util.ArrayList;
import nwsimplex.graph.SpanningTree.TreeIterator;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 *
 * @author mfj
 */
public class SpanningTreeTest
{
    
    SpanningTree sTree = new SpanningTree();
    ArrayList<Vertex> vertices = new ArrayList<Vertex>();
    ArrayList<Edge> edges = new ArrayList<Edge>();

    public SpanningTreeTest()
    {
    }

    @Before
    public void before()
    {
        sTree = new SpanningTree();
        vertices.clear();
        edges.clear();

        vertices.add(new Vertex(0, 0));
        vertices.add(new Vertex(1, 0));
        vertices.add(new Vertex(2, 0));
        vertices.add(new Vertex(3, 0));
        vertices.add(new Vertex(4, 0));
        vertices.add(new Vertex(5, 0));
        vertices.add(new Vertex(6, 0));
        vertices.add(new Vertex(7, 0));
        vertices.add(new Vertex(8, 0));

        Edge addEdgeToRoot = sTree.addEdgeToRoot(0, 0, 0, 0, vertices.get(3));
        Edge addEdgeFromRoot = sTree.addEdgeFromRoot(0, 0, 0, 0, vertices.get(0));

        edges.add(addEdgeToRoot);
        edges.add(addEdgeFromRoot);

        edges.add(new Edge(0, 0, 0, 0, vertices.get(1), vertices.get(0)));
        edges.add(new Edge(0, 0, 0, 0, vertices.get(1), vertices.get(2)));
        edges.add(new Edge(0, 0, 0, 0, vertices.get(4), vertices.get(3)));
        edges.add(new Edge(0, 0, 0, 0, vertices.get(4), vertices.get(5)));
    }

    @Test
    public void testIsConnectedToRoot()
    {
        assertTrue(sTree.isConnectedToRoot(edges.get(0)));
        assertTrue(sTree.isConnectedToRoot(edges.get(1)));

        assertFalse(sTree.isConnectedToRoot(edges.get(2)));
        assertFalse(sTree.isConnectedToRoot(edges.get(3)));
        assertFalse(sTree.isConnectedToRoot(edges.get(4)));
        assertFalse(sTree.isConnectedToRoot(edges.get(5)));
    }

    @Test
    public void testIsUpwardPointing()
    {
        assertTrue(sTree.isUpwardPointing(edges.get(0)));
        assertFalse(sTree.isUpwardPointing(edges.get(1)));
        assertFalse(sTree.isUpwardPointing(edges.get(2)));
    }

    @Test
    public void testIsDownwardPointing()
    {
        assertTrue(sTree.isDownwardPointing(edges.get(1)));
        assertFalse(sTree.isDownwardPointing(edges.get(0)));
        assertFalse(sTree.isDownwardPointing(edges.get(2)));
    }

    @Test
    public void testAddEdgeFromRoot()
    {
    }

    @Test
    public void testAddEdgeToRoot()
    {
    }

    @Test
    public void testAddEdge()
    {
        sTree.addToTree(vertices.get(1), edges.get(2));
        sTree.addToTree(vertices.get(2), edges.get(3));
        sTree.addToTree(vertices.get(4), edges.get(4));
        sTree.addToTree(vertices.get(5), edges.get(5));

        assertTrue(vertices.get(0) == vertices.get(1).parent);
        vertices.get(0).depth = 999;
        
        
        Edge e = new Edge(-1, -1, -1, -1, vertices.get(2), vertices.get(5));
        sTree.addEdge(e, edges.get(1), vertices.get(2));

        assertTrue(sTree.isTreeEdge(e));
        assertFalse(sTree.isTreeEdge(edges.get(1)));

        // test updatet predesessor relations
        assertTrue(vertices.get(1) == vertices.get(0).parent);
        assertTrue(vertices.get(2) == vertices.get(1).parent);
        assertTrue(vertices.get(5) == vertices.get(2).parent);
        assertTrue(vertices.get(4) == vertices.get(5).parent);
        assertTrue(vertices.get(3) == vertices.get(4).parent);
        assertTrue(sTree.getRoot() == vertices.get(3).parent);
    }

    @Test
    public void testCycleIterator()
    {
    }

    @Test
    public void testTreeIterator()
    {
        sTree.addToTree(vertices.get(1), edges.get(2));
        sTree.addToTree(vertices.get(2), edges.get(3));
        sTree.addToTree(vertices.get(4), edges.get(4));
        sTree.addToTree(vertices.get(5), edges.get(5));

        int count = 0;
        SpanningTree.TreeIterator i = sTree.treeIterator(sTree.getRoot());
        while(i.hasNext())
        {
            count++;
            i.next();
        }
        
        assertEquals(7, count);
    }
    
    
    public boolean isValidTree()
    {
        Vertex root = sTree.getRoot();
        TreeIterator iter = sTree.treeIterator(root);
        while(iter.hasNext())
        {
            Vertex next = iter.next();
            Edge e = next.treeEdge;
            if(e != null && e.reducedCost() != 0)
                return false;
        }
        
        return true;
    }
}
