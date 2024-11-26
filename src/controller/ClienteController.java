package controller;

import DAO.ClienteDAO;
import DAO.ConnectionFactory;
import model.Cliente;
import model.Endereco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteController {
    private ClienteDAO clienteDAO;

    // Construtor com ClienteDAO
    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    // Método para exibir saldo
    public void exibirSaldo(int numeroConta) throws SQLException {
        try {
            double saldo = clienteDAO.buscarSaldoPorNumeroConta(numeroConta);
            System.out.printf("Saldo atual da conta %d: R$ %.2f\n", numeroConta, saldo);
        } catch (SQLException e) {
            System.out.println("Erro ao exibir saldo: " + e.getMessage());
            throw e;
        }
    }

    // Método para realizar depósito
    public void realizarDeposito(int numeroConta, double valor) throws SQLException {
        if (valor > 0) {
            try {
                clienteDAO.atualizarSaldoPorNumeroConta(numeroConta, valor);
                System.out.println("Depósito realizado com sucesso.");
            } catch (SQLException e) {
                System.out.println("Erro ao realizar depósito: " + e.getMessage());
                throw e;
            }
        } else {
            System.out.println("Valor de depósito inválido.");
        }
    }

    // Método para realizar saque
    public void realizarSaque(int numeroConta, double valor) throws SQLException {
        try {
            double saldo = clienteDAO.buscarSaldoPorNumeroConta(numeroConta);
            if (saldo >= valor && valor > 0) {
                clienteDAO.atualizarSaldoPorNumeroConta(numeroConta, -valor);
                System.out.println("Saque realizado com sucesso.");
            } else {
                System.out.println("Saldo insuficiente ou valor inválido.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao realizar saque: " + e.getMessage());
            throw e;
        }
    }

    // Método para exibir extrato
    public void exibirExtrato(int numeroConta) {
        String sql = """
            SELECT tipo_transacao, valor, data_hora 
            FROM transacao 
            WHERE numero_conta = ? 
            ORDER BY data_hora DESC
        """;

        try (Connection conn = ConnectionFactory.getConnection()) {
            executarConsultaGenerica(conn, sql, numeroConta);
        } catch (SQLException e) {
            System.out.println("Erro ao consultar extrato: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método auxiliar para consulta genérica
    private void executarConsultaGenerica(Connection conn, String sql, int numeroConta) throws SQLException {
        try (var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numeroConta);
            try (var rs = stmt.executeQuery()) {
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

    // Método para atualizar cliente e endereço
	    public void atualizarCliente(int idCliente, String novoTelefone, String novoEndereco,
	            int novoNumero, String novoCep, String novoBairro,
	            String novaCidade, String novoEstado) throws Exception {
			if (idCliente <= 0) {
			throw new IllegalArgumentException("ID do cliente inválido.");
			}
			if (novoTelefone == null || novoTelefone.isBlank()) {
			throw new IllegalArgumentException("Telefone não pode ser vazio.");
			}
			if (novoEndereco == null || novoEndereco.isBlank()) {
			throw new IllegalArgumentException("Endereço não pode ser vazio.");
			}
			if (novoNumero <= 0) {
			throw new IllegalArgumentException("Número da casa não pode ser vazio.");
			}
			if (novoCep == null || novoCep.isBlank()) {
			throw new IllegalArgumentException("CEP não pode ser vazio.");
			}
			if (novoBairro == null || novoBairro.isBlank()) {
			throw new IllegalArgumentException("Bairro não pode ser vazio.");
			}
			if (novaCidade == null || novaCidade.isBlank()) {
			throw new IllegalArgumentException("Cidade não pode ser vazia.");
			}
			if (novoEstado == null || novoEstado.isBlank()) {
			throw new IllegalArgumentException("Estado não pode ser vazio.");
			}
			
			// Aqui, vamos buscar o cliente no banco de dados para atualizar.
			Cliente cliente = clienteDAO.buscarClientePorId(idCliente);
			
			if (cliente == null) {
			throw new IllegalArgumentException("Cliente não encontrado.");
			}
			
			// Atualizando o telefone
			cliente.setTelefone(novoTelefone);
			
			// Atualizar o cliente no banco
			clienteDAO.atualizarCliente(cliente);
			
			// Atualizar o endereço no banco
			Endereco endereco = new Endereco();
			endereco.setIdUsuario(idCliente);
			endereco.setLocal(novoEndereco);
			endereco.setNumeroCasa(novoNumero);
			endereco.setCep(novoCep);
			endereco.setBairro(novoBairro);
			endereco.setCidade(novaCidade);
			endereco.setEstado(novoEstado);
			
			endereco.atualizarEndereco();  // Supondo que esse método atualize no banco
			}
	    
	    public int buscarIdContaPorNumero(String numeroConta) throws SQLException {
	        String sql = "SELECT id_conta FROM conta WHERE numero_conta = ?";
	        
	        try (Connection conn = ConnectionFactory.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setString(1, numeroConta);
	            try (ResultSet rs = ps.executeQuery()) {
	                if (rs.next()) {
	                    return rs.getInt("id_conta");
	                } else {
	                    return -1; // Conta não encontrada
	                }
	            }
	        }
	    }
	    

	    public void realizarDeposito(String numeroConta, double valor) throws SQLException {
	        if (valor > 0) {
	            try {
	                // Buscar o id_conta pelo numeroConta
	                int idConta = buscarIdContaPorNumero(numeroConta);
	                
	                if (idConta == -1) {
	                    System.out.println("Conta não encontrada.");
	                    return;
	                }

	                // Atualizar saldo
	                clienteDAO.atualizarSaldoPorIdConta(idConta, valor);  // Ajustado para usar id_conta
	                
	                // Registrar transação
	                registrarTransacao(idConta, "DEPOSITO", valor);
	                System.out.println("Depósito realizado com sucesso.");
	            } catch (SQLException e) {
	                System.out.println("Erro ao realizar depósito: " + e.getMessage());
	                throw e;
	            }
	        } else {
	            System.out.println("Valor de depósito inválido.");
	        }
	    }

	    public void registrarTransacao(int idConta, String tipo, double valor) throws SQLException {
	        String sql = "INSERT INTO transacao (id_conta, tipo_transacao, valor) VALUES (?, ?, ?)";
	        
	        try (Connection conn = ConnectionFactory.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, idConta);
	            ps.setString(2, tipo);
	            ps.setDouble(3, valor);
	            ps.executeUpdate();
	        }
	    }




	    public void realizarSaque(String numeroConta, double valor) throws SQLException {
	        try {
	            // Buscar o id_conta pelo numeroConta
	            int idConta = buscarIdContaPorNumero(numeroConta);
	            
	            if (idConta == -1) {
	                System.out.println("Conta não encontrada.");
	                return;
	            }

	            // Verificar saldo
	            double saldo = clienteDAO.buscarSaldoPorIdConta(idConta);
	            if (saldo >= valor && valor > 0) {
	                // Atualizar saldo
	                clienteDAO.atualizarSaldoPorIdConta(idConta, -valor); // Atualizar com valor negativo para saque
	                
	                // Registrar transação
	                registrarTransacao(idConta, "SAQUE", valor);
	                System.out.println("Saque realizado com sucesso.");
	            } else {
	                System.out.println("Saldo insuficiente ou valor inválido.");
	            }
	        } catch (SQLException e) {
	            System.out.println("Erro ao realizar saque: " + e.getMessage());
	            throw e;
	        }
	    }
	
	}
