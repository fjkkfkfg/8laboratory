//package ru.itmo.client.controller;
//
//import javafx.beans.property.SimpleStringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.layout.VBox;
//import javafx.stage.FileChooser;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import javafx.util.Duration;
//import org.controlsfx.control.Notifications;
//import ru.itmo.client.MainApp;
//import ru.itmo.client.utility.runtime.Runner;
//import ru.itmo.general.models.Route;
//import ru.itmo.general.network.Response;
//
//import java.io.File;
//import java.util.Locale;
//import java.util.Objects;
//import java.util.ResourceBundle;
//
//public class MainController {
//
//    private MainApp mainApp;
//    private Runner runner;
//    private ResourceBundle bundle;
//
//    @FXML
//    private TableView<Route> dataTable;
//    @FXML
//    private TableColumn<Route, Integer> idColumn;
//    @FXML
//    private TableColumn<Route, String> nameColumn;
//    @FXML
//    private TableColumn<Route, String> coordinatesColumn;
//    @FXML
//    private TableColumn<Route, String> creationDateColumn;
//    @FXML
//    private TableColumn<Route, String> fromColumn;
//    @FXML
//    private TableColumn<Route, String> toColumn;
//    @FXML
//    private TableColumn<Route, Float> distanceColumn;
//    @FXML
//    private TableColumn<Route, Integer> userIdColumn;
//    @FXML
//    private CheckBox filterCheckBox;
//    @FXML
//    private Button addButton;
//    @FXML
//    private Button updateButton;
//    @FXML
//    private Button deleteButton;
//    @FXML
//    private Button clearButton;
//    @FXML
//    private Button helpButton;
//    @FXML
//    public Button addIfMinButton;
//    @FXML
//    public Button sumOfPriceButton;
//    @FXML
//    public Button executeScriptButton;
//    // Labels for route details
//    @FXML
//    private Label nameLabel;
//    @FXML
//    private Label coordinatesLabel;
//    @FXML
//    private Label creationDateLabel;
//    @FXML
//    private Label fromLabel;
//    @FXML
//    private Label toLabel;
//    @FXML
//    private Label distanceLabel;
//
//    private ObservableList<Route> routeData = FXCollections.observableArrayList();
//
//    public void setMainApp(MainApp mainApp) {
//        this.mainApp = mainApp;
//    }
//
//    public void setRunner(Runner runner) {
//        this.runner = runner;
//    }
//
//    public void setBundle(ResourceBundle bundle) {
//        this.bundle = bundle;
//    }
//
//    @FXML
//    private void initialize() {
//        addButton.setOnAction(event -> handleAdd());
//        updateButton.setOnAction(event -> handleUpdate());
//        deleteButton.setOnAction(event -> handleDelete());
//        clearButton.setOnAction(event -> handleClear());
//
//        filterCheckBox.setOnAction(event -> {
//            if (filterCheckBox.isSelected()) {
//                fetchUserRoutes();
//            } else {
//                fetchRoutes();
//            }
//        });
//
//        // Initialize the table columns
//        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
//        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
//        coordinatesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCoordinates().toString()));
//        creationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreationDate().toString()));
//        fromColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFrom().toString()));
//        toColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTo() != null ? cellData.getValue().getTo().toString() : ""));
//        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
//        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
//
//        // Set the observable list data to the table
//        dataTable.setItems(routeData);
//
//        // Listen for selection changes and show the route details when changed
//        dataTable.getSelectionModel().selectedItemProperty().addListener(
//                (observable, oldValue, newValue) -> showRouteDetails(newValue));
//    }
//
//    private void handleFilter() {
//        if (filterCheckBox.isSelected()) {
//            ObservableList<Route> filteredData = FXCollections.observableArrayList();
//            for (Route route : runner.fetchRoutes()) {
//                if (route.getUserId() == runner.getCurrentUserId()) {
//                    filteredData.add(route);
//                }
//            }
//            dataTable.setItems(filteredData);
//        } else {
//            fetchRoutes();
//        }
//        dataTable.refresh();
//    }
//
//    @FXML
//    private void handleAdd() {
//        Route newRoute = new Route();
//
//        boolean okClicked = mainApp.showRouteEditDialog(newRoute);
//
//        if (okClicked) {
//            newRoute.setUserId(runner.getCurrentUserId());
//            int newId = runner.addRoute(newRoute); // Получите новый ID
//
//            if (newId > 0) {
//                newRoute.setId(newId); // Установите новый ID в объекте Route
//                routeData.add(newRoute);
//                dataTable.getItems().add(newRoute);
//                dataTable.refresh();
//                dataTable.sort();
//
//                Notifications.create()
//                        .title("Route Added")
//                        .text("The route was successfully added." + '\n'
//                                + "Assigned id: " + newRoute.getId())
//                        .hideAfter(Duration.seconds(3))
//                        .position(Pos.BOTTOM_RIGHT)
//                        .showInformation();
//            }
//        }
//    }
//
//
//
//    @FXML
//    private void handleUpdate() {
//        Route selectedRoute = dataTable.getSelectionModel().getSelectedItem();
//        if (selectedRoute != null) {
//            boolean okClicked = mainApp.showRouteEditDialog(selectedRoute);
//            if (okClicked) {
//                runner.updateRoute(selectedRoute);
//                showRouteDetails(selectedRoute);
//                dataTable.refresh();
//            }
//        } else {
//            showAlert(
//                    bundle.getString("update.error.title"),
//                    bundle.getString("update.error.header"),
//                    bundle.getString("update.error.content")
//            );
//        }
//    }
//
//    @FXML
//    private void handleDelete() {
//        int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
//        if (selectedIndex >= 0) {
//            Route selectedRoute = dataTable.getItems().get(selectedIndex);
//            runner.deleteRoute(selectedRoute);
//            dataTable.getItems().remove(selectedIndex);
//            dataTable.refresh();
//        } else {
//            showAlert(
//                    bundle.getString("delete.error.title"),
//                    bundle.getString("delete.error.header"),
//                    bundle.getString("delete.error.content")
//            );
//        }
//    }
//
//    @FXML
//    private void handleClear() {
//        boolean confirmed = MainApp.showConfirmationDialog(
//                bundle.getString("clear.confirm.title"),
//                bundle.getString("clear.confirm.header"),
//                bundle.getString("clear.confirm.content")
//        );
//        if (confirmed) {
//            boolean success = runner.clearRoutes();
//            if (success) {
//                routeData.clear();
//                MainApp.showAlert(
//                        bundle.getString("clear.success.title"),
//                        bundle.getString("clear.success.header"),
//                        bundle.getString("clear.success.content")
//                );
//            } else {
//                MainApp.showAlert(
//                        bundle.getString("clear.error.title"),
//                        bundle.getString("clear.error.header"),
//                        bundle.getString("clear.error.content")
//                );
//            }
//        }
//    }
//
//    @FXML
//    private void handleHelp() {
//        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
//        String helpMessage = bundle.getString("help.general");
//
//        Stage helpStage = new Stage();
//        helpStage.initModality(Modality.APPLICATION_MODAL);
//        helpStage.setTitle("Help");
//
//        TextArea helpTextArea = new TextArea();
//        helpTextArea.setEditable(false);
//        helpTextArea.setWrapText(true);
//        helpTextArea.setText(helpMessage);
//        Button closeButton = new Button("Close");
//        closeButton.setOnAction(event -> helpStage.close());
//
//        VBox vbox = new VBox(helpTextArea, closeButton);
//        vbox.setSpacing(10);
//        vbox.setPadding(new Insets(10));
//
//        Scene scene = new Scene(vbox, 400, 300);
//        helpStage.setScene(scene);
//        helpStage.show();
//    }
//
//    @FXML
//    public void handleAddIfMin() {
//        Route newRoute = new Route();
//
//        boolean okClicked = mainApp.showRouteEditDialog(newRoute);
//
//        if (okClicked) {
//            newRoute.setUserId(runner.getCurrentUserId());
//            boolean added = runner.addRouteIfMin(newRoute);
//
//            if (added) {
//                routeData.add(newRoute);
//                dataTable.getItems().add(newRoute);
//                dataTable.refresh();
//                dataTable.sort();
//
//                Notifications.create()
//                        .title("Route Added")
//                        .text("The route was successfully added." + '\n'
//                                + "Assigned id: " + newRoute.getId())
//                        .hideAfter(Duration.seconds(3))
//                        .position(Pos.BOTTOM_RIGHT)
//                        .showInformation();
//            } else {
//                showAlert("failure.title", bundle.getString("add.failure.header"), bundle.getString("add.failure.content"));
//            }
//        }
//    }
//
//    @FXML
//    public void handleSumOfPrice() {
//        Response response = runner.sumOfPrice();
//        if (response != null) {
//            if (response.isSuccess()) {
//                showAlert(Alert.AlertType.INFORMATION, "Sum of Prices", response.toString());
//            } else {
//                showAlert(Alert.AlertType.ERROR, "Error", response.getMessage());
//            }
//        } else {
//            showAlert(Alert.AlertType.ERROR, "Error", "No response from server.");
//        }
//    }
//
//    private void showAlert(Alert.AlertType alertType, String title, String message) {
//        Alert alert = new Alert(alertType);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//
//    @FXML
//    public void handleExecuteScript() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select Script File");
//        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
//        if (file != null) {
//            Runner.ExitCode exitCode = runner.scriptMode(file);
//            if (exitCode == Runner.ExitCode.OK) {
//                showAlert(Alert.AlertType.INFORMATION, "Script Execution", "Script executed successfully.");
//            } else {
//                showAlert(Alert.AlertType.ERROR, "Error", "Script execution failed.");
//            }
//        }
//    }
//
//    private void showRouteDetails(Route route) {
//        if (route != null) {
//            nameLabel.setText(route.getName());
//            coordinatesLabel.setText(route.getCoordinates().toString());
//            creationDateLabel.setText(route.getCreationDate().toString());
//            fromLabel.setText(route.getFrom().toString());
//            toLabel.setText(route.getTo() != null ? route.getTo().toString() : "");
//            distanceLabel.setText(Float.toString(route.getDistance()));
//        } else {
//            nameLabel.setText("");
//            coordinatesLabel.setText("");
//            creationDateLabel.setText("");
//            fromLabel.setText("");
//            toLabel.setText("");
//            distanceLabel.setText("");
//        }
//    }
//
//    private void showAlert(String title, String header, String content) {
//        Alert alert = new Alert(Alert.AlertType.ERROR);
//        alert.initOwner(mainApp.getPrimaryStage());
//        alert.setTitle(title);
//        alert.setHeaderText(header);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
//
//    public void fetchRoutes() {
//        ObservableList<Route> routes = FXCollections.observableArrayList(runner.fetchRoutes());
//        routeData.setAll(routes);
//        dataTable.setItems(routeData);
//        dataTable.refresh();
//    }
//
//    public void fetchUserRoutes() {
//        ObservableList<Route> userRoutes = FXCollections.observableArrayList(runner.fetchRoutes()
//                .stream().filter(route -> route.getUserId() == runner.getCurrentUserId()).toList());
//        routeData.setAll(userRoutes);
//        dataTable.setItems(routeData);
//        dataTable.refresh();
//    }
//}
package ru.itmo.client.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;
import ru.itmo.general.models.Route;
import ru.itmo.general.network.Response;

import java.io.File;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController {

    private MainApp mainApp;
    private Runner runner;
    private ResourceBundle bundle;

    @FXML
    private TableView<Route> dataTable;
    @FXML
    private TableColumn<Route, Integer> idColumn;
    @FXML
    private TableColumn<Route, String> nameColumn;
    @FXML
    private TableColumn<Route, String> coordinatesColumn;
    @FXML
    private TableColumn<Route, String> creationDateColumn;
    @FXML
    private TableColumn<Route, String> fromColumn;
    @FXML
    private TableColumn<Route, String> toColumn;
    @FXML
    private TableColumn<Route, Float> distanceColumn;
    @FXML
    private TableColumn<Route, Integer> userIdColumn;
    @FXML
    private CheckBox filterCheckBox;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button helpButton;
    @FXML
    public Button addIfMinButton;
    @FXML
    public Button sumOfPriceButton;
    @FXML
    public Button executeScriptButton;
    // Labels for route details
    @FXML
    private Label nameLabel;
    @FXML
    private Label coordinatesLabel;
    @FXML
    private Label creationDateLabel;
    @FXML
    private Label fromLabel;
    @FXML
    private Label toLabel;
    @FXML
    private Label distanceLabel;

    private ObservableList<Route> routeData = FXCollections.observableArrayList();

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @FXML
    private void initialize() {
        addButton.setOnAction(event -> handleAdd());
        updateButton.setOnAction(event -> handleUpdate());
        deleteButton.setOnAction(event -> handleDelete());
        clearButton.setOnAction(event -> handleClear());

        filterCheckBox.setOnAction(event -> {
            if (filterCheckBox.isSelected()) {
                fetchUserRoutes();
            } else {
                fetchRoutes();
            }
        });

        // Initialize the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        coordinatesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCoordinates().toString()));
        creationDateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCreationDate().toString()));
        fromColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFrom().toString()));
        toColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTo() != null ? cellData.getValue().getTo().toString() : ""));
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));

        // Set the observable list data to the table
        dataTable.setItems(routeData);

        // Listen for selection changes and show the route details when changed
        dataTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showRouteDetails(newValue));
    }

    private void handleFilter() {
        if (filterCheckBox.isSelected()) {
            ObservableList<Route> filteredData = FXCollections.observableArrayList();
            for (Route route : runner.fetchRoutes()) {
                if (route.getUserId() == runner.getCurrentUserId()) {
                    filteredData.add(route);
                }
            }
            dataTable.setItems(filteredData);
        } else {
            fetchRoutes();
        }
        dataTable.refresh();
    }

    @FXML
    private void handleAdd() {
        Route newRoute = new Route();

        boolean okClicked = mainApp.showRouteEditDialog(newRoute);

        if (okClicked) {
            newRoute.setUserId(runner.getCurrentUserId());

            int newId = runner.addRoute(newRoute); // Получите новый ID

            if (newId > 0) {

                newRoute.setId(newId); // Установите новый ID в объекте Route

                routeData.add(newRoute); // Добавляем только в routeData
                dataTable.refresh(); // Обновляем таблицу
                dataTable.sort();

                Notifications.create()
                        .title("Route Added")
                        .text("The route was successfully added." + '\n'
                                + "Assigned id: " + newRoute.getId())
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.BOTTOM_RIGHT)
                        .showInformation();
            }
        }
    }

    @FXML
    private void handleUpdate() {
        Route selectedRoute = dataTable.getSelectionModel().getSelectedItem();
        if (selectedRoute != null) {
            boolean okClicked = mainApp.showRouteEditDialog(selectedRoute);
            if (okClicked) {
                runner.updateRoute(selectedRoute);
                showRouteDetails(selectedRoute);
                dataTable.refresh();
            }
        } else {
            showAlert(
                    bundle.getString("update.error.title"),
                    bundle.getString("update.error.header"),
                    bundle.getString("update.error.content")
            );
        }
    }

    @FXML
    private void handleDelete() {
        int selectedIndex = dataTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Route selectedRoute = dataTable.getItems().get(selectedIndex);
            runner.deleteRoute(selectedRoute);
            dataTable.getItems().remove(selectedIndex);
            dataTable.refresh();
        } else {
            showAlert(
                    bundle.getString("delete.error.title"),
                    bundle.getString("delete.error.header"),
                    bundle.getString("delete.error.content")
            );
        }
    }

    @FXML
    private void handleClear() {
        boolean confirmed = MainApp.showConfirmationDialog(
                bundle.getString("clear.confirm.title"),
                bundle.getString("clear.confirm.header"),
                bundle.getString("clear.confirm.content")
        );
        if (confirmed) {
            boolean success = runner.clearRoutes();
            if (success) {
                routeData.clear();
                MainApp.showAlert(
                        bundle.getString("clear.success.title"),
                        bundle.getString("clear.success.header"),
                        bundle.getString("clear.success.content")
                );
            } else {
                MainApp.showAlert(
                        bundle.getString("clear.error.title"),
                        bundle.getString("clear.error.header"),
                        bundle.getString("clear.error.content")
                );
            }
        }
    }

    @FXML
    private void handleHelp() {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());
        String helpMessage = bundle.getString("help.general");

        Stage helpStage = new Stage();
        helpStage.initModality(Modality.APPLICATION_MODAL);
        helpStage.setTitle("Help");

        TextArea helpTextArea = new TextArea();
        helpTextArea.setEditable(false);
        helpTextArea.setWrapText(true);
        helpTextArea.setText(helpMessage);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(event -> helpStage.close());

        VBox vbox = new VBox(helpTextArea, closeButton);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 400, 300);
        helpStage.setScene(scene);
        helpStage.show();
    }

    @FXML
    public void handleAddIfMin() {
        Route newRoute = new Route();

        boolean okClicked = mainApp.showRouteEditDialog(newRoute);

        if (okClicked) {
            newRoute.setUserId(runner.getCurrentUserId());
            boolean added = runner.addRouteIfMin(newRoute);

            if (added) {
                routeData.add(newRoute);
                dataTable.getItems().add(newRoute);
                dataTable.refresh();
                dataTable.sort();

                Notifications.create()
                        .title("Route Added")
                        .text("The route was successfully added." + '\n'
                                + "Assigned id: " + newRoute.getId())
                        .hideAfter(Duration.seconds(3))
                        .position(Pos.BOTTOM_RIGHT)
                        .showInformation();
            } else {
                showAlert("failure.title", bundle.getString("add.failure.header"), bundle.getString("add.failure.content"));
            }
        }
    }

    @FXML
    public void handleSumOfPrice() {
        Response response = runner.sumOfPrice();
        if (response != null) {
            if (response.isSuccess()) {
                showAlert(Alert.AlertType.INFORMATION, "Sum of Prices", response.toString());
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", response.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No response from server.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleExecuteScript() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Script File");
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
        if (file != null) {
            Runner.ExitCode exitCode = runner.scriptMode(file);
            if (exitCode == Runner.ExitCode.OK) {
                showAlert(Alert.AlertType.INFORMATION, "Script Execution", "Script executed successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Script execution failed.");
            }
        }
    }

    private void showRouteDetails(Route route) {
        if (route != null) {
            nameLabel.setText(route.getName());
            coordinatesLabel.setText(route.getCoordinates().toString());
            creationDateLabel.setText(route.getCreationDate().toString());
            fromLabel.setText(route.getFrom().toString());
            toLabel.setText(route.getTo() != null ? route.getTo().toString() : "");
            distanceLabel.setText(Float.toString(route.getDistance()));
        } else {
            nameLabel.setText("");
            coordinatesLabel.setText("");
            creationDateLabel.setText("");
            fromLabel.setText("");
            toLabel.setText("");
            distanceLabel.setText("");
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(mainApp.getPrimaryStage());
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void fetchRoutes() {
        ObservableList<Route> routes = FXCollections.observableArrayList(runner.fetchRoutes());
        routeData.setAll(routes);
        dataTable.setItems(routeData);
        dataTable.refresh();
    }

    public void fetchUserRoutes() {
        ObservableList<Route> userRoutes = FXCollections.observableArrayList(runner.fetchRoutes()
                .stream().filter(route -> route.getUserId() == runner.getCurrentUserId()).toList());
        routeData.setAll(userRoutes);
        dataTable.setItems(userRoutes); // Измените routeData на userRoutes
        dataTable.refresh();
    }
}
