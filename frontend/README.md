# User Management Angular App

This repository contains the **User Management** frontend application built with **Angular 21**, backed by a Spring Boot REST API.

## Purpose

This application provides a comprehensive **User Management** interface that allows administrators and users to:

- **View** a list of all registered users
- **Create** new user accounts
- **Update** existing user information
- **Delete** user records

It serves as the frontend layer for the Spring Boot REST API, communicating over HTTP to perform CRUD operations on user data.

## Project Structure

The Angular application lives at the root of the repository.

## Development Server

**Prerequisites:** 
- `npm` version `11.10.0` or higher is required. This is necessary to support the modern security features (`min-release-age` and `ignore-scripts`) configured in the `.npmrc` file.

To run the application locally, use the Angular CLI or npm:

```bash
npm install
npm start
```

Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files. Ensure that your Spring Boot backend API is running concurrently (default is `http://127.0.0.1:8080/SpringBootRestApi/api/user/`).

## Build

Run `npm run build` or `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Further Help

To get more help on the Angular CLI, use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
