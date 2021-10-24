package com.shiveenp.taggit.service

import com.shiveenp.taggit.models.GithubRepoSyncPayload
import com.shiveenp.taggit.util.SyncException
import com.shiveenp.taggit.util.fromJson
import org.springframework.stereotype.Service
import java.util.*

@Service
class RepoSyncJobService(private val repoSyncService: RepoSyncService) {
    fun syncGithubRepos(userId: UUID) {
        try {
            repoSyncService.syncUserStargazingData(userId)
        } catch (ex: Exception) {
            throw SyncException("Unable sync github repos")
        }
    }
}
