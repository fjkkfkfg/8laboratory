package ru.itmo.general.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Route;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

/**
 * Команда 'max_by_name'. Выводит элемент с максимальным именем.
 *
 * @author zevtos
 */
public class MaxByName extends Command {
    private CollectionManager<Route> routeCollectionManager;

    public MaxByName() {
        super(CommandName.MAX_BY_NAME, "вывести любой объект из коллекции, значение поля name которого является максимальным");

    }

    public MaxByName(CollectionManager<Route> routeCollectionManager) {
        this();
        this.routeCollectionManager = routeCollectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            if (routeCollectionManager.collectionSize() == 0) throw new EmptyValueException();
            Route route = maxByName();
            return new Response(true, null, route.toString());

        } catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!", null);
        }
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        try {
            if (arguments.length > 1 && !arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            return new Request(getName(), null);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false,
                    getName(),
                    getUsingError());
        }
    }

    private Route maxByName() {
        String maxName = "";
        int routeId = -1;
        for (Route c : routeCollectionManager.getCollection()) {
            if (c.getName().compareTo(maxName) < 0) {
                maxName = c.getName();
                routeId = c.getId();
            }
        }
        if (routeId == -1) return routeCollectionManager.getFirst();
        return routeCollectionManager.byId(routeId);
    }
}
