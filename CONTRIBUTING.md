# Contributing to RP Universe

Thank you for your interest in contributing to RP Universe! This document provides guidelines and instructions for contributing to the project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
  - [Development Environment Setup](#development-environment-setup)
  - [Building the Project](#building-the-project)
- [How to Contribute](#how-to-contribute)
  - [Reporting Bugs](#reporting-bugs)
  - [Suggesting Enhancements](#suggesting-enhancements)
  - [Pull Requests](#pull-requests)
- [Development Guidelines](#development-guidelines)
  - [Code Style](#code-style)
  - [Documentation](#documentation)
  - [Testing](#testing)
  - [Module System](#module-system)
  - [Configuration System](#configuration-system)
- [Project Structure](#project-structure)
- [Roadmap](#roadmap)
- [License](#license)

## Code of Conduct

Please be respectful and considerate of others when contributing to this project. We aim to foster an inclusive and welcoming community.

## Getting Started

### Development Environment Setup

1. **Prerequisites**:
   - Java Development Kit (JDK) 21 or higher
   - Maven
   - Git
   - An IDE (IntelliJ IDEA recommended)

2. **Clone the repository**:
   ```bash
   git clone https://github.com/Fami6Xx/RP-Universe.git
   cd RP-Universe
   ```

3. **Import the project into your IDE**:
   - For IntelliJ IDEA: File > Open > Select the project directory

### Building the Project

To build the project, run:

```bash
mvn clean package
```

The compiled JAR file will be in the `target` directory.

## How to Contribute

### Reporting Bugs

If you find a bug, please create an issue on our [GitHub repository](https://github.com/Fami6Xx/RP-Universe/issues) with the following information:

- A clear, descriptive title
- Steps to reproduce the bug
- Expected behavior
- Actual behavior
- Server software and version
- RP Universe version
- Any relevant logs or screenshots

### Suggesting Enhancements

We welcome suggestions for enhancements! Please create an issue on our [GitHub repository](https://github.com/Fami6Xx/RP-Universe/issues) with the following information:

- A clear, descriptive title
- A detailed description of the enhancement
- Why this enhancement would be useful
- Any implementation ideas you have

### Pull Requests

1. **Fork the repository**
2. **Create a new branch** for your feature or bugfix
3. **Make your changes**
4. **Test your changes** thoroughly
5. **Submit a pull request** with a clear description of the changes

Please ensure your code follows our [code style guidelines](#code-style) and includes appropriate [documentation](#documentation).

## Development Guidelines

### Code Style

- Use 4 spaces for indentation
- Follow Java naming conventions:
  - `camelCase` for variables and methods
  - `PascalCase` for classes
  - `UPPER_SNAKE_CASE` for constants
- Keep lines under 120 characters
- Add meaningful comments to explain complex logic
- Use descriptive variable and method names

### Documentation

- Add JavaDoc comments to all public classes and methods
- Include parameter descriptions, return values, and exceptions
- Document public APIs thoroughly
- Update relevant documentation files when making changes

Example JavaDoc:

```java
/**
 * Registers a new module with the module manager.
 *
 * @param module The module to register
 * @return true if the module was registered successfully, false otherwise
 * @throws IllegalArgumentException if the module is null or already registered
 */
public boolean registerModule(Module module) {
    // Implementation
}
```

### Testing

- Write unit tests for new functionality
- Ensure existing tests pass before submitting a pull request
- Test your changes on a Minecraft server before submitting

### Module System

When working with the module system:

- Extend `AbstractModule` for new modules
- Implement all required methods (`getName()`, `getDescription()`, etc.)
- Follow the module lifecycle (initialize, enable, disable, shutdown)
- Use the configuration system for module settings
- Register event listeners in the `enable()` method and unregister them in the `disable()` method

See [module-system.md](docs/module-system.md) for detailed information.

### Configuration System

When working with the configuration system:

- Add new configuration options to the appropriate section
- Include comments explaining each option
- Provide default values for all options
- Validate configuration values
- Update the configuration version when making changes

See [config-system.md](docs/config-system.md) for detailed information.

## Project Structure

The project follows a standard Maven structure:

- `src/main/java`: Java source code
- `src/main/resources`: Resources (config.yml, plugin.yml, etc.)
- `docs`: Documentation files
- `target`: Build output (generated)

Key packages:

- `me.fami6xx.rpuniverse`: Main package
- `me.fami6xx.rpuniverse.core`: Core functionality
- `me.fami6xx.rpuniverse.api`: Public API

## Roadmap

See [tasks.md](docs/tasks.md) for a list of planned improvements and features.

## License

RP Universe is licensed under the [MIT License](LICENSE). By contributing to this project, you agree to license your contributions under the same license.