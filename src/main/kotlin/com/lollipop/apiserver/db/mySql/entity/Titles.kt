package com.lollipop.apiserver.db.mySql.entity

import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.*

@Table("titles")
class Titles (
    @Column("emp_no") val empNo: Int,
    @Column("title") val title: String,
    @Column("from_date") val fromDate: LocalDate,
    @Column("to_date") val toDate: LocalDate
)