package parade.menu.display;

import parade.menu.base.MenuResource;

public class GameOverDisplay extends AbstractFullScreenBlinkingDisplay {
    public GameOverDisplay() {
        super(MenuResource.getArray(MenuResource.MenuResourceType.GAME_OVER));
    }
}
