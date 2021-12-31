package io.shiveenp.taggit

import io.github.serpro69.kfaker.Faker
import io.shiveenp.taggit.db.TaggitRepoEntity
import io.shiveenp.taggit.db.TaggitUserEntity
import io.shiveenp.taggit.models.TagInput
import io.shiveenp.taggit.models.TagMetadata
import org.apache.commons.math3.random.RandomDataGenerator
import java.time.LocalDateTime
import java.util.*

val faker = Faker()
val randomNumberGenerator = RandomDataGenerator()

fun generateMockUserEntity() =
    TaggitUserEntity(
        id = UUID.randomUUID(),
        userName = faker.name.name(),
        email = faker.theITCrowd.emails(),
        avatarUrl = "http://google.com",
        githubUserName = faker.name.neutralFirstName(),
        githubUserId = randomNumberGenerator.nextLong(Long.MIN_VALUE, Long.MAX_VALUE),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

fun generateMockRepoEntity(userId: UUID? = null,
                           metadata: TagMetadata? = null) =
    TaggitRepoEntity(
        id = UUID.randomUUID(),
        repoId = randomNumberGenerator.nextLong(Long.MIN_VALUE, Long.MAX_VALUE),
        repoName = faker.siliconValley.inventions(),
        githubLink = "http://google.com",
        githubDescription = null,
        starCount = randomNumberGenerator.nextLong(Long.MIN_VALUE, Long.MAX_VALUE),
        ownerAvatarUrl = "http://google.com",
        metadata = metadata
    )

fun generateRandomTagInput(tag: String? = null) = TagInput(tag ?: faker.book.title())

