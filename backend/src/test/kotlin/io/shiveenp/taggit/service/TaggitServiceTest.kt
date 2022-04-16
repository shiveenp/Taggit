package io.shiveenp.taggit.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.OverrideMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import io.mockk.verify
import io.shiveenp.taggit.db.*
import io.shiveenp.taggit.generateMockGithubUser
import io.shiveenp.taggit.generateMockRepoEntity
import io.shiveenp.taggit.generateMockUserEntity
import io.shiveenp.taggit.models.TagInput
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.*
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

    @MockK
    private lateinit var mapper: ObjectMapper

    private val userEntity = generateMockUserEntity()
    private val repoEntity = generateMockRepoEntity()
    private val githubUser = generateMockGithubUser()


    @OverrideMockKs
    private lateinit var taggitService: TaggitService

    @BeforeEach
    fun setup() {
        // all mocks here are for happy paths
        every { repoRepository.findByIdOrNull(any()) } returns repoEntity
        every { repoRepository.save(any()) } returnsArgument 0
    }

    @Test
    fun `loginOrRegister successfully creates a new user`() {
        runBlocking {
            coEvery { githubService.getUserData() } returns githubUser
            every { userRepository.findByGithubUserId(any()) } returns null
            every { userRepository.save(any()) } returnsArgument 0
            val expectedUser = UserEntity.from(githubUser).toDto()

            val user = taggitService.loginOrRegister()

            user.userName shouldBe expectedUser.userName
            user.githubUserName shouldBe expectedUser.githubUserName
            user.githubUserId shouldBe expectedUser.githubUserId
            user.avatarUrl shouldBe expectedUser.avatarUrl
        }
    }

    @Test
    fun `loginOrRegister successfully updates an existing user`() {
        runBlocking {
            val updatedUser = githubUser.copy(name = "new-test-name")
            coEvery { githubService.getUserData() } returns updatedUser
            every { userRepository.findByGithubUserId(any()) } returns UserEntity.from(githubUser)
            every { userRepository.save(any()) } returnsArgument 0
            val expectedUser = UserEntity.from(githubUser).toDto()

            val user = taggitService.loginOrRegister()

            user.userName shouldBe "new-test-name"
            user.githubUserName shouldBe expectedUser.githubUserName
            user.githubUserId shouldBe expectedUser.githubUserId
            user.avatarUrl shouldBe expectedUser.avatarUrl
        }
    }

    @Test
    fun `getUser should return the right user when present`() {
        runBlocking {
            every { userRepository.findAll() } returns listOf(userEntity)

            val user = taggitService.getUser()

            user shouldNotBe null
            user!!.userName shouldBe userEntity.userName
            user.githubUserName shouldBe userEntity.githubUserName
            user.githubUserId shouldBe userEntity.githubUserId
            user.avatarUrl shouldBe userEntity.avatarUrl
        }
    }

    @Test
    fun `getUser should return null when not present`() {
        runBlocking {
            every { userRepository.findAll() } returns emptyList()

            val user = taggitService.getUser()

            user shouldBe  null
        }
    }

    @Test
    fun `getUserStarredRepos should return the right data when no page and size provided`() {
        runBlocking {
            val pageRequestSlot = slot<PageRequest>()
            val expectedPageRequest = PageRequest.of(
                1,
                50,
                Sort.by("repoName").ascending()
            )
            every { repoRepository.findAll(capture(pageRequestSlot)) } returns PageImpl(listOf(repoEntity))

            val starredRepos = taggitService.getUserStarredRepos()

            pageRequestSlot.captured shouldBe expectedPageRequest
            starredRepos.data shouldBe listOf(repoEntity.toDto())
        }
    }

    @Test
    fun `getUserStarredRepos should return the right data when page and size is provided`() {
        runBlocking {
            val pageRequestSlot = slot<PageRequest>()
            val expectedPageRequest = PageRequest.of(
                3,
                100,
                Sort.by("repoName").ascending()
            )
            every { repoRepository.findAll(capture(pageRequestSlot)) } returns PageImpl(listOf(repoEntity))

            val starredRepos = taggitService.getUserStarredRepos(3, 100)

            pageRequestSlot.captured shouldBe expectedPageRequest
            starredRepos.data shouldBe listOf(repoEntity.toDto())
        }
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