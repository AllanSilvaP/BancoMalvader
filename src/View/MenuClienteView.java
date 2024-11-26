	package View;
	
	import javax.swing.*;
	import controller.BancoController;
	import exception.ValorInvalidoException;
	import model.Transacao;  // Certifique-se de importar a classe Transacao
	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
	
	public class MenuClienteView extends JFrame {
	
	    private static final long serialVersionUID = 1L;
	    private BancoController bancoController;
	
	    public MenuClienteView() {
	        bancoController = new BancoController();
	
	        setTitle("Menu Cliente");
	        setSize(300, 400);
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        setLocationRelativeTo(null);
	
	        JPanel panel = new JPanel();
	        add(panel);
	        placeComponents(panel);
	    }
	
	    public void exibirMenu() {
	        setVisible(true);
	    }
	
	    private void placeComponents(JPanel panel) {
	        panel.setLayout(null);
	
	        JButton saldoButton = new JButton("Saldo");
	        saldoButton.setBounds(10, 30, 200, 25);
	        panel.add(saldoButton);

	        saldoButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                String contaStr = JOptionPane.showInputDialog("Digite o número da conta:");
	                
	                // Ensure a valid input is provided (it can be null or empty)
	                if (contaStr != null && !contaStr.isEmpty()) {
	                    String numeroConta = contaStr;  
	                    try {
	                        double saldo = bancoController.consultarSaldo(numeroConta);  // Directly fetch the saldo from bancoController
	                        JOptionPane.showMessageDialog(null, "Saldo: " + saldo);
	                    } catch (SQLException e1) {
	                        e1.printStackTrace();
	                        JOptionPane.showMessageDialog(null, "Erro ao consultar o saldo.");
	                    }
	                } else {
	                    JOptionPane.showMessageDialog(null, "Número da conta inválido.");
	                }
	            }
	        });

	
	        JButton depositoButton = new JButton("Depósito");
	        depositoButton.setBounds(10, 70, 200, 25);
	        panel.add(depositoButton);

	        depositoButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                String contaStr = JOptionPane.showInputDialog("Digite o número da conta:");
	                
	                // Check if the account number is provided
	                if (contaStr != null && !contaStr.isEmpty()) {
	                    String numeroConta = contaStr;
	                    try {
	                        String valorStr = JOptionPane.showInputDialog("Digite o valor do depósito:");
	                        
	                        // Check if the deposit value is a valid number
	                        if (valorStr != null && !valorStr.isEmpty()) {
	                            double valor = Double.parseDouble(valorStr);
	                            
	                            // Ensure the deposit value is valid (positive)
	                            if (valor <= 0) {
	                                JOptionPane.showMessageDialog(null, "O valor do depósito deve ser positivo.");
	                            } else {
	                                try {
										bancoController.realizarDeposito(numeroConta, valor);
									} catch (SQLException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
	                                JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso.");
	                            }
	                        } else {
	                            JOptionPane.showMessageDialog(null, "Valor inválido para depósito.");
	                        }
	                    } catch (NumberFormatException ex) {
	                        JOptionPane.showMessageDialog(null, "Por favor, insira um valor válido.");
	                    } catch (ValorInvalidoException ex) {
	                        JOptionPane.showMessageDialog(null, "Erro ao realizar depósito: " + ex.getMessage());
	                    }
	                } else {
	                    JOptionPane.showMessageDialog(null, "Número da conta inválido.");
	                }
	            }
	        });
	
	        JButton saqueButton = new JButton("Saque");
	        saqueButton.setBounds(10, 110, 200, 25);
	        panel.add(saqueButton);

	        saqueButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                // Solicita o número da conta ao usuário
	                String contaStr = JOptionPane.showInputDialog("Digite o número da conta:");
	                String numeroConta = contaStr;  
	                
	                // Solicita o valor do saque ao usuário
	                String valorStr = JOptionPane.showInputDialog("Digite o valor do saque:");
	                
	                // Tenta converter o valor para double
	                try {
	                    double valor = Double.parseDouble(valorStr);

	                    // Verifica se o valor do saque é positivo
	                    if (valor <= 0) {
	                        JOptionPane.showMessageDialog(null, "O valor do saque deve ser positivo.");
	                        return;
	                    }

	                    // Chama o método do controlador para realizar o saque
	                    try {
							bancoController.realizarSaque(numeroConta, valor);
						} catch (ValorInvalidoException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}  
	                } catch (NumberFormatException ex) {
	                    // Caso o valor informado não seja um número válido
	                    JOptionPane.showMessageDialog(null, "Valor inválido. Por favor, insira um número válido.");
	                }
	            }
	        });

	
	        JButton extratoButton = new JButton("Extrato");
	        extratoButton.setBounds(10, 150, 200, 25);
	        panel.add(extratoButton);

	        extratoButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                // Solicita o número da conta ao usuário
	                String contaStr = JOptionPane.showInputDialog("Digite o número da conta:");
	                String numeroConta = contaStr;

	                // Verifica se o número da conta não é vazio ou nulo
	                if (numeroConta != null && !numeroConta.isEmpty()) {
	                    try {
	                        // Tenta obter o extrato da conta
	                        List<Transacao> transacoes = bancoController.getExtrato(numeroConta);

	                        // Verifica se há transações para a conta
	                        if (transacoes.isEmpty()) {
	                            JOptionPane.showMessageDialog(null, "Nenhuma transação encontrada para esta conta.");
	                        } else {
	                            // Cria o conteúdo do extrato
	                            StringBuilder extrato = new StringBuilder("Extrato da Conta " + numeroConta + ":\n\n");

	                            for (Transacao transacao : transacoes) {
	                                extrato.append("Data: ").append(transacao.getDataHora()).append("\n");
	                                extrato.append("Tipo: ").append(transacao.getTipoTransacao()).append("\n");
	                                extrato.append("Valor: R$ ").append(transacao.getValor()).append("\n\n");
	                            }

	                            // Exibe o extrato em uma área de texto rolável
	                            JTextArea textArea = new JTextArea(extrato.toString());
	                            textArea.setEditable(false); // Impede a edição do texto
	                            JScrollPane scrollPane = new JScrollPane(textArea);
	                            JOptionPane.showMessageDialog(null, scrollPane, "Extrato", JOptionPane.INFORMATION_MESSAGE);
	                        }
	                    } catch (Exception ex) {
	                        // Captura erros durante a execução e exibe uma mensagem
	                        JOptionPane.showMessageDialog(null, "Erro ao carregar o extrato: " + ex.getMessage());
	                    }
	                } else {
	                    // Caso o número da conta seja inválido
	                    JOptionPane.showMessageDialog(null, "Número da conta inválido.");
	                }
	            }
	        });

	
	        JButton sairButton = new JButton("Sair");
	        sairButton.setBounds(10, 230, 200, 25);
	        panel.add(sairButton);
	
	        sairButton.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	                System.exit(0); 
	            }
	        });
	    }
	
	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> {
	            MenuClienteView frame = new MenuClienteView();
	            frame.setVisible(true);
	        });
	    }
	}