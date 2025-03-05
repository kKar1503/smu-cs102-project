package parade.textrenderer.impl;

import parade.common.Card;
import parade.player.Player;
import parade.textrenderer.TextRenderer;

import java.util.List;
import java.util.Map;

public class BasicTextRenderer implements TextRenderer {
    public BasicTextRenderer() {}

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
    public void renderPlayerTurn(Player player, Card newlyDrawnCard, List<Card> parade) {
        System.out.println("hehe" /* TODO: replace this with player.getName()*/ + "'s turn.");
        System.out.println("You drew: " + newlyDrawnCard);
        System.out.println("Parade: " + parade);
        System.out.print("Your hand: " + player.getHand() + "\nSelect a card to play: ");
    }

    @Override
    public void renderEndGame(Map<Player, Integer> playerScores) {
        System.out.println("Game Over!");
        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            System.out.println(
                    "hehe" /* TODO: replace this with player.getName()*/ + ": " + entry.getValue());
        }
    }

    @Override
    public void renderSinglePlayerEndGame(Player player, int score) {
        System.out.println(
                "Game Over, " + "hehe" /* TODO: replace this with player.getName()*/ + "!");
        System.out.println("Your score: " + score);
    }

    @Override
    public void renderBye() {
        System.out.println("Bye bye buddy.");
    }
}
