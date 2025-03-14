package parade.player;

import parade.common.Card;
import parade.common.exceptions.NetworkFailureException;
import parade.common.state.client.AbstractClientData;
import parade.common.state.server.AbstractServerData;

import java.util.List;

/**
 * IPlayer interface is designed to represent a player in the game. It provides methods for the
 * player to interact with the GameEngine, local or network.
 *
 * <p>This interface is implemented by both human and AI players, allowing them to make decisions
 * based on the current state of the game.
 *
 * <p>It is supposed to use handle(AbstractServerData):void receives the AbstractServerData from the
 * GameEngine and determines what data it is. Then, it should internally decide what to do with the
 * data. For example, if it receives a PlayerTurnData, it should update its internal state to
 * reflect the current turn.
 *
 * <p>It is also supposed to handle emitting AbstractClientData to the GameEngine. For example, if
 * it decides to play a card, it should create a PlayerCardPlayData object and send it to the
 * GameEngine via send(AbstractClientData):void method.
 */
public interface IPlayer extends AutoCloseable {
    /**
     * Handles an incoming AbstractServerData from the GameEngine. This method is called by the
     * GameEngine to notify the player of changes in the game state.
     *
     * @param data The data received from the GameEngine.
     * @throws NetworkFailureException If there is a failure in the network communication. If the
     *     implementation does not use network communication, this exception should not be thrown.
     */
    void handle(AbstractServerData data) throws NetworkFailureException;

    /**
     * Sends an AbstractClientData to the GameEngine. This method is called by the player to notify
     * the GameEngine of its actions.
     *
     * @param data The data to be sent to the GameEngine.
     * @throws NetworkFailureException If there is a failure in the network communication. If the
     *     implementation does not use network communication, this exception should not be thrown.
     */
    void send(AbstractClientData data) throws NetworkFailureException;

    /**
     * Retrieves the player's current hand. If the implementation is intended for network use, the
     * underlying hand should be transient. This means that the hand should not be transmitted over
     * the network but should be constructed locally from the game state.
     *
     * @return A list of cards the player is holding.
     */
    List<Card> getHand();

    /**
     * Retrieves the cards that the player has collected from the parade. Represents the player's
     * current board state.
     *
     * @return A list of cards representing the player's board.
     */
    List<Card> getBoard();

    /**
     * Retrieves the player's name. This helps differentiate between different types of players
     * (human or AI).
     *
     * @return The player's name as a string.
     */
    String getName();

    /**
     * Retrieves the player's ID. This is used to uniquely identify the player in the GameEngine.
     *
     * @return The player's ID as a string.
     */
    String getId();
}
