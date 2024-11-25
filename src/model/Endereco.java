package model;

import java.io.Serializable;

public class Endereco implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_endereco;  // ID gerado automaticamente
    private String cep;
    private String local;
    private int numero_casa;
    private String bairro;
    private String cidade;
    private String estado;
    private Cliente cliente; // Relacionamento com Cliente
    private Funcionario funcionario;

    // Construtor completo
    public Endereco(int id_endereco, String cep, String local, int numero_casa, String bairro, String cidade, String estado) {
        this.id_endereco = id_endereco;
        setCep(cep);
        setLocal(local);
        setNumeroCasa(numero_casa);
        setBairro(bairro);
        setCidade(cidade);
        setEstado(estado);
    }

    // Construtor sem parâmetros
    public Endereco() {
    }
    
 // Construtor sem id_endereco
    public Endereco(String cep, String local, int numero_casa, String bairro, String cidade, String estado) {
        setCep(cep);
        setLocal(local);
        setNumeroCasa(numero_casa);
        setBairro(bairro);
        setCidade(cidade);
        setEstado(estado);
    }


    @Override
    public String toString() {
        return String.format("%s, %d, %s, %s, %s - %s", local, numero_casa, bairro, cidade, estado, cep);
    }

    // Método para criar um objeto Endereco a partir de uma string no formato específico
    public static Endereco fromString(String enderecoStr) {
        // Exemplo de formato: "local, numeroCasa, bairro, cidade, estado - cep"
        String[] partes = enderecoStr.split(", ");
        String local = partes[0];
        int numero_casa = Integer.parseInt(partes[1]);
        String bairro = partes[2];
        String cidade = partes[3];
        String estado = partes[4].split(" - ")[0];
        String cep = partes[4].split(" - ")[1];

        // Retorna o novo objeto Endereco com os dados extraídos
        return new Endereco(0, cep, local, numero_casa, bairro, cidade, estado); // ID será gerado pelo banco
    }
    

    // Getters e Setters

    public int getIdEndereco() {
        return id_endereco;
    }

    public void setIdEndereco(int id_endereco) {
        this.id_endereco = id_endereco;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        if (cep == null || cep.isEmpty()) {
            throw new IllegalArgumentException("CEP não pode ser nulo ou vazio.");
        }
        this.cep = cep;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        if (local == null || local.isEmpty()) {
            throw new IllegalArgumentException("Local não pode ser nulo ou vazio.");
        }
        this.local = local;
    }

    public int getNumeroCasa() {
        return numero_casa;
    }

    public void setNumeroCasa(int numero_casa) {
        if (numero_casa <= 0) {
            throw new IllegalArgumentException("Número da casa deve ser um valor positivo.");
        }
        this.numero_casa = numero_casa;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        if (bairro == null || bairro.isEmpty()) {
            throw new IllegalArgumentException("Bairro não pode ser nulo ou vazio.");
        }
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        if (cidade == null || cidade.isEmpty()) {
            throw new IllegalArgumentException("Cidade não pode ser nula ou vazia.");
        }
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        if (estado == null || estado.isEmpty()) {
            throw new IllegalArgumentException("Estado não pode ser nulo ou vazio.");
        }
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }
}
