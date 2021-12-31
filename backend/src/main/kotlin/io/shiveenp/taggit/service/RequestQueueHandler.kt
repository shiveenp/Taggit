package io.shiveenp.taggit.service

import io.shiveenp.taggit.db.RequestQueueEntity
import io.shiveenp.taggit.models.RequestQueueType
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("worker", "local", "all")
@Service
class RequestQueueHandler(
    private val repoSyncJobService: RepoSyncJobService
) {
    private val logger = KotlinLogging.logger { }

    fun handleQueueItem(item: RequestQueueEntity) {
        when (RequestQueueType.fromString(item.type)) {
            RequestQueueType.GITHUB_REPO_SYNC -> {
                repoSyncJobService.syncGithubRepos()
            }
            RequestQueueType.UNKNOWN -> logger.error { "Unable to handle request_queue id: [${item.id}] as request queue type was parsed as unknown" }
        }
    }
}
