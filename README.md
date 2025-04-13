# SMU CS102 Project

This Java project is a console-based game, simulating the card game Parade.

The project is done by a group of 6 students from SMU CS102 class A.Y. 2024/25 Semester 2.

## Group Members

- [Yam Kar Lok](https://github.com/kKar1503)
- [Lee Jia You, Greg](https://github.com/gregleejy)
- [Tan Yee Ming](https://github.com/45tera)
- [Riyaz Choudry](https://github.com/riyxz245)
- [Srividya Ravi Sivashankar](https://github.com/sri7373)
- [Koh Guang Wei, Anson](https://github.com/Aelderic)

## Set Up Instructions

Refer to [Setting Up The Environment](docs/DEVELOPMENT.md#setting-up-the-environment) for
instructions on how to set up the project.

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

This will start the game. Follow the instructions in the terminal to interact with the game. You may
also change the properties in your configuration file to update the game behavior, like Logging and
game menu types.

## Features

- [x] Fully functional Parade Game
    - [x] Base game rules
    - [x] Special rules for 2 players

## Dependencies Used

### Maven

This project uses Maven as a build automation tool. Maven is used to manage the project's
dependencies and build process.

Refer to [DEVELOPMENT.md](docs/DEVELOPMENT.md#prerequisites) for instructions on how to set up
Maven.

### Java Development Kit (JDK)

This project is built using Java 21. The JDK is required to compile and run the project.
You can download the JDK from the official Oracle website or use OpenJDK.

Refer to [DEVELOPMENT.md](docs/DEVELOPMENT.md#prerequisites) for instructions on how to set up the
JDK and IDE.

### JLine

This project uses JLine for command line input and output. JLine is a Java library built to simplify
some of the usage of the command line.

JLine has been added as a dependency in the `pom.xml` file. You do not need to download it. When you
have set up Maven, the dependencies will be automatically downloaded and added to the project.

To find out more about JLine, refer to the [JLine's GitHub Page](https://github.com/jline/jline3).

### Gson

This project uses Gson for JSON serialization and deserialization. Gson is a Java library that can
convert Java objects to JSON and vice versa. It is used in this project to convert Java log objects
to JSON format for logging purposes.

Gson has been added as a dependency in the `pom.xml` file. You do not need to download it. When you
have set up Maven, the dependencies will be automatically downloaded and added to the project.

To find out more about Gson, refer to the [Gson's GitHub Page](https://github.com/google/gson).
