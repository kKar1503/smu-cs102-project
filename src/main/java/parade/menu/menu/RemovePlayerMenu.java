package parade.menu.menu;

import parade.exception.MenuCancelledException;
import parade.menu.base.AbstractMenu;
import parade.menu.prompt.OptionsPrompt;
import parade.player.controller.AbstractPlayerController;

import java.util.List;

public class RemovePlayerMenu extends AbstractMenu<AbstractPlayerController> {
    private final List<AbstractPlayerController> controllers;
    private final OptionsPrompt prompt;

    public RemovePlayerMenu(List<AbstractPlayerController> controllers) {
        this.controllers = controllers;
        this.prompt =
                new OptionsPrompt(
                        true,
                        controllers.stream()
                                .map(x -> x.getPlayer().getName())
                                .toArray(String[]::new));
    }

    @Override
    public AbstractPlayerController start() throws MenuCancelledException {
        println("Select a player to remove:");
        return controllers.get(prompt.prompt());
    }
}
