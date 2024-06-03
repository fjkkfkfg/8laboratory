package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Route;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

import java.util.List;

/**
 * Команда 'show'. Выводит все элементы коллекции.
 *
 */
public class Show extends Command {
    private CollectionManager<Route> routeCollectionManager;

    public Show() {
        super(CommandName.SHOW, "вывести все элементы коллекции Route");
    }

    /**
     * Конструктор для создания экземпляра команды Show.
     *
     * @param routeCollectionManager менеджер коллекции
     */
    public Show(CollectionManager<Route> routeCollectionManager) {
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
        List<Route> routes = routeCollectionManager.getCollection();
        System.out.println(routes.size());
        return new Response(true, "Collection fetched successfully", routes);
    }

    /**
     * Выполняет команду
     *
     * @param arguments аргументы команды
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        if (!arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }
        return new Request(getName(), null);
    }
}
