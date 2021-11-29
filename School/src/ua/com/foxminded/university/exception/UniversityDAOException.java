package ua.com.foxminded.university.exception;

import java.sql.SQLException;

public class UniversityDAOException extends SQLException {

    public UniversityDAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
