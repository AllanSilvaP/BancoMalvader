package DAO;

import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import util.DBUtil;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ContaDAO {

    private static final Logger logger = Logger.getLogger(ContaDAO.class.getName());
    
    public boolean clienteExiste(int id_cliente) throws SQLException {
        String sql = "SELECT 1 FROM cliente WHERE id_cliente = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_cliente);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se encontrou um cliente
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao verificar se o cliente existe.", e);
            throw e;
        }
    }

    public int salvarConta(Conta conta) {
        String sqlConta = """
            INSERT INTO conta (numero_conta, agencia, saldo, tipo_conta, id_cliente)
            VALUES (?, ?, ?, ?, ?)
        """;
        String sqlContaCorrente = """
            INSERT INTO conta_corrente (limite, data_vencimento, id_conta)
            VALUES (?, ?, ?)
        """;
        String sqlContaPoupanca = """
            INSERT INTO conta_poupanca (taxa_rendimento, id_conta)
            VALUES (?, ?)
        """;

        int idContaInserida = -1;
        Connection conn = null;

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            // Inserir na tabela 'conta'
            try (PreparedStatement stmtConta = conn.prepareStatement(sqlConta, Statement.RETURN_GENERATED_KEYS)) {
                stmtConta.setString(1, conta.getNumeroConta());
                stmtConta.setString(2, conta.getAgencia());
                stmtConta.setDouble(3, conta.getSaldo());
                stmtConta.setString(4, conta.getTipoConta());
                stmtConta.setInt(5, conta.getId_cliente());
                stmtConta.executeUpdate();

                try (ResultSet rsConta = stmtConta.getGeneratedKeys()) {
                    if (rsConta.next()) {
                        idContaInserida = rsConta.getInt(1);
                    }
                }
            }

            // Inserir em conta_corrente ou conta_poupanca
            if (conta instanceof ContaCorrente corrente) {
                try (PreparedStatement stmtCorrente = conn.prepareStatement(sqlContaCorrente)) {
                    stmtCorrente.setDouble(1, corrente.getLimite());
                    stmtCorrente.setDate(2, Date.valueOf(corrente.getDataVencimento()));
                    stmtCorrente.setInt(3, idContaInserida);
                    stmtCorrente.executeUpdate();
                }
            } else if (conta instanceof ContaPoupanca poupanca) {
                try (PreparedStatement stmtPoupanca = conn.prepareStatement(sqlContaPoupanca)) {
                    stmtPoupanca.setDouble(1, poupanca.getTaxaRendimento());
                    stmtPoupanca.setInt(2, idContaInserida);
                    stmtPoupanca.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao salvar conta no banco de dados.", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.SEVERE, "Erro ao realizar rollback.", rollbackEx);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    logger.log(Level.SEVERE, "Erro ao fechar conexão.", closeEx);
                }
            }
        }

        return idContaInserida;
    }




    // Deletar conta por número
    public boolean deletarContaPorNumero(String numeroConta) throws SQLException {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("O número da conta não pode ser nulo ou vazio.");
        }

        String sql = "DELETE FROM conta WHERE numero_conta = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao deletar conta pelo número.", e);
            throw e;
        }
    }
    
    // Deletar conta por ID
    public boolean deletarConta(int idConta) throws SQLException {
        if (idConta <= 0) {
            throw new IllegalArgumentException("O ID da conta deve ser maior que zero.");
        }

        // Verifica se a conta tem saldo negativo antes de excluir
        Conta conta = buscarContaPorId(idConta);
        if (conta != null && conta.getSaldo() < 0) {
            throw new IllegalStateException("A conta possui saldo negativo. Não pode ser excluída.");
        }

        String sql = "DELETE FROM conta WHERE id_conta = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idConta);
            return stmt.executeUpdate() > 0; // Retorna true se ao menos uma linha foi afetada
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao deletar conta pelo ID: " + idConta, e);
            throw e;
        }
    }



    public Cliente buscarClientePorId(int id_cliente) throws SQLException {
        Cliente cliente = null;
        String sql = "SELECT c.id_cliente, u.nome, u.cpf, u.data_nascimento, u.telefone, " +
                     "u.tipo_usuario, u.senha " +
                     "FROM cliente c " +
                     "JOIN usuario u ON c.id_usuario = u.id_usuario " +
                     "WHERE c.id_cliente = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_cliente);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getDate("data_nascimento").toLocalDate(),
                        rs.getString("telefone"),
                        null, // Endereço, caso seja necessário buscar separadamente
                        rs.getString("senha"),
                        null  // Contas associadas, se necessário
                    );
                    cliente.setId_cliente(id_cliente);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar cliente por ID.", e);
            throw e;
        }
        return cliente;
    }




    // Atualizar conta
    public void atualizarConta(Conta conta) throws SQLException {
        if (conta == null) {
            throw new IllegalArgumentException("A conta não pode ser nula.");
        }

        String sqlConta = "UPDATE conta SET numero_conta = ?, agencia = ?, saldo = ?, tipo_conta = ?, id_cliente = ? WHERE id_conta = ?";
        String sqlContaCorrente = "UPDATE conta_corrente SET limite = ?, vencimento = ? WHERE id_conta = ?"; // Atualização na tabela conta_corrente

        try (Connection conn = DBUtil.getConnection()) {
            if (conn == null) {
                throw new SQLException("Não foi possível obter uma conexão com o banco de dados.");
            }

            // Atualizar na tabela conta
            try (PreparedStatement stmt = conn.prepareStatement(sqlConta)) {
                stmt.setString(1, conta.getNumeroConta());
                stmt.setString(2, conta.getAgencia());
                stmt.setDouble(3, conta.getSaldo());
                stmt.setString(4, conta.getTipoConta());
                stmt.setInt(5, conta.getId_cliente());
                stmt.setInt(6, conta.getId_conta());

                stmt.executeUpdate();
            }

            // Se for ContaCorrente, atualizar na tabela conta_corrente
            if (conta instanceof ContaCorrente) {
                try (PreparedStatement stmtCorrente = conn.prepareStatement(sqlContaCorrente)) {
                    ContaCorrente contaCorrente = (ContaCorrente) conta;
                    stmtCorrente.setDouble(1, contaCorrente.getLimite());
                    stmtCorrente.setDate(2, Date.valueOf(contaCorrente.getDataVencimento()));
                    stmtCorrente.setInt(3, conta.getId_conta()); // Usando id_conta da conta
                    stmtCorrente.executeUpdate();
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar conta no banco de dados.", e);
            throw e;
        }
    }

    // Mapear o tipo de conta (Corrente ou Poupança)
    private Conta mapearConta(ResultSet rs) throws SQLException {
        String tipoConta = rs.getString("tipo_conta");
        if ("CORRENTE".equalsIgnoreCase(tipoConta)) {
            return new ContaCorrente(
                    rs.getString("numero_conta"),
                    rs.getString("agencia"),
                    rs.getDouble("saldo"),
                    rs.getString("tipo_conta"),
                    rs.getInt("id_cliente"),
                    rs.getDouble("limite"),
                    rs.getDate("vencimento").toLocalDate()
            );
        } else if ("POUPANCA".equalsIgnoreCase(tipoConta)) {
            return new ContaPoupanca(
                    rs.getString("numero_conta"),
                    rs.getString("agencia"),
                    rs.getDouble("saldo"),
                    rs.getInt("id_cliente")
            );
        } else {
            throw new SQLException("Tipo de conta desconhecido: " + tipoConta);
        }
    }

    // Buscar conta por ID
    public Conta buscarContaPorId(int idConta) throws SQLException {
        Conta conta = null;
        String sql = "SELECT * FROM conta WHERE id_conta = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idConta);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    conta = mapearConta(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao buscar conta por ID.", e);
            throw e;
        }
        return conta;
    }
}

