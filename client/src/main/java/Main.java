import server.Server;
import ui.Repl;

public class Main {
    private static final int port = 8080;
    private static final String serverUrl = "http://localhost:" + port;

    public static void main(String[] args) {
//        Server server = new Server();
//        server.run(port);

        Repl repl = new Repl(serverUrl);
        repl.run();
    }
}