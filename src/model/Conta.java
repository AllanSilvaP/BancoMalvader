package model;

import exception.SaldoInsuficienteException;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import DAO.ClienteDAO;
import DAO.ConnectionFactory;

public class Conta implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id_conta;
    private String numeroConta;
    private String agencia;
    private double saldo;
    private String tipoConta;
    private int id_cliente; 
    private ContaCorrente contaCorrente;  
    private ContaPoupanca contaPoupanca;  

    public Conta(String numeroConta, String agencia, double saldo, String tipoConta, int id_cliente) {
        this.numeroConta = numeroConta;
        this.agencia = agencia;
        this.saldo = saldo;
        this.tipoConta = tipoConta;
        this.id_cliente = id_cliente;
    }

    // Getter e Setter para id_conta
    public int getId_conta() {
        return id_conta;
    }

    public void setId_conta(int id_conta) {
        this.id_conta = id_conta;
    }

    // Getter e Setter para numeroConta
    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    // Getter e Setter para agencia
    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    // Getter e Setter para saldo
    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    // Getter e Setter para tipoConta
    public String getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }

    // Getter e Setter para id_cliente (relacionamento com Cliente)
    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    // Método para buscar o cliente associado à conta
    public Cliente getCliente() {
        ClienteDAO clienteDAO = new ClienteDAO();
        return clienteDAO.buscarClientePorId(this.id_cliente);
    }

    // Método para exibir informações gerais da conta
    @Override
    public String toString() {
        String tipoDetalhado = tipoConta;
        if (contaCorrente != null) {
            tipoDetalhado = "Conta Corrente (Limite: " + contaCorrente.getLimite() + ")";
        } else if (contaPoupanca != null) {
            tipoDetalhado = "Conta Poupança (Taxa de Rendimento: " + contaPoupanca.getTaxaRendimento() + "%)";
        }
        return String.format("Conta [ID: %d, Número: %s, Agência: %s, Saldo: %.2f, Tipo: %s]",
                id_conta, numeroConta, agencia, saldo, tipoDetalhado);
    }


    public void sacar(double valor) throws SaldoInsuficienteException {
        // Verifica se o valor solicitado é maior que o saldo disponível
        if (valor > saldo) {
            // Para contas correntes, verifica se o limite permite o saque
            if (contaCorrente != null && (saldo + contaCorrente.getLimite()) >= valor) {
                saldo -= valor;  // Permite o saque se o limite da conta corrente for suficiente
            } else {
                throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque.");
            }
        } else {
            saldo -= valor; // Decrementa o saldo da conta com o valor do saque
        }
    }

    public void depositar(double valor) {
        // Verifica se o valor de depósito é válido (não negativo)
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor do depósito deve ser positivo.");
        }
        saldo += valor; // Incrementa o saldo com o valor do depósito
    }

	public double consultarSaldo() {
		return saldo;
	}

	public void setContaCorrente(ContaCorrente contaCorrente) {
	    if (contaCorrente != null && contaCorrente.getLimite() >= 0) {
	        this.contaCorrente = contaCorrente;
	    } else {
	        throw new IllegalArgumentException("Conta Corrente inválida ou limite negativo.");
	    }
	}

	public void setContaPoupanca(ContaPoupanca contaPoupanca) {
	    if (contaPoupanca != null && contaPoupanca.getTaxaRendimento() >= 0) {
	        this.contaPoupanca = contaPoupanca;
	    } else {
	        throw new IllegalArgumentException("Conta Poupança inválida ou taxa de rendimento negativa.");
	    }
	}
	
	public void atualizarConta(Conta conta) throws SQLException {
	    String query = "UPDATE conta SET saldo = ? WHERE numero_conta = ?";

	    // Estabelecendo a conexão
	    try (Connection connection = ConnectionFactory.getConnection()) {
	        // Desativando o autocommit para controle manual da transação
	        connection.setAutoCommit(false);

	        try (PreparedStatement statement = connection.prepareStatement(query)) {
	            // Definindo os parâmetros da query
	            statement.setDouble(1, conta.getSaldo());  // Atualiza o saldo da conta
	            statement.setString(2, conta.getNumeroConta());  // Identifica a conta pelo número

	            // Executando a query
	            int rowsAffected = statement.executeUpdate();

	            // Verificando se nenhuma linha foi afetada
	            if (rowsAffected == 0) {
	                throw new SQLException("Erro ao atualizar saldo: nenhuma linha foi afetada.");
	            }

	            connection.commit();
	        } catch (SQLException e) {
	            // Realizando rollback
	            connection.rollback();
	            throw new SQLException("Erro ao atualizar a conta no banco.", e);
	        } finally {
	            // Garantindo que o autocommit
	            connection.setAutoCommit(true);
	        }
	    } catch (SQLException e) {
	        throw new SQLException("Erro ao conectar ao banco ou preparar a query.", e);
	    }
	}

	 
	 public Conta buscarContaPorId(int contaId) throws SQLException {
	        String query = "SELECT * FROM conta WHERE id_conta = ?";
	        try (Connection connection = ConnectionFactory.getConnection();
	             PreparedStatement statement = connection.prepareStatement(query)) {
	            statement.setInt(1, contaId);
	            ResultSet resultSet = statement.executeQuery();

	            if (resultSet.next()) {
	                String numeroConta = resultSet.getString("numero_conta");
	                String agencia = resultSet.getString("agencia");
	                double saldo = resultSet.getDouble("saldo");
	                String tipoConta = resultSet.getString("tipo_conta");
	                int idCliente = resultSet.getInt("id_cliente");

	                return new Conta(numeroConta, agencia, saldo, tipoConta, idCliente);
	            }
	            return null;
	        }
	    }

	    public Conta buscarContaPorNumero(String numeroConta) throws SQLException {
	        String query = "SELECT * FROM conta WHERE numero_conta = ?";
	        try (Connection connection = ConnectionFactory.getConnection();
	             PreparedStatement statement = connection.prepareStatement(query)) {
	            statement.setString(1, numeroConta);
	            ResultSet resultSet = statement.executeQuery();

	            if (resultSet.next()) {
	                String agencia = resultSet.getString("agencia");
	                double saldo = resultSet.getDouble("saldo");
	                String tipoConta = resultSet.getString("tipo_conta");
	                int idCliente = resultSet.getInt("id_cliente");

	                return new Conta(numeroConta, agencia, saldo, tipoConta, idCliente);
	            }
	            return null;  // Conta não encontrada
	        }
	    }


}