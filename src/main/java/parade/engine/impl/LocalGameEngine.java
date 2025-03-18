package parade.engine.impl;

import parade.common.*;
import parade.engine.AbstractGameEngine;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.player.IPlayer;
import parade.player.computer.EasyComputer;
import parade.player.computer.HardComputer;
import parade.player.computer.NormalComputer;
import parade.player.human.LocalHuman;
import parade.renderer.IClientRenderer;
import parade.renderer.ClientRendererProvider;
import parade.renderer.impl.AdvancedClientRenderer;
import parade.renderer.impl.BasicLocalClientRenderer;
import parade.settings.SettingKey;
import parade.settings.Settings;

import java.util.*;

/**
 * Represents the game server for the Parade game. Manages players, the deck, the parade, and game
 * flow.
 */
public class LocalGameEngine extends AbstractGameEngine {
    private final AbstractLogger logger;
    private final IClientRenderer clientRenderer;
    private final Scanner scanner;

    /** Initializes the game server with a deck. */
    public LocalGameEngine() {
        logger = LoggerProvider.getInstance();
        scanner = new Scanner(System.in);
        clientRenderer = setupClientRenderer();
    }

    @Override
    public void addPlayer(IPlayer player) {
        super.addPlayer(player);
        logger.logf("Player %s added to the game", player.getName());
    }

    @Override
    public boolean removePlayer(IPlayer player) {
        logger.logf("Player %s removed from the game", player.getName());
        return super.removePlayer(player);
    }

    @Override
    public IPlayer removePlayer(int index) {
        IPlayer removedPlayer = super.removePlayer(index);
        logger.logf("Player %s removed from the game", removedPlayer.getName());
        return removedPlayer;
    }

    private void chooseComputerDifficulty(String name) {
        while (true) {
            try {
                clientRenderer.renderComputerDifficulty();
                int compInput = scanner.nextInt();
                if (compInput == 1) {
                    addPlayer(new EasyComputer(getParadeCards(), name));
                } else if (compInput == 2) {
                    addPlayer(new NormalComputer(getParadeCards(), name));
                } else if (compInput == 3) {
                    addPlayer(new HardComputer(getParadeCards(), name));
                } else {
                    throw new NoSuchElementException();
                }
                return;
            } catch (NoSuchElementException e) {
                logger.log("User entered invalid input", e);
                clientRenderer.renderln("Input not found, please try again");
                scanner.nextLine();
            }
        }
    }

    private void addComputerDisplay() {
        while (true) {
            clientRenderer.render("Enter computer's name:");
            try {
                String name = scanner.nextLine();
                chooseComputerDifficulty(name);
                return;
            } catch (NoSuchElementException e) {
                logger.log("User entered invalid input", e);
                clientRenderer.renderln("Invalid input, please try again");
            } finally {
                scanner.nextLine();
            }
        }
    }

    private void removePlayerDisplay() {
        while (true) {
        int count = 1;
        clientRenderer.renderln("Select a player to remove.");
        for (IPlayer player : getPlayers()) {
            clientRenderer.renderln(count + ". " + player.getName());
            count++;
        }
        clientRenderer.renderln(count + ". Return back to main menu");
        int input;
        try {
            input = scanner.nextInt();
            if (input < 1 || input > getPlayersCount() + 1) {
                throw new NoSuchElementException();
            }
            if (input == count) {
                return;
            }
            removePlayer(input - 1);
            return;
        } catch (NoSuchElementException e) {
            logger.log("User entered invalid input", e);
            clientRenderer.renderln("Invalid input, please try again");
        } finally {
            scanner.nextLine();
        }
    }
        
    }

    private void waitForPlayersLobby() {
        logger.logf("Waiting for players to join lobby");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            clientRenderer.renderPlayersLobby(getPlayers());
            int input;
            try {
                input = scanner.nextInt();
                scanner.nextLine();
                if (input == 1) {
                    logger.log("Adding a new player");
                    if (isLobbyFull()) {
                        clientRenderer.renderln("Lobby is full.");
                        continue;
                    }
                    clientRenderer.render("Enter player name: ");
                    String name = scanner.nextLine();
                    addPlayer(new LocalHuman(name));
                } else if (input == 2) {
                    logger.log("Adding a new computer");
                    if (isLobbyFull()) {
                        clientRenderer.renderln("Lobby is full.");
                        continue;
                    }
                    addComputerDisplay();
                } else if (input == 3) {
                    if (isLobbyEmpty()) {
                        clientRenderer.renderln("Lobby has no players.");
                        continue;
                    }
                    logger.log("Removing a player from lobby");
                    removePlayerDisplay();
                } else if (input == 4) {
                    if (!lobbyHasEnoughPlayers()) {
                        clientRenderer.renderln("Lobby does not have enough players.");
                        continue;
                    }
                    logger.log("User requested to start the game");
                    return;
                } else {
                    clientRenderer.renderln("Invalid input, please enter only 1 to 4.");
                    logger.log("User entered invalid input");
                }
            } catch (NoSuchElementException e) {
                logger.log("User entered invalid input", e);
                clientRenderer.renderln("Invalid input, please try again");
                scanner.nextLine();
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
        clientRenderer.renderWelcome();
        logger.log("Prompting user to start game in menu");
        while (true) {
            clientRenderer.renderMenu();
            try {
                int input = scanner.nextInt();
                if (input != 1 && input != 2) {
                    clientRenderer.renderln("Invalid input, please only type only 1 or 2.");
                    continue;
                }

                if (input == 1) {
                    logger.log("User is starting the game");
                    break;
                } else {
                    logger.log("User is exiting the game");
                    clientRenderer.renderBye();
                    return;
                }
            } catch (NoSuchElementException e) {
                logger.log("Invalid input received", e);
                clientRenderer.renderln("Invalid input, please try again.");
            } finally {
                scanner.nextLine();
            }
        }

        waitForPlayersLobby();

        if (!lobbyHasEnoughPlayers()) {
            logger.logf("Insufficient players to start the game, found %d", getPlayersCount());
            throw new IllegalStateException("Server requires at least two players");
        }

        logger.logf("Starting game with %d players", getPlayersCount());

        // Gives out card to everyone
        int numCardsToDraw = INITIAL_CARDS_PER_PLAYER * getPlayersCount();
        logger.logf("Dealing %d cards to %d players", numCardsToDraw, getPlayersCount());
        List<Card> drawnCards = drawFromDeck(numCardsToDraw); // Draw all the cards first
        logger.log("Drawn cards: " + Arrays.toString(drawnCards.toArray()));
        // Dish out the cards one by one, like real life you know? Like not getting the
        // direct next
        // card but alternating between players
        for (int i = 0; i < getPlayersCount(); i++) {
            IPlayer player = getPlayer(i);
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
            IPlayer player = getCurrentPlayer();

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
        clientRenderer.renderln("Final round started. Players do not draw a card.");
        for (int i = 0; i < getPlayersCount(); i++) {
            playerPlayCard(getCurrentPlayer());
            nextPlayer();
        }

        logger.log("Tabulating scores");
        Map<IPlayer, Integer> playerScores = tabulateScores();

        // Declare the final results
        clientRenderer.renderln("Game Over! Final Scores:");
        declareWinner(playerScores);
    }

    private void playerPlayCard(IPlayer player) {
        // Playing card
        logger.logf("%s playing a card", player.getName());
        Card playedCard = player.playCard(getParadeCards());
        logger.logf("%s played and placed card into parade: %s", player.getName(), playedCard);
        clientRenderer.renderln(player.getName() + " played: " + playedCard);

        // Place card in parade and receive cards from parade
        List<Card> cardsFromParade = placeCardInParade(playedCard); // Apply parade logic
        player.addToBoard(cardsFromParade);
        logger.logf(
                "%s received %d cards from parade to add to board: %s",
                player.getName(),
                cardsFromParade.size(),
                Arrays.toString(cardsFromParade.toArray()));
        clientRenderer.renderf(
                "%s received %s from parade.%n",
                player.getName(), Arrays.toString(cardsFromParade.toArray()));
    }

    /** Declares the winner based on the lowest score. */
    private void declareWinner(Map<IPlayer, Integer> playerScores) {
        IPlayer winner = null;
        int lowestScore = Integer.MAX_VALUE;

        for (Map.Entry<IPlayer, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() < lowestScore) {
                lowestScore = entry.getValue();
                winner = entry.getKey();
            }
        }

        if (winner != null) {
            clientRenderer.render(
                    "Winner: " + winner.getName() + " with " + lowestScore + " points!");
        } else {
            clientRenderer.render("The game ended in a tie!");
        }
    }

    /**
     * Sets up the client renderer.
     *
     * @return the client renderer
     */
    private IClientRenderer setupClientRenderer() {
        Settings settings = Settings.getInstance();

        String clientRendererType = settings.get(SettingKey.CLIENT_RENDERER);

        IClientRenderer clientRenderer =
                switch (clientRendererType) {
                    case "basic_local" -> new BasicLocalClientRenderer();
                    case "advanced_local" -> new AdvancedClientRenderer();
                    case "basic_network" ->
                            throw new UnsupportedOperationException(
                                    "Basic network renderer is not supported for local game");
                    default ->
                            throw new IllegalStateException(
                                    "Unknown client renderer in settings: " + clientRendererType);
                };
        logger.log("Gameplay client renderer is using " + clientRendererType);

        ClientRendererProvider.setInstance(clientRenderer);
        logger.log("Initialised client renderer");

        return clientRenderer;
    }
}
