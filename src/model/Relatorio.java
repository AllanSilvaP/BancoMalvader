package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Relatorio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tipoRelatorio;
    private LocalDateTime dataGeracao;
    private String conteudo;
    private List<Conta> contas; // Lista de contas para gerar relatório

    // Construtor
    public Relatorio(String tipoRelatorio, LocalDateTime dataGeracao, String conteudo, List<Conta> contas) {
        this.tipoRelatorio = tipoRelatorio;
        this.dataGeracao = dataGeracao;
        this.conteudo = conteudo;
        this.contas = contas;
    }

    // Gerar relatório geral em formato CSV com informações da conta
    public void gerarRelatorioGeral() {
        System.out.println("Gerando relatório geral...");
        String nomeArquivo = "relatorio_" + tipoRelatorio + "_" + dataGeracao.toString().replace(":", "_") + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            // Escreve o cabeçalho do CSV
            writer.write("Tipo,Data de Geração,Conteúdo,Numero da Conta,Tipo de Conta,Saldo");
            writer.newLine();

            // Escreve as informações do relatório
            writer.write(tipoRelatorio + "," + dataGeracao.toString() + "," + conteudo);
            writer.newLine();

            // Escreve as informações das contas
            for (Conta conta : contas) {
                writer.write("," + conta.getNumeroConta() + "," + conta.getTipoConta() + "," + conta.getSaldo());
                writer.newLine();
            }

            System.out.println("Relatório geral gerado com sucesso!");
        } catch (IOException e) {
            System.err.println("Erro ao gerar o relatório: " + e.getMessage());
        }
    }

    // Exportar relatório para Excel
    public void exportarParaExcel() {
        System.out.println("Exportando relatório para Excel...");

        String nomeArquivo = "relatorio_" + tipoRelatorio + "_" + dataGeracao.toString().replace(":", "_") + ".csv";
        File arquivo = new File(nomeArquivo);

        if (arquivo.exists()) {
            System.out.println("Arquivo CSV gerado com sucesso! Abra o arquivo no Excel.");
        } else {
            System.err.println("Erro: O arquivo não foi encontrado.");
        }
    }

    // Getters e Setters
    public String getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(String tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public List<Conta> getContas() {
        return contas;
    }

    public void setContas(List<Conta> contas) {
        this.contas = contas;
    }
}
