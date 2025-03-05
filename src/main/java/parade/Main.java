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
                if (input != 1 && input != 2) {
                    System.out.println("Invalid input, please only type only 1 or 2.");
                    continue;
                }

                if (input == 1) {
                    startGame();
                } else {
                    TextRendererProvider.getInstance().renderBye();
                }
                break;
            } catch (NoSuchElementException e) {
                System.out.println("Invalid input, please try again.");
            }
        }
    }

    static void startGame() {
        // TODO: implement game server logic
    }
}
