package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Relatorio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tipoRelatorio;  
    private LocalDateTime dataGeracao; 
    private String conteudo; 

    // Construtor
    public Relatorio(String tipoRelatorio, LocalDateTime dataGeracao, String conteudo) {
        this.tipoRelatorio = tipoRelatorio;
        this.dataGeracao = dataGeracao;
        this.conteudo = conteudo;
    }

    // Gerar relatório geral em formato CSV
    public void gerarRelatorioGeral() {
        System.out.println("Gerando relatório geral...");
        String nomeArquivo = "relatorio_" + tipoRelatorio + "_" + dataGeracao.toString().replace(":", "_") + ".csv";
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo))) {
            writer.write("Tipo,Data de Geração,Conteúdo");
            writer.newLine();

            writer.write(tipoRelatorio + "," + dataGeracao.toString() + "," + conteudo);
            writer.newLine();
            
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
}
