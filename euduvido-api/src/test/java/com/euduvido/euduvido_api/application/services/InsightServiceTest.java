package com.euduvido.euduvido_api.application.services;

import com.euduvido.euduvido_api.infrastructure.persistence.entities.AiInsightEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.AiInsightJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InsightServiceTest {

    @Mock RestTemplate restTemplate;
    @Mock JdbcTemplate jdbc;
    @Mock AiInsightJpaRepository insightRepository;

    private InsightService service;
    private final AtomicLong ids = new AtomicLong(1L);

    @BeforeEach
    void setUp() {
        service = new InsightService();
        ReflectionTestUtils.setField(service, "geminiApiKey", "test-key");
        ReflectionTestUtils.setField(service, "geminiBaseUrl", "https://gemini.test");
        ReflectionTestUtils.setField(service, "geminiModel", "gemini-test");
        ReflectionTestUtils.setField(service, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(service, "jdbc", jdbc);
        ReflectionTestUtils.setField(service, "insightRepository", insightRepository);
        ReflectionTestUtils.setField(service, "objectMapper", new ObjectMapper());

        mockDadosDW();
        when(insightRepository.save(any(AiInsightEntity.class))).thenAnswer(invocation -> {
            AiInsightEntity entity = invocation.getArgument(0);
            entity.setIdInsight(ids.getAndIncrement());
            return entity;
        });
    }

    @Test
    void gerarInsights_retornaGeminiQuandoIaFunciona() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(respostaGemini("""
                [
                  {
                    "tipo": "competitividade",
                    "titulo": "Alta procura",
                    "descricao": "O curso tem boa ocupacao.",
                    "valor_destaque": 82.5,
                    "unidade": "% de ocupacao",
                    "interpretacao": "Mantenha uma rotina consistente.",
                    "nivel": "alto"
                  }
                ]
                """));

        List<AiInsightEntity> result = service.gerarInsights("Direito", "Nordeste", 1, "Negocios");

        assertEquals(1, result.size());
        assertEquals("gemini", result.get(0).getFonte());
        assertEquals("competitividade", result.get(0).getTipo());
        assertNotNull(result.get(0).getDtGeracao());
    }

    @Test
    void gerarInsights_retornaFallbackQuandoGeminiEstaIndisponivel() throws Exception {
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenThrow(new ResourceAccessException("timeout"));

        List<AiInsightEntity> result = service.gerarInsights("Direito", "Nordeste", 1, "Negocios");

        assertFallbackValido(result);
    }

    @Test
    void gerarInsights_retornaFallbackQuandoApiKeyInvalida() throws Exception {
        HttpClientErrorException erroChave = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                null,
                "API key expired. Please renew the API key.".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenThrow(erroChave);

        List<AiInsightEntity> result = service.gerarInsights("Direito", "Nordeste", 1, "Negocios");

        assertFallbackValido(result);
    }

    private void assertFallbackValido(List<AiInsightEntity> result) {
        assertEquals(3, result.size());
        result.forEach(insight -> {
            assertNotNull(insight.getIdInsight());
            assertNotNull(insight.getTitulo());
            assertNotNull(insight.getDescricao());
            assertNotNull(insight.getInterpretacao());
            assertNotNull(insight.getNivel());
            assertNotNull(insight.getTipo());
            assertNotNull(insight.getValorDestaque());
            assertNotNull(insight.getUnidade());
            assertEquals("Direito", insight.getNoCurso());
            assertEquals("Nordeste", insight.getNoRegiao());
            assertEquals(1, insight.getTpModalidade());
            assertEquals("Negocios", insight.getNoAreaGeral());
            assertNotNull(insight.getDtGeracao());
            assertEquals("fallback", insight.getFonte());
        });
    }

    private Map<String, Object> respostaGemini(String json) {
        return Map.of(
                "candidates", List.of(Map.of(
                        "content", Map.of(
                                "parts", List.of(Map.of("text", json))
                        )
                ))
        );
    }

    private void mockDadosDW() {
        when(jdbc.queryForList(contains("vw_ranking_cursos_area"), eq("Direito")))
                .thenReturn(List.of(Map.of(
                        "no_curso", "Direito",
                        "no_area_geral", "Negocios",
                        "total_ingressantes", 1000,
                        "total_vagas", 1200,
                        "total_matriculados", 5000,
                        "total_concluintes", 700,
                        "num_ies_ofertantes", 20,
                        "pct_conclusao", 42.0
                )));
        when(jdbc.queryForList(contains("vw_ranking_cursos_regiao"), eq("Direito"), eq("Nordeste")))
                .thenReturn(List.of(Map.of(
                        "no_regiao", "Nordeste",
                        "no_curso", "Direito",
                        "no_area_geral", "Negocios",
                        "total_ingressantes", 300,
                        "total_matriculados", 1300,
                        "total_concluintes", 160,
                        "total_vagas", 400,
                        "pct_conclusao", 45.0,
                        "pct_ocupacao_vagas", 75.0,
                        "rank_na_regiao", 3
                )));
        when(jdbc.queryForList(contains("vw_ead_vs_presencial"), eq("Nordeste")))
                .thenReturn(List.of(
                        Map.of("modalidade", "Presencial", "total_matriculados", 1200, "total_ingressantes", 300, "total_concluintes", 150, "num_cursos", 10),
                        Map.of("modalidade", "EAD", "total_matriculados", 800, "total_ingressantes", 250, "total_concluintes", 120, "num_cursos", 8)
                ));
        when(jdbc.queryForList(contains("vw_resumo_geral")))
                .thenReturn(List.of(Map.of("nu_ano_censo", 2024)));
    }
}
