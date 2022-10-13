package com.lollipop.apiserver.db.mySql.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("chatting_room_participants")
class ChattingRoomParticipants (
    @Id @Column("id") val id: Long,
    @Column("participant_id") val participants: Employees,
    @Column("room_id") val room: ChattingRoom,
    @Column("created_at") val createdAt: LocalDate
)