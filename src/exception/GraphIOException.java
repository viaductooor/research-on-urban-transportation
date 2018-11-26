package exception;
import java.lang.Exception;

public class GraphIOException extends Exception{

    private static final long serialVersionUID = 100000000333214L;

    public GraphIOException(String message){
        super(message);
    }
}