package parade.renderer.impl;

import parade.common.Card;
import parade.common.Colour;
import parade.engine.AbstractGameEngine;
import parade.player.IPlayer;
import parade.renderer.IClientRenderer;
import parade.utils.ConsoleColors;

import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.swing.*;

public class AdvancedClientRenderer implements IClientRenderer {
    public AdvancedClientRenderer() {}

    @Override
    public void render(String message) {
        System.out.print(message);
    }

    @Override
    public void renderln(String message) {
        System.out.println(message);
    }

    @Override
    public void renderf(String format, Object... args) {
        System.out.printf(format, args);
    }

    @Override
    public void renderWelcome() throws IllegalStateException {
        // the stream holding the file content
        InputStream inFromFile =
                getClass().getClassLoader().getResourceAsStream("parade_ascii_art.txt");
        if (inFromFile == null) {
            throw new IllegalStateException("parade_ascii_art.txt not found");
        }
        Scanner s = new Scanner(inFromFile).useDelimiter("\\Z");
        String paradeWelcome = s.hasNext() ? s.next() : "";

        if (paradeWelcome != null) {
            System.out.println(
                    ConsoleColors.PURPLE_BOLD
                            + "============================= Welcome to Parade!"
                            + " =============================="
                            + ConsoleColors.RESET);
            System.out.println(ConsoleColors.PURPLE + paradeWelcome + ConsoleColors.RESET);
            System.out.println(
                    "===================================================================================");
        }
    }

    @Override
    public void renderMenu() {
        System.out.println("1. Start Game");
        System.out.println("2. Exit");
        System.out.print("Please select an option: ");
    }

    @Override
    public void renderPlayersLobby(List<IPlayer> players) {
        System.out.println("Players in lobby: ");
        for (int i = 1; i <= players.size(); i++) {
            System.out.printf("%d. %s\n", i, players.get(i - 1).getName());
        }
        System.out.println();
        System.out.println(
                "1. Add Player"
                        + (players.size() == AbstractGameEngine.MAX_PLAYERS
                                ? " (Lobby is full)"
                                : ""));
        System.out.println(
                "2. Start Game"
                        + (players.size() < AbstractGameEngine.MIN_PLAYERS
                                ? " (Not enough players)"
                                : ""));
        System.out.print("Please select an option: ");
    }

    @Override
    public void renderComputerDifficulty() {}
    ;

    @Override
    public void renderPlayerTurn(IPlayer player, Card newlyDrawnCard, List<Card> parade) {
        // print player's name and drawn card
        System.out.println("\n" + player.getName() + "'s turn.");
        if (newlyDrawnCard != null) {
            System.out.println(
                    "You drew: ["
                            + newlyDrawnCard.getNumber()
                            + " "
                            + newlyDrawnCard.getColour()
                            + "]");
        }
        // print cards in parade
        System.out.println(
                "\nParade\n======================================================================");
        for (Card card : parade) {
            System.out.print((parade.indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        // sort board and print
        List<Card> board = player.getBoard();
        board.sort(Comparator.comparing(Card::getColour).thenComparing(Card::getNumber));
        System.out.println(
                "\n\n"
                    + "Your board\n"
                    + "===========================================================================");
        for (Card card : board) {
            System.out.print(printCards(card) + " ");
        }
        // print player's hand
        System.out.println(
                "\n\n"
                    + "Your hand\n"
                    + "==========================================================================");
        for (Card card : player.getHand()) {
            System.out.print((player.getHand().indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        System.out.print("\n\nSelect a card to play:");
    }

    @Override
    public void renderEndGame(Map<IPlayer, Integer> playerScores) {
        System.out.println("Game Over!");
        for (Map.Entry<IPlayer, Integer> entry : playerScores.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue());
        }
    }

    @Override
    public void renderSinglePlayerEndGame(IPlayer player, int score) {
        System.out.println("Game Over, " + player.getName() + "!");
        System.out.println("Your score: " + score);
    }

    @Override
    public void renderBye() {
        System.out.println("Bye bye buddy.");
    }

    public String printCards(Card card) {
        return "[" + card.getNumber() + " " + card.getColour() + "]";
    }

    public String rendersSingleCard(Card card) {
        String colorCode;

        switch (card.getColour() + "") {
            case "RED":
                colorCode = ConsoleColors.RED_BACKGROUND_BRIGHT;
                break;
            case "BLUE":
                colorCode = ConsoleColors.BLUE_BACKGROUND_BRIGHT;
                break;
            case "GREEN":
                colorCode = ConsoleColors.GREEN_BACKGROUND_BRIGHT;
                break;
            case "YELLOW":
                colorCode = ConsoleColors.YELLOW_BACKGROUND_BRIGHT;
                break;
            case "PURPLE":
                colorCode = ConsoleColors.PURPLE_BACKGROUND_BRIGHT;
                break;
            default:
                colorCode = ConsoleColors.BLACK_BACKGROUND;
                break;
        }

        String reset = ConsoleColors.RESET;
        String numberStr = String.valueOf(card.getNumber());
        String colorName = "" + card.getColour();
        int width = 18;

        String top = "┌" + "─".repeat(width) + "┐";
        String bottom = "└" + "─".repeat(width) + "┘";
        String line1 = String.format("│ %-2s%s│", numberStr, " ".repeat(width - 3));
        String line2 = String.format("│%s│", " ".repeat(width));
        String line3 =
                String.format(
                        "│%"
                                + ((width + colorName.length()) / 2)
                                + "s%"
                                + ((width - colorName.length()) / 2)
                                + "s │",
                        colorName,
                        "");
        String line4 = line2;
        String line5 = String.format("│%s%2s │", " ".repeat(width - 3), numberStr);
        String pikachu1 = String.format("│%-18s│", "      /\\__/\\");
        String pikachu2 = String.format("│%-18s│", "     | @ . @|");
        String pikachu3 = String.format("│%-18s│", "      \\  -  /");
        String pikachu4 = String.format("│%-18s│", "  ////|     |\\\\\\\\");
        String pikachu5 = String.format("│%-18s│", "   ==\\|__|__|/==");

        return String.join(
                "\n",
                colorCode + top + reset,
                colorCode + line1 + reset,
                colorCode + line2 + reset,
                colorCode + line3 + reset,
                colorCode + pikachu1 + reset,
                colorCode + pikachu2 + reset,
                colorCode + pikachu3 + reset,
                colorCode + pikachu4 + reset,
                colorCode + pikachu5 + reset,
                colorCode + line4 + reset,
                colorCode + line5 + reset,
                colorCode + bottom + reset);
    }

    public void renderRoll() {
        String[] block = {
            "╔══════════╗",
            "║          ║",
            "║ ROLLING  ║",
            "║ DICE :)  ║",
            "║          ║",
            "╚══════════╝"
        };

        System.out.println("Shaking block...\n");

        for (int i = 0; i < 15; i++) {
            int offset = (int) (Math.random() * 6); // random indent 0–5
            printBlockWithOffset(block, offset);

            try {
                Thread.sleep(100); // short delay for shaking effect
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            clearConsole();
        }
    }

    private void printBlockWithOffset(String[] block, int offset) {
        String space = " ".repeat(offset);
        for (String line : block) {
            System.out.println(space + line);
        }
    }

    // This just prints many new lines to "clear" the screen for shaking effect
    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private String returnDice(int num) {
        String[] toPrint = {};

        String whiteBg = "\u001B[47m";
        String blackText = "\u001B[30m";
        String reset = "\u001B[0m";
        
        String[] dice1 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║         ║" + reset,
            whiteBg + blackText + "║    o    ║" + reset,
            whiteBg + blackText + "║         ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };
        

        String[] dice2 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o       ║" + reset,
            whiteBg + blackText + "║         ║" + reset,
            whiteBg + blackText + "║       o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };
        
        String[] dice3 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o       ║" + reset,
            whiteBg + blackText + "║    o    ║" + reset,
            whiteBg + blackText + "║       o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };
        
        String[] dice4 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "║         ║" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };
        
        String[] dice5 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "║    o    ║" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };
        
        String[] dice6 = {
            whiteBg + blackText + "╔═════════╗" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "║ o     o ║" + reset,
            whiteBg + blackText + "╚═════════╝" + reset
        };
        
        switch(num){
            case 1:
                toPrint = dice1;
                break;
            case 2:
                toPrint = dice2;
                break;
            case 3:
                toPrint = dice3;
                break;
            case 4:
                toPrint = dice4;
                break;
            case 5:
                toPrint = dice5;
                break;
            case 6:
                toPrint = dice6;
                break;
        }

        return String.join("\n", Arrays.stream(toPrint)
                .toArray(String[]::new));
    }
}
