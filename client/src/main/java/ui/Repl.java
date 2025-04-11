package ui;

import exception.ResponseException;
import websocket.NotificationHandler;
import websocket.messages.Error;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

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

    private void printOopsieDaisy() {
        System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
        System.out.print("Oopsie daisy, something went wrong\n");
        System.out.print(EscapeSequences.RESET_TEXT_COLOR);
    }

    private void notifyLoadGame(ServerMessage serverMessage) {
        if (serverMessage.getClass() == LoadGame.class) {
            LoadGame loadGame = (LoadGame) serverMessage;
            try {
                System.out.print(client.gameString(loadGame.gameData, client.playerColor, null));
            } catch (ResponseException ignored) {
                printOopsieDaisy();
            }
        } else {
            printOopsieDaisy();
        }
    }

    private void notifyError(ServerMessage serverMessage) {
        if (serverMessage.getClass() == Error.class) {
            Error error = (Error) serverMessage;
            System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
            System.out.print(error.message);
            System.out.print(EscapeSequences.RESET_TEXT_COLOR);
        } else {
            printOopsieDaisy();
        }
    }

    private void notifyNotification(ServerMessage serverMessage) {
        if (serverMessage.getClass() == Notification.class) {
            Notification notification = (Notification) serverMessage;
            System.out.print(notification.getMessage());
        } else {
            printOopsieDaisy();
        }
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        switch (serverMessage.getServerMessageType()) {
            case ERROR -> notifyError(serverMessage);
            case NOTIFICATION -> notifyNotification(serverMessage);
            case LOAD_GAME -> notifyLoadGame(serverMessage);
            case null, default -> printOopsieDaisy();
        }
    }
}