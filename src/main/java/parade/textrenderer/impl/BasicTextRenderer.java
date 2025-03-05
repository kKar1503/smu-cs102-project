package parade.textrenderer.impl;

import java.util.Comparator;

import parade.common.Card;
import parade.common.Colour;
import parade.player.Player;
import parade.textrenderer.TextRenderer;

import java.util.List;
import java.util.Map;
import java.util.Collections;

public class BasicTextRenderer implements TextRenderer {
    public BasicTextRenderer() {}

    @Override
    public void render(String message) {
        System.out.println(message);
    }

    @Override
    public void renderWelcome() {
        System.out.println("Welcome to Parade!");
    }

    @Override
    public void renderMenu() {
        System.out.println("1. Start Game");
        System.out.println("2. Exit");
        System.out.print("Please select an option: ");
    }

    @Override
    public void renderPlayersLobby(List<Player> players) {
        System.out.println("Players in lobby: ");
        for (int i = 1; i <= players.size(); i++) {
            System.out.printf("%d. %s\n", i, players.get(i - 1).getName());
        }
        System.out.println();
        System.out.println("1. Add Player" + (players.size() == 6 ? " (Lobby is full)" : ""));
        System.out.println("2. Start Game");
        System.out.print("Please select an option: ");
    }

    @Override
    public void renderPlayerTurn(Player player, Card newlyDrawnCard, List<Card> parade) {
        // print player's name and drawn card
        System.out.println("\n" + player.getName() + "'s turn.");
        if (newlyDrawnCard != null) {
            System.out.println("You drew: [" + newlyDrawnCard.getNumber() + " " 
                            + newlyDrawnCard.getColour() + "]");
        }
        // print cards in parade
        System.out.println("\nParade\n======================================================================");
        for (Card card : parade) {
            System.out.print((parade.indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        // sort board and print 
        List<Card> board = player.getBoard();
        Collections.sort(board, Comparator.comparing(Card::getColour)
            .thenComparing(Card::getNumber));
        System.out.println("\n\nYour board\n===========================================================================");
        for (Card card : board) {
            System.out.print(printCards(card) + " ");
        }
        // print player's hand
        System.out.println("\n\nYour hand\n==========================================================================");
        for (Card card : player.getHand()) {
            System.out.print((player.getHand().indexOf(card) + 1) + "." + printCards(card) + "  ");
        }
        System.out.print("\n\nSelect a card to play:");
    }


    @Override
    public void renderEndGame(Map<Player, Integer> playerScores) {
        System.out.println("Game Over!");
        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            System.out.println(entry.getKey().getName() + ": " + entry.getValue());
        }
    }

    @Override
    public void renderSinglePlayerEndGame(Player player, int score) {
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
}
