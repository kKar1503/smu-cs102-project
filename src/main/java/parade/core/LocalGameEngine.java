package parade.core;

import parade.card.Card;
import parade.core.result.*;
import parade.exceptions.MenuCancelledException;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.menu.manager.*;
import parade.menu.option.LobbyMenuOption;
import parade.player.Player;
import parade.player.controller.*;
import parade.settings.SettingKey;
import parade.settings.Settings;

import java.util.*;

/**
 * Represents the game server for the Parade game. Manages players, the deck, the parade, and game
 * flow.
 */
public class LocalGameEngine extends AbstractGameEngine {
    private final AbstractLogger logger;
    private final MenuManager menuManager;
    private final Scanner scanner;

    /** Initializes the game server with a deck. */
    public LocalGameEngine() {
        logger = LoggerProvider.getInstance();
        scanner = new Scanner(System.in);
        menuManager = setupMenuProvider();
    }

    private void addPlayerController(AbstractPlayerController player) {
        playerControllerManager.add(player);
        logger.logf("Player %s added to the game", player.getPlayer().getName());
    }

    private AbstractPlayerController removePlayerController(int index) {
        AbstractPlayerController playerController = playerControllerManager.remove(index);
        logger.logf("Player %s removed from the game", playerController.getPlayer().getName());
        return playerController;
    }

    private void removePlayerDisplay() {
        while (true) {
            int count = 1;
            System.out.println("Select a player to remove.");
            for (AbstractPlayerController player : playerControllerManager.getPlayerControllers()) {
                System.out.println(count++ + ". " + player.getPlayer().getName());
            }
            System.out.println(count + ". Return back to main menu");
            try {
                int input = scanner.nextInt();
                if (input < 1 || input > playerControllerManager.size() + 1) {
                    throw new NoSuchElementException("Out of range: " + input);
                }
                if (input == count) {
                    return;
                }
                removePlayerController(input - 1);
                return;
            } catch (NoSuchElementException e) {
                logger.log("User entered invalid input", e);
                System.out.println("Invalid input, please try again");
            } finally {
                scanner.nextLine();
            }
        }
    }

    private void waitForPlayersLobby() {
        logger.logf("Waiting for players to join lobby");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            LobbyMenuOption lobbyMenuOption =
                    menuManager.lobbyMenu(playerControllerManager.getPlayers());
            switch (lobbyMenuOption) {
                case ADD_PLAYER -> {
                    while (true) {
                        System.out.print("Enter player name: ");
                        String name = scanner.nextLine().trim();

                        if (name.isEmpty()) {
                            System.out.println("Name cannot be empty. Please enter a valid name.");
                            continue;
                        }

                        HumanController humanController =
                                new HumanController(
                                        PlayerNameRegistry.getUniqueName(name), menuManager);
                        addPlayerController(humanController);
                        break;
                    }
                }
                case ADD_COMPUTER -> {
                    logger.log("Adding a new computer");
                    ComputerController computerController = menuManager.newComputerMenu();
                    if (computerController == null) {
                        logger.log("User cancelled adding a computer");
                        continue;
                    }

                    addPlayerController(computerController);
                }
                case REMOVE_PLAYER -> {
                    logger.log("Removing a player from lobby");
                    removePlayerDisplay();
                }
                case START_GAME -> {
                    logger.log("User requested to start the game");
                    return;
                }
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
        try {
            menuManager.welcomeDisplay();
            logger.log("Prompting user to start game in menu");
            switch (menuManager.mainMenu()) {
                case START_GAME -> logger.log("User is starting the game");
                case EXIT -> {
                    logger.log("User is exiting the game");
                    return;
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
                    "Dealing %d cards to %d players",
                    numCardsToDraw, playerControllerManager.size());
            List<Card> drawnCards = deck.pop(numCardsToDraw); // Draw all the cards first
            logger.log("Drawn cards: " + Arrays.toString(drawnCards.toArray()));

            // "Roll" a dice to decide who starts first
            // Generate a number from 0 to 6
            Random dice = new Random();
            int diceRoll1 = dice.nextInt(7);
            int diceRoll2 = dice.nextInt(7);
            // Sets the current player based on the dice roll
            playerControllerManager.setCurrentPlayerIdx(diceRoll1 + diceRoll2);
            Player startingPlayer = playerControllerManager.peek().getPlayer();
            logger.logf(
                    "Dice roll = %d, Starting player: %s",
                    diceRoll1 + diceRoll2, startingPlayer.getName());
            System.out.printf(
                    "Dice roll: %d, %s will be starting first!%n",
                    diceRoll1 + diceRoll2, startingPlayer.getName());

            // Dish out the cards one by one, like real life you know? Like not getting the
            // direct next
            // card but alternating between players
            for (int i = 0; i < playerControllerManager.size(); i++) {
                AbstractPlayerController controller = playerControllerManager.next();
                for (int j = 0; j < INITIAL_CARDS_PER_PLAYER; j++) {
                    Card drawnCard = drawnCards.get(i + playerControllerManager.size() * j);
                    controller.draw(drawnCard);
                    logger.logf("%s drew: %s", controller.getPlayer().getName(), drawnCard);
                }
            }

            // Game loop continues until the deck is empty or an end condition is met
            logger.log("Game loop starting");
            while (shouldGameContinue()) {
                // Each player plays a card
                AbstractPlayerController controller = playerControllerManager.next();
                playerPlayCard(
                        controller,
                        new PlayCardData(
                                playerControllerManager.getPlayerControllers(),
                                parade,
                                deck.size())); // Play a card from their hand

                // Draw a card from the deck for the player
                Card drawnCard = deck.pop();
                controller.draw(drawnCard);
                logger.logf("%s drew: %s", controller.getPlayer().getName(), drawnCard);
            }

            logger.logf("Game loop finished");

            // After the game loop finishes, the extra round is played.
            logger.log("Game loop finished, running final round");
            System.out.println("Final round started. Players do not draw a card.");
            for (int i = 0; i < playerControllerManager.size(); i++) {
                AbstractPlayerController controller = playerControllerManager.next();
                playerPlayCard(
                        controller,
                        new PlayCardData(
                                playerControllerManager.getPlayerControllers(),
                                parade,
                                deck.size())); // Play a card from their hand
            }

            // Each player chooses 2 cards to discard
            for (int i = 0; i < playerControllerManager.size(); i++) {
                AbstractPlayerController controller = playerControllerManager.next();
                Player player = controller.getPlayer();
                logger.logf("%s choosing 2 cards to discard.", player.getName());

                for (int j = 0; j < 2; j++) {
                    Card discardedCard =
                            controller.discardCard(
                                    new PlayCardData(
                                            playerControllerManager.getPlayerControllers(),
                                            parade,
                                            deck.size()));
                    logger.logf("%s discarded: %s", player.getName(), discardedCard);
                    System.out.println(player.getName() + " discarded: " + discardedCard);
                }
            }

            // Add remaining cards in players' hand to their board for score calculation
            for (int i = 0; i < playerControllerManager.size(); i++) {
                playerControllerManager.next().moveCardsFromHandToBoard();
            }

            logger.log("Tabulating scores");
            Map<AbstractPlayerController, Integer> playerScores = tabulateScores();

            // Declare the final results
            System.out.println("Game Over! Final Scores:");
            DeclareWinner declareWinner = new DeclareWinner();
            AbstractResult result = declareWinner.evaluateScores(playerScores);

            switch (result) {
                case WinnerResult win -> {
                    System.out.printf(
                            "%s wins with %d points!%n",
                            win.getPlayer().getPlayer().getName(),
                            playerScores.get(win.getPlayer()));
                }
                case TieAndWinnerResult tie -> {
                    System.out.printf(
                            "Tie in score of %d points but %s wins with lesser number of cards%n",
                            playerScores.get(tie.getPlayer()),
                            tie.getPlayer().getPlayer().getName());
                }
                case TieAndNoWinnerResult overallTie -> {
                    System.out.println("Overall tie with no winners");
                    int numPlayers = overallTie.getPlayers().size();
                    int score = playerScores.get(overallTie.getPlayers().get(0));
                    for (int i = 0; i < numPlayers - 1; i++) {
                        System.out.print(
                                overallTie.getPlayers().get(i).getPlayer().getName() + ", ");
                    }
                    System.out.printf(
                            "%s have the same score of %d points and same number of cards.%n",
                            overallTie.getPlayers().get(numPlayers - 1).getPlayer().getName(),
                            score);
                }
                default -> System.out.println("Error retrieving result");
            }
        } catch (IllegalStateException e) {
            logger.log("Game engine error", e);
        } catch (MenuCancelledException e) {
            logger.log("User cancelled menu", e);
        } catch (Exception e) {
            logger.log("Unexpected error", e);
        } finally {
            menuManager.renderBye();
        }
    }

    private void playerPlayCard(AbstractPlayerController player, PlayCardData playCardData) {
        // Playing card
        logger.logf("%s playing a card", player.getPlayer().getName());
        Card playedCard = player.playCard(playCardData);
        logger.logf(
                "%s played and placed card into parade: %s",
                player.getPlayer().getName(), playedCard);
        System.out.println(player.getPlayer().getName() + " played: " + playedCard);

        // Place card in parade and receive cards from parade
        List<Card> cardsFromParade = parade.placeCard(playedCard); // Apply parade logic
        player.receiveFromParade(cardsFromParade.toArray(Card[]::new));
        logger.logf(
                "%s received %d cards from parade to add to board: %s",
                player.getPlayer().getName(),
                cardsFromParade.size(),
                Arrays.toString(cardsFromParade.toArray()));
        System.out.printf(
                "%s received %s from parade.%n",
                player.getPlayer().getName(), Arrays.toString(cardsFromParade.toArray()));
    }

    /**
     * Sets up the client renderer.
     *
     * @return the client renderer
     */
    private MenuManager setupMenuProvider() {
        Settings settings = Settings.getInstance();

        String clientRendererType = settings.get(SettingKey.CLIENT_RENDERER);

        MenuManager clientRenderer =
                switch (clientRendererType) {
                    case "basic_local" -> new BasicMenuManager();
                    case "advanced_local" -> new AdvancedMenuManager();
                    case "debug" -> new DebugMenuManager();
                    default ->
                            throw new IllegalStateException(
                                    "Unknown client renderer in settings: " + clientRendererType);
                };
        logger.log("Gameplay client renderer is using " + clientRendererType);
        return clientRenderer;
    }
}
