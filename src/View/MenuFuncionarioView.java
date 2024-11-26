package View;

import javax.swing.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import controller.BancoController;
import controller.ClienteController;
import controller.ContaController;
import controller.FuncionarioController;
import DAO.ClienteDAO;
import DAO.FuncionarioDAO;
import DAO.ContaDAO;
import model.Cliente;
import model.Conta;
import model.ContaCorrente;
import model.ContaPoupanca;
import model.Endereco;
import model.Funcionario;
import service.ContaService;

public class MenuFuncionarioView extends JFrame {

    private static final long serialVersionUID = 1L;
    private final BancoController bancoController;
    private final FuncionarioController funcionarioController;
    private final ContaService contaService;
    private static final String SENHA_ADMIN = "Admin123";

    public MenuFuncionarioView(BancoController bancoController, FuncionarioController funcionarioController, ContaService contaService) {
        if (bancoController == null || funcionarioController == null) {
            throw new IllegalArgumentException("Controladores não podem ser nulos.");
        }
        this.bancoController = bancoController;
        this.funcionarioController = funcionarioController;
        this.contaService = contaService;

        // Configuração da janela
        setTitle("Menu Funcionário");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Configuração do painel
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        add(panel);

        // Adiciona os botões ao painel
        configurarBotoes(panel, gbc);
    }
    private void configurarBotoes(JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Botão para cadastrar funcionário
        JButton cadastrarFuncionarioButton = new JButton("Cadastrar Funcionário");
        panel.add(cadastrarFuncionarioButton, gbc);
        cadastrarFuncionarioButton.addActionListener(e -> cadastrarFuncionario());

        // Botão para buscar funcionário por ID
        gbc.gridy++;
        JButton buscarFuncionarioButton = new JButton("Buscar Funcionário");
        panel.add(buscarFuncionarioButton, gbc);
        buscarFuncionarioButton.addActionListener(e -> buscarFuncionario());

        // Botão para abertura de conta
        gbc.gridy++;
        JButton aberturaContaButton = new JButton("Abertura de Conta");
        panel.add(aberturaContaButton, gbc);
        aberturaContaButton.addActionListener(e -> abrirConta());

        // Botão para encerramento de conta
        gbc.gridy++;
        JButton encerramentoContaButton = new JButton("Encerramento de Conta");
        panel.add(encerramentoContaButton, gbc);
        encerramentoContaButton.addActionListener(e -> {
			try {
				encerrarConta();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

        // Botão para consulta de dados
        gbc.gridy++;
        JButton consultaDadosButton = new JButton("Consulta de Dados");
        panel.add(consultaDadosButton, gbc);
        consultaDadosButton.addActionListener(e -> {
			try {
				consultarDados();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

        // Botão para alteração de dados
        gbc.gridy++;
        JButton alteracaoDadosButton = new JButton("Alteração de Dados");
        panel.add(alteracaoDadosButton, gbc);
        alteracaoDadosButton.addActionListener(e -> alterarDados());

        // Botão para geração de relatórios
        gbc.gridy++;
        JButton geracaoRelatoriosButton = new JButton("Geração de Relatórios");
        panel.add(geracaoRelatoriosButton, gbc);
        geracaoRelatoriosButton.addActionListener(e -> gerarRelatorios());

        // Botão para exportação para Excel
        gbc.gridy++;
        JButton exportarExcelButton = new JButton("Exportar para Excel");
        panel.add(exportarExcelButton, gbc);
        exportarExcelButton.addActionListener(e -> exportarParaExcel());

        // Botão para sair
        gbc.gridy++;
        JButton sairButton = new JButton("Sair");
        panel.add(sairButton, gbc);
        sairButton.addActionListener(e -> dispose());
    }
    
    private void buscarFuncionario() {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do funcionário:"));
            Funcionario funcionario = funcionarioController.buscarFuncionarioPorId(id);
            if (funcionario != null) {
                JOptionPane.showMessageDialog(null, "Funcionário encontrado: " + funcionario.getNome());
            } else {
                JOptionPane.showMessageDialog(null, "Funcionário não encontrado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Por favor, insira um ID válido.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar funcionário: " + ex.getMessage());
        }
    }
    public enum TipoConta {
        POUPANCA, CORRENTE;

        public static TipoConta fromString(String tipo) {
            try {
                return TipoConta.valueOf(tipo.toUpperCase());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private void abrirConta() {
        // Solicita a senha de administrador
        String senhaAdmin = JOptionPane.showInputDialog("Digite a senha de administrador:");
        if (senhaAdmin == null || !senhaAdmin.equals(SENHA_ADMIN)) {
            JOptionPane.showMessageDialog(this, "Senha de administrador incorreta ou operação cancelada.");
            return; // Não continua se a senha estiver errada
        }

        // Criando o painel para o formulário
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5)); // Layout simples de formulário

        // Campos de entrada do formulário
        JComboBox<String> tipoContaComboBox = new JComboBox<>(new String[] { "POUPANCA", "CORRENTE" });
        JTextField agenciaField = new JTextField();
        JTextField numeroContaField = new JTextField();
        JTextField saldoField = new JTextField();
        JTextField idClienteField = new JTextField();
        JTextField taxaRendimentoField = new JTextField();
        JTextField limiteField = new JTextField();
        JTextField vencimentoField = new JTextField();

        // Adicionando os campos ao painel
        panel.add(new JLabel("Tipo de Conta:"));
        panel.add(tipoContaComboBox);
        panel.add(new JLabel("Agência:"));
        panel.add(agenciaField);
        panel.add(new JLabel("Número da Conta:"));
        panel.add(numeroContaField);
        panel.add(new JLabel("Saldo Inicial:"));
        panel.add(saldoField);
        panel.add(new JLabel("ID do Cliente:"));
        panel.add(idClienteField);
        panel.add(new JLabel("Taxa de Rendimento:"));
        panel.add(taxaRendimentoField);
        panel.add(new JLabel("Limite:"));
        panel.add(limiteField);
        panel.add(new JLabel("Vencimento:"));
        panel.add(vencimentoField);

        // Botão de "Cadastrar"
        int option = JOptionPane.showConfirmDialog(this, panel, "Abrir Conta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Coleta os dados do formulário
                String tipoContaStr = (String) tipoContaComboBox.getSelectedItem();
                TipoConta tipoConta = TipoConta.fromString(tipoContaStr);
                String agencia = agenciaField.getText();
                String numeroConta = numeroContaField.getText();
                double saldo = Double.parseDouble(saldoField.getText());
                int idCliente = Integer.parseInt(idClienteField.getText());

                // Verifica se o tipo de conta é válido
                if (tipoConta == null) {
                    JOptionPane.showMessageDialog(this, "Tipo de conta inválido.");
                    return;
                }

                // Chama o método correto com base no tipo de conta
                if (tipoConta == TipoConta.POUPANCA) {
                    double taxaRendimento = Double.parseDouble(taxaRendimentoField.getText());
                    funcionarioController.abrirContaPoupanca(agencia, numeroConta, saldo, idCliente, taxaRendimento);
                } else if (tipoConta == TipoConta.CORRENTE) {
                    double limite = Double.parseDouble(limiteField.getText());
                    LocalDate vencimento = LocalDate.parse(vencimentoField.getText());
                    funcionarioController.abrirContaCorrente(agencia, numeroConta, saldo, idCliente, limite, vencimento);
                }

                JOptionPane.showMessageDialog(this, "Conta aberta com sucesso!");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao abrir conta: " + ex.getMessage());
            }
        }
    }




 // Método para encerrar conta pelo número
    private void encerrarConta() throws Exception {
        String numeroConta = JOptionPane.showInputDialog("Número da conta para encerrar:");

        try {
            // Verifica se o número da conta é válido
            if (numeroConta == null || numeroConta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O número da conta não pode ser vazio.");
                return;
            }

            // Chama o método do ContaService para encerrar a conta
            contaService.encerrarConta(numeroConta); // Passando o número da conta diretamente

            // Exibe a mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Conta com número " + numeroConta + " encerrada com sucesso!");

        } catch (SQLException ex) {
            // Tratamento de erro com SQLException
            JOptionPane.showMessageDialog(this, "Erro ao encerrar conta: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            // Tratamento de erro com IllegalArgumentException
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        } catch (Exception ex) {
            // Tratamento genérico para outras exceções
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage());
        }
    }


    private void consultarDados() throws SQLException {
        String[] opcoes = {"Consultar Conta", "Consultar Funcionário", "Consultar Cliente", "Voltar"};
        while (true) {
            String escolha = (String) JOptionPane.showInputDialog(
                    this,
                    "Escolha uma opção de consulta:",
                    "Consulta de Dados",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            if (escolha == null || escolha.equals("Voltar")) {
                break;
            }

            switch (escolha) {
                case "Consultar Conta":
                    consultarConta();
                    break;
                case "Consultar Funcionário":
                    consultarFuncionario();
                    break;
                case "Consultar Cliente":
                    consultarCliente();
                    break;
            }
        }
    }

    private void consultarConta() throws SQLException {
        try {
            // Solicita o número da conta ao funcionário
            String input = JOptionPane.showInputDialog("Digite o número da conta:");
            if (input == null || input.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Número da conta não pode ser vazio.");
                return;
            }

            // Instancia o DAO e o Controller para buscar a conta
            ContaDAO contaDAO = new ContaDAO();  // Use sua conexão de banco de dados
            ContaService contaService = new ContaService();
            ContaController contaController = new ContaController(contaService, contaDAO);  

            // Busca a conta pelo número
            Conta conta = contaController.buscarContaPorNumero(input);

            if (conta != null) {
                // Monta uma mensagem com as informações básicas da conta
                StringBuilder detalhes = new StringBuilder("Informações da Conta:");
                detalhes.append("\nTipo de Conta: ").append(conta.getTipoConta());
                detalhes.append("\nNúmero: ").append(conta.getNumeroConta());
                detalhes.append("\nAgência: ").append(conta.getAgencia());
                detalhes.append("\nSaldo: R$ ").append(String.format("%.2f", conta.getSaldo()));

                // Adiciona detalhes específicos de cada tipo de conta
                if (conta instanceof ContaCorrente) {
                    ContaCorrente contaCorrente = (ContaCorrente) conta;
                    detalhes.append("\nLimite Disponível: R$ ").append(String.format("%.2f", contaCorrente.getLimite()));
                    detalhes.append("\nData de Vencimento: ").append(contaCorrente.getDataVencimento());
                } else if (conta instanceof ContaPoupanca) {
                    ContaPoupanca contaPoupanca = (ContaPoupanca) conta;
                    detalhes.append("\nTaxa de Rendimento: ").append(String.format("%.2f", contaPoupanca.getTaxaRendimento() * 100)).append("%");
                }

                // Exibe as informações em um JOptionPane
                JOptionPane.showMessageDialog(this, detalhes.toString(), "Consulta de Conta", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Conta não encontrada.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um número de conta válido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage());
        }
    }



    private void consultarFuncionario() {
        try {
            int idFuncionario = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do funcionário:"));
            Funcionario funcionario = funcionarioController.buscarFuncionarioPorId(idFuncionario);
            if (funcionario != null) {
                String detalhes = "Código: " + funcionario.getCodigoFuncionario() +
                        "\nCargo: " + funcionario.getCargo() +
                        "\nNome: " + funcionario.getNome() +
                        "\nCPF: " + funcionario.getCpf() +
                        "\nData de Nascimento: " + funcionario.getDataNascimento() +
                        "\nTelefone: " + funcionario.getTelefone() +
                        "\nEndereço: " + funcionario.getEndereco();
                JOptionPane.showMessageDialog(this, detalhes);
            } else {
                JOptionPane.showMessageDialog(this, "Funcionário não encontrado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar funcionário: " + ex.getMessage());
        }
    }

    private void consultarCliente() throws SQLException {
        try {
            String input = JOptionPane.showInputDialog("Digite o ID do cliente:");
            if (input == null || input.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Operação cancelada ou ID vazio.");
                return;
            }

            int idCliente = Integer.parseInt(input.trim());
            ClienteDAO clienteDAO = new ClienteDAO();
            Cliente cliente = clienteDAO.buscarClientePorId(idCliente);

            if (cliente != null) {
                // Verifique se o nome do cliente não é vazio
                if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nome do cliente não encontrado.");
                    return;
                }
                
                String detalhes = formatarDetalhesCliente(cliente);
                JOptionPane.showMessageDialog(this, detalhes);
            } else {
                JOptionPane.showMessageDialog(this, "Cliente com ID " + idCliente + " não encontrado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido. Por favor, insira um número.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage());
        }
    }


    private String formatarDetalhesCliente(Cliente cliente) {
        Endereco endereco = cliente.getEndereco();
        String enderecoDetalhes = (endereco != null)
                ? endereco.getLocal() + ", Nº " + endereco.getNumeroCasa() + ", " + endereco.getBairro() + ", "
                + endereco.getCidade() + " - " + endereco.getEstado() + " (CEP: " + endereco.getCep() + ")"
                : "Não cadastrado";

        return "Detalhes do Cliente:\n" +
                "--------------------\n" +
                "Nome: " + cliente.getNome() + "\n" +
                "CPF: " + cliente.getCpf() + "\n" +
                "Data de Nascimento: " + cliente.getDataNascimento() + "\n" +
                "Telefone: " + cliente.getTelefone() + "\n" +
                "Endereço: " + enderecoDetalhes;
    }

    private void alterarDados() {
        String senha = JOptionPane.showInputDialog("Digite a senha de administrador:");
        if (senha == null || !senha.equals(SENHA_ADMIN)) {
            JOptionPane.showMessageDialog(this, "Senha inválida ou operação cancelada.");
            return;
        }

        String[] opcoes = {"Conta", "Funcionário", "Cliente", "Voltar"};
        while (true) {
            String escolha = (String) JOptionPane.showInputDialog(
                    this,
                    "Escolha o que deseja alterar:",
                    "Alteração de Dados",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
            );

            if (escolha == null || escolha.equals("Voltar")) {
                break;
            }

            switch (escolha) {
                case "Conta":
                    alterarDadosConta();
                    break;
                case "Funcionário":
                    alterarDadosFuncionario();
                    break;
                case "Cliente":
                    alterarDadosCliente();
                    break;
            }
        }
    }
    
    private void alterarDadosConta() {
        try {
            // Solicitar o ID da conta
            String inputIdConta = JOptionPane.showInputDialog("Digite o ID da conta:");
            if (inputIdConta == null || inputIdConta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID da conta não pode ser vazio.");
                return;
            }
            int idConta = Integer.parseInt(inputIdConta);

            // Buscar a conta no banco de dados
            ContaDAO contaDAO = new ContaDAO();
            Conta conta = contaDAO.buscarContaPorId(idConta); // Método para buscar conta pelo ID

            if (conta == null) {
                JOptionPane.showMessageDialog(this, "Conta não encontrada.");
                return;
            }

            // Identificar o tipo de conta e solicitar as alterações
            if (conta instanceof ContaCorrente) {
                alterarDadosContaCorrente((ContaCorrente) conta, contaDAO);
            } else if (conta instanceof ContaPoupanca) {
                alterarDadosContaPoupanca((ContaPoupanca) conta, contaDAO);
            } else {
                JOptionPane.showMessageDialog(this, "Tipo de conta desconhecido.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ID válido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar dados da conta: " + ex.getMessage());
        }
    }

    private void alterarDadosContaCorrente(ContaCorrente contaCorrente, ContaDAO contaDAO) {
        try {
            // Solicitar o novo limite e a nova data de vencimento
            String inputNovoLimite = JOptionPane.showInputDialog("Digite o novo limite da Conta Corrente:");
            String inputNovaDataVencimento = JOptionPane.showInputDialog("Digite a nova data de vencimento (yyyy-MM-dd):");

            if (inputNovoLimite == null || inputNovaDataVencimento == null || 
                inputNovoLimite.trim().isEmpty() || inputNovaDataVencimento.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Os campos de limite e data de vencimento são obrigatórios.");
                return;
            }

            // Atualizar os valores
            double novoLimite = Double.parseDouble(inputNovoLimite);
            LocalDate novaDataVencimento = LocalDate.parse(inputNovaDataVencimento);

            contaCorrente.setLimite(novoLimite);
            contaCorrente.setDataVencimento(novaDataVencimento);

            // Atualizar a conta no banco
            contaDAO.atualizarConta(contaCorrente);

            JOptionPane.showMessageDialog(this, "Conta Corrente alterada com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores válidos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar Conta Corrente: " + ex.getMessage());
        }
    }

    private void alterarDadosContaPoupanca(ContaPoupanca contaPoupanca, ContaDAO contaDAO) {
        try {
            // Solicitar a nova taxa de rendimento
            String inputNovaTaxaRendimento = JOptionPane.showInputDialog("Digite a nova taxa de rendimento (%):");

            if (inputNovaTaxaRendimento == null || inputNovaTaxaRendimento.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "A taxa de rendimento não pode ser vazia.");
                return;
            }

            // Atualizar o valor
            double novaTaxaRendimento = Double.parseDouble(inputNovaTaxaRendimento) / 100.0; // Converter para decimal
            contaPoupanca.setTaxaRendimento(novaTaxaRendimento);

            // Atualizar a conta no banco
            contaDAO.atualizarConta(contaPoupanca);

            JOptionPane.showMessageDialog(this, "Conta Poupança alterada com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um valor válido para a taxa de rendimento.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar Conta Poupança: " + ex.getMessage());
        }
    }


    private void alterarDadosFuncionario() {
        try {
            // Entrada dos dados atualizados
            int idFuncionario = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do funcionário:"));
            String novoNome = JOptionPane.showInputDialog("Digite o novo nome do funcionário:");
            String novoCpf = JOptionPane.showInputDialog("Digite o novo CPF:");
            String novaDataNascimento = JOptionPane.showInputDialog("Digite a nova data de nascimento (dd/MM/yyyy):");
            String novoTelefone = JOptionPane.showInputDialog("Digite o novo telefone:");
            String novoCep = JOptionPane.showInputDialog("Digite o novo CEP:");
            String novoLocal = JOptionPane.showInputDialog("Digite o novo endereço (Rua/Logradouro):");
            int novoNumeroCasa = Integer.parseInt(JOptionPane.showInputDialog("Digite o novo número da casa:"));
            String novoBairro = JOptionPane.showInputDialog("Digite o novo bairro:");
            String novaCidade = JOptionPane.showInputDialog("Digite a nova cidade:");
            String novoEstado = JOptionPane.showInputDialog("Digite o novo estado:");
            String novoCargo = JOptionPane.showInputDialog("Digite o novo cargo:");
            int novoCodigoFuncionario = Integer.parseInt(JOptionPane.showInputDialog("Digite o novo código do funcionário:"));
            String novaSenha = JOptionPane.showInputDialog("Digite a nova senha:");

            // Validar e converter a data de nascimento
            LocalDate dataNascimento;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                dataNascimento = LocalDate.parse(novaDataNascimento, formatter);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido! Use o formato dd/MM/yyyy.");
                return;
            }

            // Criar o endereço atualizado
            Endereco endereco = new Endereco(novoCep, novoLocal, novoNumeroCasa, novoBairro, novaCidade, novoEstado);

            // Criar um objeto Funcionario com os novos valores
            Funcionario funcionario = new Funcionario(
                novoNome,
                novoCpf,
                dataNascimento,
                novoTelefone,
                endereco,
                novoCodigoFuncionario,
                novoCargo,
                novaSenha // Assumindo que a senha será convertida para hash no DAO
            );
            funcionario.setId(idFuncionario); // Atualiza o ID do funcionário

            // Chamar a DAO para atualizar no banco
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            funcionarioDAO.atualizarFuncionario(funcionario);

            // Mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Dados do funcionário alterados com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores válidos nos campos numéricos.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar dados do funcionário: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void alterarDadosCliente() {
        try {
            int idCliente = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do cliente:"));
            String novoTelefone = JOptionPane.showInputDialog("Digite o novo telefone:");
            String novoEndereco = JOptionPane.showInputDialog("Digite o novo endereço (rua):");

            // Validação para garantir que o número da casa seja um inteiro válido
            int novoNumero = 0;
            try {
                novoNumero = Integer.parseInt(JOptionPane.showInputDialog("Digite o número da casa:"));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "O número da casa deve ser um número inteiro.");
                return;
            }

            String novoCep = JOptionPane.showInputDialog("Digite o CEP:");
            String novoBairro = JOptionPane.showInputDialog("Digite o bairro:");
            String novaCidade = JOptionPane.showInputDialog("Digite a cidade:");
            String novoEstado = JOptionPane.showInputDialog("Digite o estado:");

            // Validações básicas para os campos obrigatórios
            if (novoTelefone == null || novoEndereco == null || novoCep == null || novoBairro == null || 
                novaCidade == null || novoEstado == null || 
                novoTelefone.trim().isEmpty() || novoEndereco.trim().isEmpty() || 
                novoCep.trim().isEmpty() || novoBairro.trim().isEmpty() || 
                novaCidade.trim().isEmpty() || novoEstado.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos de endereço e telefone são obrigatórios.");
                return;
            }

            // Instanciando ClienteDAO corretamente
            ClienteDAO clienteDAO = new ClienteDAO(); // Criação da instância

            // Atualiza os dados do cliente usando o controller
            ClienteController clienteController = new ClienteController(clienteDAO); // Passando clienteDAO para o controller
            clienteController.atualizarCliente(idCliente, novoTelefone, novoEndereco, novoNumero, novoCep, novoBairro, novaCidade, novoEstado);

            JOptionPane.showMessageDialog(this, "Dados do cliente alterados com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ID válido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar dados do cliente: " + ex.getMessage());
        }
    }

    private void cadastrarFuncionario() {
        // Solicita a senha de administrador
        String senhaAdmin = JOptionPane.showInputDialog("Digite a senha de administrador:");
        if (senhaAdmin == null || !senhaAdmin.equals(SENHA_ADMIN)) {
            JOptionPane.showMessageDialog(this, "Senha de administrador incorreta ou operação cancelada.");
            return; // Não continua se a senha estiver errada
        }

        // Criando o painel para o formulário
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5)); // Layout simples de formulário

        // Campos de entrada do formulário
        JTextField tfCodigoFuncionario = new JTextField();
        JTextField tfCargo = new JTextField();
        JTextField tfNome = new JTextField();
        JTextField tfCpf = new JTextField();
        JTextField tfDataNascimento = new JTextField();
        JTextField tfTelefone = new JTextField();
        JTextField tfSenha = new JTextField();
        JTextField tfCep = new JTextField();
        JTextField tfLocal = new JTextField();
        JTextField tfNumeroCasa = new JTextField();
        JTextField tfBairro = new JTextField();
        JTextField tfCidade = new JTextField();
        JTextField tfEstado = new JTextField();

        // Adicionando os campos ao painel
        panel.add(new JLabel("Código do Funcionário:"));
        panel.add(tfCodigoFuncionario);
        panel.add(new JLabel("Cargo:"));
        panel.add(tfCargo);
        panel.add(new JLabel("Nome:"));
        panel.add(tfNome);
        panel.add(new JLabel("CPF:"));
        panel.add(tfCpf);
        panel.add(new JLabel("Data de Nascimento (dd/MM/yyyy):"));
        panel.add(tfDataNascimento);
        panel.add(new JLabel("Telefone:"));
        panel.add(tfTelefone);
        panel.add(new JLabel("Senha:"));
        panel.add(tfSenha);
        panel.add(new JLabel("CEP:"));
        panel.add(tfCep);
        panel.add(new JLabel("Logradouro:"));
        panel.add(tfLocal);
        panel.add(new JLabel("Número da Casa:"));
        panel.add(tfNumeroCasa);
        panel.add(new JLabel("Bairro:"));
        panel.add(tfBairro);
        panel.add(new JLabel("Cidade:"));
        panel.add(tfCidade);
        panel.add(new JLabel("Estado:"));
        panel.add(tfEstado);

        // Botão de "Cadastrar"
        int option = JOptionPane.showConfirmDialog(this, panel, "Cadastro de Funcionário", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                // Coleta os dados do formulário
                String codigoFuncionarioStr = tfCodigoFuncionario.getText();
                String cargo = tfCargo.getText();
                String nome = tfNome.getText();
                String cpf = tfCpf.getText();
                String dataNascimentoStr = tfDataNascimento.getText();
                String telefone = tfTelefone.getText();
                String senha = tfSenha.getText();
                String cep = tfCep.getText();
                String local = tfLocal.getText();
                String numeroCasaStr = tfNumeroCasa.getText();
                String bairro = tfBairro.getText();
                String cidade = tfCidade.getText();
                String estado = tfEstado.getText();

                // Validação básica dos campos obrigatórios
                if (codigoFuncionarioStr.isEmpty() || cargo.isEmpty() || nome.isEmpty() || cpf.isEmpty() || dataNascimentoStr.isEmpty() ||
                    telefone.isEmpty() || senha.isEmpty() || cep.isEmpty() || local.isEmpty() || numeroCasaStr.isEmpty() ||
                    bairro.isEmpty() || cidade.isEmpty() || estado.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.");
                    return;
                }

                // Convertendo e validando os dados
                LocalDate dataNascimento;
                int numeroCasa;
                int codigoFuncionario;
                try {
                    codigoFuncionario = Integer.parseInt(codigoFuncionarioStr);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    dataNascimento = LocalDate.parse(dataNascimentoStr, formatter);
                    numeroCasa = Integer.parseInt(numeroCasaStr);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Dados inválidos. Verifique o formato do código, data de nascimento ou número da casa.");
                    return;
                }

                // Criando o objeto Endereco
                Endereco endereco = new Endereco(cep, local, numeroCasa, bairro, cidade, estado);

                // Criando o objeto Funcionario com o construtor
                Funcionario funcionario = new Funcionario(
                    nome,
                    cpf,
                    dataNascimento,
                    telefone,
                    endereco,
                    codigoFuncionario,
                    cargo,
                    senha
                );

                // Salva o funcionário no banco de dados
                funcionarioController.cadastrarFuncionario(funcionario);

                JOptionPane.showMessageDialog(this, "Funcionário cadastrado com sucesso!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao cadastrar funcionário: " + ex.getMessage());
            }
        }
    }





    private void gerarRelatorios() {
        try {
            List<Conta> contas = bancoController.obterContas();  

            if (contas.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Não há contas disponíveis para gerar o relatório.");
                return;  // Se não houver contas, sai do método
            }

            // Chama o método do controlador para gerar o relatório, passando a lista de contas
            bancoController.gerarRelatorios(contas);
            
            JOptionPane.showMessageDialog(this, "Relatório geral gerado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar o relatório: " + e.getMessage());
        }
    }

    private void exportarParaExcel() {
        try {
            // Supondo que você tenha um método para obter a lista de contas, que pode vir de uma base de dados ou outra fonte
            List<Conta> contas = bancoController.obterContas();  // Método que obtém as contas para exportar o relatório

            // Chama o método do controlador para exportar o relatório, passando a lista de contas
            bancoController.exportarRelatorioParaExcel(contas);
            
            JOptionPane.showMessageDialog(this, "Relatório exportado para CSV. Abra o arquivo no Excel para visualização.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar o relatório: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BancoController bancoController = new BancoController();
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            ContaDAO contaDAO = new ContaDAO();
            ClienteDAO clienteDAO = new ClienteDAO();
            FuncionarioController funcionarioController = new FuncionarioController(funcionarioDAO, clienteDAO, contaDAO);
            ContaService contaService = new ContaService();
            MenuFuncionarioView frame = new MenuFuncionarioView(bancoController, funcionarioController, contaService);
            frame.setVisible(true);
        });
    }
}