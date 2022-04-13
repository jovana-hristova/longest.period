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
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class EmployeeService {

    private List<String> formatStrings = Arrays.asList("yyyy/MM/dd", "MM/dd/yyyy", "dd/MM/yyyy", "yyyy-MM-dd", "MM-dd-yyyy", "dd-MM-yyyy", "yyyy.MM.dd", "MM.dd.yyyy", "dd.MM.yyyy");

    private Long workingTogether(Employee employee1, Employee employee2) {
        LocalDate from = employee1.getDateFrom().isBefore(employee2.getDateFrom()) ? employee2.getDateFrom() : employee1.getDateFrom();
        LocalDate to = employee1.getDateTo().isBefore(employee2.getDateTo()) ? employee1.getDateTo() : employee2.getDateTo();
        if (to.isAfter(from) || to.isEqual(from))
            return Math.abs(ChronoUnit.DAYS.between(from, to));
        return 0L;
    }

    private LocalDate tryParseDate(String dateString) {
        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString).parse(dateString)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private Employee createEmployee(String line)  {
        String[] employee = line.split(",");
        return new Employee(Long.valueOf(employee[0]), Long.valueOf(employee[1]), tryParseDate(employee[2]), employee[3].equals("NULL") ? LocalDate.now() : tryParseDate(employee[3]));
    }


    public List<TableDTO> createTable(File file) throws IOException {

        List<TableDTO> tableDTOList = new ArrayList<>();

        List<Employee> employeeList = new BufferedReader(new FileReader(file))
                .lines()
                .map(line -> createEmployee(line))
                .collect(Collectors.toList());


        Map<Long, List<Employee>> employeesByProject = employeeList.stream().collect(groupingBy(Employee::getProjectId));

        for (List<Employee> emp : employeesByProject.values()) {

            Long max = 0L;
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

            TableDTO tableDTO = new TableDTO(employee1.getEmployeeId(), employee2.getEmployeeId(), employee1.getProjectId(), max);
            tableDTOList.add(tableDTO);
        }

        return tableDTOList;
    }

}
