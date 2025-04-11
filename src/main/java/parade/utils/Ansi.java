package parade.utils;

public enum Ansi {
    // Reset
    RESET("\033[0m"), // Text Reset

    // Clear
    CLEAR("\033[H\033[2J"), // Clear console

    // Regular Colors
    BLACK("\033[0;90m"), // BLACK - SPECIAL  \033[0;30m

    RED("\033[0;31m"), // RED
    GREEN("\033[0;32m"), // GREEN
    YELLOW("\033[0;33m"), // YELLOW
    BLUE("\033[38;5;32m"), // BLUE - SPECIAL
    PURPLE("\033[0;35m"), // PURPLE
    CYAN("\033[0;36m"), // CYAN
    WHITE("\033[0;37m"), // WHITE
    GREY("\033[0;90m"), // GREY

    // bold
    PURPLE_BOLD("\033[1;35m"), // PURPLE

    // Background
    BLACK_BACKGROUND("\033[40m"), // BLACK
    RED_BACKGROUND("\033[41m"), // RED
    PURPLE_BACKGROUND("\033[48;5;99m"), // PURPLE - SPECIAL
    WHITE_BACKGROUND("\033[47m"), // White

    // High Intensity backgrounds
    GREEN_BACKGROUND_BRIGHT("\033[0;102m"), // GREEN
    YELLOW_BACKGROUND_BRIGHT("\033[0;103m"), // YELLOW
    BLUE_BACKGROUND_BRIGHT("\033[0;104m"), // BLUE
    RED_BACKGROUND_BRIGHT("\033[0;101m"), // Bright Red
    PURPLE_BACKGROUND_BRIGHT("\033[0;105m"); // Bright Purple

    private final String code;

    Ansi(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String apply(String s) {
        return code + s + RESET.code;
    }

    public static String apply(String s, Ansi... ansis) {
        StringBuilder sb = new StringBuilder();
        for (Ansi ansi : ansis) {
            sb.append(ansi.getCode());
        }
        sb.append(s).append(RESET.getCode());
        return sb.toString();
    }
}
