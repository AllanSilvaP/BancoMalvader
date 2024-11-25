package DAO;

import model.Cliente;
import model.Endereco;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import util.DBUtil;

import java.lang.System.Logger.Level;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private static final System.Logger logger = System.getLogger(ClienteDAO.class.getName());

    public ClienteDAO() {
        // Inicializa o DAO
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
        Connection conn = null; // Declare connection outside the try

        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); // Begin transaction

            // Insert into "conta" table
            try (PreparedStatement stmtConta = conn.prepareStatement(sqlConta, Statement.RETURN_GENERATED_KEYS)) {
                stmtConta.setString(1, conta.getNumeroConta());
                stmtConta.setString(2, conta.getAgencia());
                stmtConta.setDouble(3, conta.getSaldo());
                stmtConta.setString(4, conta.getTipoConta());
                stmtConta.setInt(5, conta.getId_cliente());
                stmtConta.executeUpdate();

                // Get the generated ID for "conta"
                try (ResultSet rsConta = stmtConta.getGeneratedKeys()) {
                    if (rsConta.next()) {
                        idContaInserida = rsConta.getInt(1);
                    }
                }
            }

            // Insert into specific table for account type
            if (conta instanceof ContaCorrente corrente) {
                // Insert into "conta_corrente" table
                try (PreparedStatement stmtContaCorrente = conn.prepareStatement(sqlContaCorrente)) {
                    stmtContaCorrente.setDouble(1, corrente.getLimite());
                    stmtContaCorrente.setDate(2, Date.valueOf(corrente.getDataVencimento()));
                    stmtContaCorrente.setInt(3, idContaInserida);
                    stmtContaCorrente.executeUpdate();
                }
            } else if (conta instanceof ContaPoupanca poupanca) {
                // Insert into "conta_poupanca" table
                try (PreparedStatement stmtContaPoupanca = conn.prepareStatement(sqlContaPoupanca)) {
                    stmtContaPoupanca.setDouble(1, poupanca.getTaxaRendimento());
                    stmtContaPoupanca.setInt(2, idContaInserida);
                    stmtContaPoupanca.executeUpdate();
                }
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao salvar conta no banco de dados.", e);
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException rollbackEx) {
                    logger.log(System.Logger.Level.ERROR, "Erro ao realizar rollback.", rollbackEx);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Ensure connection is closed
                } catch (SQLException closeEx) {
                    logger.log(System.Logger.Level.ERROR, "Erro ao fechar conexão.", closeEx);
                }
            }
        }

        return idContaInserida;
    }




 // Salvar cliente no banco
    public int salvarCliente(Cliente cliente) {
        String sqlUsuario = """
            INSERT INTO usuario (nome, cpf, data_nascimento, telefone, tipo_usuario, senha) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        String sqlCliente = "INSERT INTO cliente (id_usuario) VALUES (?)";
        int idUsuarioInserido = -1;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            // Salvar usuário
            stmtUsuario.setString(1, cliente.getNome());
            stmtUsuario.setString(2, cliente.getCpf());
            stmtUsuario.setDate(3, Date.valueOf(cliente.getDataNascimento()));
            stmtUsuario.setString(4, cliente.getTelefone());
            stmtUsuario.setString(5, "CLIENTE");
            stmtUsuario.setString(6, cliente.getSenhaHash());
            stmtUsuario.executeUpdate();

            try (ResultSet rsUsuario = stmtUsuario.getGeneratedKeys()) {
                if (rsUsuario.next()) {
                    idUsuarioInserido = rsUsuario.getInt(1);
                }
            }

            // Salvar cliente associado ao usuário
            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                stmtCliente.setInt(1, idUsuarioInserido);
                stmtCliente.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Erro ao salvar cliente no banco de dados.", e);
            // Reabre a conexão e executa rollback
            try (Connection conn = ConnectionFactory.getConnection()) {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                logger.log(Level.ERROR, "Erro ao fazer rollback.", rollbackEx);
            }
        }

        return idUsuarioInserido; // Retorna o ID gerado do usuário
    }


    // Buscar cliente por ID
    public Cliente buscarClientePorId(int id) {
        String sql = """
            SELECT c.id_cliente, u.nome, u.cpf, u.data_nascimento, u.telefone, u.senha, 
                   e.cep, e.local, e.numero_casa, e.bairro, e.cidade, e.estado
            FROM cliente c
            INNER JOIN usuario u ON c.id_usuario = u.id_usuario
            LEFT JOIN endereco e ON u.id_usuario = e.id_usuario
            WHERE c.id_cliente = ?
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearCliente(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Erro ao buscar cliente por ID.", e);
        }
        return null;
    }

    // Mapear resultado para um objeto Cliente
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Endereco endereco = new Endereco(
            rs.getString("cep"),
            rs.getString("local"),
            rs.getInt("numero_casa"),
            rs.getString("bairro"),
            rs.getString("cidade"),
            rs.getString("estado")
        );

        Cliente cliente = new Cliente();
        cliente.setId_cliente(rs.getInt("id_cliente"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setEndereco(endereco);

        return cliente;
    }

    // Buscar saldo por ID do cliente
    public double buscarSaldoPorContaId(int idCliente) throws SQLException {
        String sql = "SELECT saldo FROM conta WHERE id_cliente = ?";
        double saldo = 0.0;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    saldo = rs.getDouble("saldo");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar saldo da conta do cliente " + idCliente, e);
        }
        return saldo;
    }

    // Atualizar saldo de uma conta
    public void atualizarSaldo(int idCliente, double valor) throws SQLException {
        if (valor == 0) {
            throw new IllegalArgumentException("Valor para atualização do saldo não pode ser zero.");
        }

        String sql = "UPDATE conta SET saldo = saldo + ? WHERE id_cliente = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, valor);
            stmt.setInt(2, idCliente);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhuma conta encontrada para o cliente com ID " + idCliente);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar saldo da conta do cliente " + idCliente, e);
        }
    }

    // Listar todos os clientes
    public List<Cliente> listarTodosClientes() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = """
            SELECT c.id_cliente, u.nome, u.cpf, u.data_nascimento, u.telefone, u.senha, 
                   e.cep, e.local, e.numero_casa, e.bairro, e.cidade, e.estado
            FROM cliente c
            INNER JOIN usuario u ON c.id_usuario = u.id_usuario
            LEFT JOIN endereco e ON u.id_usuario = e.id_usuario
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Erro ao listar todos os clientes.", e);
        }

        return clientes;
    }

    // Verificar se cliente existe
    public boolean clienteExiste(int id_cliente) throws SQLException {
        String sql = "SELECT 1 FROM cliente WHERE id_cliente = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id_cliente);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se o cliente existe
            }
        } catch (SQLException e) {
            logger.log(Level.INFO, "Erro ao verificar se o cliente existe.", e);
            throw e;
        }
    }
}

