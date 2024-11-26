package View;

import model.Conta;

public interface CadastroContaView {
    void exibirCadastroConta(); // Exibe a tela de cadastro de conta
    void exibirMensagem(String mensagem); // Exibe uma mensagem para o usuário
    void limparCampos(); // Limpa os campos de entrada
    Conta obterDadosConta(); // Obtém os dados da conta a partir dos campos
}