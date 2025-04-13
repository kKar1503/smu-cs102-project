package parade.utils;

public enum Ansi {
    // Cursor
    HIDE_CURSOR("\033[?25l"), // Hide cursor
    SHOW_CURSOR("\033[?25h"), // Show cursor

    // Cursor Position
    MOVE_CURSOR_TO_TOP_LEFT("\033[H"), // Move cursor to top left
    MOVE_CURSOR_TO_FORMAT("\033[%d;%dH"), // Move cursor to specific position
    MOVE_CURSOR_LEFT("\033[1D"), // Move cursor left
    MOVE_CURSOR_RIGHT("\033[1C"), // Move cursor right
    MOVE_CURSOR_UP("\033[1A"), // Move cursor up
    MOVE_CURSOR_DOWN("\033[1B"), // Move cursor down

    // Reset
    RESET("\033[0m"), // Text Reset

    // Clear
    CLEAR("\033[H\033[2J"), // Clear console

    // Regular Colors
    BLACK("\033[30m"), // BLACK

    RED("\033[31m"), // RED
    GREEN("\033[32m"), // GREEN
    YELLOW("\033[33m"), // YELLOW
    BLUE("\033[38;5;32m"), // BLUE - SPECIAL
    PURPLE("\033[35m"), // PURPLE
    CYAN("\033[36m"), // CYAN
    WHITE("\033[37m"), // WHITE
    GREY("\033[90m"), // GREY

    // bold
    PURPLE_BOLD("\033[1;35m"), // PURPLE

    // Background
    BLACK_BACKGROUND("\033[40m"), // BLACK
    RED_BACKGROUND("\033[41m"), // RED
    PURPLE_BACKGROUND("\033[48;5;99m"), // PURPLE - SPECIAL
    WHITE_BACKGROUND("\033[47m"), // White

    // High Intensity backgrounds
    GREEN_BACKGROUND_BRIGHT("\033[102m"), // GREEN
    YELLOW_BACKGROUND_BRIGHT("\033[103m"), // YELLOW
    BLUE_BACKGROUND_BRIGHT("\033[104m"), // BLUE
    RED_BACKGROUND_BRIGHT("\033[101m"), // Bright Red
    PURPLE_BACKGROUND_BRIGHT("\033[105m"); // Bright Purple

    private final String code;

    Ansi(String code) {
        this.code = code;
    }

    public String apply(String s) {
        return code + s + RESET.code;
    }

    public static String apply(String s, Ansi... ansis) {
        StringBuilder sb = new StringBuilder();
        for (Ansi ansi : ansis) {
            sb.append(ansi.code);
        }
        sb.append(s).append(RESET.code);
        return sb.toString();
    }

    @Override
    public String toString() {
        return code;
    }
}
