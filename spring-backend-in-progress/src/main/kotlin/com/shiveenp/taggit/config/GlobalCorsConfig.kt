package com.shiveenp.taggit.config

import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import java.util.*


@EnableWebFluxSecurity
class SecurityConfiguration {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .authorizeExchange { exchanges ->
                exchanges.pathMatchers("/signin").authenticated()
                exchanges.anyExchange().permitAll()
            }
            .oauth2Login(withDefaults())
            .oauth2Client(withDefaults())
            .cors()
            .and()
            .csrf()
            .disable()
            .build()
    }

    @Bean
    fun corsConfiguration(): CorsConfigurationSource {
        val corsConfig = CorsConfiguration()
        corsConfig.applyPermitDefaultValues()
        corsConfig.addAllowedMethod(HttpMethod.GET)
        corsConfig.addAllowedMethod(HttpMethod.OPTIONS)
        corsConfig.addAllowedMethod(HttpMethod.PUT)
        corsConfig.addAllowedMethod(HttpMethod.POST)
        corsConfig.addAllowedMethod(HttpMethod.DELETE)
        corsConfig.allowedOrigins = Arrays.asList(FRONTEND_LOCALHOST)
        corsConfig.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)
        return source
    }

    companion object {
        private const val FRONTEND_LOCALHOST = "http://localhost:8081"
    }
}
