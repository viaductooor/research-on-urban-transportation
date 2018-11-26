package exception;
import java.lang.Exception;

public class GraphException extends Exception{

    private static final long serialVersionUID = 100000000333213L;

    public GraphException(String message){
        super(message);
    }
}

