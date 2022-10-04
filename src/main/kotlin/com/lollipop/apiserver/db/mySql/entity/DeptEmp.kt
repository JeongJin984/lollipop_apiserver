package com.lollipop.apiserver.db.mySql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.Date

@Table("dept_emp")
class DeptEmp (
    @Column("emp_no") val empNo : Int,
    @Column("dept_no") val deptNo: Int,
    @Column("from_date") val fromDate: LocalDate,
    @Column("to_date") val toDate: LocalDate
)