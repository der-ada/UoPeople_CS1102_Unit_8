import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Generates a CSV file containing randomly created employee records.
 *
 * Usage:
 *   java EmployeeFactory <count> <outputfile>
 *
 * Examples:
 *   java EmployeeFactory 100 employees.csv
 *   java EmployeeFactory 1000000 big_dataset.csv
 *
 * CSV format:
 *   name,age,department,salary
 */
public class EmployeeFactory {

    private static final String USAGE = """
        Usage:
          java EmployeeFactory <count> <outputfile>

        Arguments:
          count       Number of employee records to generate (must be > 0)
          outputfile  Path to the output CSV file

        Examples:
          java EmployeeFactory 100 employees.csv
          java EmployeeFactory 1000000 big_dataset.csv
        """;

    private static final List<String> DEPARTMENTS =
        List.of("Engineering", "Marketing", "HR", "Finance");

    private static final int    MIN_AGE    = 18;
    private static final int    MAX_AGE    = 65;
    private static final double MIN_SALARY = 30_000;
    private static final double MAX_SALARY = 150_000;

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Error: Expected exactly 2 arguments.");
            System.err.print(USAGE);
            System.exit(1);
        }

        int count;
        try {
            count = Integer.parseInt(args[0]);
            if (count <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.err.println("Error: count must be a positive integer, got '" + args[0] + "'");
            System.exit(1);
            return;
        }

        String outputFile = args[1];

        try {
            generate(count, outputFile);
            System.out.printf("Generated %,d records -> %s%n", count, outputFile);
        } catch (IOException e) {
            System.err.println("Error writing file '" + outputFile + "': " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Generates {@code count} random employee records and writes them to a CSV file.
     *
     * @param count      number of records to generate
     * @param outputFile path to the output CSV file
     * @throws IOException if the file cannot be written
     */
    public static void generate(int count, String outputFile) throws IOException {
        Random rng = new Random();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write("name,age,department,salary");
            writer.newLine();

            for (int i = 0; i < count; i++) {
                String name       = "Emp_" + randomHex(rng, 4);
                int    age        = MIN_AGE + rng.nextInt(MAX_AGE - MIN_AGE + 1);
                String department = DEPARTMENTS.get(rng.nextInt(DEPARTMENTS.size()));
                double salary     = MIN_SALARY + rng.nextDouble() * (MAX_SALARY - MIN_SALARY);

                writer.write(String.format("%s,%d,%s,%.2f", name, age, department, salary));
                writer.newLine();
            }
        }
    }

    private static String randomHex(Random rng, int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(Integer.toHexString(rng.nextInt(16)));
        }
        return sb.toString();
    }
}
