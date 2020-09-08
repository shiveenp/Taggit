package com.shiveenp.taggit

import com.shiveenp.taggit.db.TaggitRepoRepository
import com.shiveenp.taggit.db.TaggitUserRepository
import com.shiveenp.taggit.models.Metadata
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
    fun `we can save single tags correctly`() {
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

    @Test
    fun `we can delete single tags correctly`() {
        val tagToDelete = "hello,fake-tag"
        val testRepo = generateMockRepoEntity(testUser.id, Metadata(listOf(tagToDelete)))
        taggitRepoRepository.save(testRepo)

        // technically you wanna check that the tag was saved properly before checking again,
        // but in this case we're trusting the repository impl and would hopefully test that in
        // a separate test
        webTestClient.delete()
            .uri("/user/${testUser.id}/repos/${testRepo.id}/tag?tag=$tagToDelete")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.metadata.tags")
            .isEmpty
    }

    @Test
    fun `we can delete multiple tags correctly`() {
        // let's start off by saving three tags wne we will delete them one by one
        val tagToDelete1 = "hello,fake-tag 1"
        val tagToDelete2 = "hello, fake-tag 2"
        val tagToDelete3 = "hello, fake tag 3"
        val testRepo = generateMockRepoEntity(testUser.id,
            Metadata(listOf(tagToDelete1, tagToDelete2, tagToDelete3)))
        taggitRepoRepository.save(testRepo)

        // 1 deleted, 2 remaining
        webTestClient.delete()
            .uri("/user/${testUser.id}/repos/${testRepo.id}/tag?tag=$tagToDelete1")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.metadata.tags")
            .isArray
            .jsonPath("$.metadata.tags")
            .value(hasItem(tagToDelete2))
            .jsonPath("$.metadata.tags")
            .value(hasItem(tagToDelete3))
            .jsonPath("$.metadata.tags")
            .value(Matchers.hasSize<String>(2))

        // 2 deleted, 1 remaining
        webTestClient.delete()
            .uri("/user/${testUser.id}/repos/${testRepo.id}/tag?tag=$tagToDelete2")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.metadata.tags")
            .isArray
            .jsonPath("$.metadata.tags")
            .value(hasItem(tagToDelete3))
            .jsonPath("$.metadata.tags")
            .value(Matchers.hasSize<String>(1))

        // all deleted
        webTestClient.delete()
            .uri("/user/${testUser.id}/repos/${testRepo.id}/tag?tag=$tagToDelete3")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.metadata.tags")
            .isEmpty
    }

    // Todo: Add test for deleting tags with `/`
    // Todo: Add tests for user input validation here, such as what happens when user sends a null tag,
    // take exampled from the mixit repo

    @BeforeAll
    internal fun setUp() {
        taggitUserRepository.save(testUser)
    }
}
