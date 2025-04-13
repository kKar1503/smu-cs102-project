package parade.menu.manager;

import parade.card.Card;
import parade.computer.ComputerEngine;
import parade.menu.base.AbstractPrinter;
import parade.menu.display.BoxedTextDisplay;
import parade.menu.display.FinalRoundDisplay;
import parade.menu.menu.*;
import parade.menu.option.LobbyMenuOption;
import parade.menu.option.MainMenuOption;
import parade.player.Player;
import parade.player.controller.AbstractPlayerController;

import java.util.List;

abstract class AbstractMenuManager extends AbstractPrinter implements MenuManager {
    @Override
    public MainMenuOption mainMenu() {
        return new MainMenu().start();
    }

    @Override
    public LobbyMenuOption lobbyMenu(List<Player> players, int minPlayers, int maxPlayers) {
        return new LobbyMenu(players, minPlayers, maxPlayers).start();
    }

    @Override
    public String computerNameMenu() {
        return new ComputerNameMenu().start();
    }

    @Override
    public ComputerEngine computerDifficultyMenu() {
        return new ComputerDifficultyMenu().start();
    }

    @Override
    public AbstractPlayerController removePlayerMenu(List<AbstractPlayerController> controllers) {
        return new RemovePlayerMenu(controllers).start();
    }

    @Override
    public String humanNameMenu() {
        return new HumanNameMenu().start();
    }

    String getChosenPlayerFromDice(int diceSum, List<Player> players) {
        return players.get((diceSum) % players.size()).getName();
    }

    @Override
    public void diceRollDisplay(int diceRoll1, int diceRoll2, List<Player> players) {
        printfFlush(
                "Dice roll: %d, %s will be starting first!%n",
                diceRoll1 + diceRoll2, getChosenPlayerFromDice(diceRoll1 + diceRoll2, players));
    }

    @Override
    public void playerMoveDisplay(Player player, Card playedCard, List<Card> cardsFromParade) {
        String displayText =
                String.format(
                        "%s played: %s%n" + "and received: %n%s",
                        player.getName(), playedCard, cardsFromParade);
        new BoxedTextDisplay(displayText).display();
    }

    @Override
    public void finalRoundDisplay() {
        new FinalRoundDisplay().display();
    }
}
