package View;

import javax.swing.*;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;

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

public class MenuFuncionarioView extends JFrame {

    private static final long serialVersionUID = 1L;
    private final BancoController bancoController;
    private final FuncionarioController funcionarioController;
    private static final String SENHA_ADMIN = "Admin123";

    public MenuFuncionarioView(BancoController bancoController, FuncionarioController funcionarioController) {
        if (bancoController == null || funcionarioController == null) {
            throw new IllegalArgumentException("Controladores não podem ser nulos.");
        }
        this.bancoController = bancoController;
        this.funcionarioController = funcionarioController;

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
    
    private void abrirConta() {
        String tipoConta = JOptionPane.showInputDialog("Tipo de conta (POUPANCA ou CORRENTE):").toUpperCase();
        String agencia = JOptionPane.showInputDialog("Agência:");
        String numeroConta = JOptionPane.showInputDialog("Número da conta:");
        double saldo = Double.parseDouble(JOptionPane.showInputDialog("Saldo inicial:"));
        int idCliente = Integer.parseInt(JOptionPane.showInputDialog("ID do cliente:"));

        try {
            if (tipoConta.equals("POUPANCA")) {
                funcionarioController.abrirContaPoupanca(agencia, numeroConta, saldo, idCliente);
            } else if (tipoConta.equals("CORRENTE")) {
                double limite = Double.parseDouble(JOptionPane.showInputDialog("Limite:"));
                LocalDate vencimento = LocalDate.parse(JOptionPane.showInputDialog("Vencimento (yyyy-MM-dd):"));
                funcionarioController.abrirContaCorrente(agencia, numeroConta, saldo, idCliente, limite, vencimento);
            } else {
                JOptionPane.showMessageDialog(this, "Tipo de conta inválido.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao abrir conta: " + e.getMessage());
        }
    }

 // Método para encerrar conta pelo número
    private void encerrarConta() throws Exception {
        String numeroConta = JOptionPane.showInputDialog("Número da conta para encerrar:");

        try {
            if (numeroConta == null || numeroConta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "O número da conta não pode ser vazio.");
                return;
            }

            ContaController contaController = new ContaController();
			// Chama o método para encerrar a conta
            contaController.encerrarConta(numeroConta);

            JOptionPane.showMessageDialog(this, "Conta com número " + numeroConta + " encerrada com sucesso!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao encerrar conta: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
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
            String input = JOptionPane.showInputDialog("Digite o id da conta:");
            if (input == null || input.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Id Conta não pode ser vazio.");
                return;
            }

            int idConta = Integer.parseInt(input);

            ContaController contaController = new ContaController();
            Conta conta = contaController.buscarConta(idConta);

            if (conta != null) {
                String detalhes = "Tipo de Conta: " + conta.getTipoConta() +
                        "\nNome: " + conta.getCliente().getNome() +
                        "\nCPF: " + conta.getCliente().getCpf() +
                        "\nSaldo: R$ " + conta.getSaldo();

                if (conta instanceof ContaCorrente) {
                    ContaCorrente contaCorrente = (ContaCorrente) conta;
                    detalhes += "\nLimite Disponível: R$ " + contaCorrente.getLimite() +
                                "\nData de Vencimento: " + contaCorrente.getDataVencimento();
                } else if (conta instanceof ContaPoupanca) {
                    ContaPoupanca contaPoupanca = (ContaPoupanca) conta;
                    detalhes += "\nTaxa de Rendimento: " + contaPoupanca.getTaxaRendimento() + "%";
                }

                JOptionPane.showMessageDialog(this, detalhes);
            } else {
                JOptionPane.showMessageDialog(this, "Conta não encontrada.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ID de conta válido.");
        } catch (ClassCastException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao acessar detalhes específicos da conta: " + ex.getMessage());
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
            // Obter dados da conta a ser alterada
            int idConta = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID da conta:"));
            double novoLimite = Double.parseDouble(JOptionPane.showInputDialog("Digite o novo limite:"));
            String novaDataVencimento = JOptionPane.showInputDialog("Digite a nova data de vencimento (yyyy-MM-dd):");

            // Buscar a conta no banco de dados usando o ID da conta
            ContaDAO contaDAO = new ContaDAO();
            Conta conta = contaDAO.buscarContaPorId(idConta); // Método para buscar a conta pelo ID (caso precise)

            if (conta != null) {
                // Atualizar as informações da conta
                if (conta instanceof ContaCorrente) {
                    ContaCorrente contaCorrente = (ContaCorrente) conta;
                    contaCorrente.setLimite(novoLimite);
                    contaCorrente.setDataVencimento(LocalDate.parse(novaDataVencimento));

                    // Atualizar a conta no banco
                    contaDAO.atualizarConta(contaCorrente); // Passa a conta já com os novos valores

                    JOptionPane.showMessageDialog(this, "Dados da conta alterados com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(this, "Conta não é do tipo Conta Corrente.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Conta não encontrada.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores válidos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar dados da conta: " + ex.getMessage());
        }
    }

    private void alterarDadosFuncionario() {
        try {
            // Entrada dos dados atualizados
            int idFuncionario = Integer.parseInt(JOptionPane.showInputDialog("Digite o ID do funcionário:"));
            String novoNome = JOptionPane.showInputDialog("Digite o novo nome do funcionário:");
            String novoCpf = JOptionPane.showInputDialog("Digite o novo CPF:");
            String novaDataNascimento = JOptionPane.showInputDialog("Digite a nova data de nascimento (yyyy-MM-dd):");
            String novoTelefone = JOptionPane.showInputDialog("Digite o novo telefone:");
            String novaSenha = JOptionPane.showInputDialog("Digite a nova senha:");

            // Criar um objeto Funcionario com os novos valores
            Funcionario funcionario = new Funcionario();
            funcionario.setId(idFuncionario);
            funcionario.setNome(novoNome);
            funcionario.setCpf(novoCpf);
            funcionario.setDataNascimento(LocalDate.parse(novaDataNascimento));
            funcionario.setTelefone(novoTelefone);
            funcionario.setSenha(novaSenha); // Assumindo que a senha já está sendo convertida para hash

            // Chamar a DAO para atualizar no banco
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            funcionarioDAO.atualizarFuncionario(funcionario);

            // Mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Dados do funcionário alterados com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores válidos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar dados do funcionário: " + ex.getMessage());
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

            // Atualiza os dados do cliente usando o controller
            ClienteController clienteController = new ClienteController();
            clienteController.atualizarCliente(idCliente, novoTelefone, novoEndereco, novoNumero, novoCep, novoBairro, novaCidade, novoEstado);

            JOptionPane.showMessageDialog(this, "Dados do cliente alterados com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ID válido.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar dados do cliente: " + ex.getMessage());
        }
    }


    
    private void cadastrarFuncionario() {
        try {
            // Solicita a senha de administrador
            String senhaAdmin = JOptionPane.showInputDialog("Digite a senha de administrador:");
            if (senhaAdmin == null || !senhaAdmin.equals(SENHA_ADMIN)) {
                JOptionPane.showMessageDialog(this, "Senha de administrador incorreta ou operação cancelada.");
                return;
            }

            // Coleta os dados do funcionário
            String codigoFuncionarioStr = JOptionPane.showInputDialog("Digite o código do funcionário:");
            String cargo = JOptionPane.showInputDialog("Digite o cargo:");
            String nome = JOptionPane.showInputDialog("Digite o nome do funcionário:");
            String cpf = JOptionPane.showInputDialog("Digite o CPF (apenas números):");
            String dataNascimentoStr = JOptionPane.showInputDialog("Digite a data de nascimento (yyyy-MM-dd):");
            String telefone = JOptionPane.showInputDialog("Digite o telefone:");
            String senha = JOptionPane.showInputDialog("Digite a senha para o funcionário:");

            // Coleta o endereço
            String cep = JOptionPane.showInputDialog("Digite o CEP:");
            String local = JOptionPane.showInputDialog("Digite o logradouro:");
            String numeroCasaStr = JOptionPane.showInputDialog("Digite o número da casa:");
            String bairro = JOptionPane.showInputDialog("Digite o bairro:");
            String cidade = JOptionPane.showInputDialog("Digite a cidade:");
            String estado = JOptionPane.showInputDialog("Digite o estado:");

            // Validação básica dos campos obrigatórios
            if (codigoFuncionarioStr == null || cargo == null || nome == null || cpf == null || dataNascimentoStr == null || telefone == null || senha == null ||
                cep == null || local == null || numeroCasaStr == null || bairro == null || cidade == null || estado == null ||
                codigoFuncionarioStr.trim().isEmpty() || cargo.trim().isEmpty() || nome.trim().isEmpty() || cpf.trim().isEmpty() ||
                dataNascimentoStr.trim().isEmpty() || telefone.trim().isEmpty() || senha.trim().isEmpty() || cep.trim().isEmpty() || 
                local.trim().isEmpty() || numeroCasaStr.trim().isEmpty() || bairro.trim().isEmpty() || cidade.trim().isEmpty() || estado.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.");
                return;
            }

            // Convertendo e validando os dados
            LocalDate dataNascimento;
            int numeroCasa;
            int codigoFuncionario;
            try {
                codigoFuncionario = Integer.parseInt(codigoFuncionarioStr);
                dataNascimento = LocalDate.parse(dataNascimentoStr);
                numeroCasa = Integer.parseInt(numeroCasaStr);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Dados inválidos. Verifique o formato do código, data de nascimento ou número da casa.");
                return;
            }

            // Criando o objeto Endereco
            Endereco endereco = new Endereco();
            endereco.setCep(cep);
            endereco.setLocal(local);
            endereco.setNumeroCasa(numeroCasa);
            endereco.setBairro(bairro);
            endereco.setCidade(cidade);
            endereco.setEstado(estado);

            // Criando o objeto Funcionario
            Funcionario funcionario = new Funcionario();
            funcionario.setCodigoFuncionario(codigoFuncionario);
            funcionario.setCargo(cargo);
            funcionario.setNome(nome);
            funcionario.setCpf(cpf);
            funcionario.setDataNascimento(dataNascimento);
            funcionario.setTelefone(telefone);
            funcionario.setEndereco(endereco);
            funcionario.setSenha(senha); // Define a senha informada pelo usuário

            // Salva o funcionário no banco de dados
            funcionarioController.cadastrarFuncionario(funcionario);

            JOptionPane.showMessageDialog(this, "Funcionário cadastrado com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar dados: valores numéricos inválidos.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar funcionário: " + ex.getMessage());
        }
    }



    private void gerarRelatorios() {
        try {
            // Chama o método do controlador para gerar o relatório
            bancoController.gerarRelatorios();
            JOptionPane.showMessageDialog(this, "Relatório geral gerado com sucesso!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao gerar o relatório: " + e.getMessage());
        }
    }

    private void exportarParaExcel() {
        try {
            // Chama o método do controlador para exportar o relatório
            bancoController.exportarRelatorioParaExcel();
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
            MenuFuncionarioView frame = new MenuFuncionarioView(bancoController, funcionarioController);
            frame.setVisible(true);
        });
    }
}

