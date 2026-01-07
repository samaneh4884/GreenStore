Project Description:
Intelligent online store management system with an environmental sustainability approach

Introduction:
With the rapid expansion of e-commerce, online stores have become one of the most important tools for buying and selling goods. However, most current stores focus only on the buying and selling process and the specific needs of customers, without paying enough attention to environmental sustainability aspects.

The present project aims to design and implement an intelligent and sustainable online store. In addition to common online store features (such as user registration, shopping cart, online payment, product and order management), it includes special capabilities that provide a personalized shopping experience compatible with environmental sustainability for users.

Project Goals:

1. Personalizing the User Purchase Experience:
The system allows users to select their mood or situation at the time of purchase (for example: happy, unhappy, etc.). Based on this selection, the store displays a set of products that match the user’s condition.

2. Improving Customer Interaction through Smart Feedback:
After each purchase, a short and targeted question is asked from the user to collect feedback about their experience with the product. This data is used to improve services and future recommendations.

3. Encouraging Green Purchases and Eco-Friendly Behaviors:
The system considers users who purchase products compatible with the environment or that have recyclable packaging. Special points or rewards are given for such purchases. These points can be used in future purchases as discounts or other benefits.

4. Pre-Selling Innovative Products:
The store provides the possibility of pre-selling products that have not yet been produced. If the pre-order reaches a certain level, production of the product begins. This feature allows producers to ensure there is real market demand before mass production.

Problems and Needs That This Software Solves:

Lack of Personalization in Existing Stores:
Many online stores offer the same suggestions to all users, without considering the specific interests or situation of each user. This system provides products based on the user’s selected preferences, offering a unique and purposeful experience.

Reduced Interaction and Customer Loyalty:
The lack of effective two-way communication with customers leads to reduced satisfaction and loyalty. By adding short surveys after purchase, the system collects valuable data for analyzing customer behavior.

Neglect of Environmental Sustainability in Online Shopping:
This store, with its rating system for eco-friendly purchases, promotes a green consumption culture and strengthens social responsibility and the brand.

High Risk of Production Without Demand Assessment:
The pre-sale capability helps businesses ensure market acceptance and prevent unnecessary production.

🛠️ Technologies Used

The GreenStore project is developed using the following technologies:

Java
The main programming language used for the server-side and business logic (Backend).

HTML / CSS / JavaScript
Used for building the user interface (Frontend) and creating interactive web pages.

Gradle
A build automation and dependency management tool used for compiling and running the project.

Git & GitHub
Used for version control, source code management, and team collaboration.

Related Frameworks & Technologies

Spring Boot

RESTful API

MVC (Model–View–Controller) Architecture

🧠 System Architecture

The system is designed based on a Multi-Tier Web Application Architecture, which consists of the following layers:

1. Presentation Layer (Frontend)

Web pages developed using HTML, CSS, and JavaScript

Responsible for user interaction and displaying data to users

2. Business Logic Layer (Backend)

Implemented using Java and Spring Boot

Handles user requests and core system functionalities, including:

User management

Product management

Shopping cart handling

Product recommendations

Smart and eco-friendly features of the system

3. Data Layer (Database)

Stores all system data, including:

Users

Products

Orders

Green (eco-friendly) reward points

This architecture improves separation of concerns, scalability, maintainability, and testability of the project.

▶️ How to Run the Project

To run the GreenStore project locally, follow the steps below:

1. Clone the Repository
git clone https://github.com/samaneh4884/GreenStore.git


Then navigate to the project directory:

cd GreenStore

2. Build and Run the Project Using Gradle

(If Gradle Wrapper is available)

On Linux or macOS:

./gradlew build
./gradlew run


On Windows:

gradlew.bat build
gradlew.bat run


⚠️ This step can also be handled automatically by IntelliJ IDEA.

3. Database Configuration (PostgreSQL)

Install PostgreSQL on your system.

Open the following file:

GreenStore/src/main/resources/application.properties


Configure the database credentials:

spring.datasource.username

spring.datasource.password

spring.datasource.url

After completing this step, the application will be connected to the database successfully.

4. Start the Server

Run the main application class:

GreenStore/src/main/java/com/example/GreenStore/GreenStoreApplication.java


If all previous steps are configured correctly, the server will start successfully.
In case of any issues, you can check the Spring Boot logs to identify and resolve errors.

5. Access the User Interface

After the server is running, open the following file in a web browser:

GreenStore/greenstore-client/index.html


This file serves as the starting point (entry point) of the application, allowing you to use the main features of GreenStore.
