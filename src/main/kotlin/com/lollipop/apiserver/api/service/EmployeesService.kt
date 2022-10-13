package com.lollipop.apiserver.api.service

import com.lollipop.apiserver.api.service.mapper.EmployeesMapper
import com.lollipop.apiserver.db.mySql.entity.Employees
import com.lollipop.apiserver.db.mySql.repository.EmployeesRepository
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.io.IOException

@Service
class EmployeesService (
    private val employeesRepository: EmployeesRepository,
    private val template: ReactiveStringRedisTemplate,
    private val client: DatabaseClient,
    private val employeesMapper: EmployeesMapper
){
    fun findById(id : Int) : Mono<Employees?> {
        return employeesRepository.findById(id)
    }

    fun findByLastName(lastName : String) : Flux<Employees> {
        return employeesRepository.findEmployeesByLastName(lastName)
    }

    fun findAll(): Flux<Employees?> {
        return employeesRepository.findAll()
            .doOnNext { v ->
                template.opsForList().rightPush("testList", v?.firstName + v?.lastName)
                    .subscribeOn(Schedulers.newSingle("redis"))
                    .subscribe()
            }
    }

    fun coroutineSelectJoin() : Flux<MutableMap<String, Any>?> {
        return client
            .sql("select * from dept_emp " +
                    "inner join employees on dept_emp.emp_no = employees.emp_no " +
                    "inner join departments d on dept_emp.dept_no = d.dept_no " +
                    "where dept_emp.dept_no = :deptNum")
            .bind("deptNum", "d005")
            .fetch()
            .all()
    }

    suspend fun coroutineSelect() : Employees? {
        return client
            .sql("select * from employees where emp_no = :empNo")
            .bind("empNo", 10001)
            .map(employeesMapper::apply)
            .awaitSingleOrNull()
    }

    @Transactional(rollbackFor = [IOException::class])
    suspend fun insert(): Employees? {
        return client
            .sql("insert into employees (emp_no, birth_date, first_name, last_name, gender, hire_date) " +
                    "values (1, '1999-12-12', 'XXX', 'YYY', 'M', '1111-11-11')")
            .map(employeesMapper::apply)
            .awaitSingleOrNull()
    }
}