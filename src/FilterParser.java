import java.util.function.Predicate;

/**
 * Parses a filter expression string into a Predicate&lt;Employee&gt;.
 * Supported syntax:  field operator value
 *
 * Fields:    name, age, department, salary
 * Operators: =   exact match (strings and numbers)
 *            ~   contains, case-insensitive (strings only)
 *            >   greater than (numbers only)
 *            <   less than (numbers only)
 *            >=  greater than or equal (numbers only)
 *            <=  less than or equal (numbers only)
 *
 * Examples:
 *   age>30
 *   salary>=70000
 *   department=Engineering
 *   name~ali
 */
public class FilterParser {

    /**
     * Parses a single filter expression and returns the corresponding Predicate.
     *
     * @param expression  e.g. "age>30" or "department=Engineering"
     * @return a Predicate&lt;Employee&gt; that evaluates the expression
     * @throws IllegalArgumentException if the expression cannot be parsed
     */
    public static Predicate<Employee> parse(String expression) {

        // Determine operator - check two-char operators first
        String op;
        if      (expression.contains(">=")) op = ">=";
        else if (expression.contains("<=")) op = "<=";
        else if (expression.contains(">"))  op = ">";
        else if (expression.contains("<"))  op = "<";
        else if (expression.contains("~"))  op = "~";
        else if (expression.contains("="))  op = "=";
        else throw new IllegalArgumentException(
                "Unknown operator in filter expression: " + expression);

        String[] parts = expression.split(op, 2);
        if (parts.length != 2)
            throw new IllegalArgumentException(
                "Invalid filter expression: " + expression);

        String field = parts[0].trim().toLowerCase();
        String value = parts[1].trim();

        return switch (field) {
            case "age"        -> numericPredicate(op, parseNumber(value, "age"),
                emp -> (double) emp.getAge());
            case "salary"     -> numericPredicate(op, parseNumber(value, "salary"),
                emp -> emp.getSalary());
            case "name"       -> stringPredicate(op, value,
                emp -> emp.getName());
            case "department" -> stringPredicate(op, value,
                emp -> emp.getDepartment());
            default -> throw new IllegalArgumentException(
                "Unknown field: '" + field +
                    "'. Valid fields: name, age, department, salary");
        };
    }

    // --- helpers ---

    private static double parseNumber(String value, String field) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "'" + value + "' is not a valid number for field '" + field + "'");
        }
    }

    @FunctionalInterface
    private interface EmployeeToDouble {
        double apply(Employee e);
    }

    @FunctionalInterface
    private interface EmployeeToString {
        String apply(Employee e);
    }

    private static Predicate<Employee> numericPredicate(
        String op, double threshold, EmployeeToDouble extractor) {
        return switch (op) {
            case ">"  -> emp -> extractor.apply(emp) >  threshold;
            case "<"  -> emp -> extractor.apply(emp) <  threshold;
            case ">=" -> emp -> extractor.apply(emp) >= threshold;
            case "<=" -> emp -> extractor.apply(emp) <= threshold;
            case "="  -> emp -> extractor.apply(emp) == threshold;
            default   -> throw new IllegalArgumentException(
                "Operator '" + op + "' is not supported for numeric fields");
        };
    }

    private static Predicate<Employee> stringPredicate(
        String op, String value, EmployeeToString extractor) {
        return switch (op) {
            case "=" -> emp -> extractor.apply(emp)
                .equalsIgnoreCase(value);
            case "~" -> emp -> extractor.apply(emp)
                .toLowerCase()
                .contains(value.toLowerCase());
            default  -> throw new IllegalArgumentException(
                "Operator '" + op + "' is not supported for string fields");
        };
    }
}
