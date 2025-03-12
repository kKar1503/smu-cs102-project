package parade.engine.impl;

import parade.common.*;
import parade.engine.GameEngine;
import parade.logger.Logger;
import parade.logger.LoggerProvider;
import parade.player.Player;
import parade.player.human.LocalHuman;
import parade.renderer.text.TextRenderer;
import parade.renderer.text.TextRendererProvider;

import java.util.*;

/**
 * Represents the game server for the Parade game. Manages players, the deck, the parade, and game
 * flow.
 */
public class LocalGameEngine extends GameEngine {
    private final Logger logger;
    private final TextRenderer textRenderer;

    /** Initializes the game server with a deck. */
    public LocalGameEngine() {
        logger = LoggerProvider.getInstance();
        textRenderer = TextRendererProvider.getInstance();
    }

    @Override
    public void addPlayer(Player player) {
        super.addPlayer(player);
        logger.logf("Player %s added to the game", player.getName());
    }

    private void waitForPlayersLobby() {
        logger.logf("Waiting for players to join lobby");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            textRenderer.renderPlayersLobby(getPlayers());
            int input = scanner.nextInt();
            scanner.nextLine();
            if (input == 1) {
                logger.log("Adding a new player");
                if (isLobbyFull()) {
                    textRenderer.renderln("Lobby is full.");
                    continue;
                }
                textRenderer.render("Enter player name: ");
                String name = scanner.nextLine();
                addPlayer(new LocalHuman(name));
            } else if (input == 2) {
                if (!lobbyHasEnoughPlayers()) {
                    textRenderer.renderln("Lobby does not have enough players.");
                    continue;
                }
                logger.log("User requested to start the game");
                return;
            }
        }
    }

    /**
     * Starts the game loop and manages game progression.
     *
     * @throws IllegalStateException if there are less than 2 players
     */
    @Override
    public void start() throws IllegalStateException {
        waitForPlayersLobby();

        if (lobbyHasEnoughPlayers()) {
            logger.logf("Insufficient players to start the game, found %d", getPlayersCount());
            throw new IllegalStateException("Server requires at least two players");
        }

        logger.logf("Starting game with %d players", getPlayersCount());

        // Gives out card to everyone
        int numCardsToDraw = INITIAL_CARDS_PER_PLAYER * getPlayersCount();
        logger.logf("Dealing %d cards to %d players", numCardsToDraw, getPlayersCount());
        List<Card> drawnCards = drawFromDeck(numCardsToDraw); // Draw all the cards first
        logger.log("Drawn cards: " + Arrays.toString(drawnCards.toArray()));
        // Dish out the cards one by one, like real life you know? Like not getting the direct next
        // card but alternating between players
        for (int i = 0; i < getPlayersCount(); i++) {
            Player player = getPlayer(i);
            for (int j = 0; j < INITIAL_CARDS_PER_PLAYER; j++) {
                Card drawnCard = drawnCards.get(i + getPlayersCount() * j);
                player.draw(drawnCard);
                logger.logf("%s drew: %s", player.getName(), drawnCard);
            }
        }

        // Game loop continues until the deck is empty or an end condition is met
        logger.log("Game loop starting");
        while (shouldGameContinue()) {
            // Each player plays a card
            Player player = getCurrentPlayer();

            // Draw a card from the deck for the player
            Card drawnCard = drawFromDeck();
            player.draw(drawnCard);
            logger.logf("%s drew: %s", player.getName(), drawnCard);

            playerPlayCard(player); // Play a card from their hand
            nextPlayer();
        }
        logger.logf("Game loop finished");

        // After the game loop finishes, the extra round is played.
        logger.log("Game loop finished, running final round");
        textRenderer.renderln("Final round started. Players do not draw a card.");
        for (int i = 0; i < getPlayersCount(); i++) {
            playerPlayCard(getCurrentPlayer());
            nextPlayer();
        }

        logger.log("Tabulating scores");
        Map<Player, Integer> playerScores = tabulateScores();

        // Declare the final results
        textRenderer.renderln("Game Over! Final Scores:");
        declareWinner(playerScores);
    }

    private void playerPlayCard(Player player) {
        // Playing card
        logger.logf("%s playing a card", player.getName());
        Card playedCard = player.playCard(getParadeCards());
        logger.logf("%s played and placed card into parade: %s", player.getName(), playedCard);
        textRenderer.renderln(player.getName() + " played: " + playedCard);

        // Place card in parade and receive cards from parade
        List<Card> cardsFromParade = placeCardInParade(playedCard); // Apply parade logic
        player.addToBoard(cardsFromParade);
        logger.logf(
                "%s received %d cards from parade to add to board: %s",
                player.getName(),
                cardsFromParade.size(),
                Arrays.toString(cardsFromParade.toArray()));
        textRenderer.renderf(
                "%s received %s from parade.%n",
                player.getName(), Arrays.toString(cardsFromParade.toArray()));
    }

    /** Declares the winner based on the lowest score. */
    private void declareWinner(Map<Player, Integer> playerScores) {
        Player winner = null;
        int lowestScore = Integer.MAX_VALUE;

        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() < lowestScore) {
                lowestScore = entry.getValue();
                winner = entry.getKey();
            }
        }

        if (winner != null) {
            textRenderer.render(
                    "Winner: " + winner.getName() + " with " + lowestScore + " points!");
        } else {
            textRenderer.render("The game ended in a tie!");
        }
    }
}
