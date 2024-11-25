package model;

import java.io.Serializable;
import java.time.LocalDate;

public class Funcionario extends Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private int codigoFuncionario;
    private String cargo;
    private Endereco endereco;

    public Funcionario() {
    	super("", "", null, "", "", new Endereco());  // Chama o construtor de Usuario com valores padrão
    }

    public Funcionario(String nome, String cpf, LocalDate dataNascimento, String telefone, Endereco endereco, int codigoFuncionario, String cargo, String senha) {
        super(nome, cpf, dataNascimento, telefone, senha, endereco);  // Chama o construtor de Usuario

        if (codigoFuncionario <= 0) {
            throw new IllegalArgumentException("Código do funcionário não pode ser nulo ou vazio.");
        }
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo não pode ser nulo ou vazio.");
        }
        this.codigoFuncionario = codigoFuncionario;
        this.cargo = cargo;
    }

    @Override
    public Usuario getUsuario() {
        return this;  // Retorna o próprio objeto Funcionario como um Usuario
    }

    @Override
    public String consultarDados() {
        return String.format("Nome: %s, Cargo: %s", nome, cargo);
    }

    @Override
    public String toString() {
        return String.format("Funcionario{codigoFuncionario='%s', cargo='%s', nome='%s', cpf='%s', telefone='%s', endereco=%s}",
                codigoFuncionario, cargo, nome, cpf, telefone, (endereco != null ? endereco.toString() : "Não Informado"));
    }

    // Getters e Setters
    public int getCodigoFuncionario() {
        return codigoFuncionario;
    }

    public void setCodigoFuncionario(int codigoFuncionario) {
        this.codigoFuncionario = codigoFuncionario;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo não pode ser nulo ou vazio.");
        }
        this.cargo = cargo;
    }
    
    public Endereco getEndereco() {
    	return endereco;
    }
    
    public void setEndereco(Endereco endereco) {
    	this.endereco = endereco;
    }
    
    public int getIdUsuario() {
        return super.getId();
    }

    public void setIdUsuario(int idUsuario) {
        super.setId(idUsuario);
    }

	@Override
	public String obterDadosCliente() {
		 return String.format("Funcionário: %s, CPF: %s, Telefone: %s, Endereço: %s", 
                 nome, 
                 cpf, 
                 telefone, 
                 endereco != null ? endereco.toString() : "Endereço não disponível");
	}
}
