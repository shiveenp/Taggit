package io.shiveenp.taggit.db

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaggitUserRepository : CrudRepository<TaggitUserEntity, UUID> {
    fun findByGithubUserId(githubUserId: Long): TaggitUserEntity?
}

@Repository
interface TaggitRepoRepository : PagingAndSortingRepository<TaggitRepoEntity, UUID>
