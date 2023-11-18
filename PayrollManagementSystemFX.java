import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.Optional;

public class PayrollManagementSystemFX extends Application {

    private PayrollSystem payrollSystem = new PayrollSystem();

    private TableView<Employee> employeeTable = new TableView<>();
    private ObservableList<Employee> employeeData = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Payroll Management System");

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);

        Button addEmployeeButton = createStyledButton("Add Employee");
        addEmployeeButton.setOnAction(e -> addEmployee());

        Button findEmployeeButton = createStyledButton("Find Employee");
        findEmployeeButton.setOnAction(e -> findEmployee());

        Button updateEmployeeButton = createStyledButton("Update Employee");
        updateEmployeeButton.setOnAction(e -> updateEmployee());

        Button removeEmployeeButton = createStyledButton("Remove Employee");
        removeEmployeeButton.setOnAction(e -> removeEmployee());

        Button displayEmployeesButton = createStyledButton("Display All Employees");
        displayEmployeesButton.setOnAction(e -> displayEmployeesInNewWindow());

        Button exitButton = createStyledButton("Exit");
        exitButton.setOnAction(e -> primaryStage.close());

        root.getChildren().addAll(addEmployeeButton, findEmployeeButton, updateEmployeeButton, removeEmployeeButton,
                displayEmployeesButton, exitButton);

        BackgroundFill backgroundFill = new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);
        root.setBackground(background);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("big-button");
        return button;
    }

    private void displayEmployeesInNewWindow() {
        Stage secondaryStage = new Stage();
        VBox secondaryRoot = new VBox(15);
        secondaryRoot.setAlignment(Pos.CENTER);

        initializeTableColumns();
        employeeTable.setItems(employeeData);
        secondaryRoot.getChildren().add(employeeTable);

        Scene secondaryScene = new Scene(secondaryRoot, 600, 400);
        secondaryStage.setScene(secondaryScene);
        secondaryStage.setTitle("Employee List");
        secondaryStage.show();

        updateEmployeeTable();
    }

    private void initializeTableColumns() {
        // Clear existing columns
        employeeTable.getColumns().clear();

        TableColumn<Employee, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Employee, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("Salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        employeeTable.getColumns().addAll(Collections.singletonList(idColumn));
        employeeTable.getColumns().addAll(Collections.singletonList(nameColumn));
        employeeTable.getColumns().addAll(Collections.singletonList(salaryColumn));

    }

    private void updateEmployeeTable() {
        employeeData.clear();
        employeeData.addAll(payrollSystem.getAllEmployees());
    }

    private void addEmployee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Employee");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Employee ID, Name, and Salary (comma-separated):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(data -> {
            String[] employeeData = data.split(",");
            if (employeeData.length == 3) {
                try {
                    int id = Integer.parseInt(employeeData[0]);
                    String name = employeeData[1].trim();
                    double salary = Double.parseDouble(employeeData[2]);
                    Employee newEmployee = new Employee(id, name, salary);
                    payrollSystem.addEmployee(newEmployee);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Employee added successfully!");
                    alert.showAndWait();
                    updateEmployeeTable();
                } catch (NumberFormatException e) {
                    showErrorAlert("Invalid input. Please enter valid numeric values.");
                }
            } else {
                showErrorAlert("Invalid input format. Please enter ID, Name, and Salary separated by commas.");
            }
        });
    }

    private void findEmployee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Find Employee");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Employee ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(employeeId -> {
            try {
                int searchId = Integer.parseInt(employeeId);
                Employee foundEmployee = payrollSystem.findEmployee(searchId);
                if (foundEmployee != null) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Employee Found");
                    alert.setHeaderText(null);
                    alert.setContentText("Employee found: " + foundEmployee);
                    alert.showAndWait();
                } else {
                    showErrorAlert("Employee not found!");
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid input. Please enter a valid numeric value for Employee ID.");
            }
        });
    }

    private void updateEmployee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Update Employee");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Employee ID to update:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(employeeId -> {
            try {
                int updateId = Integer.parseInt(employeeId);
                Employee existingEmployee = payrollSystem.findEmployee(updateId);
                if (existingEmployee != null) {
                    TextInputDialog updateDialog = new TextInputDialog(existingEmployee.toString());
                    updateDialog.setTitle("Update Employee Details");
                    updateDialog.setHeaderText(null);
                    updateDialog.setContentText("Enter updated Employee ID, Name, and Salary (comma-separated):");

                    Optional<String> updateResult = updateDialog.showAndWait();
                    updateResult.ifPresent(data -> {
                        String[] employeeData = data.split(",");
                        if (employeeData.length == 3) {
                            try {
                                int id = Integer.parseInt(employeeData[0]);
                                String name = employeeData[1].trim();
                                double salary = Double.parseDouble(employeeData[2]);
                                Employee updatedEmployee = new Employee(id, name, salary);
                                payrollSystem.updateEmployee(updateId, updatedEmployee);
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Success");
                                alert.setHeaderText(null);
                                alert.setContentText("Employee updated successfully!");
                                alert.showAndWait();
                                updateEmployeeTable();
                            } catch (NumberFormatException e) {
                                showErrorAlert("Invalid input. Please enter valid numeric values.");
                            }
                        } else {
                            showErrorAlert(
                                    "Invalid input format. Please enter ID, Name, and Salary separated by commas.");
                        }
                    });
                } else {
                    showErrorAlert("Employee not found!");
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid input. Please enter a valid numeric value for Employee ID.");
            }
        });
    }

    private void removeEmployee() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Remove Employee");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter Employee ID to remove:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(employeeId -> {
            try {
                int removeId = Integer.parseInt(employeeId);
                boolean removed = payrollSystem.removeEmployee(removeId);
                if (removed) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText(null);
                    alert.setContentText("Employee removed successfully!");
                    alert.showAndWait();
                    updateEmployeeTable();
                } else {
                    showErrorAlert("Employee not found!");
                }
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid input. Please enter a valid numeric value for Employee ID.");
            }
        });
    }

    private void showErrorAlert(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }
}
