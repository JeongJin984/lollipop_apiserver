package com.lollipop.apiserver.api.config

import com.lollipop.apiserver.api.service.EmployeesService
import com.lollipop.apiserver.db.mySql.entity.Employees
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunctions.nest
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono

@Configuration
class APIConfig(private val employeesService: EmployeesService) {
    fun findById(req: ServerRequest): Mono<ServerResponse> = ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body<Employees>(employeesService.findById(10001))
        .switchIfEmpty(notFound().build())

    @Bean
    fun routerFunction() = nest(path("/hi"),
        router {
            listOf(
                GET("/", ::findById))
        }
    )
}