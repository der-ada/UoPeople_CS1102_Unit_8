# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

## [1.0.0] - 2026-06-03

### Added
- `Employee` data class with name, age, department, and salary fields
- `Function<Employee, String>` to produce "Name | Department" strings
- Stream pipeline to map all employees to concatenated strings
- Average salary calculation using `mapToDouble().average()`
- Age filter using `Predicate<Employee>` with configurable threshold
