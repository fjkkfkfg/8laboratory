package ru.itmo.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;

import java.util.ResourceBundle;

public class LoginController {
    private MainApp mainApp;
    private Runner runner;
    private ResourceBundle bundle;
    private ComboBox<String> languageComboBox;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private TextArea messageOutput;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        System.out.println(username);
        String password = passwordField.getText();
        System.out.println(password);
        Runner.ExitCode result = runner.executeLogin(username, password);
        if (result == Runner.ExitCode.OK) {
            mainApp.showMainScreen(bundle);
        } else {
            messageOutput.appendText(bundle.getString("login.failed") + "\n");
        }
    }

    @FXML
    private void handleRegister() {
        mainApp.showRegisterScreen(bundle);
    }
}

//package ru.itmo.client.controller;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import ru.itmo.client.MainApp;
//import ru.itmo.client.utility.runtime.Runner;
//
//import java.util.Locale;
//import java.util.ResourceBundle;
//
//public class LoginController {
//    private MainApp mainApp;
//    private Runner runner;
//    private ResourceBundle bundle;
//
//    @FXML
//    private ComboBox<String> languageComboBox;
//    @FXML
//    private TextField usernameField;
//    @FXML
//    private PasswordField passwordField;
//    @FXML
//    private Button loginButton;
//    @FXML
//    private TextArea messageOutput;
//
//    public void setMainApp(MainApp mainApp) {
//        this.mainApp = mainApp;
//    }
//
//    public void setBundle(ResourceBundle bundle) {
//        this.bundle = bundle;
//    }
//
//    public void setRunner(Runner runner) {
//        this.runner = runner;
//    }
//
//    @FXML
//    private void initialize() {
//        // Set default language to English
//        languageComboBox.setValue("English");
//
//        // Add listener to the language combo box
//        languageComboBox.setOnAction(event -> handleLanguageChange());
//
//        loginButton.setOnAction(event -> handleLogin());
//    }
//
//    @FXML
//    private void handleLogin() {
//        String username = usernameField.getText();
//        String password = passwordField.getText();
//        Runner.ExitCode result = runner.executeLogin(username, password);
//        if (result == Runner.ExitCode.OK) {
//            mainApp.showMainScreen(bundle);
//        } else {
//            messageOutput.appendText(bundle.getString("login.failed") + "\n");
//        }
//    }
//
//    @FXML
//    private void handleRegister() {
//        mainApp.showRegisterScreen(bundle);
//    }
//
//    @FXML
//    private void handleLanguageChange() {
//        String selectedLanguage = languageComboBox.getValue();
//        Locale locale;
//
//        switch (selectedLanguage) {
//            case "Русский":
//                locale = new Locale("ru");
//                break;
//            case "Slovenský":
//                locale = new Locale("sk");
//                break;
//            case "中文":
//                locale = Locale.SIMPLIFIED_CHINESE;
//                break;
//            case "Shqip":
//                locale = new Locale("sq");
//                break;
//            default:
//                locale = new Locale("en");
//                break;
//        }
//
//       // mainApp.setLocale(locale);
//    }
//}
