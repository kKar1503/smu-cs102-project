package parade.utils;

public class ConsoleColors {
    // Reset
    public static final String RESET = "\033[0m"; // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m"; // BLACK
    public static final String RED = "\033[0;31m"; // RED
    public static final String GREEN = "\033[0;32m"; // GREEN
    public static final String YELLOW = "\033[0;33m"; // YELLOW
    public static final String BLUE = "\033[0;34m"; // BLUE
    public static final String PURPLE = "\033[0;35m"; // PURPLE
    public static final String CYAN = "\033[0;36m"; // CYAN
    public static final String WHITE = "\033[0;37m"; // WHITE
    public static final String GREY = "\033[0;90m"; // GREY

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m"; // BLACK
    public static final String RED_BOLD = "\033[1;31m"; // RED
    public static final String GREEN_BOLD = "\033[1;32m"; // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m"; // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m"; // CYAN
    public static final String WHITE_BOLD = "\033[1;37m"; // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m"; // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m"; // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m"; // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m"; // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m"; // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m"; // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m"; // BLACK
    public static final String RED_BACKGROUND = "\033[41m"; // RED
    public static final String GREEN_BACKGROUND = "\033[42m"; // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m"; // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m"; // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m"; // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m"; // BLACK
    public static final String RED_BRIGHT = "\033[0;91m"; // RED
    public static final String GREEN_BRIGHT = "\033[0;92m"; // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m"; // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m"; // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m"; // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m"; // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m"; // YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m"; // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m"; // PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m"; // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m"; // BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m"; // RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m"; // GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m"; // YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m"; // BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m"; // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m"; // WHITE

    // Helper functions for colors
    public static String black(String text) { return BLACK + text + RESET; }
    public static String red(String text) { return RED + text + RESET; }
    public static String green(String text) { return GREEN + text + RESET; }
    public static String yellow(String text) { return YELLOW + text + RESET; }
    public static String blue(String text) { return BLUE + text + RESET; }
    public static String purple(String text) { return PURPLE + text + RESET; }
    public static String cyan(String text) { return CYAN + text + RESET; }
    public static String white(String text) { return WHITE + text + RESET; }
    public static String grey(String text) { return GREY + text + RESET; }

    // Helper functions for bold colors
    public static String blackBold(String text) { return BLACK_BOLD + text + RESET; }
    public static String redBold(String text) { return RED_BOLD + text + RESET; }
    public static String greenBold(String text) { return GREEN_BOLD + text + RESET; }
    public static String yellowBold(String text) { return YELLOW_BOLD + text + RESET; }
    public static String blueBold(String text) { return BLUE_BOLD + text + RESET; }
    public static String purpleBold(String text) { return PURPLE_BOLD + text + RESET; }
    public static String cyanBold(String text) { return CYAN_BOLD + text + RESET; }
    public static String whiteBold(String text) { return WHITE_BOLD + text + RESET; }

    // Helper functions for background colors
    public static String blackBackground(String text) { return BLACK_BACKGROUND + text + RESET; }
    public static String redBackground(String text) { return RED_BACKGROUND + text + RESET; }
    public static String greenBackground(String text) { return GREEN_BACKGROUND + text + RESET; }
    public static String yellowBackground(String text) { return YELLOW_BACKGROUND + text + RESET; }
    public static String blueBackground(String text) { return BLUE_BACKGROUND + text + RESET; }
    public static String purpleBackground(String text) { return PURPLE_BACKGROUND + text + RESET; }
    public static String cyanBackground(String text) { return CYAN_BACKGROUND + text + RESET; }
    public static String whiteBackground(String text) { return WHITE_BACKGROUND + text + RESET; }

    // Helper functions for high intensity colors
    public static String redBright(String text) { return RED_BRIGHT + text + RESET; }
    public static String greenBright(String text) { return GREEN_BRIGHT + text + RESET; }
    public static String yellowBright(String text) { return YELLOW_BRIGHT + text + RESET; }
    public static String blueBright(String text) { return BLUE_BRIGHT + text + RESET; }
    public static String purpleBright(String text) { return PURPLE_BRIGHT + text + RESET; }
    public static String cyanBright(String text) { return CYAN_BRIGHT + text + RESET; }
    public static String whiteBright(String text) { return WHITE_BRIGHT + text + RESET; }
}
