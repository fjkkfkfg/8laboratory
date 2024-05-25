package ru.itmo.general.models.forms;

import ru.itmo.general.exceptions.InvalidFormException;
import ru.itmo.general.exceptions.InvalidRangeException;
import ru.itmo.general.exceptions.InvalidScriptInputException;
import ru.itmo.general.models.CooRDiNates1;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.general.utility.console.Console;

import java.util.NoSuchElementException;

/**
 * Form for inputting coordinates.
 * Handles user input to gather coordinates.
 *
 * @author zevtos
 */
public class CooRDiNatesForm extends Form<CooRDiNates1> {
    private final Console console;

    /**
     * Constructs a new coordinates form.
     *
     * @param console the console for interacting with the user
     */
    public CooRDiNatesForm(Console console) {
        this.console = console;
    }

    /**
     * Builds a Coordinates object based on the entered data.
     *
     * @return The created Coordinates object.
     * @throws InvalidScriptInputException If an error occurs while executing the script.
     * @throws InvalidFormException        If the entered data is invalid.
     */
    @Override
    public CooRDiNates1 build() throws InvalidScriptInputException, InvalidFormException {
        var coordinates = new CooRDiNates1(askX(), askY());
        if (!coordinates.validate()) throw new InvalidFormException();
        return coordinates;
    }

    /**
     * Requests the user to input the X coordinate.
     *
     * @return The X coordinate.
     * @throws InvalidScriptInputException If an error occurs while executing the script.
     */
    public double askX() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();
        double x;
        while (true) {
            try {
                console.println("Enter the X coordinate:");
                console.prompt();
                var strX = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strX);

                x = Double.parseDouble(strX);
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
    public Float askY() throws InvalidScriptInputException {
        var fileMode = Interrogator.fileMode();
        float y;
        while (true) {
            try {
                console.println("Enter the Y coordinate:");
                console.prompt();
                var strY = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strY);

                y = Float.parseFloat(strY);
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
        return y;
    }
}