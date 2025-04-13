package parade.menu.base;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class MenuResource {
    public enum MenuResourceType {
        ASCII_FINAL("ascii_final.txt"),
        ASCII_GAME_OVER("ascii_game_over.txt"),
        ADVANCED_WELCOME_SCREEN("advanced_welcome_screen.txt"),
        BASIC_WELCOME_MENU("basic_welcome_menu.txt"),

        // Dice
        DICE_ROLLING("dice/rolling.txt"),
        DICE_ONE("dice/1.txt"),
        DICE_TWO("dice/2.txt"),
        DICE_THREE("dice/3.txt"),
        DICE_FOUR("dice/4.txt"),
        DICE_FIVE("dice/5.txt"),
        DICE_SIX("dice/6.txt");

        private final String fileName;

        MenuResourceType(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

    public static Map<MenuResourceType, String> menuResources = null;

    public static String get(MenuResourceType menuResourceType) {
        if (menuResources == null) {
            menuResources = new HashMap<>();
            for (MenuResourceType type : MenuResourceType.values()) {
                String resource = loadMenuResource(type.getFileName());
                menuResources.put(type, resource);
            }
        }

        return menuResources.get(menuResourceType);
    }

    public static String[] getArray(MenuResourceType menuResourceType) {
        String resource = get(menuResourceType);
        return resource.split(System.lineSeparator());
    }

    private static String loadMenuResource(String filename) {
        try (InputStream is = MenuResource.class.getResourceAsStream("/menu/" + filename);
                Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {

            StringBuilder sb = new StringBuilder();
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append(System.lineSeparator());
            }
            return sb.toString();
        } catch (IOException | NullPointerException e) {
            return "[Failed to load menu resource: " + filename + "]";
        }
    }
}
