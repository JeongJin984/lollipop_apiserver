package com.lollipop.apiserver.api.service.mapper

import com.lollipop.apiserver.db.mySql.entity.ChattingContent
import com.lollipop.apiserver.db.mySql.entity.ChattingRoom
import com.lollipop.apiserver.db.mySql.entity.Employees
import com.lollipop.apiserver.db.mySql.entity.Gender
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.function.BiFunction

@Component
class ChattingContentMapper : BiFunction<Row, Any, ChattingContent> {
    override fun apply(row: Row, u: Any): ChattingContent {
        val result = ChattingContent(
            row.get("id").toString().toLong(),
            row.get("content").toString(),
            LocalDate.parse(row.get("created_at").toString())
        )

        if(row.get("emp_no") != null) {
            result.writer = Employees(
                row.get("emp_no").toString().toInt(),
                LocalDate.parse(row.get("birth_date").toString()),
                row.get("first_name").toString(),
                row.get("last_name").toString(),
                Gender.valueOf(row.get("gender").toString()),
                LocalDate.parse(row.get("created_at").toString())
            )
        }

        if(row.get("room_id")  != null){
            result.chattingRoom = ChattingRoom(
                row.get("room_id").toString().toLong(),
                row.get("room_name").toString(),
                LocalDate.parse(row.get("created_at").toString())
            )
        }
        return result
    }
}