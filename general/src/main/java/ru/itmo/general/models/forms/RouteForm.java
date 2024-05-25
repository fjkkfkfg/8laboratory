package ru.itmo.general.models.forms;

import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.CooRDiNates1;
import ru.itmo.general.models.LocactioN;
import ru.itmo.general.models.Route;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.utility.console.Console;

import java.util.Date;

public class RouteForm extends Form<Route> {
    private final Console console;


    public RouteForm(Console console) {
        this.console = console;

    }

    @Override
    public Route build() throws InvalidScriptInputException, InvalidFormException {
        String name = askName();
        CooRDiNates1 coordinates = askCoordinates();
        LocactioN from = askLocation("Enter the starting location:");
        LocactioN to = askLocation("Enter the ending location (optional, press Enter to skip):");
        float distance = askDistance();
        Date creationDate = new Date(); // Automatically set to the current date
        Integer nextId = null;
        Route route = new Route(nextId,name, coordinates, creationDate, from, to, distance);
        if (!route.validate()) {
            throw new InvalidFormException("Route data is invalid.");
        }
        return route;
    }

    private String askName() throws InvalidScriptInputException {
        console.println("Enter the name of the route:");
        console.prompt();
        String name = Interrogator.getUserScanner().nextLine().trim();
        if (name.isEmpty()) {
            throw new InvalidScriptInputException();
        }
        return name;
    }

    private CooRDiNates1 askCoordinates() throws InvalidScriptInputException, InvalidFormException {
        CooRDiNatesForm coordinatesForm = new CooRDiNatesForm(console);
        return coordinatesForm.build();
    }

    private LocactioN askLocation(String prompt) throws InvalidScriptInputException, InvalidFormException {
        console.println(prompt);
        console.prompt();
        String input = Interrogator.getUserScanner().nextLine().trim();
        if (input.isEmpty() && prompt.contains("optional")) {
            return null; // Optional input for "to" location
        }
        LocatioNForm locationForm = new LocatioNForm(console);
        return locationForm.build();
    }

    private float askDistance() throws InvalidScriptInputException {
        console.println("Enter the distance of the route (must be greater than 1):");
        console.prompt();
        try {
            float distance = Float.parseFloat(Interrogator.getUserScanner().nextLine().trim());
            if (distance <= 1) {
                throw new InvalidScriptInputException();
            }
            return distance;
        } catch (NumberFormatException e) {
            throw new InvalidScriptInputException();
        }
    }
}
