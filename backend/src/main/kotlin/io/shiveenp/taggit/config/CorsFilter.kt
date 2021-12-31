package io.shiveenp.taggit.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpMethod
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Profile("web", "local", "all")
@Configuration
class CorsFilter {
    @Bean
    fun corsWebFilter(): CorsWebFilter? {
        val corsConfig = CorsConfiguration()
        corsConfig.applyPermitDefaultValues()
        corsConfig.addAllowedMethod(HttpMethod.GET)
        corsConfig.addAllowedMethod(HttpMethod.OPTIONS)
        corsConfig.addAllowedMethod(HttpMethod.PUT)
        corsConfig.addAllowedMethod(HttpMethod.POST)
        corsConfig.addAllowedMethod(HttpMethod.DELETE)
        corsConfig.allowedOrigins = listOf(FRONTEND_LOCAL)
        corsConfig.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)
        return CorsWebFilter(source)
    }

    companion object {
        private const val FRONTEND_LOCAL = "http://localhost:8081"
    }
}