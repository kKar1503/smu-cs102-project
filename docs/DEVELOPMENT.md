# Development Guide

This document will guide you through setting up the development environment,
contributing to the project, and following the best practices to ensure a smooth development
process.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Setting up the Environment](#setting-up-the-environment)
- [Building the Project](#building-the-project)
- [Running the Project](#running-the-project)
- [Running Tests](#running-tests)
- [Contributing](#contributing)
- [Code Style](#code-style)
- [Directory Structure](#directory-structure)
- [Additional Resources](#additional-resources)

---

## Prerequisites

Before you begin, ensure you have the following tools installed:

1. **Java 21+** - This project uses Java 21 or higher. You can download the JDK
   from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or
   use [AdoptOpenJDK](https://adoptopenjdk.net/). You can also consider using
   [SDKMan](https://sdkman.io/) to help manage you JDK version and also other tools.
2. **Maven** - To build the project. If you don't have Maven, install it
   from [here](https://maven.apache.org/install.html).
3. **Git** - To clone the repository and manage version control.
4. **IDE** - I recommend using IntelliJ IDEA (_cause it's just that good buddy..._),
   but any IDE with Java support should work.

---

## Setting up the Environment

1. **Clone the repository:**

   ```bash
   git clone https://github.com/kKar1503/smu-cs102-project.git # With HTTPS

   git clone git@github.com:kKar1503/smu-cs102-project.git # With SSH
   ```

2. **Navigate to the project directory:**

   ```bash
   cd smu-cs102-project
   ```

3. **Import the project into your IDE:**

   If you're using IntelliJ IDEA, you can open the project directly. For other IDEs, you might need
   to import the Maven project.

    - In IntelliJ, go to `File -> Open`, select the project directory, and IntelliJ will
      automatically recognize the Maven project. (_Look, it's just that good_)

4. **Prepare the configuration file:**

   Copy the `config/config.example.properties` file to `config/config.properties` and
   modify it according to your needs. The example file contains all the necessary
   configurations for the game.

---

## Building the Project

To build the project, run the following command:

```bash
mvn clean package
```

This command will:

- Clean the previous builds
- Compile the source code
- Run the tests
- Package the code into a JAR file (located in the `target/` directory)

---

## Running the Project

### Local Game Mode

To run the project in local game mode, use the following command:

```bash
java -jar target/parade-game-1.0.0.jar # Change the version number accordingly
```

There are also readily available scripts in [scripts](../scripts) directory.

```bash
# Linux & MacOS users
chmod +x ./scripts/run.sh
./scripts/run.sh
```

```cmd
# Windows users
.\scripts\run.bat
```

This will start the game in local game mode. Follow the instructions in the terminal to interact
with the game.

### Network Game Mode

The project temporarily does not support network game mode.

---

## Contributing

For team members, you should be able to contribute directly to the repository without needing
to fork the repository. However, to add the project to your own GitHub, I think it's still
nice to just add a fork to your GitHub.

1. **Fork the repository** and clone it to your local machine. (_Skip if you're added as
   contributor._)
2. **Create a new branch** for your feature or bug fix:

   ```bash
   git checkout -b feature/your-feature # Feature branch
   git checkout -b fix/your-bug-fix     # Bug fix branch
   ```

3. **Make your changes** and commit them with clear, descriptive messages.

   ```bash
   git commit -m "Add a brief description of the changes"
   ```

   Not enforced, but I recommend that you also
   learn [Conventional Commit](https://www.conventionalcommits.org/en/v1.0.0/)
   and utilise it to help structure your commit message.

4. **Push your changes** to your forked repository:

   ```bash
   git push origin feature/your-feature
   ```

5. **Create a pull request** with a detailed description of what you have done.

---

## Code Style

Please follow the coding conventions below to keep the project consistent:

1. **Java Naming Conventions**: Use camelCase for variables and methods, and PascalCase for classes.
2. **Comments**: Write clear and concise comments. Use Javadoc for class and method-level
   documentation.
3. **Indentation**: Use 4 spaces for indentation (no tabs).
4. **Test Coverage**: Write unit tests for new functionality and bug fixes. (_If you're familiar
   with writing tests._)
5. **Avoid Large Pull Requests**: Break large changes into smaller, manageable pull requests.
6. **Styling**: [Google Java Format](https://github.com/google/google-java-format). Both IntelliJ &
   VSCode has
   extensions to integrate Google Java Format.
    * [VSCode extension](https://marketplace.visualstudio.com/items?itemName=JoseVSeb.google-java-format-for-vs-code)
    * [IntelliJ plugin](https://plugins.jetbrains.com/plugin/8527-google-java-format)

---

## Directory Structure

Here is an overview of the project directory structure:

```
parade-game/
├── pom.xml                         # Maven configuration file
├── config/                         # Configurations
│   ├── config.example.properties   # Example configuration file
├── src/
│   └── main/
│       ├── java/                   # Source code
│       │   └── parade/
│       │       ├── card/           # All cards related classes
│       │       ├── computer/       # Engines for computer controllers
│       │       ├── core/           # Core logic used by the game
│       │       │   └── result/     # Game results
│       │       ├── exception/      # Exceptions
│       │       ├── logger/         # Logger
│       │       │   └── impl/       # Implementations of Logger
│       │       ├── menu/           # Contains the menu & displays shown to players
│       │       │   ├── base/       # Base abstract menu classes
│       │       │   ├── display/    # Displays (no interactions)
│       │       │   ├── manager/    # Menu managers
│       │       │   ├── option/     # Menu options enums
│       │       │   └── prompt/     # Prompts (user inputs)
│       │       ├── player/         # Player
│       │       │   └── controller/ # Player controllers (interact with underlying Player)
│       │       ├── setting/        # Setting
│       │       ├── utils/          # Util
│       │       └── Game.java       # Game entry file
│       └── resources/              # Configuration files and resources
└── target/                         # Compiled classes and JAR files
```

- `src/main/java`: Contains the main source code for the project.
- `src/main/resources`: Contains non-code resources (e.g., configuration files).
- `target`: Contains the compiled class files and the executable JAR files.

---

## Additional Resources

- **Maven Documentation**: [https://maven.apache.org/guides/](https://maven.apache.org/guides/)
- **JLine JavaDoc**
  : [https://www.javadoc.io/doc/org.jline/jline](https://www.javadoc.io/doc/org.jline/jline)
