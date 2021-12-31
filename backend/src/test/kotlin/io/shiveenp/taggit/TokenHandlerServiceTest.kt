package io.shiveenp.taggit

import com.shiveenp.taggit.config.ExternalProperties
import com.shiveenp.taggit.security.TokenHandlerService
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class TokenHandlerServiceTest {
    private val externalProperties = mockk<ExternalProperties>()

    private val tokenHandlerService = TokenHandlerService(externalProperties)

    private val mockSigningKey = Base64.getEncoder().encodeToString("G+sXE/8FwgtZle5jI/ZGhygponrn5YXmu0bIo0JiGYc=".toByteArray())

    @BeforeEach
    internal fun setUp() {
        every { externalProperties.jwtSigningKey } returns mockSigningKey
    }

    @Test
    fun `should create and decode and jwt properly`() {
        val userId = UUID.randomUUID()
        val jwt = tokenHandlerService.saveUserIdAndGetJwt(userId, "")
        val retrievedUserId = tokenHandlerService.getUserIdFromJwtClaims(jwt)
        retrievedUserId shouldBe userId.toString()
    }
}
