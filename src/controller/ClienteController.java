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
    
    public void exibirSaldo(int numeroConta) throws SQLException {
        try {
            double saldo = clienteDAO.buscarSaldoPorNumeroConta(numeroConta); // Usa o número da conta como int
            System.out.printf("Saldo atual da conta %d: R$ %.2f\n", numeroConta, saldo);
        } catch (SQLException e) {
            System.out.println("Erro ao exibir saldo: " + e.getMessage());
            throw e;
        }
    }
    

    public void realizarDeposito(int numeroConta, double valor) throws SQLException {
        if (valor > 0) {
            try {
                clienteDAO.atualizarSaldoPorNumeroConta(numeroConta, valor); // Novo método baseado em numeroConta
                System.out.println("Depósito realizado com sucesso.");
            } catch (SQLException e) {
                System.out.println("Erro ao realizar depósito: " + e.getMessage());
                throw e;
            }
        } else {
            System.out.println("Valor de depósito inválido.");
        }
    }

    public void realizarSaque(int numeroConta, double valor) throws SQLException {
        try {
            double saldo = clienteDAO.buscarSaldoPorNumeroConta(numeroConta); // Alterado para usar numeroConta
            if (saldo >= valor && valor > 0) {
                clienteDAO.atualizarSaldoPorNumeroConta(numeroConta, -valor); // Atualizar com valor negativo para saque
                System.out.println("Saque realizado com sucesso.");
            } else {
                System.out.println("Saldo insuficiente ou valor inválido.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao realizar saque: " + e.getMessage());
            throw e;
        }
    }

    public void exibirExtrato(int numeroConta) {
        String sql = """
            SELECT tipo_transacao, valor, data_hora 
            FROM transacao 
            WHERE numero_conta = ? 
            ORDER BY data_hora DESC
        """;

        try (Connection conn = ConnectionFactory.getConnection()) {
            executarConsultaGenerica(conn, sql, numeroConta); // Atualizado para passar numeroConta
        } catch (SQLException e) {
            System.out.println("Erro ao consultar extrato: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void executarConsultaGenerica(Connection conn, String sql, int numeroConta) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroConta); // Alterado para aceitar numeroConta
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("Tipo: %s, Valor: %.2f, Data/Hora: %s\n",
                            rs.getString("tipo_transacao"),
                            rs.getDouble("valor"),
                            rs.getTimestamp("data_hora"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao executar consulta genérica: " + e.getMessage());
            throw e;
        }
    }
}
