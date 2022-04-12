package com.longest.period.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    Long employeeId;

    Long projectId;

    LocalDate dateFrom;

    LocalDate dateTo;

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId=" + employeeId +
                ", projectId=" + projectId +
                ", dateFrom=" + dateFrom +
                ", dateTo=" + dateTo +
                '}';
    }

}
