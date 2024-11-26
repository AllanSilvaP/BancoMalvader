package service;

import java.sql.SQLException;
import java.util.logging.Logger;
import DAO.ContaDAO;
import model.Conta;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;

public class ContaService {
    private static final Logger logger = Logger.getLogger(ContaService.class.getName());
    private final ContaDAO contaDAO;

    public ContaService() {
        this.contaDAO = new ContaDAO();
    }

    public void abrirConta(Conta novaConta) throws SQLException {
        if (novaConta == null) {
            throw new IllegalArgumentException("Conta inválida. Não foi possível abrir a conta.");
        }

        if (novaConta.getSaldo() < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }

        contaDAO.salvarConta(novaConta);
		logger.info("Conta aberta com sucesso para o cliente: " + novaConta.getCliente().getNome());
    }

    public void realizarSaque(String numeroConta, double valor) throws SaldoInsuficienteException, ValorInvalidoException {
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor de saque deve ser positivo.");
        }

        try {
            Conta conta = contaDAO.buscarContaPorNumero(numeroConta);

            if (conta == null) {
                throw new IllegalArgumentException("Conta não encontrada.");
            }


            conta.sacar(valor);

        
            conta.atualizarConta(conta);

            logger.info("Saque de " + valor + " realizado com sucesso na conta " + numeroConta);
        } catch (SQLException e) {
            logger.severe("Erro ao realizar saque na conta " + numeroConta + ": " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar a conta no banco após o saque.", e);
        } catch (SaldoInsuficienteException e) {
            logger.warning("Falha ao realizar saque: " + e.getMessage());
            throw e; // Relança a exceção para ser tratada em outro lugar, se necessário
        }
    }

    public void realizarDeposito(String numeroConta, double valor) throws ValorInvalidoException {
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor de depósito deve ser positivo.");
        }

        try {
            // Busca a conta pelo número
            Conta conta = contaDAO.buscarContaPorNumero(numeroConta);

            if (conta == null) {
                throw new IllegalArgumentException("Conta não encontrada.");
            }

            // Realiza o depósito
            conta.depositar(valor);

            // Atualiza o saldo da conta no banco
            conta.atualizarConta(conta);

            logger.info("Depósito de " + valor + " realizado com sucesso na conta " + numeroConta);
        } catch (SQLException e) {
            logger.severe("Erro ao realizar depósito na conta " + numeroConta + ": " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar a conta no banco após o depósito.", e);
        }
    }



    // Método para atualizar os dados de uma conta
    public void atualizarConta(Conta conta) {
        if (conta == null) {
            throw new IllegalArgumentException("Conta inválida para atualização.");
        }

        try {
            contaDAO.atualizarConta(conta); // Atualiza a conta no banco
            logger.info("Conta atualizada com sucesso: " + conta.getId_conta());
        } catch (SQLException e) {
            logger.severe("Erro ao atualizar conta: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar conta.", e);
        }
    }

    // Método para buscar uma conta pelo ID
    public Conta buscarContaPorId(int idConta) {
        try {
            return contaDAO.buscarContaPorId(idConta); // Busca a conta no banco
        } catch (SQLException e) {
            logger.severe("Erro ao buscar conta: " + e.getMessage());
            throw new RuntimeException("Erro ao buscar conta.", e);
        }
    }

    public void encerrarConta(String numeroConta) throws SQLException, IllegalArgumentException {
        // Verifica se a conta existe pelo número da conta
        Conta conta = contaDAO.buscarContaPorNumero(numeroConta);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada.");
        }

        // Verifica se o saldo da conta é zero
        if (conta.getSaldo() > 0) {
            throw new IllegalArgumentException("Não é possível encerrar a conta. O saldo precisa estar zerado.");
        }

        // Chama o DAO para realizar a operação de encerramento da conta
        contaDAO.deletarContaPorNumero(numeroConta);
    }

}