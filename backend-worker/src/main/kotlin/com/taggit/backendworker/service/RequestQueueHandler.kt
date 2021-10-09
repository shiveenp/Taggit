package com.taggit.backendworker.service

import com.taggit.backendworker.db.RequestQueueEntity
import com.taggit.backendworker.model.RequestQueueType
import org.springframework.stereotype.Service

@Service
class RequestQueueHandler(
    private val repoSyncService: RepoSyncService
) {

    fun handleQueueItem(item: RequestQueueEntity) {
        when(RequestQueueType.valueOf(item.type)) {
            RequestQueueType.GITHUB_REPO_SYNC -> {
                repoSyncService.syncGithubRepos(item.payload)
            }
        }
    }
}
