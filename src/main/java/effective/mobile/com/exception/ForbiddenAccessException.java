package effective.mobile.com.exception;

public class ForbiddenAccessException extends InvalidActionException {
    public ForbiddenAccessException(Object userId, Class<?> recourseClass, Object id) {
        super(String.format("User with id = %s have not access to recourse %s with id = %s", userId,
            recourseClass.getName(), id));
    }

    public ForbiddenAccessException(Object userId, Class<?> recourseClass, Object id, String reason) {
        super(String.format("User with id = %s have not access to recourse %s with id = %s; Reason - %s", userId,
            recourseClass.getName(), id, reason));
    }
}
