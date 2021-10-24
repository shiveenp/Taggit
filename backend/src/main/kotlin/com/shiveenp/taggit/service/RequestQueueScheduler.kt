package com.shiveenp.taggit.service

import com.shiveenp.taggit.db.RequestQueueRepository
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Profile("worker", "local")
@Service
class RequestQueueScheduler(
    private val requestQueueRepository: RequestQueueRepository,
    private val requestQueueHandler: RequestQueueHandler
) {
    private val logger = KotlinLogging.logger { }

    // we want to ensure we only process one request at a time for the user.
    // this is to ensure data consistency for long-running ops such as repo sync.
    // ideally this map would only contain one entry for a user at any given time
    val inProgressItemsMap = ConcurrentHashMap<UUID, String>()

    @Async
    @Scheduled(fixedRate = 5000)
    fun checkAndScheduleJob() {
        logger.debug { "Checking for new request queue items..." }
        val newItem = requestQueueRepository.topItem()
        if (newItem != null) {
            val hasRequestInflight = inProgressItemsMap[newItem.userId] != null
            if (hasRequestInflight) {
                logger.debug { "Ignoring new request for user: [${newItem.userId}] while there's an in progress request" }
                // this is akin to visibility timeout. the thinking here was that since we pull the top item based on updateAt
                // field, setting it to now for the skipped request will allow other older requests to surface up. Otherwise,
                // this scheduler will get stuck pulling the same item again and again and starving newer (comparative to the skipped)
                // requests.
                requestQueueRepository.setUpdatedAtToNow(newItem.id)
                return
            }
            try {
                inProgressItemsMap[newItem.userId] = newItem.type
                requestQueueRepository.setProcessingStatusFor(newItem.id)
                requestQueueHandler.handleQueueItem(newItem)
                requestQueueRepository.markAsDoneFor(newItem.id)
                inProgressItemsMap.remove(newItem.userId)
            } catch (ex: Exception) {
                logger.error { "Unable to handle request queue item: ${newItem.id}" }
                requestQueueRepository.markAsErrorFor(newItem.id)
            }
        }
    }
}
