package com.lollipop.apiserver.db.mySql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("departments")
class Departments(
    @Id @Column("emp_no") val deptNo : Int,
    @Column("dept_name") val deptName : String
)