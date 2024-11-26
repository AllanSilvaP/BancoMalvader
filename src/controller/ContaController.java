package controller;

import model.Conta;
import service.ContaService;

import java.sql.SQLException;

import DAO.ContaDAO;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;

public class ContaController {
    private ContaService contaService;
    private ContaDAO contaDAO;

    public ContaController() {
        this.contaService = new ContaService();
        this.contaDAO = new ContaDAO();
    }

    // Método para criar uma nova conta
    public void criarConta(Conta conta) {
        try {
            contaService.abrirConta(conta);
            System.out.println("Conta criada com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao criar conta no banco de dados: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao criar conta: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erro no sistema: " + e.getMessage());
        }
    }

    // Método corrigido para retornar uma Conta
    public Conta buscarConta(int idConta) {
        try {
            Conta conta = contaService.buscarContaPorId(idConta);
            if (conta != null) {
                System.out.println("Conta encontrada: " + conta);
                return conta;
            } else {
                System.out.println("Conta não encontrada.");
                return null;
            }
        } catch (RuntimeException e) {
            System.out.println("Erro ao buscar conta: " + e.getMessage());
            return null;
        }
    }

    // Método para atualizar os dados de uma conta
    public void atualizarConta(Conta conta) {
        try {
            contaService.atualizarConta(conta);
            System.out.println("Conta atualizada com sucesso!");
        } catch (RuntimeException e) {
            System.out.println("Erro ao atualizar conta: " + e.getMessage());
        }
    }

    public void encerrarConta(String numeroConta) throws SQLException {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("O número da conta não pode ser nulo ou vazio.");
        }

        // Usa a instância de contaDAO para chamar o método
        boolean deletada = contaDAO.deletarContaPorNumero(numeroConta);
        if (!deletada) {
            throw new IllegalStateException("Nenhuma conta foi encontrada com o número fornecido.");
        }

        System.out.println("Conta com número " + numeroConta + " encerrada com sucesso.");
    }


    // Método para realizar saque em uma conta
    public void realizarSaque(int idConta, double valor) {
        try {
            contaService.realizarSaque(idConta, valor);
            System.out.println("Saque realizado com sucesso!");
        } catch (SaldoInsuficienteException | ValorInvalidoException e) {
            System.out.println("Erro ao realizar saque: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erro no sistema: " + e.getMessage());
        }
    }

    // Método para realizar depósito em uma conta
    public void realizarDeposito(int idConta, double valor) {
        try {
            contaService.realizarDeposito(idConta, valor);
            System.out.println("Depósito realizado com sucesso!");
        } catch (ValorInvalidoException e) {
            System.out.println("Erro ao realizar depósito: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erro no sistema: " + e.getMessage());
        }
    }
}

