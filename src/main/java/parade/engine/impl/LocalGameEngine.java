package parade.engine.impl;

import parade.common.*;
import parade.common.state.client.AbstractClientData;
import parade.common.state.client.ClientCardPlayData;
import parade.common.state.server.ServerPlayerTurnData;
import parade.computer.EasyComputerEngine;
import parade.computer.HardComputerEngine;
import parade.computer.NormalComputerEngine;
import parade.controller.IPlayerController;
import parade.controller.local.ILocalPlayerController;
import parade.controller.local.LocalComputerController;
import parade.controller.local.LocalHumanController;
import parade.engine.AbstractGameEngine;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
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
public class LocalGameEngine extends AbstractGameEngine<ILocalPlayerController> {
    private final AbstractLogger logger;
    private final IClientRenderer clientRenderer;
    private final Scanner scanner;

    /** Initializes the game server with a deck. */
    public LocalGameEngine() {
        super(new Lobby("Local Lobby", 2, 6, null));
        logger = LoggerProvider.getInstance();
        scanner = new Scanner(System.in);
        clientRenderer = setupClientRenderer();
    }

    public void addPlayerController(ILocalPlayerController player) {
        playerControllerManager.add(player);
        logger.logf("Player %s added to the game", player.getPlayer().getName());
    }

    public boolean removePlayerController(ILocalPlayerController player) {
        boolean removed = playerControllerManager.remove(player);
        if (removed) {
            logger.logf("Player %s removed from the game", player.getPlayer().getName());
        } else {
            logger.logf("Player %s not found in the game", player.getPlayer().getName());
        }
        return removed;
    }

    public ILocalPlayerController removePlayerController(int index) {
        ILocalPlayerController removedPlayer = playerControllerManager.remove(index);
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
                    addPlayerController(new LocalComputerController(new EasyComputerEngine()));
                } else if (compInput == 2) {
                    addPlayerController(new LocalComputerController(new NormalComputerEngine()));
                } else if (compInput == 3) {
                    addPlayerController(new LocalComputerController(new HardComputerEngine()));
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
            for (IPlayerController player : playerControllerManager.getPlayerControllers()) {
                clientRenderer.renderln(count + ". " + player.getPlayer().getName());
                count++;
            }
            clientRenderer.renderln(count + ". Return back to main menu");
            int input;
            try {
                input = scanner.nextInt();
                if (input < 1 || input > playerControllerManager.getLobby().size() + 1) {
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
            clientRenderer.renderPlayersLobby(playerControllerManager.getLobby());
            int input = 0;
            try {
                input = scanner.nextInt();
                scanner.nextLine();
                if (input == 1) {
                    logger.log("Adding a new player");
                    if (playerControllerManager.getLobby().isFull()) {
                        clientRenderer.renderln("Lobby is full.");
                        continue;
                    }
                    clientRenderer.render("Enter player name: ");
                    String name = scanner.nextLine();
                    addPlayerController(new LocalHumanController(name));
                } else if (input == 2) {
                    logger.log("Adding a new computer");
                    if (playerControllerManager.getLobby().isFull()) {
                        clientRenderer.renderln("Lobby is full.");
                        continue;
                    }
                    addComputerDisplay();
                } else if (input == 3) {
                    if (playerControllerManager.getLobby().isEmpty()) {
                        clientRenderer.renderln("Lobby has no players.");
                        continue;
                    }
                    logger.log("Removing a player from lobby");
                    removePlayerDisplay();
                } else if (input == 4) {
                    if (!playerControllerManager.getLobby().isReady()) {
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

        if (!playerControllerManager.getLobby().isReady()) {
            logger.logf(
                    "Insufficient players to start the game, found %d",
                    playerControllerManager.getLobby().size());
            throw new IllegalStateException("Server requires at least two players");
        }

        logger.logf("Starting game with %d players", playerControllerManager.getLobby().size());

        // Gives out card to everyone
        int numCardsToDraw = INITIAL_CARDS_PER_PLAYER * playerControllerManager.getLobby().size();
        logger.logf(
                "Dealing %d cards to %d players",
                numCardsToDraw, playerControllerManager.getLobby().size());
        List<Card> drawnCards = deck.pop(numCardsToDraw); // Draw all the cards first
        logger.log("Drawn cards: " + Arrays.toString(drawnCards.toArray()));
        // Dish out the cards one by one, like real life you know? Like not getting the
        // direct next
        // card but alternating between players
        List<ILocalPlayerController> playerControllers =
                playerControllerManager.getPlayerControllers();
        for (int i = 0; i < playerControllers.size(); i++) {
            for (int j = 0; j < INITIAL_CARDS_PER_PLAYER; j++) {
                ILocalPlayerController playerController = playerControllers.get(i);
                Card drawnCard = drawnCards.get(i + playerControllers.size() * j);
                playerController.getPlayer().addToHand(drawnCard);
                logger.logf("%s drew: %s", playerController.getPlayer().getName(), drawnCard);
            }
        }

        // Game loop continues until the deck is empty or an end condition is met
        logger.log("Game loop starting");
        while (shouldGameContinue()) {
            // Each player plays a card
            ILocalPlayerController player = playerControllerManager.next();

            // Draw a card from the deck for the player
            Card drawnCard = deck.pop();
            player.getPlayer().addToHand(drawnCard);
            logger.logf("%s drew: %s", player.getPlayer().getName(), drawnCard);

            playerPlayCard(
                    player,
                    new ServerPlayerTurnData(
                            playerControllerManager.getPlayers().toArray(Player[]::new),
                            player.getPlayer(),
                            parade.getCards().toArray(Card[]::new),
                            deck.size(),
                            player.getPlayer().getHand().size())); // Play a card from their hand
        }
        logger.logf("Game loop finished");

        // After the game loop finishes, the extra round is played.
        logger.log("Game loop finished, running final round");
        clientRenderer.renderln("Final round started. Players do not draw a card.");
        for (int i = 0; i < playerControllers.size(); i++) {
            ILocalPlayerController player = playerControllerManager.next();
            playerPlayCard(
                    player,
                    new ServerPlayerTurnData(
                            playerControllerManager.getPlayers().toArray(Player[]::new),
                            player.getPlayer(),
                            parade.getCards().toArray(Card[]::new),
                            deck.size(),
                            player.getPlayer().getHand().size())); // Play a card from their hand
        }

        logger.log("Tabulating scores");
        Map<ILocalPlayerController, Integer> playerScores = tabulateScores();

        // Declare the final results
        clientRenderer.renderln("Game Over! Final Scores:");
        declareWinner(playerScores);
    }

    private void playerPlayCard(
            ILocalPlayerController player, ServerPlayerTurnData playerTurnData) {
        // Playing card
        logger.logf("%s playing a card", player.getPlayer().getName());
        AbstractClientData clientData = player.send(playerTurnData);

        Card playedCard;
        if (clientData instanceof ClientCardPlayData cardPlayData) {
            playedCard = cardPlayData.getCard();
        } else if (clientData == null) {
            logger.logf("%s did not play a card", player.getPlayer().getName());
            throw new IllegalStateException("Client data is null");
        } else {
            logger.logf(
                    "%s did not play a card, received: %s",
                    player.getPlayer().getName(), clientData);
            throw new IllegalStateException("Client data is not a card play data");
        }

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
    private void declareWinner(Map<ILocalPlayerController, Integer> playerScores) {
        ILocalPlayerController winner = null;
        int lowestScore = Integer.MAX_VALUE;

        for (Map.Entry<ILocalPlayerController, Integer> entry : playerScores.entrySet()) {
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
