package model;

import java.io.Serializable;
import java.time.LocalDate;
import exception.SaldoInsuficienteException;

public class ContaCorrente extends Conta implements Serializable {
    private static final long serialVersionUID = 1L;
    private double limite;
    private LocalDate dataVencimento;

    // Construtor completo para ContaCorrente
    public ContaCorrente(String numeroConta, String agencia, Cliente cliente, double saldo, double limite, LocalDate dataVencimento) {
        super(numeroConta, agencia, saldo, "Corrente", cliente.getId_cliente());
        if (limite < 0) {
            throw new IllegalArgumentException("O limite não pode ser negativo.");
        }
        this.limite = limite;
        this.dataVencimento = dataVencimento;
    }

    // Novo construtor aceitando valores diretamente
    public ContaCorrente(String numeroConta, String agencia, double saldo, String tipoConta, int idCliente, double limite, LocalDate dataVencimento) {
        super(numeroConta, agencia, saldo, tipoConta, idCliente);
        if (limite < 0) {
            throw new IllegalArgumentException("O limite não pode ser negativo.");
        }
        this.limite = limite;
        this.dataVencimento = dataVencimento;
    }

    // Construtor vazio para inicialização
    public ContaCorrente() {
        super();
    }

    // Métodos
    public double consultarLimite() {
        return limite;
    }

    @Override
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (valor <= getSaldo()) {
            super.sacar(valor); // Saque dentro do saldo
        } else if (valor <= getSaldo() + limite) {
            double diferenca = valor - getSaldo();
            super.sacar(getSaldo()); // Consome o saldo disponível
            limite -= diferenca;    // Ajusta o limite
        } else {
            throw new SaldoInsuficienteException("Saldo insuficiente, inclusive o limite.");
        }
    }

    public void redefinirLimite(double novoLimite) {
        if (novoLimite < 0) {
            throw new IllegalArgumentException("O limite deve ser positivo.");
        }
        this.limite = novoLimite;
    }

    // Getters e Setters
    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        if (limite < 0) {
            throw new IllegalArgumentException("O limite deve ser positivo.");
        }
        this.limite = limite;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    // Representação textual da ContaCorrente
    @Override
    public String toString() {
        return String.format("Conta Corrente [ID: %d, Número: %s, Agência: %s, Saldo: %.2f, Limite: %.2f, Vencimento: %s]",
                getId_conta(), getNumeroConta(), getAgencia(), getSaldo(), limite, dataVencimento);
    }
}

