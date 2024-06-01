//package ru.itmo.client.controller;
//
//import javafx.fxml.FXML;
//import ru.itmo.client.MainApp;
//import ru.itmo.client.utility.runtime.Runner;
//
//import java.util.ResourceBundle;
//
//public class RootLayoutController {
//    private MainApp mainApp;
//    private Runner runner;
//    private ResourceBundle bundle;
//
//    public void setMainApp(MainApp mainApp, ResourceBundle bundle) {
//        this.mainApp = mainApp;
//        this.bundle = bundle;
//    }
//
//    public void setRunner(Runner runner) {
//        this.runner = runner;
//    }
//
//    @FXML
//    private void handleExit() {
//        System.exit(0);
//    }1
//
//
//
//    @FXML
//    private void handleAbout() {
//        String infoMessage = mainApp.getRunner().getInfo();
//        MainApp.showAlert("Информация о коллекции", null, infoMessage);
//    }
//}


package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;

import java.util.Locale;
import java.util.ResourceBundle;

public class RootLayoutController {
    private MainApp mainApp;
    private Runner runner;
    private ResourceBundle bundle;

    @FXML
    private ComboBox<String> languageComboBox;

    public void setMainApp(MainApp mainApp, ResourceBundle bundle) {
        this.mainApp = mainApp;
        this.bundle = bundle;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void initialize() {
        // Set default language
        languageComboBox.setValue("English");
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleAbout() {
        String infoMessage = mainApp.getRunner().getInfo();
        MainApp.showAlert("Информация о коллекции", null, infoMessage);
    }

    @FXML
    private void handleLanguageChange() {
        String selectedLanguage = languageComboBox.getValue();
        Locale locale;
        switch (selectedLanguage) {
            case "Русский":
                locale = new Locale("ru");
                break;
            case "Slovenský":
                locale = new Locale("sk");
                break;
            case "中文":
                locale = new Locale("zh");
                break;
            case "Shqip":
                locale = new Locale("al");
                break;
            default:
                locale = new Locale("en");
                break;
        }
        bundle = ResourceBundle.getBundle("messages", locale);
        mainApp.setBundle(bundle);
        if (runner.getCurrentUserId() == null) {
            mainApp.showLoginScreen(bundle);
        } else {
            mainApp.showMainScreen(bundle);
        }
        //mainApp.showMainScreen(bundle);

    }
}

