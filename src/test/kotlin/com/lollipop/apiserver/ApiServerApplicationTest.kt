package com.lollipop.apiserver

import com.lollipop.apiserver.api.service.EmployeesService
import com.lollipop.apiserver.db.mySql.entity.Employees
import com.lollipop.apiserver.db.mySql.repository.EmployeesRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import reactor.core.publisher.Mono

@SpringBootTest
@Profile("dev")
class ApiServerApplicationTest @Autowired constructor (
    val employeesService: EmployeesService
){

    @Test
    fun test() {
        employeesService.findById(10001)
            .subscribe()
    }


}