package com.lollipop.apiserver.db.mySql.repository

import com.lollipop.apiserver.db.mySql.entity.Departments
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface DepartmentsRepository : ReactiveCrudRepository<Departments?, Int?> {

}