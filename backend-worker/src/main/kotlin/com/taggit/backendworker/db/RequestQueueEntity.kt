package com.taggit.backendworker.db

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "request_queue")
data class RequestQueueEntity(
    @Id
    val id: UUID,
    val type: String,
    val payload: String,
    val status: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
