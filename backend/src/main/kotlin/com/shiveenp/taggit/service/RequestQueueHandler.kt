package com.shiveenp.taggit.service

import com.shiveenp.taggit.db.RequestQueueEntity
import com.shiveenp.taggit.models.RequestQueueType
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Profile("worker", "local")
@Service
class RequestQueueHandler(
    private val repoSyncJobService: RepoSyncJobService
) {
    private val logger = KotlinLogging.logger { }

    fun handleQueueItem(item: RequestQueueEntity) {
        when (RequestQueueType.fromString(item.type)) {
            RequestQueueType.GITHUB_REPO_SYNC -> {
                repoSyncJobService.syncGithubRepos(item.userId)
            }
            RequestQueueType.UNKNOWN -> logger.error { "Unable to handle request_queue id: [${item.id}] as request queue type was parsed as unknown" }
        }
    }
}
