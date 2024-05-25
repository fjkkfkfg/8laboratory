package ru.itmo.general.commands.custom;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.EmptyValueException;
import ru.itmo.general.exceptions.InvalidNumberOfElementsException;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Route;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.base.Accessible;

import java.rmi.AccessException;

/**
 * Команда 'remove_head'. Выводит первый элемент коллекции и удаляет его.
 *
 * @author zevtos
 */
public class RemoveHead extends Command {
    private CollectionManager<Route> routeCollectionManager;
    private Accessible dao;

    public RemoveHead() {
        super(CommandName.REMOVE_HEAD, "вывести первый элемент коллекции и удалить его");
    }

    public RemoveHead(CollectionManager<Route> routeCollectionManager, Accessible dao) {
        this();
        this.routeCollectionManager = routeCollectionManager;
        this.dao = dao;
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
            Route routeToRemove = routeCollectionManager.getLast();
            if (!dao.checkOwnership(routeToRemove.getId(), request.getUserId()))
                throw new AccessException("У вас нет доступа к этому билету");
            routeCollectionManager.remove(routeToRemove);
            return new Response(true, "Билет успешно удален.");

        } catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!");
        } catch (AccessException e) {
            return new Response(false, e.getMessage());
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
            if (arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();
            return new Request(getName(), null);
        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false, getName(), getUsingError());
        }
    }
}
