package com.shiveenp.taggit.db

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository
import java.util.*
import javax.persistence.EntityManager

@Repository
interface TaggitUserRepository : CrudRepository<TaggitUserEntity, UUID> {
    fun findByGithubUserId(githubUserId: Long): TaggitUserEntity?
}

@Repository
interface TaggitRepoRepository : PagingAndSortingRepository<TaggitRepoEntity, UUID> {


    fun findAllByUserId(userId: UUID): List<TaggitRepoEntity>
    fun findAllByUserId(userId: UUID, pageable: Pageable): Page<TaggitRepoEntity>
}
