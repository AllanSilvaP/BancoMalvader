package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // Variáveis para conexão
	private static String url = "jdbc:mysql://db-malvader.c7miwyc2szll.us-east-2.rds.amazonaws.com:3306/db_malvader"; 
    private static String user = "AllanBanco"; 
    private static String password = "Banco.lula13";

    // Método estático para obter a conexão
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new SQLException("Erro ao estabelecer conexão com o banco de dados", e);
        }
    }
}
