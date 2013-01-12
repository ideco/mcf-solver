package nwsimplex.util;

import java.util.Comparator;
import nwsimplex.graph.Edge;

/**
 * Compares two edges by their absolut value of the reduced cost. This can be 
 * interpreted as measure for the violation of the optamility condition.
 * @author mfj
 */
public class ReducedCostComparator implements Comparator<Edge>
{
    /**
     * a static instance of the <tt>ReducedCostComparator<tt>
     */
    public final static ReducedCostComparator RCC = new ReducedCostComparator();
    
    @Override
    public int compare(Edge t1, Edge t2)
    {
        return Math.abs(t1.reducedCost()) - Math.abs(t1.reducedCost()); 
    }
}