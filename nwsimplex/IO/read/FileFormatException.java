package nwsimplex.IO.read;

/**
 * Indicates that the read file is not valid formatted.
 * @author Kseniya
 */
public class FileFormatException extends RuntimeException {

    /**
     * Creates a new instance of
     * <code>FileFormatException</code> without detail message.
     */
    public FileFormatException() {
    }

    /**
     * Constructs an instance of
     * <code>FileFormatException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public FileFormatException(String msg) {
        super(msg);
    }
    
    /**
     * Allows to protocol additional arguments in the error message.
     * @param msg the detail message
     * @param args 
     */
    public FileFormatException(String msg, Object... args) {
        super(String.format(msg, args));
    }
}
