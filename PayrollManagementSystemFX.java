import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Arrays;

import java.io.*;

public class PayrollManagementSystemFX extends Application {
    TextField nameField;
    TextField departmentField;
    TextField salaryField;
    TextField phoneField;
    TextField emailField;

    private TreeSet<Employee> employeeList = new TreeSet<>(
            Comparator.comparingInt(emp -> Integer.parseInt(emp.getId())));
    private TableView<Employee> tableView = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Payroll Management System");

        // Initialize TableView columns
        TableColumn<Employee, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Employee, String> departmentColumn = new TableColumn<>("Department");
        departmentColumn.setCellValueFactory(cellData -> cellData.getValue().departmentProperty());

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(cellData -> cellData.getValue().salaryProperty().asObject());

        TableColumn<Employee, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

        TableColumn<Employee, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        tableView.getColumns().addAll(idColumn, nameColumn, departmentColumn, salaryColumn, phoneColumn, emailColumn);

        tableView.getStyleClass().add("table-view");

        // Create UI elements
        TextField idField = new TextField();
        idField.setPromptText("ID");
        nameField = new TextField();
        nameField.setPromptText("Name");
        departmentField = new TextField();
        departmentField.setPromptText("Department");
        salaryField = new TextField();
        salaryField.setPromptText("Salary");
        phoneField = new TextField();
        phoneField.setPromptText("Phone");
        emailField = new TextField();
        emailField.setPromptText("Email");

        Button addButton = new Button("Add");
        addButton.getStyleClass().add("button");
        addButton.setOnAction(e -> addEmployee(idField.getText(), nameField.getText(), departmentField.getText(),
                salaryField.getText(), phoneField.getText(), emailField.getText()));

        Button findButton = new Button("Find");
        findButton.getStyleClass().add("button");
        findButton.setOnAction(e -> findEmployee(idField.getText()));

        Button updateButton = new Button("Update");
        updateButton.getStyleClass().add("button");
        updateButton.setOnAction(e -> updateEmployee(idField.getText(), nameField.getText(), departmentField.getText(),
                salaryField.getText(), phoneField.getText(), emailField.getText()));

        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().add("button");
        removeButton.setOnAction(e -> removeEmployee(idField.getText()));

        // Layout
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        gridPane.add(new Label("ID:"), 0, 0);
        gridPane.add(idField, 1, 0);
        gridPane.add(new Label("Name:"), 0, 1);
        gridPane.add(nameField, 1, 1);
        gridPane.add(new Label("Department:"), 0, 2);
        gridPane.add(departmentField, 1, 2);
        gridPane.add(new Label("Salary:"), 0, 3);
        gridPane.add(salaryField, 1, 3);
        gridPane.add(new Label("Phone:"), 0, 4);
        gridPane.add(phoneField, 1, 4);
        gridPane.add(new Label("Email:"), 0, 5);
        gridPane.add(emailField, 1, 5);

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.BASELINE_CENTER);
        buttonBox.getChildren().addAll(addButton, updateButton, findButton, removeButton);

        HBox mainBox = new HBox(20);
        mainBox.getChildren().addAll(tableView, gridPane, buttonBox);

        Scene scene = new Scene(mainBox, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        primaryStage.setScene(scene);

        // Load data from CSV file
        loadDataFromCSV();
        tableView.setItems(FXCollections.observableArrayList(employeeList));
        primaryStage.show();
    }

    private void addEmployee(String id, String name, String department, String salary, String phone, String email) {
        try {
            double parsedSalary = Double.parseDouble(salary);
            Employee employee = new Employee(id, name, department, parsedSalary, phone, email);
            employeeList.add(employee);
            clearFields();
            saveDataToCSV();
            tableView.setItems(FXCollections.observableArrayList(employeeList));
        } catch (NumberFormatException e) {
            showAlert("Invalid Salary", "Please enter a valid numeric value for Salary.");
        }
    }

    private void updateEmployee(String id, String name, String department, String salary, String phone, String email) {
        Employee dummyEmployee = new Employee(id, "", "", 0, "", "");
        Employee foundEmployee = employeeList.floor(dummyEmployee);

        if (foundEmployee != null && foundEmployee.getId().equals(id)) {
            tableView.getSelectionModel().select(foundEmployee);
            employeeList.remove(dummyEmployee);
            double parsedSalary = Double.parseDouble(salary);
            Employee employee = new Employee(id, name, department, parsedSalary, phone, email);
            employeeList.add(employee);
            tableView.setItems(FXCollections.observableArrayList(employeeList));
        } else {
            showAlert("Employee Not Found", "No employee found with the specified ID.");
            clearFields();
        }
    }

    private void findEmployee(String id) {
        Employee dummyEmployee = new Employee(id, "", "", 0, "", "");
        Employee foundEmployee = employeeList.floor(dummyEmployee);

        if (foundEmployee != null && foundEmployee.getId().equals(id)) {
            tableView.getSelectionModel().select(foundEmployee);
            int index = tableView.getItems().indexOf(foundEmployee);
            tableView.scrollTo(index);
            showEmployeeDetails(foundEmployee);
        } else {
            showAlert("Employee Not Found", "No employee found with the specified ID.");
            clearFields();
        }
    }

    private void removeEmployee(String id) {
        Employee dummyEmployee = new Employee(id, "", "", 0, "", "");
        employeeList.remove(dummyEmployee);
        clearFields();
        saveDataToCSV();
        tableView.setItems(FXCollections.observableArrayList(employeeList));
    }

    private void clearFields() {
        nameField.clear();
        departmentField.clear();
        salaryField.clear();
        phoneField.clear();
        emailField.clear();
    }

    private void showEmployeeDetails(Employee employee) {
        nameField.setText(employee.getName());
        departmentField.setText(employee.getDepartment());
        salaryField.setText(String.valueOf(employee.getSalary()));
        phoneField.setText(employee.getPhone());
        emailField.setText(employee.getEmail());
    }

    private void saveDataToCSV() {
        try (PrintWriter writer = new PrintWriter(new File("employee_data.csv"))) {
            for (Employee employee : employeeList) {
                writer.println(employee.toCSV());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadDataFromCSV() {
        try (BufferedReader reader = new BufferedReader(new FileReader("employee_data.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    Employee employee = new Employee(data[0], data[1], data[2], Double.parseDouble(data[3]),
                            data[4], data[5]);
                    employeeList.add(employee);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Employee class
    public static class Employee {
        private final String id;
        private String name;
        private String department;
        private double salary;
        private String phone;
        private String email;

        public Employee(String id, String name, String department, double salary, String phone, String email) {
            this.id = id;
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.phone = phone;
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public StringProperty idProperty() {
            return new SimpleStringProperty(id);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public StringProperty nameProperty() {
            return new SimpleStringProperty(name);
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public StringProperty departmentProperty() {
            return new SimpleStringProperty(department);
        }

        public double getSalary() {
            return salary;
        }

        public void setSalary(double salary) {
            this.salary = salary;
        }

        public DoubleProperty salaryProperty() {
            return new SimpleDoubleProperty(salary);
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public StringProperty phoneProperty() {
            return new SimpleStringProperty(phone);
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public StringProperty emailProperty() {
            return new SimpleStringProperty(email);
        }

        public String toCSV() {
            return String.join(",", Arrays.asList(id, name, department, String.valueOf(salary), phone, email));
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
