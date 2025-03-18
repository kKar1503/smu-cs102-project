package parade.renderer.impl.display;

import parade.player.IPlayer;

import java.util.Map;

public class RenderEndGame {
    private final Map<IPlayer, Integer> playerScores;

    public RenderEndGame(Map<IPlayer, Integer> playerScores) {
        this.playerScores = playerScores;
    }

    public void displayEndGame() {
        try {
            for (int i = 0; i < 30; i++) {
                System.out.print("\033[H\033[2J");
                System.out.flush();

                System.out.println(
                        " ".repeat(i)
                                + " ██████╗  █████╗ ███╗   ███╗███████╗     ██████╗ ██╗  "
                                + " ██╗███████╗██████╗ ");
                System.out.println(
                        " ".repeat(i)
                                + "██╔════╝ ██╔══██╗████╗ ████║██╔════╝     ██╔══██╗██║  "
                                + " ██║██╔════╝██╔══██╗");
                System.out.println(
                        " ".repeat(i)
                                + "██║  ███╗███████║██╔████╔██║█████╗       ██║  ██║██║   ██║█████╗"
                                + "  ██████╔╝");
                System.out.println(
                        " ".repeat(i)
                                + "██║   ██║██╔══██║██║╚██╔╝██║██╔══╝       ██║  ██║██║   ██║██╔══╝"
                                + "  ██╔══██╗");
                System.out.println(
                        " ".repeat(i)
                                + "╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗    "
                                + " ██████╔╝╚██████╔╝███████╗██║  ██║");
                System.out.println(
                        " ".repeat(i)
                                + " ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝     ╚═════╝  ╚═════╝"
                                + " ╚══════╝╚═╝  ╚═╝");

                Thread.sleep(100);
            }

            for (int i = 0; i < 6; i++) {
                System.out.print("\033[H\033[2J");

                System.out.println("\n\033[5m");
                System.out.println("      =============================================");
                System.out.println("      ||    ███████╗██╗███╗   ██╗ █████╗ ██╗     ||");
                System.out.println("      ||    ██╔════╝██║████╗  ██║██╔══██╗██║     ||");
                System.out.println("      ||    █████╗  ██║██╔██╗ ██║███████║██║     ||");
                System.out.println("      ||    ██╔══╝  ██║██║╚██╗██║██╔══██║██║     ||");
                System.out.println("      ||    ██║     ██║██║ ╚████║██║  ██║███████╗||");
                System.out.println("      ||    ╚═╝     ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝╚══════╝||");
                System.out.println("      =============================================");
                System.out.println("\033[0m");
            }

            // Table Header
            System.out.println("        ┌──────────────────┬───────────┐");
            System.out.println("        │     Player       │  Score    │");
            System.out.println("        ├──────────────────┼───────────┤");

            // Display Player Scores in Table Format
            for (Map.Entry<IPlayer, Integer> entry : playerScores.entrySet()) {
                System.out.printf(
                        "       │ %-16s │ %7d   │\n", entry.getKey().getName(), entry.getValue());
            }

            // Table Footer
            System.out.println("        └──────────────────┴───────────┘");

            // Goodbye Message
            System.out.println("\n      THANK YOU FOR PLAYING! SEE YOU NEXT TIME!\n");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
