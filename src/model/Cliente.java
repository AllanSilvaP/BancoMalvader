package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Cliente extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id_cliente; // O id_cliente agora é somente leitura, mas será atribuído automaticamente após a inserção no banco.
    private Conta conta;

    public Cliente(String nome, String cpf, LocalDate dataNascimento, String telefone, Endereco endereco, String senha, Conta conta) {
        super(nome, cpf, dataNascimento, telefone, senha, endereco);
        this.conta = conta;
    }
/*
    public Cliente() {
        super("Desconhecido", "", null, "", "", null);  // Inicializa com valores vazios
    }
*/
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


    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }
    
    public void setUsuario(Usuario usuario) {
        this.setNome(usuario.getNome());
        this.setCpf(usuario.getCpf());
        this.setDataNascimento(usuario.getDataNascimento());
        this.setTelefone(usuario.getTelefone());
        this.setEndereco(usuario.getEndereco());
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
    

}