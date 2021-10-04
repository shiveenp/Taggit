package com.taggit.backendworker.db

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*
import javax.transaction.Transactional

interface RequestQueueRepository: JpaRepository<RequestQueueEntity, UUID> {
    @Query("SELECT * FROM request_queue WHERE status='PENDING' ORDER BY timestamp ASC LIMIT 1 FOR UPDATE SKIP LOCKED;", nativeQuery = true)
    fun topItem(): RequestQueueEntity?

    @Modifying
    @Transactional
    @Query(" update requestQueue rq set rq.status = 'PROCESSING', rq.lastUpdated = now() where rq.id = ?1 and status='PENDING';", nativeQuery = true)
    fun setProcessingStatusFor(id: UUID): Int
}
