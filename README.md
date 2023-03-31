<p align="center">
  <img src="https://github.com/pavlin-dimitrov/Tinder/blob/develop/blob/main/tinder.png" alt="Tinder Logo"/>
</p>

<h1 align="center">Tinder</h1>
<p align="center">A RESTful API for the Tinder web app clone built with Spring Boot</p>

<p align="center">
  <a href="https://github.com/pavlin-dimitrov/Tinder/blob/develop/LICENSE.md">
    <img alt="License: MIT" src="https://img.shields.io/badge/License-MIT-yellow.svg">
  </a>
  <img alt="GitHub last commit" src="https://img.shields.io/github/last-commit/pavlin-dimitrov/Tinder/develop">
  <img alt="GitHub repo size" src="https://img.shields.io/github/repo-size/pavlin-dimitrov/Tinder">
</p>

## 🌟 Project Overview

<p align="center">This project is a Spring Boot backend for a Tinder web app clone.</p> 
<p align="center">It provides a RESTful API for user authentication, profile creation and management, and matches management.</p> 
<p align="center">The API is built using Spring Security, Spring Data JPA, MySQL database and Flyway migrations.</p>
<p align="center">The project also includes unit tests and integration tests.</p>

## ⚙️ Used Technologies

*	Java 11;
*	Spring Boot;
*	Spring Data JPA;
*	Spring Security;
*	Hibernate;
*	H2 in-memory database (for development and testing purposes);
*	MySQL;
*	DBeaver (for production);
*	Lombok;
*	Passay (for password validation);
*	JSON Web Tokens (JWT);
*	io.jsonwebtoken library (for JWT handling);
*	Swagger (for API documentation);
*	Gradle (for building and dependency management);


## 💻 Features

* Register and login
* Account management
* Set random list of friends - BOT type accounts
* Secure user authentication
* Show friend info
* Rate friend
* Calculate distance between user and friend
* Show filtered list of friend sorted by location or rating, ordered by asc/desc, using limit.

## 🚀 Getting Started

### Prerequisites

Make sure you have the following software installed on your local machine:

* JDK 11 or higher
* Gradle
* MySQL Server

### Installation

1. Clone the repository:

   ```sh
   git clone https://github.com/pavlin-dimitrov/Tinder.git
   
   Use the `develop` branch.

2. Navigate to the project directory:

   ```sh
    cd Tinder

3. Install the required dependencies:

   ```sh
    gradle build
    
4. Update the application.properties file with your MySQL server credentials:

   ```sh
    spring.datasource.url=jdbc:mysql://localhost:3306/tinder_db?useSSL=false
    spring.datasource.username=YOUR_USERNAME
    spring.datasource.password=YOUR_PASSWORD

    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    mail.smtp.socketFactory.port=587
    spring.mail.username=[YOUR EMAIL HERE]
    spring.mail.password=[GENERATED APP PASSWORD HERE]
    
5. Start the application:

   ```sh
    gradle bootRun
    
Now, the Tinder application should be up and running on http://localhost:8080.



### <img src="https://github.com/pavlin-dimitrov/Tinder/blob/develop/blob/main/postman.png" alt="Postman" width="24" height="24"> Postman API's

  https://www.postman.com/restless-desert-638182/workspace/tinder/overview

📚 Documentation
For more information, check out our [Wiki](https://github.com/pavlin-dimitrov/Tinder/wiki).

📄 License
This project is licensed under the MIT License - see the [MIT License](https://github.com/pavlin-dimitrov/Tinder/blob/develop/LICENSE.md) file for details.
