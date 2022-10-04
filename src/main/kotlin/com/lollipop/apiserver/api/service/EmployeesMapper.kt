package com.lollipop.apiserver.api.service

import com.lollipop.apiserver.db.mySql.entity.Employees
import com.lollipop.apiserver.db.mySql.entity.Gender
import io.r2dbc.spi.Row
import org.springframework.stereotype.Component
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.function.BiFunction

@Component
class EmployeesMapper: BiFunction<Row, Any, Employees> {
    override fun apply(row: Row, u: Any): Employees {
        return Employees(
            row.get("emp_no").toString().toInt(),
            LocalDate.parse(row.get("birth_date").toString()),
            row.get("first_name").toString(),
            row.get("last_name").toString(),
            Gender.valueOf(row.get("gender").toString()),
            LocalDate.parse(row.get("birth_date").toString())
        )
    }
}