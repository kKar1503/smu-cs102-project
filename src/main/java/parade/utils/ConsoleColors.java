package parade.utils;

public class ConsoleColors {
    // Reset
    public static final String RESET = "\033[0m"; // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;90m"; // BLACK - SPECIAL  \033[0;30m

    public static final String RED = "\033[0;31m"; // RED
    public static final String GREEN = "\033[0;32m"; // GREEN
    public static final String YELLOW = "\033[0;33m"; // YELLOW
    public static final String BLUE = "\033[38;5;32m"; // BLUE - SPECIAL
    public static final String PURPLE = "\033[0;35m"; // PURPLE
    public static final String CYAN = "\033[0;36m"; // CYAN
    public static final String WHITE = "\033[0;37m"; // WHITE
    public static final String GREY = "\033[0;90m"; // GREY

    // bold
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK
    public static final String RED_BACKGROUND = "\033[41m"; // RED
    public static final String PURPLE_BACKGROUND = "\033[48;5;99m"; // PURPLE - SPECIAL

    // High Intensity backgrounds
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m"; // GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m"; // YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m"; // BLUE
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m"; // Bright Red
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // Bright Purple

    // Helper functions for colors
    public static String black(String text) {
        return BLACK + text + RESET;
    }

    public static String red(String text) {
        return RED + text + RESET;
    }

    public static String green(String text) {
        return GREEN + text + RESET;
    }

    public static String yellow(String text) {
        return YELLOW + text + RESET;
    }

    public static String blue(String text) {
        return BLUE + text + RESET;
    }

    public static String purple(String text) {
        return PURPLE + text + RESET;
    }

    public static String white(String text) {
        return WHITE + text + RESET;
    }

    public static String grey(String text) {
        return GREY + text + RESET;
    }

    // Helper functions for background colors
    public static String blackBackground(String text) {
        return BLACK_BACKGROUND + text + RESET;
    }

    public static String redBackground(String text) {
        return RED_BACKGROUND + text + RESET;
    }

    public static String brightGreenBackground(String text) {
        return GREEN_BACKGROUND_BRIGHT + text + RESET;
    }

    public static String brightYellowBackground(String text) {
        return YELLOW_BACKGROUND_BRIGHT + text + RESET;
    }

    public static String brightBlueBackground(String text) {
        return BLUE_BACKGROUND_BRIGHT + text + RESET;
    }

    public static String purpleBackground(String text) {
        return PURPLE_BACKGROUND + text + RESET;
    }
}
