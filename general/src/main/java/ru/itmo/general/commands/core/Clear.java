package ru.itmo.general.commands.core;

import ru.itmo.general.commands.Command;
import ru.itmo.general.commands.CommandName;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Route;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;

/**
 * Команда 'clear'. Очищает коллекцию.
 *
 */
public class Clear extends Command {
    private CollectionManager<Route> ticketCollectionManager;

    public Clear() {
        super(CommandName.CLEAR, "очистить коллекцию");
    }

    public Clear(CollectionManager<Route> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            int userId = request.getUserId();
            ticketCollectionManager.clear(userId);
            return new Response(true, "Коллекция очищена от билетов текущего пользователя.");
        } catch (Exception e){
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
        if (!arguments[1].isEmpty()) {
            return new Request(false, getName(), getUsingError());
        }
        return new Request(getName(), null);
    }
}
