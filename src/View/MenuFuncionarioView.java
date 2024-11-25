package View;

import javax.swing.*;
import DAO.FuncionarioDAO;
import DAO.ContaDAO;
import controller.BancoController;
import controller.FuncionarioController;
import model.Funcionario;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;

public class MenuFuncionarioView extends JFrame {

    private static final long serialVersionUID = 1L;
    private final BancoController bancoController;
    private final FuncionarioController funcionarioController;

    // Construtor que recebe os controladores necessários
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

    // Configuração dos botões no painel
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
        encerramentoContaButton.addActionListener(e -> encerrarConta());

        // Botão para consulta de dados
        gbc.gridy++;
        JButton consultaDadosButton = new JButton("Consulta de Dados");
        panel.add(consultaDadosButton, gbc);
        consultaDadosButton.addActionListener(e -> consultarDados());

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

    // Métodos dos botões

    private void cadastrarFuncionario() {
        try {
            String nome = JOptionPane.showInputDialog("Digite o nome do funcionário:");
            if (nome != null && !nome.trim().isEmpty()) {
                Funcionario novoFuncionario = new Funcionario();
                novoFuncionario.setNome(nome);
                funcionarioController.cadastrarFuncionario(novoFuncionario);
                JOptionPane.showMessageDialog(null, "Funcionário cadastrado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "O nome não pode ser vazio.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao cadastrar funcionário: " + ex.getMessage());
        }
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

    private void encerrarConta() {
        String numeroConta = JOptionPane.showInputDialog("Número da conta para encerrar:");
        try {
            funcionarioController.encerrarConta(numeroConta);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao encerrar conta: " + e.getMessage());
        }
    }

    private void consultarDados() {
        bancoController.consultarDados();
    }

    private void alterarDados() {
        try {
            int numeroConta = Integer.parseInt(JOptionPane.showInputDialog("Digite o número da conta para alterar:"));
            String novoNome = JOptionPane.showInputDialog("Digite o novo nome do cliente:");
            bancoController.alterarDados(String.valueOf(numeroConta), novoNome);
            JOptionPane.showMessageDialog(this, "Dados alterados com sucesso!");
        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao alterar os dados: " + ex.getMessage());
        }
    }

    private void gerarRelatorios() {
        bancoController.gerarRelatorios();
    }

    private void exportarParaExcel() {
        bancoController.exportarRelatorioParaExcel();
    }
    

    // Método principal para testar a interface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BancoController bancoController = new BancoController();
            FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
            ContaDAO contaDAO = new ContaDAO();
            FuncionarioController funcionarioController = new FuncionarioController(funcionarioDAO, contaDAO);
            MenuFuncionarioView frame = new MenuFuncionarioView(bancoController, funcionarioController);
            frame.setVisible(true);
        });
    }
}
