package io.shiveenp.taggit.db

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*
import javax.transaction.Transactional

interface RequestQueueRepository: JpaRepository<RequestQueueEntity, UUID> {
    @Query("SELECT * FROM request_queue WHERE status='PENDING' ORDER BY updated_at ASC LIMIT 1 FOR UPDATE SKIP LOCKED;", nativeQuery = true)
    fun topItem(): RequestQueueEntity?

    @Modifying
    @Transactional
    @Query("UPDATE request_queue SET updated_at = now() WHERE id = ?1", nativeQuery = true)
    fun setUpdatedAtToNow(id: UUID): Int

    @Modifying
    @Transactional
    @Query(" UPDATE request_queue SET status = 'PROCESSING', updated_at = now() WHERE id = ?1 AND status='PENDING';", nativeQuery = true)
    fun setProcessingStatusFor(id: UUID): Int

    @Modifying
    @Transactional
    @Query(" UPDATE request_queue SET status = 'DONE', updated_at = now() WHERE id = ?1 AND status='PROCESSING';", nativeQuery = true)
    fun markAsDoneFor(id: UUID): Int

    @Modifying
    @Transactional
    @Query(" UPDATE request_queue SET status = 'ERROR', updated_at = now() WHERE id = ?1 AND status='PROCESSING';", nativeQuery = true)
    fun markAsErrorFor(id: UUID): Int
}
