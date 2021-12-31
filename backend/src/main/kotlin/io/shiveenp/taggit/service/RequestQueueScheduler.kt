package io.shiveenp.taggit.service

import io.shiveenp.taggit.db.RequestQueueRepository
import mu.KotlinLogging
import org.springframework.context.annotation.Profile
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicBoolean

@Profile("worker", "local", "all")
@Service
class RequestQueueScheduler(
    private val requestQueueRepository: RequestQueueRepository,
    private val requestQueueHandler: RequestQueueHandler
) {
    private val logger = KotlinLogging.logger { }

    // we want to ensure we only process one request at a time for the user.
    // this is to ensure data consistency for long-running ops such as repo sync.
    // ideally this map would only contain one entry for a user at any given time
    private var isProcessingInflight: AtomicBoolean = AtomicBoolean(false)

    @Async
    @Scheduled(fixedRate = 5000)
    fun checkAndScheduleJob() {
        val newItem = requestQueueRepository.topItem()
        if (newItem != null) {
            if (isProcessingInflight.get()) {
                logger.debug { "Ignoring new request while there's an in progress request" }
                // this is akin to visibility timeout. the thinking here was that since we pull the top item based on updateAt
                // field, setting it to now for the skipped request will allow other older requests to surface up. Otherwise,
                // this scheduler will get stuck pulling the same item again and again and starving newer (comparative to the skipped)
                // requests.
                requestQueueRepository.setUpdatedAtToNow(newItem.id)
                return
            }
            try {
                isProcessingInflight = AtomicBoolean(true)
                requestQueueRepository.setProcessingStatusFor(newItem.id)
                requestQueueHandler.handleQueueItem(newItem)
                requestQueueRepository.markAsDoneFor(newItem.id)
                isProcessingInflight = AtomicBoolean(false)
            } catch (ex: Exception) {
                logger.error(ex) { "Unable to handle request queue item: ${newItem.id}" }
                requestQueueRepository.markAsErrorFor(newItem.id)
            }
        }
    }
}
