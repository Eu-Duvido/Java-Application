package com.euduvido.euduvido_api.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade que representa um insight gerado pelo Gemini para um usuário.
 *
 * Fluxo:
 *   1. Java chama GET /insights/dados-perfil na API Python
 *   2. Python retorna dados do DW + prompt_sugerido
 *   3. Java envia para o Gemini
 *   4. Gemini retorna JSON com 3 insights
 *   5. Java salva cada insight aqui
 */
@Entity
@Table(
    name = "ai_insights",
    indexes = {
        @Index(name = "ix_ai_perfil",     columnList = "no_curso, no_regiao, tp_modalidade"),
        @Index(name = "ix_ai_tipo",       columnList = "tipo"),
        @Index(name = "ix_ai_nivel",      columnList = "nivel"),
        @Index(name = "ix_ai_dt_geracao", columnList = "dt_geracao")
    }
)
public class AiInsightEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_insight")
    private Long idInsight;

    // ── Perfil do usuário ──────────────────────────────────────────────────

    @Column(name = "no_curso", nullable = false, length = 255)
    private String noCurso;

    @Column(name = "no_area_geral", nullable = false, length = 255)
    private String noAreaGeral;

    @Column(name = "no_regiao", nullable = false, length = 50)
    private String noRegiao;

    /** 1 = Presencial | 2 = EAD */
    @Column(name = "tp_modalidade", nullable = false)
    private Integer tpModalidade;

    @Column(name = "nu_ano_censo")
    private Integer nuAnoCenso;

    // ── Conteúdo do insight ────────────────────────────────────────────────

    /** competitividade | conclusao | modalidade */
    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    /** Número principal exibido no card */
    @Column(name = "valor_destaque", nullable = false)
    private Double valorDestaque;

    /** Ex.: '% de ocupação', 'inscritos/vaga' */
    @Column(name = "unidade", nullable = false, length = 100)
    private String unidade;

    /** Texto motivacional gerado pelo Gemini */
    @Column(name = "interpretacao", nullable = false, columnDefinition = "TEXT")
    private String interpretacao;

    /** JSON com séries para o front renderizar gráficos */
    @Column(name = "dados_grafico", nullable = false, columnDefinition = "JSON")
    private String dadosGrafico;

    /** alto | medio | baixo — define a cor do card no front */
    @Column(name = "nivel", nullable = false, length = 10)
    private String nivel;

    @Column(name = "dt_geracao", nullable = false)
    private LocalDateTime dtGeracao;

    // ── Getters e Setters ──────────────────────────────────────────────────

    public Long getIdInsight() { return idInsight; }
    public void setIdInsight(Long idInsight) { this.idInsight = idInsight; }

    public String getNoCurso() { return noCurso; }
    public void setNoCurso(String noCurso) { this.noCurso = noCurso; }

    public String getNoAreaGeral() { return noAreaGeral; }
    public void setNoAreaGeral(String noAreaGeral) { this.noAreaGeral = noAreaGeral; }

    public String getNoRegiao() { return noRegiao; }
    public void setNoRegiao(String noRegiao) { this.noRegiao = noRegiao; }

    public Integer getTpModalidade() { return tpModalidade; }
    public void setTpModalidade(Integer tpModalidade) { this.tpModalidade = tpModalidade; }

    public Integer getNuAnoCenso() { return nuAnoCenso; }
    public void setNuAnoCenso(Integer nuAnoCenso) { this.nuAnoCenso = nuAnoCenso; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Double getValorDestaque() { return valorDestaque; }
    public void setValorDestaque(Double valorDestaque) { this.valorDestaque = valorDestaque; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public String getInterpretacao() { return interpretacao; }
    public void setInterpretacao(String interpretacao) { this.interpretacao = interpretacao; }

    public String getDadosGrafico() { return dadosGrafico; }
    public void setDadosGrafico(String dadosGrafico) { this.dadosGrafico = dadosGrafico; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public LocalDateTime getDtGeracao() { return dtGeracao; }
    public void setDtGeracao(LocalDateTime dtGeracao) { this.dtGeracao = dtGeracao; }
}
