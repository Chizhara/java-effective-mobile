package effective.mobile.com.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Class<?> entityClassName, Object idValue) {
        super(String.format("%s with id=%s was not found", entityClassName.getName(), idValue));
    }

    public NotFoundException(Class<?> entityClassName, String reason) {
        super(String.format("%s was not found; Cause: ", entityClassName.getName(), reason));
    }

}