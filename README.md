<p align="center">
  <img src="divPortal/src/assets/img/logo/elter_div_logo.png" alt="eLTER Project Logo" width="300" height="auto"/>
</p>

# eLTER DiV

![](https://img.shields.io/badge/license-EUPL--1.2-green)
![](https://img.shields.io/badge/angular-12.0.5-green)
![](https://img.shields.io/badge/Java-14-green)

# Description
The web app that visualise that provides search of meta data for sites, standard observations and habitats and provides visualisation of data e.g. maps and diagrams of earth observation or timeseries data.


-   [Installation and usage](#installation-and-usage)
-   [Coding style](#coding-style)
-   [File naming nomenclature](#file-naming-nomenclature)
-   [Reproducibility](#reproducibility)
-   [Authors](#authors)
-   [License](#license)
-   [Acknowledgments](#acknowledgments)

## Installation and usage
Ensure you have the following software installed on your machine:
1. Node.js and npm: [Download and install Node.js](https://nodejs.org/en).
2. Angular CLI: Install Angular CLI globally using npm
 ```
npm install -g @angular/cli
 ```  
3. Java Development Kit (JDK): [Download and install JDK](https://www.oracle.com/java/technologies/downloads/#java14).
4. Maven: [Download and install Maven](https://maven.apache.org/install.html).

### Backend (Spring Boot)
1. Build the project:
```
mvn clean install
```

2. Run the application:
```
mvn spring-boot:run
```

### Frontend (Angular)
1. Install dependencies:
```
npm install
```
2. Run the Angular application:
```
ng serve
```

## Coding style
To maintain the quality and readability of our code, we follow certain coding standards. 
Contributors are expected to adhere to these guidelines when writing code for this project:


### Java
#### Formatting
-   Indentation: 4 spaces
-   Line Length: 120 characters
-   Braces: K&R style
#### Naming Conventions
-   Classes/Interfaces: PascalCase
-   Methods/Variables: camelCase
-   Constants: UPPER_CASE

### Angular
#### Formatting
-   Indentation: 2 spaces
-   Line Length: 120 characters
-   Braces: K&R style
#### Naming Conventions
-   Components/Services: PascalCase
-   Variables/Functions: camelCase
-   Constants: UPPER_CASE


## File naming nomenclature

-   Use descriptive names that reflect the content or purpose of the
    file.
-   For Angular files and folders - use hyphens (-) to separate different elements of the file name.
-   For Java files and folders - use PascalCase (UpperCamelCase) for file names.
-   Keep file names concise, avoiding unnecessary abbreviations while
    maintaining sufficient detail.

## Reproducibility

Ensure the reproducibility of your work by:

-   Providing detailed descriptions of methods and protocols in the
    documentation.
-   Including version-controlled source code for all scripts and
    analysis workflows.
-   Specifying versions and sources of external libraries and tools
    used.

## Authors

List of contributors to the project.

| Author | Affiliation | e-mail |
| :---: | :---: | :---: |
| Vladan Minić | [Biosense Institute](https://biosens.rs/en) | [minic\@biosense.rs](mailto:minic@biosense.rs) |
| Mina Bjelica | [Biosense Institute](https://biosens.rs/en) | [minab\@biosense.rs](mailto:minab@biosense.rs) |
| Milica Milovanović | [Biosense Institute](https://biosens.rs/en) | [milicam\@biosense.rs](mailto:milicam@biosense.rs) |


## Licence
-   This project is licensed under the [EUPL    License](https://eupl.eu/) - see the [LICENSE](LICENSE) file for
    details.


## Acknowledgments
Development & maintenance funded through:

<p align="center">
  <img src="divPortal/src/assets/img/logo/eLTER_PLUS_Logo.png" alt="eLTER Project Logo" width="300" height="auto"/>
</p>
