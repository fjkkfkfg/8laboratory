package ru.itmo.general.commands.update;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.exceptions.*;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Route;
import ru.itmo.general.models.forms.Form;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.utility.base.Accessible;

import java.rmi.AccessException;

/**
 * Команда 'update'. Обновляет элемент коллекции.
 *
 */
public class Update extends Command {
    private CollectionManager<Route> routeCollectionManager;
    private Form<Route> routeForm;
    private Accessible dao;

    public Update() {
        super(CommandName.UPDATE, "<ID> {element} обновить значение элемента коллекции по ID");
    }

    public Update(CollectionManager<Route> routeCollectionManager, Accessible dao) {
        this();
        this.routeCollectionManager = routeCollectionManager;
        this.dao = dao;
    }

    public Update(Form<Route> routeForm) {
        this();
        this.routeForm = routeForm;
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

            var new_route = ((Route) request.getData());
            var id = new_route.getId();
            var route = routeCollectionManager.byId(id);
            if (route == null) throw new NotFoundException();
            if (!dao.checkOwnership(route.getId(), request.getUserId()))
                throw new AccessException("У вас нет доступа к этому маршруту");
            route.update(new_route);

            return new Response(true, "Билет успешно обновлен.");

        } catch (EmptyValueException exception) {
            return new Response(false, "Коллекция пуста!");
        } catch (NotFoundException exception) {
            return new Response(false, "Билета с таким ID в коллекции нет!");
        } catch (AccessException e) {
            return new Response(false, e.getMessage());
        }
    }

    /**
     * Выполняет команду
     *
     * @param arguments Аргументы команды.
     * @return Успешность выполнения команды.
     */
    @Override
    public Request execute(String[] arguments) {
        System.out.println("Началось изменение маршрута");
        try {
            if (arguments[1].isEmpty()) throw new InvalidNumberOfElementsException();

            var id = Integer.parseInt(arguments[1]);

            var newRoute = routeForm.build();
            newRoute.setId(id);
            return new Request(getName(), newRoute);

        } catch (InvalidNumberOfElementsException exception) {
            return new Request(false,
                    getName(),
                    getUsingError());
        } catch (NumberFormatException exception) {
            return new Request(false,
                    getName(),
                    "ID должен быть представлен числом!");
        } catch (InvalidScriptInputException e) {
            return new Request(false,
                    getName(),
                    "Некорректный ввод в скрипте!");
        } catch (InvalidFormException e) {
            return new Request(false,
                    getName(),
                    "Поля билета не валидны! Билет не обновлен!");
        }
    }
}
