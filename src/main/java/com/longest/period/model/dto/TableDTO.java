package com.longest.period.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TableDTO {

    Long firstEmployeeID;
    Long secondEmployeeID;
    Long projectID;
    Long days;
}
