package DAO;

import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import util.DBUtil;
import util.DataManager;

import java.sql.*;
import java.util.List;
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
                // Inserir na tabela 'conta_corrente'
                try (PreparedStatement stmtCorrente = conn.prepareStatement(sqlContaCorrente)) {
                    stmtCorrente.setDouble(1, corrente.getLimite());
                    stmtCorrente.setDate(2, Date.valueOf(corrente.getDataVencimento()));
                    stmtCorrente.setInt(3, idContaInserida);
                    stmtCorrente.executeUpdate();
                }
            } else if (conta instanceof ContaPoupanca poupanca) {
                // Inserir na tabela 'conta_poupanca'
                try (PreparedStatement stmtPoupanca = conn.prepareStatement(sqlContaPoupanca)) {
                    stmtPoupanca.setDouble(1, poupanca.getTaxaRendimento());
                    stmtPoupanca.setInt(2, idContaInserida);
                    stmtPoupanca.executeUpdate();
                }
            }

            // Commit no banco de dados
            conn.commit();

            // Atualiza a lista de contas no arquivo
            List<Conta> contas = DataManager.carregarContas("contas.dat");  // Carrega as contas do arquivo
            contas.add(conta);  // Adiciona a nova conta à lista
            DataManager.salvarContas(contas, "contas.dat");  // Salva a lista atualizada de contas no arquivo

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

        String disableFKChecks = "SET FOREIGN_KEY_CHECKS = 0";
        String enableFKChecks = "SET FOREIGN_KEY_CHECKS = 1";
        String deleteContaSQL = "DELETE FROM conta WHERE numero_conta = ?";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteContaSQL)) {

            // Desativa verificações de chave estrangeira
            stmt.execute(disableFKChecks);

            // Executa o comando DELETE
            deleteStmt.setString(1, numeroConta);
            boolean resultado = deleteStmt.executeUpdate() > 0;

            // Reativa verificações de chave estrangeira
            stmt.execute(enableFKChecks);

            // Se a exclusão for bem-sucedida, atualize a lista de contas no arquivo
            if (resultado) {
                List<Conta> contas = DataManager.carregarContas("contas.dat");  // Carrega as contas do arquivo
                contas.removeIf(conta -> conta.getNumeroConta().equals(numeroConta));  // Remove a conta pela chave
                DataManager.salvarContas(contas, "contas.dat");  // Salva a lista atualizada de contas no arquivo
            }

            return resultado;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao deletar conta pelo número: " + numeroConta, e);
            throw e;
        }
    }

    
    // Deletar conta por ID
    public boolean deletarConta(int idConta) throws SQLException {
        if (idConta <= 0) {
            throw new IllegalArgumentException("O ID da conta deve ser maior que zero.");
        }

        Conta conta = buscarContaPorId(idConta); // Verifica se a conta existe
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada.");
        }

        if (conta.getSaldo() < 0) {
            throw new IllegalStateException("A conta possui saldo negativo. Não pode ser excluída.");
        }

        String disableFKChecks = "SET FOREIGN_KEY_CHECKS = 0";
        String enableFKChecks = "SET FOREIGN_KEY_CHECKS = 1";
        String deleteContaSQL = "DELETE FROM conta WHERE id_conta = ?";

        try (Connection conn = DBUtil.getConnection();
             Statement stmt = conn.createStatement();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteContaSQL)) {

            // Desativa verificações de chave estrangeira
            stmt.execute(disableFKChecks);

            // Executa o comando DELETE
            deleteStmt.setInt(1, idConta);
            boolean resultado = deleteStmt.executeUpdate() > 0;

            // Reativa verificações de chave estrangeira
            stmt.execute(enableFKChecks);

            return resultado;
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
        ClienteDAO clienteDAO = new ClienteDAO(); // Instancia o ClienteDAO
        Cliente cliente = clienteDAO.buscarClientePorId(rs.getInt("id_cliente")); // Busca o cliente pelo id_cliente

        if ("CORRENTE".equalsIgnoreCase(tipoConta)) {
            return new ContaCorrente(
                    rs.getString("numero_conta"),
                    rs.getString("agencia"),
                    cliente,  // Passa o Cliente
                    rs.getDouble("saldo"),
                    rs.getDouble("limite"),
                    rs.getDate("vencimento").toLocalDate()
            );
        } else if ("POUPANCA".equalsIgnoreCase(tipoConta)) {
            return new ContaPoupanca(
                    rs.getString("numero_conta"),
                    rs.getString("agencia"),
                    cliente,  // Passa o Cliente
                    rs.getDouble("saldo"),
                    rs.getDouble("taxa_rendimento")
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

    public Conta buscarContaPorNumero(String numeroConta) throws SQLException {
        String sql = """
            SELECT c.id_conta, c.numero_conta, c.agencia, c.saldo, c.tipo_conta, 
                   c.id_cliente, cc.limite, cc.data_vencimento, cp.taxa_rendimento
            FROM conta c
            LEFT JOIN conta_corrente cc ON c.id_conta = cc.id_conta
            LEFT JOIN conta_poupanca cp ON c.id_conta = cp.id_conta
            WHERE c.numero_conta = ?
        """;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id_cliente = rs.getInt("id_cliente");
                    ClienteDAO clienteDAO = new ClienteDAO();
                    Cliente cliente = clienteDAO.buscarClientePorId(id_cliente); // Buscar o cliente associado

                    // Verifica o tipo de conta
                    String tipoConta = rs.getString("tipo_conta").trim().toUpperCase();
                    if ("CORRENTE".equalsIgnoreCase(tipoConta)) {
                        return new ContaCorrente(
                            rs.getString("numero_conta"),
                            rs.getString("agencia"),
                            cliente, // Passa o cliente
                            rs.getDouble("saldo"),
                            rs.getDouble("limite"),
                            rs.getDate("data_vencimento").toLocalDate()
                        );
                    } else if ("POUPANCA".equalsIgnoreCase(tipoConta)) {
                        return new ContaPoupanca(
                            rs.getString("numero_conta"),
                            rs.getString("agencia"),
                            cliente, // Passa o cliente
                            rs.getDouble("saldo"),
                            rs.getDouble("taxa_rendimento")
                        );
                    }
                }
                return null; // Se não encontrar a conta
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log de erro, útil para depuração
            throw new SQLException("Erro ao consultar a conta: " + e.getMessage());
        }
    }
    
 // Método para atualizar o saldo de uma conta
    public void atualizarSaldo(String numeroConta, double novoSaldo) throws SQLException {
        String query = "UPDATE conta SET saldo = ? WHERE numero_conta = ?";

        // Obtém a conexão da ConnectionFactory
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, novoSaldo);
            statement.setString(2, numeroConta);
            statement.executeUpdate();
        }
    }


}