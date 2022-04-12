package com.longest.period.service;

import com.longest.period.model.Employee;
import com.longest.period.model.dto.TableDTO;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
public class EmployeeService {

    public  List<String> formatStrings = Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd", "dd-MM-yyyy");

    public Long workingTogether(Employee employee1, Employee employee2) {
        LocalDate from = employee1.getDateFrom().isBefore(employee2.getDateFrom()) ? employee2.getDateFrom() : employee1.getDateFrom();
        LocalDate to = employee1.getDateTo().isBefore(employee2.getDateTo()) ? employee1.getDateTo() : employee2.getDateTo();
        if (to.isAfter(from) || to.isEqual(from))
            return Math.abs(ChronoUnit.DAYS.between(from, to));
        return Long.valueOf(0);
    }

    public LocalDate tryParseDate(String dateString) {
        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString).parse(dateString)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            } catch (ParseException e) {
            }
        }

        return null;
    }


    public List<TableDTO> getTable(File file) throws IOException {

        List<TableDTO> tableDTOList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";

        List<Employee> employeeList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String[] employee = line.split(",");
            Employee e = new Employee(Long.valueOf(employee[0]), Long.valueOf(employee[1]), tryParseDate(employee[2]), employee[3].equals("NULL") ? LocalDate.now() : tryParseDate(employee[3]));
            employeeList.add(e);
        }

        Map<Long, List<Employee>> employeeMap = employeeList.stream().collect(groupingBy(Employee::getProjectId));

        for (List<Employee> emp : employeeMap.values()) {

            Long max = Long.valueOf(0);
            Employee employee1 = null;
            Employee employee2 = null;
            for (int i = 0; i < emp.size() - 1; i++) {
                for (int j = i + 1; j < emp.size(); j++) {
                    if (workingTogether(emp.get(i), emp.get(j)) >= max) {
                        max = workingTogether(emp.get(i), emp.get(j));
                        employee1 = emp.get(i);
                        employee2 = emp.get(j);
                    }
                }
            }

            TableDTO tableDTO = new TableDTO();
            tableDTO.setFirstEmployeeID(employee1.getEmployeeId());
            tableDTO.setSecondEmployeeID(employee2.getEmployeeId());
            tableDTO.setProjectID(employee1.getProjectId());
            tableDTO.setDays(max);

            tableDTOList.add(tableDTO);

        }

        return tableDTOList;
    }

}
