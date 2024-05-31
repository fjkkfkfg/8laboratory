//package ru.itmo.client.utility.runtime;
//
//import ru.itmo.client.MainApp;
//import ru.itmo.general.exceptions.ScriptRecursionException;
//import ru.itmo.general.managers.CommandManager;
//import ru.itmo.general.models.Route;
//import ru.itmo.general.network.Request;
//import ru.itmo.general.network.Response;
//import ru.itmo.general.utility.Interrogator;
//import ru.itmo.client.utility.console.StandartConsole;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.*;
//
///**
// * Запускает выполнение программы.
// */
//public class Runner {
//    private final Set<String> scriptSet = new HashSet<>();
//    private Request request;
//    private ServerConnection connection;
//
//    /**
//     * Конструктор для Runner.
//     *
//     * @param connection TCPClient
//     */
//    public Runner(ServerConnection connection) {
//        this.connection = connection;
//        createCommandManager();
//    }
//
//    public ExitCode scriptMode(File file) {
//        String argument = file.getAbsolutePath();
//        scriptSet.add(argument);
//        if (!file.exists()) {
//            return ExitCode.ERROR;
//        }
//
//        String[] userCommand;
//        try (Scanner scriptScanner = new Scanner(file)) {
//            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
//            Scanner tmpScanner = Interrogator.getUserScanner();
//            Interrogator.setUserScanner(scriptScanner);
//            Interrogator.setFileMode();
//
//            do {
//                userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
//                userCommand[1] = userCommand[1].trim();
//                if (userCommand[0].equals("execute_script")) {
//                    if (scriptSet.contains(userCommand[1])) throw new ScriptRecursionException();
//                }
//                Response response = connection.sendCommand(userCommand);
//                ExitCode commandStatus;
//                if (response.isSuccess()) {
//                    commandStatus = ExitCode.OK;
//                } else {
//                    commandStatus = ExitCode.ERROR;
//                }
//                if (commandStatus != ExitCode.OK) return commandStatus;
//            } while (scriptScanner.hasNextLine());
//
//            Interrogator.setUserScanner(tmpScanner);
//            Interrogator.setUserMode();
//
//        } catch (NoSuchElementException | IllegalStateException exception) {
//            showError("Ошибка чтения из скрипта.");
//            return ExitCode.ERROR;
//        } catch (FileNotFoundException exception) {
//            showError("Файл не найден");
//            return ExitCode.ERROR;
//        } catch (ScriptRecursionException exception) {
//            showError("Обнаружена рекурсия");
//            return ExitCode.ERROR;
//        } finally {
//            scriptSet.remove(argument);
//        }
//        return ExitCode.OK;
//    }
//
//    /**
//     * Создает менеджер команд приложения.
//     */
//    private void createCommandManager() {
//        CommandManager.initClientCommandsBeforeRegistration();
//    }
//
//    public ExitCode executeRegister(String username, String password) {
//        Response response = connection.sendCommand(new String[]{"register", username, password});
//        if (response.isSuccess()) {
//            return ExitCode.OK;
//        } else {
//            MainApp.showAlert("Ошибка регистрации", response.getMessage(), (response.getData() == null) ? "" : response.getData().toString());
//            return ExitCode.ERROR;
//        }
//    }
//
//    public ExitCode executeLogin(String username, String password) {
//        Response response = connection.sendCommand(new String[]{"login", username, password});
//        if (response.isSuccess()) {
//            return ExitCode.OK;
//        } else {
//            MainApp.showAlert("Ошибка входа", response.getMessage(), (response.getData() == null) ? "" : response.getData().toString());
//            return ExitCode.ERROR;
//        }
//    }
//
//    public List<Route> fetchRoutes() {
//        List<Route> routes = connection.receiveRoutes();
//        return routes != null ? routes : new ArrayList<>();
//    }
//
//    public int addRoute(Route newRoute) {
//        Response response = connection.sendCommand("add", newRoute);
//
//        if (response.isSuccess()) {
//            int newId = (Integer) response.getData();
//            newRoute.setId(newId);  // Установите новый ID в объекте Route
//            return newId;
//        } else {
//            MainApp.showAlert("Ошибка добавления", "Маршрут не был добавлен", response.getMessage());
//            return -1;
//        }
//    }
//
//
//
//
//    public void updateRoute(Route selectedRoute) {
//        connection.sendCommand("update", selectedRoute);
//    }
//
//    public void deleteRoute(Route selectedRoute) {
//        connection.sendCommand("remove_by_id", selectedRoute);
//    }
//
//    public boolean clearRoutes() {
//        Response response = connection.sendCommand("clear", null);
//        if (response.isSuccess()) {
//            return true;
//        } else {
//            MainApp.showAlert("Ошибка очистки маршрутов", "Маршруты не были добавлены", response.getMessage());
//            return false;
//        }
//    }
//
//    // В классе Runner
//    public String getInfo() {
//        Response response = connection.sendCommand("info", null);
//        return (String) response.getData();
//    }
//
//    public Integer getCurrentUserId() {
//        return connection.getCurrentUserId();
//    }
//
//    public boolean addRouteIfMin(Route newRoute) {
//        Response response = connection.sendCommand("add_if_min", newRoute);
//
//        if (response.isSuccess()) {
//            newRoute.setId((Integer) response.getData());
//            return true;
//        } else {
//            MainApp.showAlert("Ошибка добавления", "Маршрут не был добавлен", response.getMessage());
//            return false;
//        }
//    }
//
//    public Response sumOfPrice() {
//        return connection.sendCommand("sum_of_price", null);
//    }
//
//
//    /**
//     * Коды завершения выполнения программы.
//     */
//    public enum ExitCode {
//        OK,
//        ERROR,
//        EXIT,
//        ERROR_NULL_RESPONSE,
//    }
//
//    private void showError(String message) {
//        // Здесь можно добавить код для отображения предупреждения в GUI
//        System.err.println(message); // Временно выводим в консоль
//    }
//}
package ru.itmo.client.utility.runtime;
import javafx.application.Platform;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.console.StandartConsole;
import ru.itmo.general.exceptions.ScriptRecursionException;
import ru.itmo.general.managers.CommandManager;
import ru.itmo.general.models.Route;
import ru.itmo.general.models.forms.RouteForm;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.utility.console.Console;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Запускает выполнение программы.
 */
public class Runner {
    private final Set<String> scriptSet = new HashSet<>();
    private Request request;
    private ServerConnection connection;

    /**
     * Конструктор для Runner.
     *
     * @param connection TCPClient
     */
    public Runner(ServerConnection connection) {
        this.connection = connection;
        createCommandManager();
    }

//    public ExitCode scriptMode(File file) {
//        System.out.println("Script mode: " + file.getAbsolutePath());
//        String argument = file.getAbsolutePath();
//        scriptSet.add(argument);
//        if (!file.exists()) {
//            return ExitCode.ERROR;
//        }
//        String[] userCommand;
//        try (Scanner scriptScanner = new Scanner(file)) {
//            System.out.println("started successfully");
//            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
//            Interrogator.setUserScanner(scriptScanner);
//            Interrogator.setFileMode();
//            do {
//                System.out.println("starting script");
//                userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
//                userCommand[1] = userCommand[1].trim();
//                if (userCommand[0].equals("execute_script")) {
//                    if (scriptSet.contains(userCommand[1])) throw new ScriptRecursionException();
//                }
//                var command = CommandManager.getCommands().get(userCommand[0]);
//                if (command == null) {
//                    return ExitCode.ERROR;
//                }
//                var req = command.execute(userCommand);
//                if (!req.isSuccess()) return Runner.ExitCode.ERROR;
//                Response response = connection.sendCommand(req);
//                System.out.println(response.toString());
//                ExitCode commandStatus;
//                if (response.isSuccess()) {
//                    commandStatus = ExitCode.OK;
//                } else {
//                    commandStatus = ExitCode.ERROR;
//                }
//                if (commandStatus != ExitCode.OK) return commandStatus;
//            } while (scriptScanner.hasNextLine());
//
//        } catch (NoSuchElementException | IllegalStateException exception) {
//            showError("Ошибка чтения из скрипта.");
//            return ExitCode.ERROR;
//        } catch (FileNotFoundException exception) {
//            showError("Файл не найден");
//            return ExitCode.ERROR;
//        } catch (ScriptRecursionException exception) {
//            showError("Обнаружена рекурсия");
//            return ExitCode.ERROR;
//        } finally {
//            scriptSet.remove(argument);
//        }
//        return ExitCode.OK;
//    }

    public ExitCode scriptMode(File file) {
        String argument = file.getAbsolutePath();
        scriptSet.add(argument);
        if (!file.exists()) {
            return ExitCode.ERROR;
        }
        String[] userCommand;
        try (Scanner scriptScanner = new Scanner(file)) {
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            Scanner tmpScanner = Interrogator.getUserScanner();
            Interrogator.setUserScanner(scriptScanner);
            Interrogator.setFileMode();

            do {
                userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                userCommand[1] = userCommand[1].trim();
                if (userCommand[0].equals("execute_script")) {
                    if (scriptSet.contains(userCommand[1])) throw new ScriptRecursionException();
                }
                Response response = connection.sendCommand(userCommand);
                ExitCode commandStatus;
                if (response.isSuccess()) {
                    commandStatus = ExitCode.OK;
                } else {
                    commandStatus = ExitCode.ERROR;
                }
                if (commandStatus != ExitCode.OK) return commandStatus;
            } while (scriptScanner.hasNextLine());

            Interrogator.setUserScanner(tmpScanner);
            Interrogator.setUserMode();

        } catch (NoSuchElementException | IllegalStateException exception) {
            showError("Ошибка чтения из скрипта.");
            return ExitCode.ERROR;
        } catch (FileNotFoundException exception) {
            showError("Файл не найден");
            return ExitCode.ERROR;
        } catch (ScriptRecursionException exception) {
            showError("Обнаружена рекурсия");
            return ExitCode.ERROR;
        } finally {
            scriptSet.remove(argument);
        }
        return ExitCode.OK;
    }

    /**
     * Создает менеджер команд приложения.
     */
    private void createCommandManager() {
        CommandManager.initClientCommandsBeforeRegistration();
        Console console = null;
        CommandManager.initClientCommandsAfterRegistration(console,new RouteForm(new StandartConsole()));
    }

//    private void createCommandManager() {
//        CommandManager.initClientCommandsBeforeRegistration();
//        Console console = new StandartConsole();
//        RouteForm routeForm = new RouteForm(new StandartConsole());
//        CommandManager.initClientCommandsAfterRegistration(console, routeForm);
//    }

    public ExitCode executeRegister(String username, String password) {
        Response response = connection.sendCommand(new String[]{"register", username, password});
        if (response.isSuccess()) {
            return ExitCode.OK;
        } else {
            MainApp.showAlert("Ошибка регистрации", response.getMessage(), (response.getData() == null) ? "" : response.getData().toString());
            return ExitCode.ERROR;
        }
    }

    public ExitCode executeLogin(String username, String password) {
        Response response = connection.sendCommand(new String[]{"login", username, password});
        if (response.isSuccess()) {
            return ExitCode.OK;
        } else {
            MainApp.showAlert("Ошибка входа", response.getMessage(), (response.getData() == null) ? "" : response.getData().toString());
            return ExitCode.ERROR;
        }
    }
//    public List<Route> fetchRoutes() {
//        List<Route> tickets = connection.receiveRoutes();
//        return tickets != null ? tickets : new ArrayList<>();
//    }

    public List<Route> fetchRoutes() {
        System.out.println(1);
        List<Route> routes = connection.receiveRoutes();
        if (routes != null) {
            System.out.println("Fetched routes from server: " + routes.size());
            for (Route route : routes) {
                System.out.println(route);
            }
        } else {
            System.out.println("No routes fetched from server.");
        }
        return routes != null ? routes : new ArrayList<>();
    }


    public int addRoute(Route newRoute) {
        Response response = connection.sendCommand("add", newRoute);

        if (response.isSuccess()) {
            int newId = (int) response.getData();
            return newId;
        } else {
            MainApp.showAlert("Ошибка добавления", "Билет не был добавлен", response.getMessage());
            return -1;
        }
    }


    //public void updateRoute(Route selectedRoute) {
//        connection.sendCommand("update", selectedRoute);
//    }

//    public boolean updateRoute(Route selectedRoute) {
//        Response response = connection.sendCommand("update", selectedRoute);
//        if (response.isSuccess()) {
//            System.out.println("Route successfully updated: " + selectedRoute.getId());
//            return true;
//        } else {
//            MainApp.showAlert("Ошибка епта, прав нет", "操你妈！！Маршрут не изменён ",response.getMessage());
//
//            System.err.println("Failed to update route: " + selectedRoute.getId() + " - " + response.getMessage());
//            return false;
//        }
//    }
public boolean updateRoute(Route selectedRoute) {
    Response response = connection.sendCommand("update", selectedRoute);
    if (response.isSuccess()) {
        System.out.println("Route successfully updated: " + selectedRoute.getId());
        return true;
    } else {
        Platform.runLater(() -> MainApp.showAlert("Ошибка епта, прав нет", "操你妈！！Маршрут не изменён ",response.getMessage()));
        System.err.println("Failed to update route: " + selectedRoute.getId() + " - " + response.getMessage());
        return false;
    }
}

//    public void deleteRoute(Route selectedRoute) {
//        connection.sendCommand("remove_by_id", selectedRoute);
//    }

    public boolean deleteRoute(Route selectedRoute) {
        int id = selectedRoute.getId();
        Response response = connection.sendCommand("remove_by_id", id);

        if (response.isSuccess()) {
            System.out.println("Route successfully deleted: " + id);
            return true;
        } else {
            MainApp.showAlert("Ошибка епта, прав нет", "操你妈！！Маршрут не был удалён ",response.getMessage());
            System.err.println("Failed to delete route: " + id + " - " + response.getMessage());
            return false;
        }
    }


    public boolean clearRoutes() {
        Response response = connection.sendCommand("clear", null);
        if (response.isSuccess()) {
            return true;
        } else {
            MainApp.showAlert("Ошибка очистки билетов", "Маршруты не были изменены", response.getMessage());
            return false;
        }
    }

    // В классе Runner
    public String getInfo() {
        Response response = connection.sendCommand("info", null);
        return (String) response.getData();
    }

    public Integer getCurrentUserId() {
        return connection.getCurrentUserId();
    }

    public boolean addRouteIfMin(Route newRoute) {
        Response response = connection.sendCommand("add_if_min", newRoute);

        if (response.isSuccess()) {
            newRoute.setId((Integer) response.getData());
            return true;
        } else {
            MainApp.showAlert("Ошибка добавления", "Билет не был добавлен", response.getMessage());
            return false;
        }
    }

    public Response sumOfPrice() {
        return connection.sendCommand("sum_of_price", null);
    }


    /**
     * Коды завершения выполнения программы.
     */
    public enum ExitCode {
        OK,
        ERROR,
        EXIT,
        ERROR_NULL_RESPONSE,
    }

    private void showError(String message) {
        System.err.println(message);
    }
}