package parade.core;

import parade.card.Card;
import parade.computer.EasyComputerEngine;
import parade.computer.HardComputerEngine;
import parade.computer.NormalComputerEngine;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.player.controller.AbstractPlayerController;
import parade.player.controller.ComputerController;
import parade.player.controller.HumanController;
import parade.player.controller.PlayCardData;
import parade.renderer.local.ClientRendererProvider;
import parade.renderer.local.IClientRenderer;
import parade.renderer.local.impl.AdvancedClientRenderer;
import parade.renderer.local.impl.BasicLocalClientRenderer;
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
        super();
        logger = LoggerProvider.getInstance();
        scanner = new Scanner(System.in);
        clientRenderer = setupClientRenderer();
    }

    private void addPlayerController(AbstractPlayerController player) {
        playerControllerManager.add(player);
        logger.logf("Player %s added to the game", player.getPlayer().getName());
    }

    private boolean removePlayerController(AbstractPlayerController player) {
        boolean removed = playerControllerManager.remove(player);
        if (removed) {
            logger.logf("Player %s removed from the game", player.getPlayer().getName());
        } else {
            logger.logf("Player %s not found in the game", player.getPlayer().getName());
        }
        return removed;
    }

    private AbstractPlayerController removePlayerController(int index) {
        AbstractPlayerController removedPlayer = playerControllerManager.remove(index);
        if (removedPlayer != null) {
            logger.logf("Player %s removed from the game", removedPlayer.getPlayer().getName());
        } else {
            logger.logf("Player at index %d is null", index);
        }
        return removedPlayer;
    }

    private void chooseComputerDifficulty(String name) {
        while (true) {
            try {
                clientRenderer.renderComputerDifficulty();
                int compInput = scanner.nextInt();
                if (compInput == 1) {
                    addPlayerController(new ComputerController(new EasyComputerEngine()));
                } else if (compInput == 2) {
                    addPlayerController(new ComputerController(new NormalComputerEngine()));
                } else if (compInput == 3) {
                    addPlayerController(new ComputerController(new HardComputerEngine()));
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
            for (AbstractPlayerController player : playerControllerManager.getPlayerControllers()) {
                clientRenderer.renderln(count + ". " + player.getPlayer().getName());
                count++;
            }
            clientRenderer.renderln(count + ". Return back to main menu");
            int input;
            try {
                input = scanner.nextInt();
                if (input < 1 || input > playerControllerManager.size() + 1) {
                    throw new NoSuchElementException();
                }
                if (input == count) {
                    return;
                }
                removePlayerController(input - 1);
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
            clientRenderer.renderPlayersLobby(playerControllerManager.getPlayers());
            int input = 0;
            try {
                input = scanner.nextInt();
                scanner.nextLine();
                if (input == 1) {
                    logger.log("Adding a new player");
                    if (playerControllerManager.isFull()) {
                        clientRenderer.renderln("Lobby is full.");
                        continue;
                    }
                    clientRenderer.render("Enter player name: ");
                    String name = scanner.nextLine();
                    addPlayerController(new HumanController(name));
                } else if (input == 2) {
                    logger.log("Adding a new computer");
                    if (playerControllerManager.isFull()) {
                        clientRenderer.renderln("Lobby is full.");
                        continue;
                    }
                    addComputerDisplay();
                } else if (input == 3) {
                    if (playerControllerManager.isEmpty()) {
                        clientRenderer.renderln("Lobby has no players.");
                        continue;
                    }
                    logger.log("Removing a player from lobby");
                    removePlayerDisplay();
                } else if (input == 4) {
                    if (!playerControllerManager.isReady()) {
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

        if (!playerControllerManager.isReady()) {
            logger.logf(
                    "Insufficient players to start the game, found %d",
                    playerControllerManager.size());
            throw new IllegalStateException("Server requires at least two players");
        }

        logger.logf("Starting game with %d players", playerControllerManager.size());

        // Gives out card to everyone
        int numCardsToDraw = INITIAL_CARDS_PER_PLAYER * playerControllerManager.size();
        logger.logf(
                "Dealing %d cards to %d players", numCardsToDraw, playerControllerManager.size());
        List<Card> drawnCards = deck.pop(numCardsToDraw); // Draw all the cards first
        logger.log("Drawn cards: " + Arrays.toString(drawnCards.toArray()));
        // Dish out the cards one by one, like real life you know? Like not getting the
        // direct next
        // card but alternating between players
        List<AbstractPlayerController> playerControllers =
                playerControllerManager.getPlayerControllers();
        for (int i = 0; i < playerControllers.size(); i++) {
            for (int j = 0; j < INITIAL_CARDS_PER_PLAYER; j++) {
                AbstractPlayerController playerController = playerControllers.get(i);
                Card drawnCard = drawnCards.get(i + playerControllers.size() * j);
                playerController.getPlayer().addToHand(drawnCard);
                logger.logf("%s drew: %s", playerController.getPlayer().getName(), drawnCard);
            }
        }

        // Game loop continues until the deck is empty or an end condition is met
        logger.log("Game loop starting");
        while (shouldGameContinue()) {
            // Each player plays a card
            AbstractPlayerController player = playerControllerManager.next();

            // Draw a card from the deck for the player
            Card drawnCard = deck.pop();
            player.draw(drawnCard);
            logger.logf("%s drew: %s", player.getPlayer().getName(), drawnCard);

            playerPlayCard(
                    player,
                    new PlayCardData(
                            playerControllerManager.getPlayerControllers(),
                            parade,
                            deck.size())); // Play a card from their hand
        }
        logger.logf("Game loop finished");

        // After the game loop finishes, the extra round is played.
        logger.log("Game loop finished, running final round");
        clientRenderer.renderln("Final round started. Players do not draw a card.");
        for (int i = 0; i < playerControllers.size(); i++) {
            AbstractPlayerController player = playerControllerManager.next();
            playerPlayCard(
                    player,
                    new PlayCardData(
                            playerControllerManager.getPlayerControllers(),
                            parade,
                            deck.size())); // Play a card from their hand
        }

        logger.log("Tabulating scores");
        Map<AbstractPlayerController, Integer> playerScores = tabulateScores();

        // Declare the final results
        clientRenderer.renderln("Game Over! Final Scores:");
        declareWinner(playerScores);
    }

    private void playerPlayCard(AbstractPlayerController player, PlayCardData playCardData) {
        // Playing card
        logger.logf("%s playing a card", player.getPlayer().getName());
        Card playedCard = player.playCard(playCardData);
        logger.logf(
                "%s played and placed card into parade: %s",
                player.getPlayer().getName(), playedCard);
        clientRenderer.renderln(player.getPlayer().getName() + " played: " + playedCard);

        // Place card in parade and receive cards from parade
        List<Card> cardsFromParade = parade.placeCard(playedCard); // Apply parade logic
        player.getPlayer().addToBoard(cardsFromParade.toArray(Card[]::new));
        logger.logf(
                "%s received %d cards from parade to add to board: %s",
                player.getPlayer().getName(),
                cardsFromParade.size(),
                Arrays.toString(cardsFromParade.toArray()));
        clientRenderer.renderf(
                "%s received %s from parade.%n",
                player.getPlayer().getName(), Arrays.toString(cardsFromParade.toArray()));
    }

    /** Declares the winner based on the lowest score. */
    private void declareWinner(Map<AbstractPlayerController, Integer> playerScores) {
        AbstractPlayerController winner = null;
        int lowestScore = Integer.MAX_VALUE;

        for (Map.Entry<AbstractPlayerController, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() < lowestScore) {
                lowestScore = entry.getValue();
                winner = entry.getKey();
            }
        }

        if (winner != null) {
            clientRenderer.render(
                    "Winner: "
                            + winner.getPlayer().getName()
                            + " with "
                            + lowestScore
                            + " points!");
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
