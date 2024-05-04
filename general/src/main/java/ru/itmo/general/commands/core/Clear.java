package ru.itmo.general.commands.core;

import ru.itmo.general.commands.CommandName;
import ru.itmo.general.models.Ticket;
import ru.itmo.general.network.Request;
import ru.itmo.general.network.Response;
import ru.itmo.general.commands.Command;
import ru.itmo.general.managers.CollectionManager;

/**
 * Команда 'clear'. Очищает коллекцию.
 *
 * @author zevtos
 */
public class Clear extends Command {
    private CollectionManager<Ticket> ticketCollectionManager;
    public Clear() {
        super(CommandName.CLEAR, "очистить коллекцию");
    }
    public Clear(CollectionManager<Ticket> ticketCollectionManager) {
        this();
        this.ticketCollectionManager = ticketCollectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Успешность выполнения команды.
     */
    @Override
    public Response execute(Request arguments) {
        try {
            ticketCollectionManager.clearCollection();
            return new Response(true, "Коллекция очищена!");
        } catch (Exception e){
            return new Response(false, e.toString());
        }
    }
    /**
     * Выполняет команду
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