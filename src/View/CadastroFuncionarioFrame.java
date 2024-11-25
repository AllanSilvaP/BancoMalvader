package View;

import model.Funcionario;
import model.Endereco;
import model.Usuario;  // Certifique-se de ter a classe Usuario
import DAO.FuncionarioDAO;
import DAO.UsuarioDAO; // Crie um DAO para Usuario
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CadastroFuncionarioFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private FuncionarioDAO funcionarioDAO;
    private UsuarioDAO usuarioDAO;

    private JTextField nomeField;
    private JTextField cpfField;
    private JTextField telefoneField;
    private JTextField cepField;
    private JTextField localField;
    private JTextField numeroCasaField;
    private JTextField bairroField;
    private JTextField cidadeField;
    private JTextField estadoField;
    private JPasswordField senhaField;
    private JTextField cargoField;
    private JTextField dataNascimentoField;
    private JTextField codigoFuncionarioField;

    public CadastroFuncionarioFrame(String nome, String cpf, String telefone, String senha) {
        this.funcionarioDAO = new FuncionarioDAO();
        this.usuarioDAO = new UsuarioDAO();  // Inicializando o UsuarioDAO

        setTitle("Cadastro de Funcionário");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        add(panel);
        placeComponents(panel, gbc, nome, cpf, telefone, senha);
    }

    private void placeComponents(JPanel panel, GridBagConstraints gbc, String nome, String cpf, String telefone, String senha) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);

        nomeField = new JTextField(20);
        nomeField.setText(nome);
        gbc.gridx = 1;
        panel.add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("CPF:"), gbc);

        cpfField = new JTextField(20);
        cpfField.setText(cpf);
        gbc.gridx = 1;
        panel.add(cpfField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Telefone:"), gbc);

        telefoneField = new JTextField(20);
        telefoneField.setText(telefone);
        gbc.gridx = 1;
        panel.add(telefoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Data de Nascimento:"), gbc);

        dataNascimentoField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(dataNascimentoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("CEP:"), gbc);

        cepField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(cepField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Local:"), gbc);

        localField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(localField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Número:"), gbc);

        numeroCasaField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(numeroCasaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Bairro:"), gbc);

        bairroField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(bairroField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Cidade:"), gbc);

        cidadeField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(cidadeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(new JLabel("Estado:"), gbc);

        estadoField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(estadoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        panel.add(new JLabel("Cargo:"), gbc);

        cargoField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(cargoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        panel.add(new JLabel("Senha:"), gbc);

        senhaField = new JPasswordField(20);
        senhaField.setText(senha);
        gbc.gridx = 1;
        panel.add(senhaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 12;
        panel.add(new JLabel("Código de Funcionário:"), gbc);

        codigoFuncionarioField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(codigoFuncionarioField, gbc);

        // Botão de cadastro
        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(e -> {
            Funcionario funcionario = obterDadosFuncionario();
            if (funcionario != null) {
                try {
                    // Primeiro, salva o usuário
                    Usuario usuario = obterFuncionario(funcionario);
                    usuarioDAO.salvarUsuario(usuario); // Salva o usuário
                    // Depois, salva o funcionário
                    funcionario.setIdUsuario(usuario.getId());  // Atribui o ID do usuário ao funcionário
                    funcionarioDAO.salvarFuncionario(funcionario);
                    limparCampos();
                    exibirMensagem("Funcionário cadastrado com sucesso!");
                } catch (Exception ex) {
                    exibirMensagem("Erro ao cadastrar funcionário: " + ex.getMessage());
                }
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 13;
        panel.add(cadastrarButton, gbc);
    }

    private Funcionario obterDadosFuncionario() {
        String nome = nomeField.getText();
        String cpf = cpfField.getText();
        String telefone = telefoneField.getText();
        String dataNascimentoStr = dataNascimentoField.getText();
        String cep = cepField.getText();
        String local = localField.getText();
        int numeroCasa = Integer.parseInt(numeroCasaField.getText());
        String bairro = bairroField.getText();
        String cidade = cidadeField.getText();
        String estado = estadoField.getText();
        String cargo = cargoField.getText();
        String senha = new String(senhaField.getPassword());
        int codigoFuncionario = Integer.parseInt(codigoFuncionarioField.getText());

        // Convertendo a data de nascimento
        LocalDate dataNascimento;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dataNascimento = LocalDate.parse(dataNascimentoStr, formatter);
        } catch (Exception e) {
            exibirMensagem("Formato de data inválido! Por favor, use o formato dd/MM/yyyy.");
            return null; // Retorna null caso a data esteja inválida
        }

        // Criando o objeto Endereco
        Endereco endereco = new Endereco(cep, local, numeroCasa, bairro, cidade, estado);

        // Criando o objeto Funcionario
        Funcionario funcionario = new Funcionario(nome, cpf, dataNascimento, telefone, endereco, codigoFuncionario, cargo, senha);

        return funcionario;
    }

    private Funcionario obterFuncionario(Funcionario funcionario) {
        // Criando o objeto Usuario
        return funcionario;
    }
    private void limparCampos() {
        nomeField.setText("");
        cpfField.setText("");
        telefoneField.setText("");
        dataNascimentoField.setText("");
        cepField.setText("");
        localField.setText("");
        numeroCasaField.setText("");
        bairroField.setText("");
        cidadeField.setText("");
        estadoField.setText("");
        cargoField.setText("");
        senhaField.setText("");
        codigoFuncionarioField.setText("");
    }

    private void exibirMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
    }
}
