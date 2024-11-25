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

    // Método para abrir uma conta
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

    // Método para encerrar uma conta
    public void encerrarConta(int contaId) {
        try {
            Conta conta = contaDAO.buscarContaPorId(contaId);

            if (conta == null) {
                throw new IllegalArgumentException("Conta não encontrada.");
            }

            if (conta.getSaldo() < 0) {
                throw new IllegalStateException("A conta possui saldo devedor. Não é possível encerrá-la.");
            }

            boolean deletada = contaDAO.deletarConta(contaId);
            if (deletada) {
                logger.info("Conta com número " + contaId + " encerrada com sucesso.");
            } else {
                throw new IllegalStateException("Erro ao encerrar a conta. Nenhuma linha foi afetada.");
            }
        } catch (SQLException e) {
            logger.severe("Erro ao encerrar conta: " + e.getMessage());
            throw new RuntimeException("Erro ao encerrar conta.", e);
        }
    }

    // Método para realizar um saque
    public void realizarSaque(int contaId, double valor) throws SaldoInsuficienteException, ValorInvalidoException {
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor de saque deve ser positivo.");
        }

        try {
            Conta conta = contaDAO.buscarContaPorId(contaId);

            if (conta == null) {
                throw new IllegalArgumentException("Conta não encontrada.");
            }

            if (conta.getSaldo() < valor) {
                throw new SaldoInsuficienteException("Saldo insuficiente para realizar o saque.");
            }

            conta.sacar(valor); // Chama sacar na instância de Conta
            contaDAO.atualizarConta(conta); // Atualiza a conta no banco
            logger.info("Saque de " + valor + " realizado com sucesso na conta " + contaId);

        } catch (SQLException e) {
            logger.severe("Erro ao realizar saque: " + e.getMessage());
            throw new RuntimeException("Erro ao realizar saque.", e);
        }
    }

    // Método para realizar um depósito
    public void realizarDeposito(int contaId, double valor) throws ValorInvalidoException {
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor de depósito deve ser positivo.");
        }

        try {
            Conta conta = contaDAO.buscarContaPorId(contaId);

            if (conta == null) {
                throw new IllegalArgumentException("Conta não encontrada.");
            }

            conta.depositar(valor); // Chama depositar na instância de Conta
            contaDAO.atualizarConta(conta); // Atualiza a conta no banco
            logger.info("Depósito de " + valor + " realizado com sucesso na conta " + contaId);

        } catch (SQLException e) {
            logger.severe("Erro ao realizar depósito: " + e.getMessage());
            throw new RuntimeException("Erro ao realizar depósito.", e);
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
}
