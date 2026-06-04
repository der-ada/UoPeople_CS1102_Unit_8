# Programming Assignment Unit 8

**Course:** CS-1102 — Programming 1  
**Unit:** 8  
**Topic:** Function Interface and Streams

## Overview

A plain Java program that demonstrates the use of the `Function` interface
and the Stream API to process a dataset of employees.

## Requirements

- Java 22 (Eclipse Temurin recommended)
- IntelliJ IDEA (or any Java IDE)

## Project Structure

```
programming-assignment-unit8/
├── src/
│   └── EmployeeProcessor.java
├── .editorconfig
├── .gitattributes
├── .gitignore
├── CHANGELOG.md
└── README.md
```

## How to Run

1. Open the project in IntelliJ IDEA.
2. Make sure the Project SDK is set to Java 22.
3. Run `EmployeeProcessor.java` directly (no build tool required).

## Topics Covered

- `java.util.function.Function<T, R>`
- `java.util.function.Predicate<T>`
- Stream operations: `map()`, `filter()`, `mapToDouble()`, `average()`
- `Collectors.toList()`
