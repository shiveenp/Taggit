package io.shiveenp.taggit.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.junit5.MockKExtension
import io.shiveenp.taggit.db.RepoRepository
import io.shiveenp.taggit.db.RequestQueueRepository
import io.shiveenp.taggit.db.UserRepository
import io.shiveenp.taggit.generateMockRepoEntity
import io.shiveenp.taggit.models.TagInput
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.util.*
import javax.persistence.EntityManagerFactory

@ExtendWith(MockKExtension::class)
class TaggitServiceTest {

    @MockK
    private lateinit var githubService: GithubService
    @MockK
    private lateinit var userRepository: UserRepository
    @MockK
    private lateinit var repoRepository: RepoRepository
    @MockK
    private lateinit var entityManagerFactory: EntityManagerFactory
    @MockK
    private lateinit var requestQueueRepository: RequestQueueRepository

    private val repoEntity = generateMockRepoEntity()
    private val mapper = jacksonObjectMapper()

    @OverrideMockKs
    private lateinit var taggitService: TaggitService

    @BeforeEach
    fun setup() {
        every { repoRepository.findByIdOrNull(any()) } returns repoEntity
        every { repoRepository.save(any()) } returnsArgument 0
    }

    @Test
    fun `add repo tag returns the right data on successfully save`() {
        val repoId = UUID.randomUUID()
        val tagInput = TagInput("test")
        runBlocking {
            val repoEntity = taggitService.addRepoTag(repoId, tagInput)
            repoEntity shouldNotBe null
            repoEntity?.metadata shouldNotBe null
            repoEntity!!.metadata!!.tags shouldBe listOf("test")
        }
    }

    @Test
    fun `add repo tag throws exception if user tries to save untagged keyword`() {
        val repoId = UUID.randomUUID()
        val tagInput = TagInput("untagged")
        shouldThrow<IllegalArgumentException> {
            runBlocking {
                taggitService.addRepoTag(repoId, tagInput)
            }
        }
    }

    @Test
    fun `should strip non alphanumeric characters from the tag string`() {
        val repoId = UUID.randomUUID()
        val tagInput = TagInput("test-new'")
        runBlocking {
            val repoEntity = taggitService.addRepoTag(repoId, tagInput)
            repoEntity shouldNotBe null
            repoEntity?.metadata shouldNotBe null
            repoEntity!!.metadata!!.tags shouldBe listOf("testnew")
        }
    }

    @Test
    fun `should throw exception if blank string left after stripping non alphanumeric chars`() {
        val repoId = UUID.randomUUID()
        val tagInput = TagInput("'#$%%^@")
        shouldThrow<IllegalArgumentException> {
            runBlocking {
                taggitService.addRepoTag(repoId, tagInput)
            }
        }
    }
}