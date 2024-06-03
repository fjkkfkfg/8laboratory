package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Route;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.time.LocalDateTime;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 *
 */
public class Info extends Command {
    private CollectionManager<Route> routeCollectionManager;

    public Info() {
        super(CommandName.INFO, "вывести информацию о коллекции");
    }

    /**
     * Конструктор для создания экземпляра команды Info.
     *
     * @param routeCollectionManager менеджер коллекции маршрутов
     */
    public Info(CollectionManager<Route> routeCollectionManager) {
        this();
        this.routeCollectionManager = routeCollectionManager;
    }

    /**
     * Выполняет команду
     *
     * @param arguments аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request arguments) {

        LocalDateTime routeLastSaveTime = routeCollectionManager.getLastSaveTime();
        String routeLastSaveTimeString = (routeLastSaveTime == null) ? "в данной сессии сохранения еще не происходило" :
                routeLastSaveTime.toLocalDate().toString() + " " + routeLastSaveTime.toLocalTime().toString();

        String message;

        message = "Сведения о коллекции:" + '\n' +
                " Тип: " + routeCollectionManager.collectionType() + '\n' +
                " Количество элементов Route: " + routeCollectionManager.collectionSize() + '\n' +
                " Дата последнего сохранения:" + routeLastSaveTimeString;

        return new Response(true, null, message);
    }

    /**
     * Выполняет команду
     *
     * @param arguments аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        if (arguments.length > 1 && !arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }

        return new Request(getName(), null);
    }
}
