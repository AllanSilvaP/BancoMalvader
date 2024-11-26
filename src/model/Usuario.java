package model;

import java.io.Serializable;
import java.time.LocalDate;

public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    protected String nome;
    protected String cpf;
    protected LocalDate dataNascimento;
    protected String telefone;
    protected String senha;
    protected Endereco endereco;

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
        this.senha = senha;
        this.endereco = endereco;
    }

    public boolean login(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("A senha não pode ser vazia.");
        }
        return this.senha.equals(senha);
    }

    public void logout() {
        System.out.println("Usuário " + nome + " deslogado com sucesso.");
    }

    public String consultarDados() {
        return String.format(
            "Nome: %s, CPF: %s, Telefone: %s, Data de Nascimento: %s, Endereço: %s",
            nome, cpf, telefone, dataNascimento, endereco
        );
    }

    public abstract String obterDadosCliente();

    public int getId() {
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

    public String getSenha() {
        return senha;
    }

    public Endereco getEndereco() {
        return endereco;
    }

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
        this.senha = senha;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public abstract Usuario getUsuario();
}