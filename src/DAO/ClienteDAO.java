package DAO;

import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.Endereco;
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

    // Salvar conta no banco
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

            // Inserir na tabela específica de conta corrente ou poupança
            if (conta instanceof ContaCorrente corrente) {
                try (PreparedStatement stmtContaCorrente = conn.prepareStatement(sqlContaCorrente)) {
                    stmtContaCorrente.setDouble(1, corrente.getLimite());
                    stmtContaCorrente.setDate(2, Date.valueOf(corrente.getDataVencimento()));
                    stmtContaCorrente.setInt(3, idContaInserida);
                    stmtContaCorrente.executeUpdate();
                }
            } else if (conta instanceof ContaPoupanca poupanca) {
                try (PreparedStatement stmtContaPoupanca = conn.prepareStatement(sqlContaPoupanca)) {
                    stmtContaPoupanca.setDouble(1, poupanca.getTaxaRendimento());
                    stmtContaPoupanca.setInt(2, idContaInserida);
                    stmtContaPoupanca.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Erro ao salvar conta no banco de dados.", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.ERROR, "Erro ao realizar rollback.", rollbackEx);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    logger.log(Level.ERROR, "Erro ao fechar conexão.", closeEx);
                }
            }
        }

        return idContaInserida;
    }
    
    public void atualizarSaldoPorNumeroConta(int numeroConta, double valor) throws SQLException {
        if (valor == 0) {
            throw new IllegalArgumentException("Valor para atualização do saldo não pode ser zero.");
        }

        String sql = "UPDATE conta SET saldo = saldo + ? WHERE numero_conta = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, valor); 
            stmt.setInt(2, numeroConta); 

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Nenhuma conta encontrada para o número " + numeroConta);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao atualizar saldo da conta com número " + numeroConta, e);
        }
    }
    
 // Buscar saldo utilizando o número da conta (int)
    public double buscarSaldoPorNumeroConta(int numeroConta) throws SQLException {
        String sql = "SELECT saldo FROM conta WHERE numero_conta = ?";
        double saldo = 0.0;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroConta); // Usa numeroConta como parâmetro

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    saldo = rs.getDouble("saldo"); // Obtém o saldo da conta
                } else {
                    throw new SQLException("Conta com número " + numeroConta + " não encontrada.");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar saldo da conta com número " + numeroConta, e);
        }

        return saldo;
    }

    public int salvarCliente(Cliente cliente) {
        String sqlUsuario = """
            INSERT INTO usuario (nome, cpf, data_nascimento, telefone, tipo_usuario, senha) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        String sqlCliente = "INSERT INTO cliente (id_usuario) VALUES (?)";
        int idUsuarioInserido = -1;

        Connection conn = null; // Declare connection outside the try-with-resources block

        try {
            conn = ConnectionFactory.getConnection(); // Initialize the connection
            conn.setAutoCommit(false);

            // Save user
            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
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
            }

            // Save associated client
            try (PreparedStatement stmtCliente = conn.prepareStatement(sqlCliente)) {
                stmtCliente.setInt(1, idUsuarioInserido);
                stmtCliente.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Erro ao salvar cliente no banco de dados.", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    logger.log(Level.ERROR, "Erro ao fazer rollback.", rollbackEx);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // Ensure the connection is closed in the finally block
                } catch (SQLException closeEx) {
                    logger.log(Level.ERROR, "Erro ao fechar conexão.", closeEx);
                }
            }
        }

        return idUsuarioInserido;
    }
    
    // Método para buscar cliente por CPF
 // Dentro de `clienteDAO.buscarClientePorCpf(cpf)`
    public Cliente buscarClientePorCpf(String cpf) {
        Cliente cliente = null;
        // Consulta ao banco de dados para buscar cliente pelo CPF
        try {
        	Connection conn = ConnectionFactory.getConnection();
            String query = "SELECT * FROM clientes WHERE cpf = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, cpf);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Criação do objeto Cliente com os dados obtidos
                cliente = new Cliente();
                cliente.setCpf(rs.getString("cpf"));
                cliente.setNome(rs.getString("nome"));
                cliente.setSenha(rs.getString("senha"));  // Verifique se o nome do campo está correto
                // Adicionar outras propriedades conforme necessário
                System.out.println("Cliente encontrado: " + cliente.getCpf());
            } else {
                System.out.println("Nenhum cliente encontrado com o CPF: " + cpf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cliente;
    }

    // Dentro de `clienteDAO.buscarContasPorCliente(cpf)`
    public List<Conta> buscarContasPorCliente(int idCliente) throws SQLException {
        String query = "SELECT * FROM conta WHERE id_cliente = ?";
        List<Conta> contas = new ArrayList<>();
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Conta conta = new Conta();
                    conta.setId_conta(rs.getInt("id_conta"));
                    conta.setNumeroConta(rs.getString("numero_conta"));
                    conta.setSaldo(rs.getDouble("saldo"));
                    contas.add(conta);
                }
            }
        }
        return contas;
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

    public double buscarSaldoPorNumeroConta(String numeroConta) throws SQLException {
        String sql = "SELECT saldo FROM conta WHERE numero_conta = ?";
        double saldo = 0.0;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, numeroConta); // Substituí o idCliente pelo número da conta
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    saldo = rs.getDouble("saldo");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao buscar saldo da conta com número " + numeroConta, e);
        }
        return saldo;
    }
    
    public void atualizarCliente(Cliente cliente) throws SQLException {
        String sql = "UPDATE cliente SET telefone = ? WHERE id_cliente = ?";

        try (PreparedStatement stmt = DBUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, cliente.getTelefone());
            stmt.setInt(2, cliente.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.ERROR, "Erro ao atualizar cliente no banco.", e);
            throw e;
        }
    }


    // Atualizar saldo de uma conta
    public void atualizarSaldo(int idCliente, double valor) throws SQLException {
        if (valor == 0) {
            throw new IllegalArgumentException("Valor para atualização do saldo não pode ser zero.");
        }

        String sql = "UPDATE conta SET saldo = saldo + ? WHERE numero_conta = ?";
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
    public Cliente buscarClientePorIdUsuario(int idUsuario) throws SQLException {
        String query = "SELECT * FROM cliente WHERE id_usuario = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = new Cliente();
                    cliente.setId_cliente(rs.getInt("id_cliente"));
                    cliente.setNome(rs.getString("nome"));
                    // Preencha os outros campos conforme necessário
                    return cliente;
                }
            }
        }
        return null; // Retorna null caso não encontre o cliente
    }
}

