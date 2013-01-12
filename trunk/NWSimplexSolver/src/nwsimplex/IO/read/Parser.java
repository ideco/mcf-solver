package nwsimplex.IO.read;

/**
 *
 * @author Kseniya
 */
public interface Parser<E> {

    /**
     * Reads the file on the given path and parses a desired instance.
     * @param filePath path to file
     * @return the parsed instance
     */
    public E parse(java.nio.file.Path filePath) throws java.io.IOException;
}
