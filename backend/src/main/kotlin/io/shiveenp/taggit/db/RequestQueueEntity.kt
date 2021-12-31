package io.shiveenp.taggit.db

import java.time.OffsetDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "request_queue")
data class RequestQueueEntity(
    @Id
    val id: UUID,
    val type: String,
    val payload: String?,
    val status: String,
    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    val createdAt: OffsetDateTime,
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    val updatedAt: OffsetDateTime
)
