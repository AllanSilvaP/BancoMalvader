package controller;

import DAO.FuncionarioDAO;
import DAO.ContaDAO;
import model.Funcionario;
import model.ContaPoupanca;
import model.ContaCorrente;

import java.sql.SQLException;
import java.time.LocalDate;

public class FuncionarioController {

    private final FuncionarioDAO funcionarioDAO;
    private final ContaDAO contaDAO;

    // Construtor que inicializa o FuncionarioDAO e ContaDAO
    public FuncionarioController(FuncionarioDAO funcionarioDAO, ContaDAO contaDAO) {
        if (funcionarioDAO == null || contaDAO == null) {
            throw new IllegalArgumentException("Os DAOs não podem ser nulos.");
        }
        this.funcionarioDAO = funcionarioDAO;
        this.contaDAO = contaDAO;
    }

    // Método para buscar funcionário por ID
    public Funcionario buscarFuncionarioPorId(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID deve ser um número positivo.");
        }

        Funcionario funcionario = funcionarioDAO.buscarFuncionarioPorId(id);
        if (funcionario == null) {
            System.out.println("Nenhum funcionário encontrado para o ID: " + id);
        }
        return funcionario;
    }

    // Método para cadastrar um novo funcionário
    public void cadastrarFuncionario(Funcionario funcionario) throws SQLException {
        validarFuncionario(funcionario);
        funcionarioDAO.salvarFuncionario(funcionario);
        System.out.println("Funcionário cadastrado com sucesso!");
    }

    // Método para alterar os dados de um funcionário
    public void alterarFuncionario(Funcionario funcionario) throws SQLException {
        if (funcionario == null || funcionario.getId() <= 0) {
            throw new IllegalArgumentException("Dados inválidos para atualização do funcionário.");
        }

        validarFuncionario(funcionario);
        funcionarioDAO.atualizarFuncionario(funcionario);
        System.out.println("Funcionário atualizado com sucesso!");
    }

    // Método para deletar um funcionário por ID
    public void deletarFuncionario(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("O ID do funcionário deve ser positivo.");
        }

        funcionarioDAO.deletarFuncionario(id);
        System.out.println("Funcionário deletado com sucesso!");
    }

    // Método para validar os dados do funcionário
    private void validarFuncionario(Funcionario funcionario) {
        if (funcionario == null) {
            throw new IllegalArgumentException("O funcionário não pode ser nulo.");
        }
        if (funcionario.getNome() == null || funcionario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do funcionário não pode ser nulo ou vazio.");
        }
        if (funcionario.getCpf() == null || funcionario.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("O CPF do funcionário não pode ser nulo ou vazio.");
        }
        if (funcionario.getDataNascimento() == null) {
            throw new IllegalArgumentException("A data de nascimento do funcionário não pode ser nula.");
        }
        if (funcionario.getTelefone() == null || funcionario.getTelefone().trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone do funcionário não pode ser nulo ou vazio.");
        }
        if (funcionario.getCargo() == null || funcionario.getCargo().trim().isEmpty()) {
            throw new IllegalArgumentException("O cargo do funcionário não pode ser nulo ou vazio.");
        }
    }

    // Método para abrir uma conta poupança
    public void abrirContaPoupanca(String agencia, String numeroConta, double saldo, int idCliente) throws SQLException {
        if (saldo < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }

        ContaPoupanca novaConta = new ContaPoupanca(numeroConta, agencia, saldo, idCliente);
        contaDAO.salvarConta(novaConta);
        System.out.println("Conta poupança criada com sucesso!");
    }

    // Método para abrir uma conta corrente
    public void abrirContaCorrente(String agencia, String numeroConta, double saldo, int idCliente, double limite, LocalDate vencimento) throws SQLException {
        if (saldo < 0) {
            throw new IllegalArgumentException("O saldo inicial não pode ser negativo.");
        }
        if (limite < 0) {
            throw new IllegalArgumentException("O limite não pode ser negativo.");
        }
        if (vencimento == null) {
            throw new IllegalArgumentException("A data de vencimento não pode ser nula.");
        }

        ContaCorrente novaConta = new ContaCorrente(numeroConta, agencia, saldo, "Corrente", idCliente, limite, vencimento);
        contaDAO.salvarConta(novaConta);
        System.out.println("Conta corrente criada com sucesso!");
    }
    
    public void encerrarConta(String numeroConta) throws SQLException {
        if (numeroConta == null || numeroConta.trim().isEmpty()) {
            throw new IllegalArgumentException("O número da conta não pode ser nulo ou vazio.");
        }

        boolean deletada = contaDAO.deletarContaPorNumero(numeroConta);
        if (!deletada) {
            throw new IllegalStateException("Nenhuma conta foi encontrada com o número fornecido.");
        }

        System.out.println("Conta com número " + numeroConta + " encerrada com sucesso.");
    }

}

