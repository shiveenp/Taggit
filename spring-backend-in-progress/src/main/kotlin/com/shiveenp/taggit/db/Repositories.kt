package com.shiveenp.taggit.db

import com.shiveenp.taggit.db.TaggitRepoEntity
import com.shiveenp.taggit.db.TaggitUserEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaggitUserRepository : CrudRepository<TaggitUserEntity, UUID> {
    fun findByGithubUserId(githubUserId: Long): TaggitUserEntity?
}

@Repository
interface TaggitRepoRepository : PagingAndSortingRepository<TaggitRepoEntity, UUID> {
    fun findAllByUserId(userId: UUID): List<TaggitRepoEntity>
    fun findAllByUserId(userId: UUID, pageable: Pageable): List<TaggitRepoEntity>

    @Query(
        value = "SELECT * FROM repo r WHERE r.user_id = :userId and :tagJsonbQuery order by r.repo_name asc",
        nativeQuery = true)
    fun findAllByMetadataTags(@Param("userId") userId: UUID, @Param("tagJsonbQuery") tagJsonbQuery: String): List<TaggitRepoEntity>
}
