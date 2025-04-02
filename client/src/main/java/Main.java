import ui.Repl;

public class Main {
    private static final int PORT = 8080;
    private static final String SERVER_URL = "http://localhost:" + PORT;

    public static void main(String[] args) {

        Repl repl = new Repl(SERVER_URL);
        repl.run();
    }
}