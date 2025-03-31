package parade.engine;

import parade.common.*;
import parade.common.state.client.AbstractClientData;
import parade.common.state.server.ServerPlayerTurnData;
import parade.controller.network.INetworkPlayerController;
import parade.logger.AbstractLogger;
import parade.logger.LoggerProvider;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class NetworkGameEngine extends AbstractGameEngine<INetworkPlayerController> {
    private static final AbstractLogger logger = LoggerProvider.getInstance();

    private final BlockingQueue<AbstractClientData> clientDataQueue = new LinkedBlockingQueue<>();

    public NetworkGameEngine(Lobby lobby, INetworkPlayerController owner) {
        super(lobby);
        addPlayerController(owner);
    }

    public NetworkGameEngine(String lobbyName, int maxPlayers, INetworkPlayerController owner) {
        this(new Lobby(lobbyName, 2, maxPlayers, owner.getPlayer()), owner);
    }

    public NetworkGameEngine(
            String lobbyName,
            String lobbyPassword,
            int maxPlayers,
            INetworkPlayerController owner) {
        this(new Lobby(lobbyName, lobbyPassword, 2, maxPlayers, owner.getPlayer()), owner);
    }

    public void addPlayerController(INetworkPlayerController player) {
        playerControllerManager.add(player);
        logger.logf("Player %s added to the game", player.getPlayer().getName());
    }

    public boolean removePlayerController(INetworkPlayerController player) {
        boolean removed = playerControllerManager.remove(player);
        if (removed) {
            logger.logf("Player %s removed from the game", player.getPlayer().getName());
        } else {
            logger.logf("Player %s not found in the game", player.getPlayer().getName());
        }
        return removed;
    }

    public INetworkPlayerController removePlayerController(int index) {
        INetworkPlayerController removedPlayer = playerControllerManager.remove(index);
        if (removedPlayer != null) {
            logger.logf("Player %s removed from the game", removedPlayer.getPlayer().getName());
        } else {
            logger.logf("Player at index %d is null", index);
        }
        return removedPlayer;
    }

    public INetworkPlayerController removePlayerController(Player player) {
        INetworkPlayerController removedPlayer = playerControllerManager.remove(player);
        if (removedPlayer != null) {
            logger.logf("Player %s removed from the game", removedPlayer.getPlayer().getName());
        } else {
            logger.logf("There is no controller for %s", player);
        }
        return removedPlayer;
    }

    public Lobby getLobby() {
        return playerControllerManager.getLobby();
    }

    public List<INetworkPlayerController> getControllers() {
        return playerControllerManager.getPlayerControllers();
    }

    /**
     * Starts the game loop and manages game progression.
     *
     * @throws IllegalStateException if there are less than 2 players
     */
    @Override
    public void start() throws IllegalStateException {
        logger.log("Setting player controllers' data queue to that of the network game engine");
        for (INetworkPlayerController playerController :
                playerControllerManager.getPlayerControllers()) {
            playerController.setLobbyDataQueue(clientDataQueue);
        }

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
        List<INetworkPlayerController> playerControllers =
                playerControllerManager.getPlayerControllers();
        for (int i = 0; i < playerControllers.size(); i++) {
            for (int j = 0; j < INITIAL_CARDS_PER_PLAYER; j++) {
                INetworkPlayerController playerController = playerControllers.get(i);
                Card drawnCard = drawnCards.get(i + playerControllers.size() * j);
                playerController.getPlayer().addToHand(drawnCard);
                logger.logf("%s drew: %s", playerController.getPlayer().getName(), drawnCard);
            }
        }

        // Game loop continues until the deck is empty or an end condition is met
        logger.log("Game loop starting");
        while (shouldGameContinue()) {
            // Each player plays a card
            INetworkPlayerController player = playerControllerManager.next();

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
        for (int i = 0; i < playerControllers.size(); i++) {
            INetworkPlayerController player = playerControllerManager.next();
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
        Map<INetworkPlayerController, Integer> playerScores = tabulateScores();

        // Declare the final results
        declareWinner(playerScores);
    }

    private void playerPlayCard(
            INetworkPlayerController player, ServerPlayerTurnData playerTurnData) {}

    /** Declares the winner based on the lowest score. */
    private void declareWinner(Map<INetworkPlayerController, Integer> playerScores) {
        INetworkPlayerController winner = null;
        int lowestScore = Integer.MAX_VALUE;

        for (Map.Entry<INetworkPlayerController, Integer> entry : playerScores.entrySet()) {
            if (entry.getValue() < lowestScore) {
                lowestScore = entry.getValue();
                winner = entry.getKey();
            }
        }
    }
}
