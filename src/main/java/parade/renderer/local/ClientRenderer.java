package parade.renderer.local;

import parade.card.Card;
import parade.player.Player;
import parade.player.controller.PlayCardData;

import java.util.List;
import java.util.Map;

public interface ClientRenderer {
    /**
     * Render a message to the screen. This is used to display messages to the user.
     *
     * @param message the message to render
     */
    void render(String message);

    /**
     * Render a message to the screen. This is used to display messages to the user. Appends a new
     * line to the end of the message.
     *
     * @param message the message to render
     */
    void renderln(String message);

    /**
     * Render a message to the screen. This is used to display messages to the user. This is a
     * formatted message, this has the same format as the printf method in Java.
     *
     * @param format the format string
     * @param args the arguments to format the string with
     */
    void renderf(String format, Object... args);

    /**
     * Render the welcome message for the game. This is used to display the welcome message for the
     * game. This can make damn fancy.
     */
    void renderWelcome();

    /**
     * Render the menu for the game. This is used to display the main menu for the game.
     *
     * <p>This should ends with a print() method instead of println(). Because we expect the method
     * follow directly with a Scanner that reads the input.
     */
    void renderMenu();

    /**
     * Render the screen for a single player, this is to display for the player to prompt them for
     * input to join the lobby.
     */
    void renderPlayersLobby(List<Player> lobby);

    /**
     * Render the screen for a human player, this is to display for the player to prompt them for
     * input to choose the difficulty of the computer player.
     */
    void renderComputerDifficulty();

    /**
     * Render the screen for a single player, this is to display for the player to prompt them for
     * input.
     *
     * <p>This render needs the user to be able to see the cards in their hand, and the parade cards
     * on the table.
     *
     * <p>The text render for this method is also suggested to end with a print() method instead of
     * println(). Because we kinda expect the method follow directly with a Scanner that reads the
     * input.
     *
     * @param player the player to render the screen for
     * @param newlyDrawnCard the card that the player has drawn
     * @param playCardData the data object that contains sufficient information for the player to
     *     make a decision for their turn
     * @param toDiscard tells the method to render for players to play or discard card
     */
    void renderPlayerTurn(
            Player player, Card newlyDrawnCard, PlayCardData playCardData, boolean toDiscard);

    /**
     * Render the end game message for all players. This method is used for the naive implementation
     * of the local multiplayer Parade game.
     *
     * @param playerScores a map of players and their scores
     */
    void renderEndGame(Map<Player, Integer> playerScores);

    /** Bye bye buddy. */
    void renderBye();
}
