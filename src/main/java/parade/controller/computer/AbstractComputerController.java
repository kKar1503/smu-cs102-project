package parade.controller.computer;

import parade.common.Card;
import parade.common.Player;
import parade.controller.IPlayerController;

import java.util.List;

/**
 * The Computer class is an abstract representation of an AI player in the game. It contains common
 * functionality shared among different AI difficulty levels.
 */
public abstract class AbstractComputerController implements IPlayerController {
    protected Player player;

    /**
     * Constructs a Computer player with a given name and initial hand.
     *
     * @param cards The initial set of cards assigned to the AI player's hand.
     * @param name The name of the AI player.
     */
    public AbstractComputerController(List<Card> cards, String name) {
        player = new Player(name);
        player.addToHand(cards.toArray(Card[]::new));
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}
