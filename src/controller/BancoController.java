package controller;

import model.*;
import service.ContaService;
import exception.SaldoInsuficienteException;
import exception.ValorInvalidoException;
import util.DataManager;
import DAO.ConnectionFactory;
import DAO.ContaDAO;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BancoController {

    private List<Conta> contas;
    private List<Funcionario> funcionarios;
    private static final String CAMINHO_ARQUIVO_CONTAS = "contas.dat";  // Caminho do arquivo onde as contas são armazenadas
    private ContaDAO contaDAO = new ContaDAO();	
    private ContaService contaService = new ContaService();

    public BancoController() {
    	
        this.contas = new ArrayList<>();
        this.funcionarios = new ArrayList<>();
        carregarDados();
    }

    // Carregar dados ao iniciar o sistema
    public void carregarDados() {
        contas = DataManager.carregarContas("contas.dat");
        funcionarios = DataManager.carregarFuncionarios("funcionarios.dat");
        System.out.println("Dados carregados com sucesso!");
    }

    // Salvar dados no arquivo
    private void salvarDados() {
        DataManager.salvarContas(contas, "contas.dat");
        DataManager.salvarFuncionarios(funcionarios, "funcionarios.dat");
        System.out.println("Dados salvos com sucesso!");
    }

    // Métodos para manipulação de contas
    public void abrirConta(Conta novaConta) throws SQLException {
        if (isCpfCadastrado(novaConta.getCliente().getCpf())) {
            System.out.println("CPF já cadastrado! Não é possível abrir outra conta.");
            return;
        }
        contas.add(novaConta);
        salvarDados();
        System.out.println("Conta aberta com sucesso.");
    }

    public void encerrarConta(String numeroConta) {
        Conta contaParaRemover = contas.stream()
            .filter(conta -> conta.getNumeroConta().equals(numeroConta))
            .findFirst()
            .orElse(null);

        if (contaParaRemover != null) {
            contas.remove(contaParaRemover);
            salvarDados();
            System.out.println("Conta encerrada com sucesso.");
        } else {
            System.out.println("Conta não encontrada.");
        }
    }

    public Conta consultarConta(String numeroConta) {
        // Primeiro verifica na lista em memória
        for (Conta conta : contas) {
            if (conta.getNumeroConta().equals(numeroConta)) {
                return conta;
            }
        }

        // Se não encontrou, verifica no banco de dados
        try {
            return contaDAO.buscarContaPorNumero(numeroConta);
        } catch (SQLException e) {
            System.out.println("Erro ao consultar conta no banco de dados: " + e.getMessage());
            return null;
        }
    }




    public boolean isCpfCadastrado(String cpfCliente) {
        String sql = "SELECT COUNT(*) FROM cliente WHERE cpf = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpfCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            System.out.println("Erro ao verificar CPF: " + ex.getMessage());
        }
        return false;
    }

    // Métodos de operações bancárias
    public double consultarSaldo(String numeroConta) throws SQLException {
        Conta conta = consultarConta(numeroConta);  // Consulta na memória primeiro

        if (conta == null) {  // Se não encontrar na memória, vai ao banco
            conta = contaDAO.buscarContaPorNumero(numeroConta);
            if (conta == null) {
                throw new IllegalArgumentException("Conta não encontrada.");
            }
        }
        return conta.getSaldo();
    }



    public void realizarDeposito(String numeroConta, double valor) throws ValorInvalidoException, SQLException {
        // Verifica se o valor do depósito é válido (não negativo)
        if (valor <= 0) {
            throw new ValorInvalidoException("O valor do depósito deve ser positivo.");
        }

        // Consultar a conta pelo número
        Conta conta = consultarConta(numeroConta);
        
        if (conta != null) {
            try {
                // Chama o método no serviço para realizar o depósito
                contaService.realizarDeposito(conta.getNumeroConta(), valor);

                // Registra a transação
                registrarTransacao("DEPOSITO", valor, numeroConta);

                // Salva os dados (se necessário)
                salvarDados();

                // Imprime mensagem de sucesso
                System.out.println("Depósito realizado com sucesso.");
            } catch (RuntimeException e) {
                System.out.println("Erro no sistema: " + e.getMessage());
            }
        } else {
            System.out.println("Erro: Conta não encontrada.");
            throw new ValorInvalidoException("Conta não encontrada.");
        }
    }


    public void realizarSaque(String numeroConta, double valor) throws ValorInvalidoException {
        // Consultar a conta pelo número
        Conta conta = consultarConta(numeroConta);

        // Verifica se a conta foi encontrada
        if (conta != null) {
            // Verifica se o valor do saque é válido
            if (valor <= 0) {
                System.out.println("O valor do saque deve ser positivo.");
                return;
            }

            try {
                // Chama o método no serviço para realizar o saque
                contaService.realizarSaque(conta.getNumeroConta(), valor);

                // Registra a transação de saque
                registrarTransacao("SAQUE", valor, numeroConta);

                // Salva os dados após a operação
                salvarDados();

                // Mensagem de sucesso
                System.out.println("Saque realizado com sucesso.");
            } catch (SaldoInsuficienteException e) {
                // Caso ocorra erro devido ao saldo insuficiente
                System.out.println("Erro ao realizar saque: " + e.getMessage());
            } catch (RuntimeException e) {
                // Caso ocorra outro erro
                System.out.println("Erro no sistema: " + e.getMessage());
            }
        } else {
            // Caso a conta não seja encontrada
            System.out.println("Conta não encontrada.");
        }
    }



    public void realizarTransferencia(String numeroContaOrigem, String numeroContaDestino, double valor) {
        // Consultar as contas de origem e destino
        Conta contaOrigem = consultarConta(numeroContaOrigem);
        Conta contaDestino = consultarConta(numeroContaDestino);

        // Verifica se ambas as contas foram encontradas
        if (contaOrigem != null && contaDestino != null) {
            // Verifica se o valor da transferência é válido
            if (valor <= 0) {
                System.out.println("O valor da transferência deve ser positivo.");
                return;
            }

            try {
                // Realiza o saque da conta de origem
                contaOrigem.sacar(valor);

                // Realiza o depósito na conta de destino
                contaDestino.depositar(valor);

                // Atualiza os saldos no banco
                contaDAO.atualizarConta(contaOrigem);
                contaDAO.atualizarConta(contaDestino);

                // Registra as transações de transferência
                registrarTransacao("TRANSFERENCIA", valor, numeroContaOrigem);
                registrarTransacao("TRANSFERENCIA", valor, numeroContaDestino);

                // Salva os dados após a operação
                salvarDados();

                // Mensagem de sucesso
                System.out.println("Transferência realizada com sucesso.");
            } catch (SaldoInsuficienteException e) {
                // Caso ocorra erro devido ao saldo insuficiente
                System.out.println("Erro ao realizar transferência: " + e.getMessage());
            } catch (SQLException e) {
                // Caso ocorra erro ao atualizar o banco
                System.out.println("Erro ao atualizar o banco de dados: " + e.getMessage());
            }
        } else {
            // Caso uma das contas não seja encontrada
            System.out.println("Uma das contas não foi encontrada.");
        }
    }



    private void registrarTransacao(String tipoTransacao, double valor, String numeroConta) {
        String sql = "INSERT INTO transacao (tipo_transacao, valor, data_hora, id_conta) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define os parâmetros da transação
            stmt.setString(1, tipoTransacao);
            stmt.setDouble(2, valor);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(4, numeroConta); // A referência da conta

            // Executa o comando SQL
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Transação registrada com sucesso.");
            } else {
                System.out.println("Erro: Nenhuma transação registrada.");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao registrar transação: " + e.getMessage());
        }
    }

    public void gerarRelatorios(List<Conta> contas) {
        // Carregar os dados dos funcionários
        List<Funcionario> funcionarios = carregarFuncionarios();

        // Construir o conteúdo do relatório incluindo as contas e funcionários
        StringBuilder conteudoRelatorio = new StringBuilder();
        conteudoRelatorio.append("Detalhes do Relatório Geral com Informações de Contas e Funcionários.\n\n");

        // Adiciona detalhes das contas
        conteudoRelatorio.append("Contas:\n");
        for (Conta conta : contas) {
            conteudoRelatorio.append(conta.toString()).append("\n");
        }

        // Adiciona detalhes dos funcionários
        conteudoRelatorio.append("\nFuncionários:\n");
        for (Funcionario funcionario : funcionarios) {
            conteudoRelatorio.append(funcionario.toString()).append("\n");
        }

        // Cria o relatório
        Relatorio relatorio = new Relatorio(
            "Relatório Geral",  // Tipo do Relatório
            LocalDateTime.now(),  // Data de Geração
            conteudoRelatorio.toString(),  // Conteúdo do Relatório
            contas  // Lista de contas associadas
        );

        // Gera o relatório em formato CSV
        relatorio.gerarRelatorioGeral();
    }

    // Método para exportar o relatório para Excel
    public void exportarRelatorioParaExcel(List<Conta> contas) {
        // Carregar os dados dos funcionários
        List<Funcionario> funcionarios = carregarFuncionarios();

        // Construir o conteúdo do relatório para exportação
        StringBuilder conteudoRelatorio = new StringBuilder();
        conteudoRelatorio.append("Detalhes da Exportação de Relatório com Dados de Contas e Funcionários.\n\n");

        // Adiciona detalhes das contas
        conteudoRelatorio.append("Contas:\n");
        for (Conta conta : contas) {
            conteudoRelatorio.append(conta.toString()).append("\n");
        }

        // Adiciona detalhes dos funcionários
        conteudoRelatorio.append("\nFuncionários:\n");
        for (Funcionario funcionario : funcionarios) {
            conteudoRelatorio.append(funcionario.toString()).append("\n");
        }

        // Cria o relatório
        Relatorio relatorio = new Relatorio(
            "Exportação Relatório",  // Tipo do Relatório
            LocalDateTime.now(),  // Data de Geração
            conteudoRelatorio.toString(),  // Conteúdo do Relatório
            contas  // Lista de contas associadas
        );

        // Exporta o relatório para Excel (no caso, CSV)
        relatorio.exportarParaExcel();
    }

    public List<Transacao> getExtrato(String numeroConta) {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT tipo_transacao, valor, data_hora FROM transacao WHERE id_conta = ? ORDER BY data_hora DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, numeroConta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transacoes.add(new Transacao(
                    rs.getString("tipo_transacao"),
                    rs.getDouble("valor"),
                    rs.getTimestamp("data_hora")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao consultar extrato: " + e.getMessage());
        }
        return transacoes;
    }

    public void alterarDados(String numeroConta, String novoNome) throws SQLException {
        Conta conta = consultarConta(numeroConta);
        if (conta != null && conta.getCliente() != null) {
            Cliente cliente = conta.getCliente();
            cliente.setNome(novoNome);
            salvarDados(); // Atualiza as alterações
            System.out.println("Nome do cliente alterado com sucesso.");
        } else {
            System.out.println("Conta ou cliente não encontrados.");
        }
    }


    public void consultarDados() {
        System.out.println("Contas cadastradas:");
        contas.forEach(System.out::println);
        System.out.println("\nFuncionários cadastrados:");
        funcionarios.forEach(System.out::println);
    }

    public void cadastrarConta(Conta novaConta, String cpfCliente) {
        String sqlConta = "INSERT INTO conta (numero_conta, agencia, saldo, tipo, cpf_cliente) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlConta)) {

            // Verifica se o cliente existe antes de criar a conta
            if (!isCpfCadastrado(cpfCliente)) {
                throw new IllegalArgumentException("Cliente não encontrado no sistema.");
            }

            // Insere a nova conta no banco
            stmt.setString(1, novaConta.getNumeroConta());
            stmt.setString(2, novaConta.getAgencia());
            stmt.setDouble(3, novaConta.getSaldo());
            stmt.setString(4, novaConta.getTipoConta());
            stmt.setString(5, cpfCliente);
            stmt.executeUpdate();

            System.out.println("Conta cadastrada com sucesso para o cliente com CPF: " + cpfCliente);
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar conta: " + e.getMessage());
        }
    }

    public List<Conta> obterContas() {
        // Usa o DataManager para carregar as contas de um arquivo
        List<Conta> contas = DataManager.carregarContas(CAMINHO_ARQUIVO_CONTAS);
        
        if (contas.isEmpty()) {
            System.out.println("Nenhuma conta encontrada no arquivo.");
        }
        
        return contas;
    }
    public List<Funcionario> carregarFuncionarios() {
        List<Funcionario> funcionarios = DataManager.carregarFuncionarios("funcionarios.dat");
        if (funcionarios == null || funcionarios.isEmpty()) {
            // Caso o arquivo não exista ou esteja vazio, cria uma lista vazia de funcionários
            System.out.println("Nenhum funcionário encontrado. Criando lista vazia.");
            funcionarios = new ArrayList<>();
        }
        return funcionarios;
    }

}