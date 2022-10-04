package com.lollipop.apiserver.db.mySql.repository

import com.lollipop.apiserver.db.mySql.entity.Employees
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux

interface EmployeesRepository : ReactiveCrudRepository<Employees?, Int?> {
    @Query("select * from employees where last_name=:lastName")
    fun findEmployeesByLastName(lastName: String) : Flux<Employees>
}