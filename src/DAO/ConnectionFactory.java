package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    // CONECTA
    private static final String URL = "jdbc:mysql://db-malvader.c7miwyc2szll.us-east-2.rds.amazonaws.com:3306/db_malvader";
    private static final String USER = "AllanBanco";
    private static final String PASSWORD = "Banco.lula13";


    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new SQLException("Erro ao estabelecer conexão com o banco de dados", e);
        }
    }
}
