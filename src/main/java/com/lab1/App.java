package com.lab1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lab1.model.Employee;
import com.lab1.model.PensionPlan;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class App {

    public static void main(String[] args) throws Exception {
        List<Employee> employees = loadSampleEmployees();
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        // Feature 1: Print all employees in JSON (sorted)
        System.out.println("=== All Employees ===");
        List<Employee> sortedEmployees = employees.stream()
                .sorted(Comparator.comparing(Employee::getYearlySalary).reversed()
                        .thenComparing(Employee::getLastName))
                .collect(Collectors.toList());
        System.out.println(mapper.writeValueAsString(sortedEmployees));

        // Feature 2: Quarterly Upcoming Enrollees
        System.out.println("\n=== Quarterly Upcoming Enrollees ===");
        LocalDate now = LocalDate.now();
        LocalDate startOfNextQuarter = getStartOfNextQuarter(now);
        LocalDate endOfNextQuarter = startOfNextQuarter.plusMonths(2).withDayOfMonth(startOfNextQuarter.plusMonths(2).lengthOfMonth());

        List<Employee> upcomingEnrollees = employees.stream()
                .filter(emp -> emp.getPensionPlan() == null)
                .filter(emp -> {
                    LocalDate employmentDate = emp.getEmploymentDate();
                    LocalDate threeYearMark = employmentDate.plusYears(3);
                    return (threeYearMark.isEqual(startOfNextQuarter) || threeYearMark.isAfter(startOfNextQuarter))
                            && (threeYearMark.isBefore(endOfNextQuarter) || threeYearMark.isEqual(endOfNextQuarter));
                })
                .sorted(Comparator.comparing(Employee::getEmploymentDate).reversed())
                .collect(Collectors.toList());

        System.out.println(mapper.writeValueAsString(upcomingEnrollees));
    }

    private static LocalDate getStartOfNextQuarter(LocalDate current) {
        int currentMonth = current.getMonthValue();
        int startMonth;
        if (currentMonth <= 3) startMonth = 4;
        else if (currentMonth <= 6) startMonth = 7;
        else if (currentMonth <= 9) startMonth = 10;
        else startMonth = 1;
        int year = (startMonth == 1) ? current.getYear() + 1 : current.getYear();
        return LocalDate.of(year, Month.of(startMonth), 1);
    }

    private static List<Employee> loadSampleEmployees() {
        List<Employee> list = new ArrayList<>();

        // Daniel Agar with PensionPlan
        Employee daniel = new Employee(1, "Daniel", "Agar", LocalDate.of(2018, 1, 17), 105945.50);
        daniel.setPensionPlan(new PensionPlan("EX1089", LocalDate.of(2023, 1, 17), 100.00));
        list.add(daniel);

        // Bernard Shaw without PensionPlan
        list.add(new Employee(2, "Benard", "Shaw", LocalDate.of(2018, 10, 3), 197750.00));

        // Carly Agar with PensionPlan
        Employee carly = new Employee(3, "Carly", "Agar", LocalDate.of(2014, 5, 16), 842000.75);
        carly.setPensionPlan(new PensionPlan("SM2307", LocalDate.of(2019, 11, 4), 1555.50));
        list.add(carly);

        // Wesley Schneider without PensionPlan
        list.add(new Employee(4, "Wesley", "Schneider", LocalDate.of(2018, 11, 2), 74500.00));

        return list;
    }
}

