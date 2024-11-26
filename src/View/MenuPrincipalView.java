package View;

import javax.swing.*;
import DAO.FuncionarioDAO;
import DAO.ContaDAO;
import DAO.ClienteDAO;
import controller.BancoController;
import controller.FuncionarioController;
import service.ContaService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipalView extends JFrame {

    private static final long serialVersionUID = 1L;
    private final BancoController bancoController;
    private final FuncionarioController funcionarioController;
    private final ClienteDAO clienteDAO;
    private final ContaService contaService;
    

    // Construtor recebe os controladores
    public MenuPrincipalView(BancoController bancoController, FuncionarioController funcionarioController) {
        if (bancoController == null || funcionarioController == null) {
            throw new IllegalArgumentException("Controladores não podem ser nulos.");
        }

        this.bancoController = bancoController;
        this.funcionarioController = funcionarioController;
		this.clienteDAO = new ClienteDAO();
		this.contaService = new ContaService();
        

        // Configuração da janela
        setTitle("Menu Principal");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Adiciona painel principal e organiza componentes
        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);
    }

    // Método para posicionar os componentes no painel
    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Botão para acessar o menu de funcionários
        JButton funcionarioButton = new JButton("Funcionário");
        funcionarioButton.setBounds(10, 80, 150, 25);
        panel.add(funcionarioButton);

        funcionarioButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Exibe o menu do funcionário reutilizando os controladores existentes
                new MenuFuncionarioView(bancoController, funcionarioController, contaService).setVisible(true);
                dispose(); // Fecha a tela atual
            }
        });

        // Botão para acessar o menu de clientes
        JButton clienteButton = new JButton("Cliente");
        clienteButton.setBounds(170, 80, 150, 25);
        panel.add(clienteButton);

        clienteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Exibe o menu do cliente
                new MenuClienteView().setVisible(true);
                dispose(); // Fecha a tela atual
            }
        });
    }

    // Método principal para iniciar o programa
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Inicializa os controladores uma única vez
                BancoController bancoController = new BancoController();
                FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
                ContaDAO contaDAO = new ContaDAO();
                ClienteDAO clienteDAO = new ClienteDAO();
                FuncionarioController funcionarioController = new FuncionarioController(funcionarioDAO, clienteDAO, contaDAO);

                // Passa os controladores para a MenuPrincipalView
                MenuPrincipalView frame = new MenuPrincipalView(bancoController, funcionarioController);
                frame.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Erro ao iniciar o programa: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

	public ClienteDAO getClienteDAO() {
		return clienteDAO;
	}
}