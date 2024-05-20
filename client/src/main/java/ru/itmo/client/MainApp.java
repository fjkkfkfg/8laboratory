package ru.itmo.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import lombok.Getter;
import lombok.SneakyThrows;
import ru.itmo.client.controller.*;
import ru.itmo.client.utility.runtime.Runner;
import ru.itmo.client.utility.runtime.ServerConnection;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.models.forms.TicketForm;
import ru.itmo.general.utility.gui.GuiMessageOutput;

import javax.swing.*;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;
    @Getter
    private Runner runner;
    private ResourceBundle bundle;

    @Override
    public void start(Stage primaryStage) {
        CommandManager.initClientCommandsBeforeRegistration();
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Ticket Management System");

        Locale.setDefault(new Locale("ru"));
        ResourceBundle bundle = ResourceBundle.getBundle("messages", Locale.getDefault());

        ServerConnection connection = new ServerConnection("localhost", 4093); // Укажите хост и порт вашего сервера
        runner = new Runner(connection);
        this.bundle = bundle;
        initRootLayout(bundle);
        showLoginScreen(bundle);
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void initRootLayout(ResourceBundle bundle) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/RootLayout.fxml"));
            loader.setResources(bundle);
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this, bundle);
            controller.setRunner(runner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLoginScreen(ResourceBundle bundle) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/LoginScreen.fxml"));
            loader.setResources(bundle);
            VBox loginScreen = loader.load();

            rootLayout.setCenter(loginScreen);

            LoginController controller = loader.getController();
            controller.setMainApp(this);
            controller.setBundle(bundle);
            controller.setRunner(runner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showRegisterScreen(ResourceBundle bundle) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/RegisterScreen.fxml"));
            loader.setResources(bundle);
            VBox registerScreen = loader.load();

            rootLayout.setCenter(registerScreen);

            RegisterController controller = loader.getController();
            controller.setMainApp(this);
            controller.setBundle(bundle);
            controller.setRunner(runner);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public void showMainScreen(ResourceBundle bundle) {
        CommandManager.initClientCommandsAfterRegistration(new TicketForm(new GuiMessageOutput(new JTextArea())));
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/MainScreen.fxml"));
            loader.setResources(bundle);
            VBox mainScreen = loader.load();

            rootLayout.setCenter(mainScreen);

            MainController controller = loader.getController();
            controller.setMainApp(this);
            controller.setRunner(runner);
            controller.setBundle(bundle); // добавим установку bundle
            Thread.sleep(100);
            controller.fetchTickets(); // вызов метода для заполнения таблицы данными
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean showTicketEditDialog(Ticket ticket) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("/view/TicketEditDialog.fxml"));
            AnchorPane page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle(bundle.getString("edit.title"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            TicketEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTicket(ticket);
            controller.setBundle(bundle);

            dialogStage.showAndWait();
            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public static void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();
    }

    public Window getPrimaryStage() {
        return primaryStage;
    }
}
