package io.shiveenp.taggit.integration

import io.shiveenp.taggit.db.RepoRepository
import io.shiveenp.taggit.mockRepoEntity
import io.shiveenp.taggit.models.TagInput
import io.shiveenp.taggit.models.TagMetadata
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RepoControllerIT {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var repoRepository: RepoRepository

    companion object {
        @Container
        val container = PostgreSQLContainer<Nothing>("postgres:12").apply {
            withDatabaseName("testdb")
            withUsername("duke")
            withPassword("s3crEt")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl);
            registry.add("spring.datasource.password", container::getPassword);
            registry.add("spring.datasource.username", container::getUsername);
        }
    }

    @Test
    fun `we can save single tags correctly`() {
        val testRepo = mockRepoEntity()
        repoRepository.save(testRepo)
        val tagInputToSave = TagInput("testnewtag")
        webTestClient.post()
            .uri("/api/repos/${testRepo.id}/tag")
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
        val testRepo = mockRepoEntity()
        repoRepository.save(testRepo)
        val tagInputToSave1 = TagInput("testtag1")
        val tagInputToSave2 = TagInput("testtag2")
        // save first tag
        webTestClient.post()
            .uri("/api/repos/${testRepo.id}/tag").bodyValue(tagInputToSave1)
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
            .uri("/api/repos/${testRepo.id}/tag").bodyValue(tagInputToSave2)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.metadata.tags")
            .isNotEmpty
            .jsonPath("$.metadata.tags")
            .isArray
            .jsonPath("$.metadata.tags")
            .value(Matchers.hasItem(tagInputToSave1.tag))
            .jsonPath("$.metadata.tags")
            .value(Matchers.hasItem(tagInputToSave2.tag))
            .jsonPath("$.metadata.tags")
            .value(Matchers.hasSize<String>(2))
    }

    @Test
    fun `saving tags with just special characters results in error`() {
        val tagToDelete1 = "/" // this tag could cause issues if not sent as a query param
        val tagToDelete2 = "🤓🦆🚀🏕🥰"
        val tagToDelete3 = "       "
        val testRepo = mockRepoEntity(
            TagMetadata(listOf(tagToDelete1, tagToDelete2, tagToDelete3))
        )
        repoRepository.save(testRepo)

        webTestClient.post()
            .uri("/api/repos/${testRepo.id}/tag")
            .exchange()
            .expectStatus()
            .is4xxClientError
    }

    @Test
    fun `we can delete single tags correctly`() {
        val tagToDelete = "helloTagToDelete"
        val testRepo = mockRepoEntity(TagMetadata(listOf(tagToDelete)))
        repoRepository.save(testRepo)

        webTestClient.delete()
            .uri("/api/repos/${testRepo.id}/tag?tag=helloTagToDelete")
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .expectBody()
            .jsonPath("$.metadata.tags")
            .isEmpty
    }
}