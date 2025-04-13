package parade.core;

import static parade.constants.GameEngineValues.INITIAL_CARDS_PER_PLAYER;

import parade.card.Card;
import parade.computer.ComputerEngine;
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
import parade.utils.Ansi;

import java.util.*;

/**
 * Represents the game server for the Parade game. Manages players, the deck, the parade, and game
 * flow.
 */
public class LocalGameEngine extends AbstractGameEngine {
    private final AbstractLogger logger;
    private final MenuManager menuManager;

    /** Initializes the game server with a deck. */
    public LocalGameEngine() {
        logger = LoggerProvider.getInstance();
        menuManager = setupMenuProvider();
    }

    private void addPlayerController(AbstractPlayerController player) {
        playerControllerManager.add(player);
        logger.logf("Player %s added to the game", player.getPlayer().getName());
    }

    private void removePlayerController(AbstractPlayerController controller) {
        boolean isRemoved = playerControllerManager.remove(controller);
        logger.logf(
                "Player %s %s removed from the game",
                controller.getPlayer().getName(), isRemoved ? "successfully" : "not");
    }

    private void waitForPlayersLobby() {
        logger.logf("Waiting for players to join lobby");
        while (true) {
            LobbyMenuOption lobbyMenuOption =
                    menuManager.lobbyMenu(playerControllerManager.getPlayers());
            switch (lobbyMenuOption) {
                case ADD_PLAYER -> {
                    logger.log("Adding a new human player");
                    try {
                        String name = menuManager.humanNameMenu();
                        HumanController humanController =
                                new HumanController(
                                        PlayerNameRegistry.getUniqueName(name), menuManager);
                        addPlayerController(humanController);
                    } catch (MenuCancelledException e) {
                        logger.log("User cancelled adding a human player");
                    }
                }
                case ADD_COMPUTER -> {
                    logger.log("Adding a new computer");
                    try {
                        String name = menuManager.computerNameMenu();
                        ComputerEngine engine = menuManager.computerDifficultyMenu();
                        ComputerController computerController =
                                new ComputerController(
                                        PlayerNameRegistry.getUniqueName(name), engine);
                        addPlayerController(computerController);
                    } catch (MenuCancelledException e) {
                        logger.log("User cancelled adding a computer");
                    }
                }
                case REMOVE_PLAYER -> {
                    logger.log("Removing a player from lobby");
                    try {
                        AbstractPlayerController removedPlayer =
                                menuManager.removePlayerMenu(
                                        playerControllerManager.getPlayerControllers());
                        removePlayerController(removedPlayer);
                    } catch (MenuCancelledException e) {
                        logger.log("User cancelled removing a player");
                    }
                }
                case START_GAME -> {
                    logger.log("User requested to start the game");
                    return;
                }
                case QUIT_GAME -> {
                    throw new MenuCancelledException();
                }
            }
        }
    }

    private void rollDice() {
        // "Roll" a die to decide who starts first
        // Generate a number from 1 to 6
        Random dice = new Random();
        int diceRoll1 = dice.nextInt(1, 7);
        int diceRoll2 = dice.nextInt(1, 7);
        menuManager.renderRoll(diceRoll1, diceRoll2, playerControllerManager.getPlayers());
        // Sets the current player based on the dice roll
        playerControllerManager.setCurrentPlayerIdx(diceRoll1 + diceRoll2);
        Player startingPlayer = playerControllerManager.peek().getPlayer();
        logger.logf(
                "Dice roll = %d, Starting player: %s",
                diceRoll1 + diceRoll2, startingPlayer.getName());
    }

    private void distributeCards() {
        // Gives out card to everyone
        int numCardsToDraw = INITIAL_CARDS_PER_PLAYER * playerControllerManager.size();
        logger.logf(
                "Dealing %d cards to %d players", numCardsToDraw, playerControllerManager.size());
        List<Card> drawnCards = deck.pop(numCardsToDraw); // Draw all the cards first
        logger.log("Drawn cards: " + Arrays.toString(drawnCards.toArray()));

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
    }

    /**
     * Starts the game loop and manages game progression.
     *
     * @throws IllegalStateException if there are less than 2 players
     */
    @Override
    public void start() throws IllegalStateException {
        hideCursor();
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

            rollDice();

            distributeCards();

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
            menuManager.finalRoundDisplay();
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
            menuManager.renderEndGame(playerScores);

            // Declare the final results
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

    private void hideCursor() {
        System.out.println(Ansi.HIDE_CURSOR); // hide cursor for the game, stop blinking top corner
        Runtime.getRuntime() // shutdown hook helps to handle the missing cursor when Ctrl+C
                .addShutdownHook(new Thread(() -> System.out.println(Ansi.SHOW_CURSOR)));
    }

    private void playerPlayCard(AbstractPlayerController player, PlayCardData playCardData) {
        // Playing card
        logger.logf("%s playing a card", player.getPlayer().getName());
        Card playedCard = player.playCard(playCardData);
        logger.logf(
                "%s played and placed card into parade: %s",
                player.getPlayer().getName(), playedCard);

        // Place card in parade and receive cards from parade
        List<Card> cardsFromParade = parade.placeCard(playedCard); // Apply parade logic
        player.receiveFromParade(cardsFromParade.toArray(Card[]::new));
        logger.logf(
                "%s received %d cards from parade to add to board: %s",
                player.getPlayer().getName(),
                cardsFromParade.size(),
                Arrays.toString(cardsFromParade.toArray()));
    }

    private MenuManager setupMenuProvider() {
        Settings settings = Settings.get();
        String menuType = settings.get(SettingKey.CLIENT_MENU);

        MenuManager menuManager =
                switch (menuType) {
                    case "basic" -> new BasicMenuManager();
                    case "advanced" -> new AdvancedMenuManager();
                    case "debug" -> new DebugMenuManager();
                    default ->
                            throw new IllegalStateException(
                                    "Unknown client menu in settings: " + menuType);
                };
        logger.log("Gameplay client menu is using " + menuType);
        return menuManager;
    }
}
