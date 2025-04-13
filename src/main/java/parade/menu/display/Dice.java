package parade.menu.display;

import parade.menu.base.AbstractDisplay;
import parade.menu.base.MenuResource;
import parade.menu.base.MenuResource.MenuResourceType;

import java.util.Random;

public class Dice extends AbstractDisplay {
    private static final String[] DICE_ROLLING =
            MenuResource.getArray(MenuResourceType.DICE_ROLLING);

    private static final String[] DICE_ONE = MenuResource.getArray(MenuResourceType.DICE_ONE);
    private static final String[] DICE_TWO = MenuResource.getArray(MenuResourceType.DICE_TWO);
    private static final String[] DICE_THREE = MenuResource.getArray(MenuResourceType.DICE_THREE);
    private static final String[] DICE_FOUR = MenuResource.getArray(MenuResourceType.DICE_FOUR);
    private static final String[] DICE_FIVE = MenuResource.getArray(MenuResourceType.DICE_FIVE);
    private static final String[] DICE_SIX = MenuResource.getArray(MenuResourceType.DICE_SIX);

    private final String[] dice;

    public Dice(int dice1, int dice2) {
        String[] die1 = getDiceFromNumber(dice1);
        String[] die2 = getDiceFromNumber(dice2);
        this.dice = new String[die1.length];

        String middleSpace = " ".repeat((terminalWidth % 2) + 2);
        for (int i = 0; i < die1.length; i++) {
            this.dice[i] = die1[i] + middleSpace + die2[i];
        }
    }

    private String[] getDiceFromNumber(int dice) {
        return switch (dice) {
            case 1 -> DICE_ONE;
            case 2 -> DICE_TWO;
            case 3 -> DICE_THREE;
            case 4 -> DICE_FOUR;
            case 5 -> DICE_FIVE;
            case 6 -> DICE_SIX;
            default -> throw new IllegalArgumentException("Invalid dice value: " + dice);
        };
    }

    @Override
    public void display() {
        CentralisedDisplay[] rollingDice = generateRollingDiceVariations();
        CentralisedDisplay dice = new CentralisedDisplay(this.dice, 0, 0);

        for (int i = 0; i < 15; i++) {
            clear();

            CentralisedDisplay rollingDie = rollingDice[new Random().nextInt(rollingDice.length)];

            rollingDie.display();
            flush();

            sleep(100, false); // short delay for shaking effect
        }

        clear();
        dice.display();
        flush();
    }

    private CentralisedDisplay[] generateRollingDiceVariations() {
        CentralisedDisplay[] rollingDice = new CentralisedDisplay[7];
        for (int i = 0; i < 7; i++) {
            rollingDice[i] = new CentralisedDisplay(DICE_ROLLING, 0, i);
        }
        return rollingDice;
    }
}
