package com.shiveenp.taggit

import com.shiveenp.taggit.db.TaggitRepoRepository
import com.shiveenp.taggit.db.TaggitUserRepository
import org.hamcrest.Matchers
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

//https://dev.to/rieckpil/write-spring-boot-integration-tests-with-testcontainers-junit-4-5-40am
//https://dev.to/sivalabs/springboot-integration-testing-using-testcontainers-starter-13h2
// https://www.baeldung.com/spring-boot-testcontainers-integration-test
// https://dzone.com/articles/testcontainers-and-spring-boot

@TestInstance(TestInstance.Lifecycle.PER_CLASS) //https://stackoverflow.com/a/48821395
@Testcontainers
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [RepoTagControllerTest.Companion.Initializer::class, TaggitApplication::class])
class RepoTagControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient
    private val testUser = generateMockUserEntity()

    @Autowired
    private lateinit var taggitUserRepository: TaggitUserRepository

    @Autowired
    private lateinit var taggitRepoRepository: TaggitRepoRepository

    companion object {

        @Container
        val postgreSQLContainer = PostgreSQLContainer<Nothing>().apply {
            withUsername("postgres")
            withPassword("")
            withDatabaseName("taggit")
        }

        class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
            override fun initialize(configurableApplicationContext: ConfigurableApplicationContext) {
                TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.jdbcUrl,
                    "spring.datasource.username=" + postgreSQLContainer.username,
                    "spring.datasource.password=" + postgreSQLContainer.password
                ).applyTo(configurableApplicationContext.getEnvironment());
            }
        }
    }

    @Test
    fun `we can save single repos tags correctly`() {
        val testRepo = generateMockRepoEntity(testUser.id)
        taggitRepoRepository.save(testRepo)
        val tagInputToSave = generateRandomTagInput()
        webTestClient.post()
            .uri("/user/${testUser.id}/repos/${testRepo.id}/tag")
            .bodyValue(tagInputToSave)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.metadata.tags")
            .isNotEmpty
            .jsonPath("$.metadata.tags")
            .isEqualTo(tagInputToSave.tag)
    }

    @Test
    fun `we can save multiple tags correctly`() {
        val testRepo = generateMockRepoEntity(testUser.id)
        taggitRepoRepository.save(testRepo)
        val tagInputToSave1 = generateRandomTagInput()
        val tagInputToSave2 = generateRandomTagInput()
        // save first tag
        webTestClient.post()
            .uri("/user/${testUser.id}/repos/${testRepo.id}/tag")
            .bodyValue(tagInputToSave1)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.metadata.tags")
            .isNotEmpty
            .jsonPath("$.metadata.tags")
            .isEqualTo(tagInputToSave1.tag)
        // save second tag
        webTestClient.post()
            .uri("/user/${testUser.id}/repos/${testRepo.id}/tag")
            .bodyValue(tagInputToSave2)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.metadata.tags")
            .isNotEmpty
            .jsonPath("$.metadata.tags")
            .isArray
            .jsonPath("$.metadata.tags")
            .value(hasItem(tagInputToSave1.tag))
            .jsonPath("$.metadata.tags")
            .value(hasItem(tagInputToSave2.tag))
            .jsonPath("$.metadata.tags")
            .value(Matchers.hasSize<String>(2))
    }


    @BeforeAll
    internal fun setUp() {
        taggitUserRepository.save(testUser)
    }
}
