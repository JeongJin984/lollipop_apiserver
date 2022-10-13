package com.lollipop.apiserver.db.mySql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("chatting_room")
class ChattingRoom (
    @Id @Column("room_id") val roomId: Long,
    @Column("room_name") val roomName: String,
    @Column("created_at") val createdAt: LocalDate
) {
    val participants: MutableList<Employees> = mutableListOf()
}