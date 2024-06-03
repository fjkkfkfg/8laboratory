package ru.itmo.client.main;


import ru.itmo.client.network.TCPClient;
import ru.itmo.general.utility.gui.GuiMessageOutput;
import ru.itmo.client.utility.runtime.Runner;
import ru.itmo.general.utility.Interrogator;
import ru.itmo.client.utility.console.StandartConsole;

import javax.swing.*;
import java.util.Scanner;

/**
 * Главный класс приложения.
 *
 */
public class Main {
    private static final int PORT = 4093;

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        Interrogator.setUserScanner(new Scanner(System.in));
        var gui = new GuiMessageOutput(new JTextArea());
        var client = new TCPClient("localhost", PORT, gui);
        //new Runner(new StandartConsole(), client).run();
    }
}
