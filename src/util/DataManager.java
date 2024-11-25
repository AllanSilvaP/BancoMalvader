package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Funcionario;
import model.Conta;

public class DataManager {

    public static void salvarContas(List<Conta> contas, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(contas);
        } catch (IOException e) {
            System.err.println("Erro ao salvar as contas: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Conta> carregarContas(String filename) {
        List<Conta> contas = new ArrayList<>();
        File arquivo = new File(filename);
        
        if (!arquivo.exists()) {
            System.err.println("Arquivo não encontrado: " + filename);
            return contas;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) { 
                contas = (List<Conta>) obj; 
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar as contas: " + e.getMessage());
        }
        return contas;
    }

    public static void salvarFuncionarios(List<Funcionario> funcionarios, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(funcionarios);
        } catch (IOException e) {
            System.err.println("Erro ao salvar os funcionários: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Funcionario> carregarFuncionarios(String filename) {
        List<Funcionario> funcionarios = new ArrayList<>();
        File arquivo = new File(filename);
        
        if (!arquivo.exists()) {
            System.err.println("Arquivo não encontrado: " + filename);
            return funcionarios;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) { 
                funcionarios = (List<Funcionario>) obj; 
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar os funcionários: " + e.getMessage());
        }
        return funcionarios;
    }
}
