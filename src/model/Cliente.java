package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Cliente extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id_cliente; // O id_cliente agora é somente leitura, mas será atribuído automaticamente após a inserção no banco.
    private Conta conta;

    public Cliente(String nome, String cpf, LocalDate dataNascimento, String telefone, Endereco endereco, String senha, Conta conta) {
        // Não passa o id_cliente para o super, pois ele será gerado automaticamente após a inserção
        super(nome, cpf, dataNascimento, telefone, senha, endereco);
        this.conta = conta;
    }

    public Cliente() {
        super("", "", null, "", "", null);  // Inicializa com valores vazios
    }

    @Override
    public Usuario getUsuario() {
        return this;
    }

    @Override
    public String consultarDados() {
        return String.format("Cliente: %s, CPF: %s, Telefone: %s", nome, cpf, telefone);
    }

    @Override
    public String toString() {
        return String.format("Cliente [Nome: %s, CPF: %s, Conta: %s, Endereço: %s]", 
                nome, 
                cpf,
                (conta != null ? conta.getNumeroConta() : "Sem Conta"),
                endereco != null ? endereco.toString() : "Endereço não disponível");
    }

    // Getter para id_cliente
    public int getId_cliente() {
        return id_cliente;
    }

    // Setter para id_cliente que pode ser chamado após a inserção no banco para atribuir o ID gerado
    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    @Override
    public String obterDadosCliente() {
        return String.format("Cliente: %s, CPF: %s, Telefone: %s, Endereço: %s, Conta: %s", 
                nome, 
                cpf, 
                telefone, 
                endereco != null ? endereco.toString() : "Endereço não disponível",
                conta != null ? conta.getNumeroConta() : "Sem conta");
    }
    
    public void setUsuario(Usuario usuario) {
        this.setNome(usuario.getNome());
        this.setCpf(usuario.getCpf());
        this.setDataNascimento(usuario.getDataNascimento());
        this.setTelefone(usuario.getTelefone());
        this.setEndereco(usuario.getEndereco());
        // Outros campos de Usuario, se necessário
    }

}
