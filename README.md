# School Management System

A comprehensive school management system built with Spring Boot to streamline administrative tasks in educational institutions.

## Features

- User management with different roles (Admin, Teacher, Student)
- Student management
- Teacher management with advisor capabilities
- Lesson program management
- Role-based access control
- RESTful API architecture

## Technologies

- Java 11+
- Spring Boot
- Spring Data JPA
- Spring Security
- Maven
- PostgreSQL (or your database)
- Lombok
- RESTful API

## Project Structure

The project follows a layered architecture:

- **Entity Layer**: Domain objects
- **Repository Layer**: Data access
- **Service Layer**: Business logic
- **Controller Layer**: API endpoints
- **DTO Layer**: Data transfer objects (Request/Response)
- **Mapper Layer**: Object mapping

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/ffozdemir/school-management.git
   ```

2. Configure database connection in `application.properties`

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## API Documentation

The API documentation is available at `/swagger-ui.html` when running the application.

### Main Endpoints

- `/auth`: Authentication endpoints
- `/students`: Student management
- `/teachers`: Teacher management
- `/lesson-programs`: Lesson program management

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Contact

Project Link: [https://github.com/ffozdemir/school-management](https://github.com/ffozdemir/school-management)