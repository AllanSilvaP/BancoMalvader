package View;

import model.Funcionario;

public interface CadastroFuncionarioView {
    void exibirCadastroFuncionario(); // Exibe a tela de cadastro de funcionário
    void exibirMensagem(String mensagem); // Exibe uma mensagem para o usuário
    void limparCampos(); // Limpa os campos de entrada
    Funcionario obterDadosFuncionario(); // Obtém os dados do funcionário a partir dos campos
}
