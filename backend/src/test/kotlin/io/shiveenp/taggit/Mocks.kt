package io.shiveenp.taggit

import io.github.serpro69.kfaker.Faker
import io.shiveenp.taggit.db.RepoEntity
import io.shiveenp.taggit.db.UserEntity
import io.shiveenp.taggit.models.GithubUser
import io.shiveenp.taggit.models.TagMetadata
import org.apache.commons.math3.random.RandomDataGenerator
import java.time.LocalDateTime
import java.util.*

val faker = Faker()
val randomNumberGenerator = RandomDataGenerator()

fun mockUserEntity() =
    UserEntity(
        id = UUID.randomUUID(),
        userName = faker.name.name(),
        email = faker.theITCrowd.emails(),
        avatarUrl = "http://google.com",
        githubUserName = faker.name.neutralFirstName(),
        githubUserId = randomNumberGenerator.nextLong(Long.MIN_VALUE, Long.MAX_VALUE),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

fun mockRepoEntity(metadata: TagMetadata? = null) =
    RepoEntity(
        id = UUID.randomUUID(),
        repoId = 123,
        repoName = faker.siliconValley.inventions(),
        githubLink = "http://google.com",
        githubDescription = null,
        starCount = 5,
        ownerAvatarUrl = "http://google.com",
        metadata = metadata
    )

fun mockGithubUser() = GithubUser(
    id = 100L,
    login = faker.name.neutralFirstName(),
    avatarUrl = "https://fake.url",
    name = faker.name.neutralFirstName(),
    email = "fake@email.com",
    githubToken = faker.random.randomString()
)