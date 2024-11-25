package controller;

import model.Conta;
import service.ContaService;

import java.sql.SQLException;

import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;

public class ContaController {
    private ContaService contaService;

    public ContaController() {
        this.contaService = new ContaService();
    }

    public void criarConta(Conta conta) {
        try {
            try {
				contaService.abrirConta(conta);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("Conta criada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao criar conta: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erro no sistema: " + e.getMessage());
        }
    }

    public void buscarConta(int idConta) {
        try {
            Conta conta = contaService.buscarContaPorId(idConta);
            if (conta != null) {
                System.out.println("Conta encontrada: " + conta);
            } else {
                System.out.println("Conta não encontrada.");
            }
        } catch (RuntimeException e) {
            System.out.println("Erro ao buscar conta: " + e.getMessage());
        }
    }

    public void atualizarConta(Conta conta) {
        try {
            contaService.atualizarConta(conta);
            System.out.println("Conta atualizada com sucesso!");
        } catch (RuntimeException e) {
            System.out.println("Erro ao atualizar conta: " + e.getMessage());
        }
    }

    public void deletarConta(int idConta) throws ValorInvalidoException {
        try {
            contaService.encerrarConta(idConta);
            System.out.println("Conta deletada com sucesso!");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao deletar conta: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erro no sistema: " + e.getMessage());
        }
    }

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
