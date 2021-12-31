package io.shiveenp.taggit.service

import io.shiveenp.taggit.util.SyncException
import org.springframework.stereotype.Service

@Service
class RepoSyncJobService(private val repoSyncService: RepoSyncService) {
    fun syncGithubRepos() {
        try {
            repoSyncService.syncUserStargazingData()
        } catch (ex: Exception) {
            throw SyncException("Unable sync github repos", ex)
        }
    }
}
