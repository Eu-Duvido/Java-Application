package com.euduvido.euduvido_api.infrastructure.repositories;

import com.euduvido.euduvido_api.domain.entities.Challenge;
import com.euduvido.euduvido_api.domain.enums.ChallengeStatus;
import com.euduvido.euduvido_api.domain.enums.Difficulty;
import com.euduvido.euduvido_api.domain.enums.GoalType;
import com.euduvido.euduvido_api.domain.pagination.PageResult;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.ChallengeEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.entities.UserEntity;
import com.euduvido.euduvido_api.infrastructure.persistence.repositories.UserJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MySQLContainer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = {
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@Transactional
@EnabledIf("dockerAvailable")
class ChallengeRepositoryImplTest {

    static boolean dockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Exception e) {
            return false;
        }
    }

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("euduvido_test")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void startContainer() {
        mysql.start();
    }

    @AfterAll
    static void stopContainer() {
        mysql.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                mysql.getJdbcUrl() + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.MySQL8Dialect");
    }

    @PersistenceContext EntityManager em;
    @Autowired ChallengeRepositoryImpl challengeRepository;
    @Autowired UserJpaRepository userJpaRepository;

    private UserEntity savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userJpaRepository.saveAndFlush(
                new UserEntity(null, "Test User", "repo-test@test.com", "password", null));
    }

    private ChallengeEntity persistChallenge(String title, LocalDateTime deadline, ChallengeStatus status) {
        ChallengeEntity entity = new ChallengeEntity();
        entity.setTitle(title);
        entity.setDescription("Descrição de " + title);
        entity.setDifficulty(Difficulty.EASY);
        entity.setSubject("Math");
        entity.setGoalType(GoalType.HOURS);
        entity.setGoalValue(2);
        entity.setDeadline(deadline);
        entity.setStatus(status);
        entity.setLocationRequired(false);
        entity.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        entity.setCreator(savedUser);
        entity.setParticipants(List.of());
        em.persist(entity);
        em.flush();
        return entity;
    }

    @Test
    void findById_returnsSavedChallenge() {
        ChallengeEntity saved = persistChallenge("Desafio A", LocalDateTime.now().plusDays(7), ChallengeStatus.PENDING);

        Optional<Challenge> result = challengeRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Desafio A");
    }

    @Test
    void findExpiredCandidates_returnsOnlyPendingAndActiveWithPastDeadline() {
        persistChallenge("Expirado Pending", LocalDateTime.now().minusHours(2), ChallengeStatus.PENDING);
        persistChallenge("Expirado Active",  LocalDateTime.now().minusHours(1), ChallengeStatus.ACTIVE);
        persistChallenge("Completado",       LocalDateTime.now().minusHours(1), ChallengeStatus.COMPLETED);
        persistChallenge("Futuro",           LocalDateTime.now().plusDays(3),   ChallengeStatus.PENDING);
        em.flush();
        em.clear();

        List<Challenge> expired = challengeRepository.findExpiredCandidates(
                LocalDateTime.now(),
                List.of(ChallengeStatus.PENDING, ChallengeStatus.ACTIVE));

        assertThat(expired).hasSize(2);
        assertThat(expired).extracting(Challenge::getTitle)
                .containsExactlyInAnyOrder("Expirado Pending", "Expirado Active");
    }

    @Test
    void findAllPaged_returnsPaginatedResults() {
        persistChallenge("C1", LocalDateTime.now().plusDays(1), ChallengeStatus.PENDING);
        persistChallenge("C2", LocalDateTime.now().plusDays(2), ChallengeStatus.ACTIVE);
        persistChallenge("C3", LocalDateTime.now().plusDays(3), ChallengeStatus.PENDING);
        em.flush();
        em.clear();

        PageResult<Challenge> page = challengeRepository.findAllPaged(Optional.empty(), 0, 2);

        assertThat(page.totalElements()).isEqualTo(3);
        assertThat(page.content()).hasSize(2);
    }

    @Test
    void findAllPaged_filtersByStatus() {
        persistChallenge("Pending1", LocalDateTime.now().plusDays(1), ChallengeStatus.PENDING);
        persistChallenge("Active1",  LocalDateTime.now().plusDays(2), ChallengeStatus.ACTIVE);
        em.flush();
        em.clear();

        PageResult<Challenge> page = challengeRepository.findAllPaged(
                Optional.of(ChallengeStatus.PENDING), 0, 10);

        assertThat(page.content()).allMatch(c -> c.getStatus() == ChallengeStatus.PENDING);
    }
}
