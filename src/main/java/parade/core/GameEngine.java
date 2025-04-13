package parade.core;

import parade.card.Card;
import parade.computer.ComputerEngine;
import parade.core.result.*;
import parade.exception.MenuCancelledException;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.menu.manager.*;
import parade.menu.option.LobbyMenuOption;
import parade.player.Player;
import parade.player.controller.*;
import parade.setting.Setting;
import parade.setting.SettingKey;
import parade.utils.Ansi;

import java.util.*;

public class GameEngine extends AbstractGameEngine {
    private final AbstractLogger logger;
    private final MenuManager menuManager;

    public GameEngine() {
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
                    menuManager.lobbyMenu(
                            playerControllerManager.getPlayers(), MIN_PLAYERS, MAX_PLAYERS);
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
        // Generate a number from 1 to 6 (number of faces on dice)
        Random dice = new Random();
        int diceRoll1 = dice.nextInt(1, 7);
        int diceRoll2 = dice.nextInt(1, 7);
        menuManager.diceRollDisplay(diceRoll1, diceRoll2, playerControllerManager.getPlayers());
        playerControllerManager.setCurrentPlayerIdx(diceRoll1 + diceRoll2);
        Player startingPlayer = playerControllerManager.peek().getPlayer();
        logger.logf(
                "Dice roll = %d, Starting player: %s",
                diceRoll1 + diceRoll2, startingPlayer.getName());
    }

    private void distributeCards() {
        int numCardsToDraw = INITIAL_CARDS_PER_PLAYER * playerControllerManager.size();
        logger.logf(
                "Dealing %d cards to %d players", numCardsToDraw, playerControllerManager.size());
        List<Card> drawnCards = deck.pop(numCardsToDraw);
        logger.log("Drawn cards: " + Arrays.toString(drawnCards.toArray()));

        // Dish out the cards one by one, like real life you know? Like not getting the
        // direct next card but alternating between players
        for (int i = 0; i < playerControllerManager.size(); i++) {
            AbstractPlayerController controller = playerControllerManager.next();
            for (int j = 0; j < INITIAL_CARDS_PER_PLAYER; j++) {
                Card drawnCard = drawnCards.get(i + playerControllerManager.size() * j);
                controller.draw(drawnCard);
                logger.logf("%s drew: %s", controller.getPlayer().getName(), drawnCard);
            }
        }
    }

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

            logger.log("Game loop starting");
            while (shouldGameContinue()) {
                AbstractPlayerController controller = playerControllerManager.next();
                playerPlayCard(
                        controller,
                        new PlayCardData(
                                playerControllerManager.getPlayerControllers(),
                                parade,
                                deck.size()));

                Card drawnCard = deck.pop();
                controller.draw(drawnCard);
                logger.logf("%s drew: %s", controller.getPlayer().getName(), drawnCard);
            }

            logger.log("Game loop finished, running final round");
            menuManager.finalRoundDisplay();
            for (int i = 0; i < playerControllerManager.size(); i++) {
                AbstractPlayerController controller = playerControllerManager.next();
                playerPlayCard(
                        controller,
                        new PlayCardData(
                                playerControllerManager.getPlayerControllers(),
                                parade,
                                deck.size()));
            }

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

            for (int i = 0; i < playerControllerManager.size(); i++) {
                playerControllerManager.next().moveCardsFromHandToBoard();
            }

            logger.log("Tabulating scores");
            Map<AbstractPlayerController, Integer> playerScores = tabulateScores();
            DeclareWinner declareWinner = new DeclareWinner();
            GameResult result = declareWinner.evaluateScores(playerScores);
            menuManager.endGameDisplay(playerScores, result);
        } catch (IllegalStateException e) {
            logger.log("Game engine error", e);
        } catch (MenuCancelledException e) {
            logger.log("User cancelled menu", e);
        } catch (Exception e) {
            logger.log("Unexpected error", e);
        } finally {
            menuManager.byeByeDisplay();
        }
    }

    private void hideCursor() {
        System.out.println(Ansi.HIDE_CURSOR); // hide cursor for the game, stop blinking top corner
        Runtime.getRuntime() // shutdown hook helps to handle the missing cursor when Ctrl+C
                .addShutdownHook(new Thread(() -> System.out.println(Ansi.SHOW_CURSOR)));
    }

    private void playerPlayCard(AbstractPlayerController player, PlayCardData playCardData) {
        logger.logf("%s playing a card", player.getPlayer().getName());
        Card playedCard = player.playCard(playCardData);
        logger.logf(
                "%s played and placed card into parade: %s",
                player.getPlayer().getName(), playedCard);

        List<Card> cardsFromParade = parade.placeCard(playedCard);
        player.receiveFromParade(cardsFromParade.toArray(Card[]::new));
        logger.logf(
                "%s received %d cards from parade to add to board: %s",
                player.getPlayer().getName(),
                cardsFromParade.size(),
                Arrays.toString(cardsFromParade.toArray()));

        menuManager.playerMoveDisplay(player.getPlayer(), playedCard, cardsFromParade);
    }

    private MenuManager setupMenuProvider() {
        Setting settings = Setting.get();
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
