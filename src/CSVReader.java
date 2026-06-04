import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a CSV file produced by EmployeeFactory and returns a list of Employee objects.
 *
 * Expected CSV format (with header row):
 *   name,age,department,salary
 *   Emp_a3f9,35,Engineering,90000.00
 */
public class CSVReader {

    /**
     * Reads the given CSV file and returns its contents as a list of Employee objects.
     * The first row is treated as a header and skipped.
     *
     * @param filePath path to the CSV file
     * @return list of Employee objects parsed from the file
     * @throws IOException              if the file cannot be read
     * @throws IllegalArgumentException if a row cannot be parsed
     */
    public static List<Employee> read(String filePath) throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine(); // skip header

            if (line == null) {
                throw new IllegalArgumentException(
                    "File '" + filePath + "' is empty.");
            }

            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 4);
                if (parts.length != 4) {
                    throw new IllegalArgumentException(
                        "Invalid row at line " + lineNumber +
                        ": expected 4 columns, got " + parts.length +
                        " -> '" + line + "'");
                }

                try {
                    String name       = parts[0].trim();
                    int    age        = Integer.parseInt(parts[1].trim());
                    String department = parts[2].trim();
                    double salary     = Double.parseDouble(parts[3].trim());
                    employees.add(new Employee(name, age, department, salary));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                        "Invalid number at line " + lineNumber +
                        ": " + e.getMessage() + " -> '" + line + "'");
                }
            }
        }

        return employees;
    }
}
