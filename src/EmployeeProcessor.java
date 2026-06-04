import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * CLI tool for processing an employee dataset using the Function interface
 * and Java Streams.
 *
 * Usage:
 *   java EmployeeProcessor [-i file] [--filter expression] ...
 *
 * Examples:
 *   java EmployeeProcessor
 *   java EmployeeProcessor -i employees.csv --filter age>30
 *   java EmployeeProcessor --infile employees.csv --filter age>30 --filter salary>=70000
 */
public class EmployeeProcessor {

    private static final String USAGE = """
        Usage:
          java EmployeeProcessor [-i file] [--filter expression] ...

        Options:
          -h, --help            Show this help message
          -?, --help            Same as above
          -i, --infile file     Read employees from a CSV file instead of
                                the built-in dataset
          --filter expr         Filter employees by a field expression (repeatable)

        Filter expressions:
          field operator value

          Fields:     name, age, department, salary
          Operators:  =   exact match (all fields)
                      ~   contains, case-insensitive (name, department)
                      >   greater than (age, salary)
                      <   less than (age, salary)
                      >=  greater than or equal (age, salary)
                      <=  less than or equal (age, salary)

          Multiple --filter flags are combined with AND logic.

        Examples:
          java EmployeeProcessor
          java EmployeeProcessor -i employees.csv
          java EmployeeProcessor -i employees.csv --filter age>30
          java EmployeeProcessor -i employees.csv --filter age>30 --filter salary>=70000
          java EmployeeProcessor --filter department=Engineering
          java EmployeeProcessor --filter name~al
        """;

    // Known flags that take a following value argument
    private static final List<String> FLAGS_WITH_VALUE =
        List.of("--filter", "-i", "--infile");

    // Known flags that stand alone
    private static final List<String> FLAGS_STANDALONE =
        List.of("-h", "--help", "-?");

    public static void main(String[] args) {

        // Show help and exit if requested
        for (String arg : args) {
            if (FLAGS_STANDALONE.contains(arg)) {
                System.out.print(USAGE);
                return;
            }
        }

        // Validate all flags and their arguments
        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-")) continue;

            if (!FLAGS_WITH_VALUE.contains(args[i]) && !FLAGS_STANDALONE.contains(args[i])) {
                System.err.println("Error: Unknown option '" + args[i] + "'");
                System.err.println("Run with -h or --help for usage information.");
                System.exit(1);
            }

            if (FLAGS_WITH_VALUE.contains(args[i])) {
                if (i + 1 >= args.length || args[i + 1].startsWith("-")) {
                    System.err.println("Error: '" + args[i] + "' requires a value.");
                    System.err.println("Run with -h or --help for usage information.");
                    System.exit(1);
                }
            }
        }

        // 1. Load dataset: from CSV if --infile/-i given, otherwise use built-in data
        List<Employee> employees = loadEmployees(args);

        // 2. Parse --filter arguments into Predicates and combine with AND
        Predicate<Employee> combinedFilter = parseFilters(args);

        // 3. Function interface: Employee -> "Name, Department"
        Function<Employee, String> nameAndDept =
            emp -> emp.getName() + ", " + emp.getDepartment();

        // 4. Apply filter, then map to concatenated strings using streams
        List<String> nameDeptList = employees.stream()
            .filter(combinedFilter)
            .map(nameAndDept)
            .toList();

        // 5. Average salary of filtered employees
        OptionalDouble avgSalary = employees.stream()
            .filter(combinedFilter)
            .mapToDouble(Employee::getSalary)
            .average();

        // --- Output ---
        System.out.println("=== Active filters ===");
        printFilters(args);

        System.out.println("\n=== Name, Department ===");
        if (nameDeptList.isEmpty()) {
            System.out.println("(no employees match the given filters)");
        } else {
            nameDeptList.forEach(System.out::println);
        }

        System.out.println("\n=== Average Salary ===");
        avgSalary.ifPresentOrElse(
            avg -> System.out.printf("$%.2f%n", avg),
            ()  -> System.out.println("(no data)"));
    }

    /**
     * Loads employees from a CSV file if -i/--infile is given,
     * otherwise returns the built-in dataset.
     */
    private static List<Employee> loadEmployees(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            if ("-i".equals(args[i]) || "--infile".equals(args[i])) {
                String filePath = args[i + 1];
                try {
                    List<Employee> loaded = CSVReader.read(filePath);
                    System.out.println("Loaded " + loaded.size() +
                        " employees from " + filePath + "\n");
                    return loaded;
                } catch (IOException e) {
                    System.err.println("Error reading file '" + filePath +
                        "': " + e.getMessage());
                    System.exit(1);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing file '" + filePath +
                        "': " + e.getMessage());
                    System.exit(1);
                }
            }
        }

        // Built-in dataset
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee("Alice",  35, "Engineering", 90_000));
        employees.add(new Employee("Bob",    28, "Marketing",   55_000));
        employees.add(new Employee("Carol",  42, "Engineering", 105_000));
        employees.add(new Employee("David",  31, "HR",          62_000));
        employees.add(new Employee("Eve",    24, "Marketing",   48_000));
        employees.add(new Employee("Frank",  38, "Finance",     78_000));
        return employees;
    }

    /**
     * Reads --filter arguments from the CLI and combines them into a single
     * Predicate using Predicate.and() - all filters must match (AND logic).
     */
    private static Predicate<Employee> parseFilters(String[] args) {
        Predicate<Employee> combined = emp -> true; // start: accept all

        for (int i = 0; i < args.length - 1; i++) {
            if ("--filter".equals(args[i])) {
                try {
                    Predicate<Employee> next = FilterParser.parse(args[i + 1]);
                    combined = combined.and(next);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error in filter '" + args[i + 1] +
                        "': " + e.getMessage());
                    System.err.println("Run with -h or --help for usage information.");
                    System.exit(1);
                }
            }
        }
        return combined;
    }

    /** Prints the active filters, or "(none)" if no --filter was given. */
    private static void printFilters(String[] args) {
        boolean any = false;
        for (int i = 0; i < args.length - 1; i++) {
            if ("--filter".equals(args[i])) {
                System.out.println("  " + args[i + 1]);
                any = true;
            }
        }
        if (!any) System.out.println("  (none - showing all employees)");
    }
}
