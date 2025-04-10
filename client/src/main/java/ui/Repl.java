package ui;

import websocket.NotificationHandler;
import websocket.messages.Notification;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("♕ 240 Chess Client ♕\n");
        System.out.println("Welcome to Chess. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Exception e) {
                var msg = e.getMessage();
                System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

    @Override
    public void notify(Notification notification) {
        System.out.print(notification.toString());
    }
}