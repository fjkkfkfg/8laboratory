package ru.itmo.client.controller;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ru.itmo.client.MainApp;
import ru.itmo.general.models.CooRDiNates1;
import ru.itmo.general.models.LocactioN;
import ru.itmo.general.models.Route;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.ResourceBundle;

public class RouteEditDialogController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField coordinatesField;
    @FXML
    private DatePicker creationDateField;
    @FXML
    private TextField fromField;
    @FXML
    private TextField toField;
    @FXML
    private TextField distanceField;

    private Stage dialogStage;
    private Route route;
    private boolean okClicked = false;
    private ResourceBundle bundle;

    @FXML
    private void initialize() {
        // Инициализация при необходимости
    }

//    public void setRoute(Route route) {
//        this.route = route;
//
//        nameField.setText(route.getName());
//        coordinatesField.setText(route.getCoordinates().toString());
//        creationDateField.setValue(route.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//        fromField.setText(route.getFrom().toString());
//        toField.setText(route.getTo() != null ? route.getTo().toString() : "");
//        distanceField.setText(Float.toString(route.getDistance()));
//    }

    public void setRoute(Route route) {
        this.route = route;

        nameField.setText(route.getName());
        coordinatesField.setText(route.getCoordinates().toString());
        if (route.getCreationDate() instanceof Timestamp) {
            creationDateField.setValue(((Timestamp) route.getCreationDate()).toLocalDateTime().toLocalDate());
        } else {
            // Если не Timestamp, то пытаемся преобразовать через Instant
            creationDateField.setValue(route.getCreationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
        fromField.setText(route.getFrom().toString());
        toField.setText(route.getTo() != null ? route.getTo().toString() : "");
        distanceField.setText(Float.toString(route.getDistance()));
    }






    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            route.setName(nameField.getText());
            route.setCoordinates(new CooRDiNates1(
                    Double.parseDouble(coordinatesField.getText().split(";")[0]),
                    Float.parseFloat(coordinatesField.getText().split(";")[1])
            ));
            route.setCreationDate(Timestamp.valueOf(creationDateField.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDateTime()));
            route.setFrom(parseLocation(fromField.getText()));
            route.setTo(parseLocation(toField.getText()));
            route.setDistance(Float.parseFloat(distanceField.getText()));

            okClicked = true;
            dialogStage.close();
        } else {
            MainApp.showAlert(bundle.getString("route.edit.invalid.title"),
                    bundle.getString("route.edit.invalid.header"),
                    bundle.getString("route.edit.invalid.content"));
        }
    }

//    @FXML
//    private void handleOk() {
//        if (isInputValid()) {
//            route.setName(nameField.getText());
//            route.setCoordinates(new CooRDiNates1(
//                    Double.parseDouble(coordinatesField.getText().split(";")[0]),
//                    Float.parseFloat(coordinatesField.getText().split(";")[1])
//            ));
//            route.setCreationDate(Timestamp.valueOf(creationDateField.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toLocalDateTime()));
//            route.setFrom(parseLocation(fromField.getText()));
//            route.setTo(parseLocation(toField.getText()));
//            route.setDistance(Float.parseFloat(distanceField.getText()));
//
//            okClicked = true;
//            dialogStage.close();
//        } else {
//            MainApp.showAlert(bundle.getString("route.edit.invalid.title"),
//                    bundle.getString("route.edit.invalid.header"),
//                    bundle.getString("route.edit.invalid.content"));
//        }
//    }

//    @FXML
//    private void handleOk() {
//
//        if (isInputValid()) {
//            route.setName(nameField.getText());
//            route.setCoordinates(new CooRDiNates1(
//                    Double.parseDouble(coordinatesField.getText().split(";")[0]),
//                    Float.parseFloat(coordinatesField.getText().split(";")[1])
//            ));
//            //route.setCreationDate(ZonedDateTime.of(creationDateField.getValue().atStartOfDay(), ZoneId.systemDefault()));
//            route.setCreationDate(Date.from(creationDateField.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
//            route.setFrom(parseLocation(fromField.getText()));
//            route.setTo(parseLocation(toField.getText()));
//            route.setDistance(Float.parseFloat(distanceField.getText()));
//
//            System.out.println("Route before sending: " + route);
//
//            okClicked = true;
//            dialogStage.close();
//        } else {
//            MainApp.showAlert(bundle.getString("route.edit.invalid.title"),
//                    bundle.getString("route.edit.invalid.header"),
//                    bundle.getString("route.edit.invalid.content"));
//        }
//    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += bundle.getString("route.edit.invalid.name") + "\n";
        }
        if (coordinatesField.getText() == null || coordinatesField.getText().isEmpty()) {
            errorMessage += bundle.getString("route.edit.invalid.coordinates") + "\n";
            MainApp.showAlert("Ошибка добавления", "Билет не был добавлен", errorMessage);
        } else {
            try {
                String[] coords = coordinatesField.getText().split(";");
                Double x = Double.parseDouble(coords[0]);
                Float y = Float.parseFloat(coords[1]);
            } catch (NumberFormatException e) {
                errorMessage += bundle.getString("route.edit.invalid.coordinates.format") + "\n";
            }
        }
        if (creationDateField.getValue() == null) {
            errorMessage += bundle.getString("route.edit.invalid.creationDate") + "\n";
        }
        if (fromField.getText() == null || fromField.getText().isEmpty()) {
            errorMessage += bundle.getString("route.edit.invalid.from") + "\n";
        } else {
            try {
                parseLocation(fromField.getText());
            } catch (NumberFormatException e) {
                errorMessage += bundle.getString("route.edit.invalid.from.format") + "\n";
            }
        }
        if (toField.getText() == null || toField.getText().isEmpty()) {
            errorMessage += bundle.getString("route.edit.invalid.to") + "\n";
        } else {
            try {
                parseLocation(toField.getText());
            } catch (NumberFormatException e) {
                errorMessage += bundle.getString("route.edit.invalid.to.format") + "\n";
            }
        }
        if (distanceField.getText() == null || distanceField.getText().isEmpty()) {
            errorMessage += bundle.getString("route.edit.invalid.distance") + "\n";
        } else {
            try {
                float distance = Float.parseFloat(distanceField.getText());
                if (distance <= 1) {
                    errorMessage += bundle.getString("route.edit.invalid.distance.format") + "\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += bundle.getString("route.edit.invalid.distance.format") + "\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            MainApp.showAlert(bundle.getString("route.edit.invalid.title"),
                    bundle.getString("route.edit.invalid.header"),
                    errorMessage);
            return false;
        }
    }

    private LocactioN parseLocation(String text) {
        String cleanedText = text.replace("(", "").replace(")", "");
        String[] parts = cleanedText.split(",");
        if (parts.length < 2) {
            throw new NumberFormatException("Invalid location format");
        }
        Long x = Long.parseLong(parts[0].trim());
        Integer y = Integer.parseInt(parts[1].trim());
        String name = parts.length == 3 ? parts[2].trim() : "";
        return new LocactioN(x, y, name);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
