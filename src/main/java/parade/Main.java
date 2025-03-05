package parade;

import parade.settings.SettingKey;
import parade.settings.Settings;
import parade.textrenderer.TextRendererProvider;
import parade.textrenderer.impl.BasicTextRenderer;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Settings settings =
                new Settings.Builder()
                        .shouldValidateProperties(true)
                        .fromClasspath("config.properties")
                        .build();

        String gameplayMode = settings.get(SettingKey.GAMEPLAY_MODE);

        switch (gameplayMode) {
            case "local":
                TextRendererProvider.setInstance(new BasicTextRenderer());
                break;
            case "network":
                throw new UnsupportedOperationException("Network mode is not yet supported");
            default:
                throw new IllegalStateException(
                        "Unknown gameplay mode in settings: " + gameplayMode);
        }

        TextRendererProvider.getInstance().renderWelcome();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            TextRendererProvider.getInstance().renderMenu();
            try {
                int input = scanner.nextInt();
                scanner.nextLine();
                if (input == 2) {
                    break;
                }
                startGame();
            } catch (NoSuchElementException e) {
                System.out.println("Invalid input, please try again.");
            }
        }

        TextRendererProvider.getInstance().renderBye();
    }

    static void startGame() {
        // TODO: implement game server logic
    }
}
