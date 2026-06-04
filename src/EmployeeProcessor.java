import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.function.Predicate;

public class EmployeeProcessor {

    // --- Data model ---

    static class Employee {
        private final String name;
        private final int age;
        private final String department;
        private final double salary;

        public Employee(String name, int age, String department, double salary) {
            this.name       = name;
            this.age        = age;
            this.department = department;
            this.salary     = salary;
        }

        public String getName()       { return name; }
        public int    getAge()        { return age; }
        public String getDepartment() { return department; }
        public double getSalary()     { return salary; }
    }

    // --- Main program ---

    public static void main(String[] args) {

        // 1. Build the dataset and store it in a collection
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Alice",  35, "Engineering", 90_000));
        employees.add(new Employee("Bob",    28, "Marketing",   55_000));
        employees.add(new Employee("Carol",  42, "Engineering", 105_000));
        employees.add(new Employee("David",  31, "HR",          62_000));
        employees.add(new Employee("Eve",    24, "Marketing",   48_000));
        employees.add(new Employee("Frank",  38, "Finance",     78_000));

        // 2. Function interface: Employee -> "Name | Department"
        Function<Employee, String> nameAndDept =
            emp -> emp.getName() + ", " + emp.getDepartment();

        // 3. Generate a new collection of concatenated strings using streams
        List<String> nameDeptList = employees.stream()
            .map(nameAndDept)
            .toList();

        System.out.println("=== Name & Department (all employees) ===");
        nameDeptList.forEach(System.out::println);

        // 4. Average salary of ALL employees
        OptionalDouble avgSalary = employees.stream()
            .mapToDouble(Employee::getSalary)
            .average();

        System.out.printf("%n=== Average Salary (all employees) ===%n");
        avgSalary.ifPresent(avg -> System.out.printf("$%.2f%n", avg));

        // 5. Filter: only employees older than 30
        int ageThreshold = 30;
        Predicate<Employee> olderThan30 = emp -> emp.getAge() > ageThreshold;

        List<String> filteredNameDept = employees.stream()
            .filter(olderThan30)
            .map(nameAndDept)
            .toList();

        OptionalDouble filteredAvgSalary = employees.stream()
            .filter(olderThan30)
            .mapToDouble(Employee::getSalary)
            .average();

        System.out.printf("%n=== Name & Department (age > %d) ===%n", ageThreshold);
        filteredNameDept.forEach(System.out::println);

        System.out.printf("%n=== Average Salary (age > %d) ===%n", ageThreshold);
        filteredAvgSalary.ifPresent(avg -> System.out.printf("$%.2f%n", avg));
    }
}
