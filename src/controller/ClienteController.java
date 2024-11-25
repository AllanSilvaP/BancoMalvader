package controller;

import DAO.ClienteDAO;
import DAO.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteController {
    private ClienteDAO clienteDAO;

    public ClienteController(Connection connection) {
        this.clienteDAO = new ClienteDAO();
    }

    public ClienteController() throws SQLException {
        this(ConnectionFactory.getConnection());
    }

    public void exibirSaldo(int idCliente) throws SQLException {
        try {
            double saldo = clienteDAO.buscarSaldoPorContaId(idCliente);
            System.out.printf("Saldo atual da conta do cliente %d: R$ %.2f\n", idCliente, saldo);
        } catch (SQLException e) {
            System.out.println("Erro ao exibir saldo: " + e.getMessage());
            throw e;  
        }
    }

    public void realizarDeposito(int idCliente, double valor) throws SQLException {
        if (valor > 0) {
            try {
                clienteDAO.atualizarSaldo(idCliente, valor);
                System.out.println("Depósito realizado com sucesso.");
            } catch (SQLException e) {
                System.out.println("Erro ao realizar depósito: " + e.getMessage());
                throw e;  
            }
        } else {
            System.out.println("Valor de depósito inválido.");
        }
    }

    public void realizarSaque(int idCliente, double valor) throws SQLException {
        try {
            double saldo = clienteDAO.buscarSaldoPorContaId(idCliente);
            if (saldo >= valor && valor > 0) {
                clienteDAO.atualizarSaldo(idCliente, -valor);
                System.out.println("Saque realizado com sucesso.");
            } else {
                System.out.println("Saldo insuficiente ou valor inválido.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao realizar saque: " + e.getMessage());
            throw e; 
        }
    }

    public void exibirExtrato(int idCliente) {
        String sql = "SELECT tipo_transacao, valor, data_hora FROM transacao WHERE id_cliente = ? ORDER BY data_hora DESC";
        try (Connection conn = ConnectionFactory.getConnection()) {
            executarConsultaGenerica(conn, sql, idCliente);
        } catch (SQLException e) {
            System.out.println("Erro ao consultar extrato: " + e.getMessage());
            e.printStackTrace(); 
        }
    }

    private void executarConsultaGenerica(Connection conn, String sql, int idCliente) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.printf("Tipo: %s, Valor: %.2f, Data/Hora: %s\n",
                        rs.getString("tipo_transacao"),
                        rs.getDouble("valor"),
                        rs.getTimestamp("data_hora"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao executar consulta genérica: " + e.getMessage());
            throw e;  
        }
    }
}
