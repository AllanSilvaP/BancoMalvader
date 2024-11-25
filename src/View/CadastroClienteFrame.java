package View;

import model.Cliente;
import model.Endereco;
import DAO.ClienteDAO;
import DAO.UsuarioDAO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CadastroClienteFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private ClienteDAO clienteDAO;
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
    private JTextField dataNascimentoField;

    public CadastroClienteFrame() {
        this.clienteDAO = new ClienteDAO();
        this.usuarioDAO = new UsuarioDAO();

        setTitle("Cadastro de Cliente");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        add(panel);
        placeComponents(panel, gbc);
    }

    private void placeComponents(JPanel panel, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nome:"), gbc);

        nomeField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nomeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("CPF:"), gbc);

        cpfField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(cpfField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Telefone:"), gbc);

        telefoneField = new JTextField(20);
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
        panel.add(new JLabel("Senha:"), gbc);

        senhaField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(senhaField, gbc);

        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.addActionListener(e -> cadastrarCliente());
        gbc.gridx = 1;
        gbc.gridy = 11;
        panel.add(cadastrarButton, gbc);
    }

    private void cadastrarCliente() {
        try {
            // Obter dados do formulário e criar o objeto Usuario (agora instanciando Cliente)
            Cliente cliente = obterDadosCliente();

            // Salvar o usuário e obter o ID gerado pelo banco
            int idUsuario = usuarioDAO.salvarUsuario(cliente); // Agora passando o cliente para salvar o usuário
            if (idUsuario == -1) {
                exibirMensagem("CPF já cadastrado!");
                return;
            }

            // Atualizar o ID do usuário no endereço
            cliente.getEndereco().setIdEndereco(idUsuario);

            // Salvar cliente no banco
            clienteDAO.salvarCliente(cliente);

            limparCampos();
            exibirMensagem("Cliente cadastrado com sucesso!");
        } catch (Exception ex) {
            exibirMensagem("Erro ao cadastrar cliente: " + ex.getMessage());
        }
    }

    private Cliente obterDadosCliente() {
        String nome = nomeField.getText();
        String cpf = cpfField.getText();
        String telefone = telefoneField.getText();
        String senha = new String(senhaField.getPassword());
        LocalDate dataNascimento = LocalDate.parse(dataNascimentoField.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        // Criar o endereço com os dados do formulário
        Endereco endereco = new Endereco(
            cepField.getText(),
            localField.getText(),
            Integer.parseInt(numeroCasaField.getText()),
            bairroField.getText(),
            cidadeField.getText(),
            estadoField.getText()
        );

        // Criar o cliente (instanciando Cliente, que estende Usuario)
        Cliente cliente = new Cliente(nome, cpf, dataNascimento, telefone, endereco, senha, null);
        
        // Associar o endereço ao cliente
        endereco.setCliente(cliente);

        return cliente;
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
        senhaField.setText("");
    }

    private void exibirMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
    }
}
