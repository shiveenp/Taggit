package com.taggit.backendworker.service

import com.taggit.backendworker.db.RequestQueueRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RequestQueueScheduler(
    private val requestQueueRepository: RequestQueueRepository
) {

    @Scheduled(fixedRate = 1000)
    fun checkAndScheduleJob() {
        val newItem = requestQueueRepository.topItem()
        if (newItem != null) {
             requestQueueRepository.setProcessingStatusFor(newItem.id)
        }

    }
}
