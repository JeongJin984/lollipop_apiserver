package com.lollipop.apiserver.db.mySql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.util.*

@Table("employees")
class Employees (
    @Id @Column("emp_no") val empNo : Int,
    @Column("birth_date") val birthDate: LocalDate,
    @Column("first_name") val firstName: String,
    @Column("last_name") val lastName: String,
    @Column("gender") val gender: Gender,
    @Column("hire_date") val hireDate: LocalDate,
) {
    val writeChatting: MutableList<ChattingContent> = mutableListOf()
    val enteredRoom: MutableList<ChattingRoomParticipants> = mutableListOf()
}