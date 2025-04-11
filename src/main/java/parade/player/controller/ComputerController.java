package parade.player.controller;

import parade.card.Card;
import parade.computer.ComputerEngine;

/**
 * The ComputerController class is a representation of a computer player in the game. It relies on
 * the underlying IComputerEngine to determine the best moves for the player. The controller handles
 * the game state updates and player actions based on the server data received.
 *
 * <p>When prompted to play a card, the computer player will use its attached IComputerEngine
 * internal logic to decide which card to play. The controller will then send the chosen card back
 * to the game server.
 */
public class ComputerController extends AbstractPlayerController {
    private final ComputerEngine computerEngine;

    /**
     * Constructs a Computer player with a given name and the computer engine.
     *
     * @param name The name of the AI player.
     * @param computerEngine The computer engine to attach for the controller.
     */
    public ComputerController(String name, ComputerEngine computerEngine) {
        super(String.format("%s [%s]", name, computerEngine.getName()));
        this.computerEngine = computerEngine;
    }

    @Override
    public Card playCard(PlayCardData playCardData) {
        return computerEngine.process(
                player,
                playCardData.getOtherPlayers().stream()
                        .map(AbstractPlayerController::getPlayer)
                        .toList(),
                playCardData.getParade(),
                playCardData.getDeckSize());
    }

    @Override
    public Card discardCard(PlayCardData playCardData) {
        return computerEngine.discardCard(player, playCardData.getParade());
    }
}
