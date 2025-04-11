package parade.engine.impl;

import parade.common.Card;
import parade.engine.AbstractGameEngine;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;
import parade.player.IPlayer;
import parade.player.computer.EasyComputer;
import parade.player.computer.HardComputer;
import parade.player.computer.NormalComputer;
import parade.player.human.LocalHuman;
import parade.renderer.ClientRendererProvider;
import parade.renderer.IClientRenderer;
import parade.renderer.impl.AdvancedClientRenderer;
import parade.renderer.impl.BasicLocalClientRenderer;
import parade.renderer.impl.DebugClientRenderer;
import parade.result.AbstractResult;
import parade.result.DeclareWinner;
import parade.result.TieAndNoWinnerResult;
import parade.result.TieAndWinnerResult;
import parade.result.WinnerResult;
import parade.settings.SettingKey;
import parade.settings.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

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

    private void chooseComputerDifficulty(String baseName) {
        while (true) {
            try {
                clientRenderer.renderComputerDifficulty();
                int compInput = scanner.nextInt();
                IPlayer player;

                // Generate a unique base name like 23, 23(1), etc.
                String uniqueBase = generateUniqueName(baseName, getPlayers());

                // Create the appropriate computer player – no need to add [Difficulty] here
                switch (compInput) {
                    case 1:
                        player = new EasyComputer(getParadeCards(), uniqueBase);
                        break;
                    case 2:
                        player = new NormalComputer(getParadeCards(), uniqueBase);
                        break;
                    case 3:
                        player = new HardComputer(getParadeCards(), uniqueBase);
                        break;
                    default:
                        throw new NoSuchElementException();
                }

                addPlayer(player);
                return;

            } catch (NoSuchElementException e) {
                logger.log("User entered invalid input", e);
                clientRenderer.renderln("Input not found, please try again.");
                scanner.nextLine();
            }
        }
    }

    private void addComputerDisplay() {
        while (true) {
            clientRenderer.render("Enter computer's name: ");
            try {
                String baseName = scanner.nextLine().trim();

                if (baseName.isEmpty()) {
                    clientRenderer.renderln("Name cannot be empty. Please try again.");
                    continue;
                }

                chooseComputerDifficulty(baseName); // <-- now passing clean base name only
                return;

            } catch (NoSuchElementException e) {
                logger.log("User entered invalid input", e);
                clientRenderer.renderln("Invalid input, please try again.");
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
                    String name;
                    while (true) {
                        name = scanner.nextLine().trim();

                        if (name.isEmpty()) {
                            clientRenderer.renderln(
                                    "Name cannot be empty. Please enter a valid name.");
                            clientRenderer.render("Enter player name: ");
                            continue;
                        }

                        name = generateUniqueName(name, getPlayers());
                        break;
                    }

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

        // "Roll" a dice to decide who starts first
        // Generate a number from 0 to 6
        Random dice = new Random();
        int diceRoll1 = dice.nextInt(7);
        int diceRoll2 = dice.nextInt(7);
        // Sets the current player based on the dice roll
        setCurrentPlayer(diceRoll1 + diceRoll2);
        logger.logf(
                "Dice roll = %d, Starting player: %s",
                diceRoll1 + diceRoll2, getCurrentPlayer().getName());
        clientRenderer.renderf(
                "Dice roll: %d, %s will be starting first!\n",
                diceRoll1 + diceRoll2, getCurrentPlayer().getName());

        // Dish out the cards one by one, like real life you know? Like not getting the
        // direct next
        // card but alternating between players
        for (int i = 0; i < getPlayersCount(); i++) {
            IPlayer player = getCurrentPlayer();
            for (int j = 0; j < INITIAL_CARDS_PER_PLAYER; j++) {
                Card drawnCard = drawnCards.get(i + getPlayersCount() * j);
                player.draw(drawnCard);
                logger.logf("%s drew: %s", player.getName(), drawnCard);
            }
            nextPlayer();
        }

        // Game loop continues until the deck is empty or an end condition is met
        logger.log("Game loop starting");
        while (shouldGameContinue()) {
            // Each player plays a card
            IPlayer player = getCurrentPlayer();
            playerPlayCard(player); // Play a card from their hand

            // Draw a card from the deck for the player
            Card drawnCard = drawFromDeck();
            player.draw(drawnCard);
            logger.logf("%s drew: %s", player.getName(), drawnCard);

            nextPlayer();
        }

        logger.logf("Game loop finished");

        // After the game loop finishes, the extra round is played.
        logger.log("Game loop finished, running final round");
        clientRenderer.renderln("Final round started. Players do not draw a card.");
        // Each player plays their last card
        for (int i = 0; i < getPlayersCount(); i++) {
            playerPlayCard(getCurrentPlayer());
            nextPlayer();
        }

        // Each player chooses 2 cards to discard
        for (int i = 0; i < getPlayersCount(); i++) {
            IPlayer player = getCurrentPlayer();
            logger.logf("%s choosing 2 cards to discard.", player.getName());

            for (int j = 0; j < 2; j++) {
                Card discardedCard = player.discardCard(getParadeCards());
                logger.logf("%s discarded: %s", player.getName(), discardedCard);
                clientRenderer.renderln(player.getName() + " discarded: " + discardedCard);
            }
            nextPlayer();
        }

        // Add remaining cards in players' hand to their board for score calculation
        for (int i = 0; i < getPlayersCount(); i++) {
            IPlayer player = getCurrentPlayer();
            player.addToBoard(player.getHand());
            nextPlayer();
        }

        logger.log("Tabulating scores");
        Map<IPlayer, Integer> playerScores = tabulateScores();

        // Declare the final results
        clientRenderer.renderln("Game Over! Final Scores:");
        DeclareWinner declareWinner = new DeclareWinner();
        AbstractResult result = declareWinner.evaluateScores(playerScores);

        switch (result) {
            case WinnerResult win ->
                    clientRenderer.renderf(
                            "%s wins with %d points!\n",
                            win.getPlayer().getName(), playerScores.get(win.getPlayer()));

            case TieAndWinnerResult tie ->
                    clientRenderer.renderf(
                            "Tie in score of %d points but %s wins with lesser number of cards\n",
                            playerScores.get(tie.getPlayer()), tie.getPlayer().getName());

            case TieAndNoWinnerResult overallTie -> {
                clientRenderer.renderln("Overall tie with no winners");
                int numPlayers = overallTie.getPlayers().size();
                int score = playerScores.get(overallTie.getPlayers().get(0));
                for (int i = 0; i < numPlayers - 1; i++) {
                    clientRenderer.render(overallTie.getPlayers().get(i).getName() + ", ");
                }
                clientRenderer.renderf(
                        "%s have the same score of %d points and same number of cards.\n",
                        overallTie.getPlayers().get(numPlayers - 1).getName(), score);
            }

            default -> clientRenderer.renderln("Error retrieving result\n");
        }
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
            clientRenderer.renderEndGame(playerScores);
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
                    case "debug" -> new DebugClientRenderer();
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

    private String generateUniqueName(String baseName, List<IPlayer> players) {
        List<String> existingNames = new ArrayList<>();
        for (IPlayer player : players) {
            existingNames.add(player.getName());
        }

        if (existingNames.stream()
                .noneMatch(name -> name.equals(baseName) || name.startsWith(baseName + "("))) {
            return baseName;
        }

        int count = 1;
        String newName;
        while (true) {
            newName = baseName + "(" + count + ")";
            String finalNewName = newName;
            boolean exists =
                    existingNames.stream()
                            .anyMatch(
                                    name ->
                                            name.equals(finalNewName)
                                                    || name.startsWith(finalNewName + "["));
            if (!exists) {
                break;
            }
            count++;
        }

        return newName;
    }
}
