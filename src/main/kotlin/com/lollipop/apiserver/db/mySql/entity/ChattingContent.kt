package com.lollipop.apiserver.db.mySql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("chatting_content")
class ChattingContent (
    @Id @Column("id") val id: Long,
    @Column("content") val content: String,
    @Column("created_at") val createdAt: LocalDate
) {
    var writer: Employees? = null
    var chattingRoom: ChattingRoom? = null
}