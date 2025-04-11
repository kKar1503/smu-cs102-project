package parade.menu;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

class MenuResource {
    static enum MenuResourceType {
        ADVANCED_WELCOME_MENU("advanced_welcome_menu.txt");

        private final String fileName;

        MenuResourceType(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }

    static Map<MenuResourceType, String> menuResources = null;

    static String get(MenuResourceType menuResourceType) {
        if (menuResources == null) {
            menuResources = new HashMap<>();
            for (MenuResourceType type : MenuResourceType.values()) {
                String resource = loadMenuResource(type.getFileName());
                menuResources.put(type, resource);
            }
        }

        return menuResources.get(menuResourceType);
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
