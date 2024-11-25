package exception;

import java.sql.SQLException;

public class ClienteNotFoundException extends SQLException {

	private static final long serialVersionUID = 1L;

	public ClienteNotFoundException(String message) {
        super(message);
    }
}
