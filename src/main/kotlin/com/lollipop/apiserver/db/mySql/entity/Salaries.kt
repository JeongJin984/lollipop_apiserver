package com.lollipop.apiserver.db.mySql.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.*

@Table("salaries")
class Salaries (
    @Column("emp_no") val empNo: Long,
    @Column("salary") val salary: Long,
    @Column("from_date") val fromDate: LocalDate,
    @Column("to_date") val toDate: LocalDate
)