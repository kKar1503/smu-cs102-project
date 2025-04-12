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

    private final String[] dice1;
    private final String[] dice2;
    private final int diceWidth;

    public Dice(int dice1, int dice2) {
        this.dice1 = getDiceString(dice1);
        this.dice2 = getDiceString(dice2);
        this.diceWidth = this.dice1[0].length();
    }

    private String[] getDiceString(int dice) {
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
        int topOffset = (terminalHeight - DICE_ROLLING.length) / 2;
        int leftOffset = (terminalWidth - DICE_ROLLING[0].length()) / 2;
        for (int i = 0; i < 15; i++) {
            clear();
            for (int j = 0; j < topOffset; j++) {
                println();
            }

            String offset = " ".repeat(new Random().nextInt(7) + leftOffset);

            for (String line : DICE_ROLLING) {
                println(offset + line);
            }
            flush();

            sleep(100); // short delay for shaking effect
        }

        clear();
        topOffset = (terminalHeight - dice1.length) / 2;
        leftOffset = (terminalWidth / 2) - diceWidth - 1;
        String middleSpace = " ".repeat((terminalWidth % 2) + 2);
        String leftPadding = " ".repeat(leftOffset);
        for (int j = 0; j < topOffset; j++) {
            println();
        }
        for (int i = 0; i < dice1.length; i++) {
            println(leftPadding + dice1[i] + middleSpace + dice2[i]);
        }
        flush();
        sleep(2500);
        clear();
    }
}
