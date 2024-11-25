package model;

import java.io.Serializable;

public class ContaPoupanca extends Conta implements Serializable {
    private static final long serialVersionUID = 1L;
    private double taxaRendimento;

    // Construtor completo (compatível com ContaDAO)
    public ContaPoupanca(String numeroConta, String agencia, double saldo, int idCliente) {
        super(numeroConta, agencia, saldo, "Poupança", idCliente);
        this.taxaRendimento = 0.05; // Valor padrão para taxa de rendimento
    }


    // Construtor completo (original)
    public ContaPoupanca(String numeroConta, String agencia, Cliente cliente, double saldo, double taxaRendimento) {
        super(numeroConta, agencia, saldo, "Poupança", cliente.getId_cliente());
        if (taxaRendimento < 0 || taxaRendimento > 1) {
            throw new IllegalArgumentException("A taxa de rendimento deve estar entre 0 e 1.");
        }
        this.taxaRendimento = taxaRendimento;
    }

    // Construtor vazio
    public ContaPoupanca() {
        super();
    }

    // Métodos
    public double calcularRendimento() {
        return getSaldo() * taxaRendimento;
    }

    // Getters e Setters
    public double getTaxaRendimento() {
        return taxaRendimento;
    }

    public void setTaxaRendimento(double taxaRendimento) {
        if (taxaRendimento < 0 || taxaRendimento > 1) {
            throw new IllegalArgumentException("A taxa de rendimento deve estar entre 0 e 1.");
        }
        this.taxaRendimento = taxaRendimento;
    }

    // Representação textual da ContaPoupança
    @Override
    public String toString() {
        return String.format("Conta Poupança [ID: %d, Número: %s, Agência: %s, Saldo: %.2f, Taxa de Rendimento: %.2f]",
                getId_conta(), getNumeroConta(), getAgencia(), getSaldo(), taxaRendimento);
    }
}