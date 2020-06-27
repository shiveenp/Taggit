package com.shiveenp.taggit

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
interface TaggitUserRepository : CrudRepository<TaggitUserEntity, UUID> {
    fun findByGithubUserId(githubUserId: Long) : TaggitUserEntity?
}
