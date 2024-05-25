package ru.itmo.general.models.forms;

import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidRangeException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.LocactioN;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.utility.console.Console;

import java.util.NoSuchElementException;

import static ru.itmo.general.utility.Interrogator.fileMode;

public class LocatioNForm extends Form<LocactioN> {
    private final Console console;

    public LocatioNForm(Console console) {
        this.console = console;
    }

    @Override
    public LocactioN build() throws InvalidScriptInputException, InvalidFormException {
        Long x = (long) askX();
        Integer y = askY();
        String name = askName();
        LocactioN location = new LocactioN(x, y, name);
        if (!location.validate()) {
            throw new InvalidFormException("Invalid LocactioN data");
        }
        return location;
    }

    public double askX() throws InvalidScriptInputException {
        var fileMode = fileMode();
        Long x;
        while (true) {
            try {
                console.println("Enter the X coordinate:");
                console.prompt();
                var strX = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strX);

                x = Long.parseLong(strX);
                break;
            } catch (NoSuchElementException exception) {
                System.err.println("X coordinate not recognized!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NumberFormatException exception) {
                System.err.println("X coordinate must be a number!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NullPointerException | IllegalStateException exception) {
                System.err.println("An unexpected error occurred!");
                System.exit(0);
            }
        }
        return x;
    }

    /**
     * Requests the user to input the Y coordinate.
     *
     * @return The Y coordinate.
     * @throws InvalidScriptInputException If an error occurs while executing the script.
     */
    public Integer askY() throws InvalidScriptInputException {
        var fileMode = fileMode();
        float y;
        while (true) {
            try {
                console.println("Enter the Y coordinate:");
                console.prompt();
                var strY = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strY);

                y = Integer.parseInt(strY);
                if (y <= -420) throw new InvalidRangeException("Y value must be greater than -420");
                break;
            } catch (NoSuchElementException exception) {
                System.err.println("Y coordinate not recognized!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (NumberFormatException exception) {
                System.err.println("Y coordinate must be a number!");
                if (fileMode) throw new InvalidScriptInputException();
            } catch (InvalidRangeException exception) {
                System.err.println(exception.getMessage());
            } catch (NullPointerException | IllegalStateException exception) {
                System.err.println("An unexpected error occurred!");
                System.exit(0);
            }
        }
        return (int) y;
    }

    private String askName() throws InvalidScriptInputException {
        while (true) {
            console.println("Enter the name (up to 502 characters):");
            console.prompt();
            var name = Interrogator.getUserScanner().nextLine().trim();
            if (fileMode()) console.println(name);
            if (name.length() <= 502) {
                return name;
            } else {
                System.err.println("Name must be 502 characters or fewer!");
            }
        }
    }
}
