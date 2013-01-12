package nwsimplex.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import nwsimplex.graph.Vertex;

/**
 *
 * @author Kseniya
 */
public abstract class VertexUtils {

    /**
     * Sorts the vertices by their IDs.
     * @param vertices to sort
     * @return new container with same vertices, but sorted
     */
    public static Collection<Vertex> sortByID(Collection<Vertex> vertices) {
        // shallow copy since we keep vertex instances
        List<Vertex> retList = new ArrayList<>(vertices);
        Comparator<Vertex> idComparator = new Comparator<Vertex>() {
            @Override
            public int compare(Vertex firstVertex, Vertex secondVertex) {
                return new Integer(firstVertex.ID).compareTo(secondVertex.ID);
            }
        };
        Collections.sort(retList, idComparator);
        return retList;
    }

    /**
     * Finds vertex by a given ID.
     * @param vertices source container to search in
     * @param id of the vertex to be found
     * @return the vertex if found
     * @throws NoSuchElementException if no vertex with this ID exists in the 
     *         given collection
     */
    public static Vertex findByID(Collection<Vertex> vertices, int id) throws NoSuchElementException {
        for (Vertex vertex : vertices) {
            if (vertex.ID == id) {
                return vertex;
            }
        }
        throw new NoSuchElementException(String.format("No vertex with %d found.", id));
    }
}
