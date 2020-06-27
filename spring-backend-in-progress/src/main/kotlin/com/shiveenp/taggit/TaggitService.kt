package com.shiveenp.taggit

import org.springframework.stereotype.Service

@Service
class TaggitService(private val githubService: GithubService,
                    private val taggitUserRepository: TaggitUserRepository) {

    suspend fun loginOrRegister(): TaggitUser {
        val githubUser = githubService.getUserData()
        val existingUser = taggitUserRepository.findByGithubUserId(githubUser.id)
        return if (existingUser != null) {
            taggitUserRepository.save(existingUser
                .toDto()
                .updateUsing(githubUser)
                .toEntity())
                .toDto()
        } else {
            taggitUserRepository.save(TaggitUserEntity.from(githubUser)).toDto()
        }
    }
}
