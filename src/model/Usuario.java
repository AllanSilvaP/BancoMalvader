package model;

import java.io.Serializable;
import java.time.LocalDate;
import org.mindrot.jbcrypt.BCrypt;

public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    // Atributos
    private int id;  // Novo campo de id
    protected String nome;
    protected String cpf;
    protected LocalDate dataNascimento;
    protected String telefone;
    protected String senhaHash;
    protected Endereco endereco;

    // Construtor da classe Usuario
    public Usuario(String nome, String cpf, LocalDate dataNascimento, String telefone, String senha, Endereco endereco) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("O CPF não pode ser vazio.");
        }
        if (dataNascimento == null) {
            throw new IllegalArgumentException("A data de nascimento não pode ser nula.");
        }
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone não pode ser vazio.");
        }
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }

        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.senhaHash = gerarHashSenha(senha);  // Gera o hash da senha
        this.endereco = endereco;
    }

    private String gerarHashSenha(String senha) {
        if (senha == null || senha.isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser nula ou vazia.");
        }
        return BCrypt.hashpw(senha, BCrypt.gensalt());
    }

    public boolean login(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        return BCrypt.checkpw(senha, this.senhaHash);
    }

    // Método de logout
    public void logout() {
        System.out.println("Usuário " + nome + " deslogado com sucesso.");
    }

    // Método para consultar dados básicos do usuário
    public String consultarDados() {
        return String.format(
            "Nome: %s, CPF: %s, Telefone: %s, Data de Nascimento: %s, Endereço: %s",
            nome, cpf, telefone, dataNascimento, endereco
        );
    }

    // Métodos abstratos que as subclasses devem implementar
    public abstract String obterDadosCliente();

    // Getters

    public int getId() {  // Método getId()
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    // Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("O CPF não pode ser vazio.");
        }
        this.cpf = cpf;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("A data de nascimento não pode ser nula.");
        }
        this.dataNascimento = dataNascimento;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new IllegalArgumentException("O telefone não pode ser vazio.");
        }
        this.telefone = telefone;
    }

    public void setSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        this.senhaHash = gerarHashSenha(senha);
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    // Métodos obrigatórios para subclasses
    public abstract Usuario getUsuario();
}
