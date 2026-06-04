/**
 * Represents an employee in the dataset.
 * Intentionally implemented as a static inner class rather than a record,
 * to maintain compatibility with the course's Java version requirements
 * and to demonstrate explicit OOP design.
 */
public class Employee {

    private final String name;
    private final int    age;
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

    @Override
    public String toString() {
        return String.format("%-12s %-15s age=%-3d salary=$%.0f",
            name, department, age, salary);
    }
}
