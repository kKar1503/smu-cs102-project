package parade.renderer.impl.display;

// import com.googlecode.lanterna.graphics.TextGraphics;
// import com.googlecode.lanterna.input.*;
// import com.googlecode.lanterna.screen.*;
// import com.googlecode.lanterna.terminal.*;

public class RenderMenu {
    public static void renderMenu() {
        System.out.println("\n");
        String asciiArt = """
██     ██ ███████ ██       ██████  ██████  ███    ███ ███████     ████████  ██████      ████████ ██   ██ ███████      ██████   █████  ███    ███ ███████ ██ 
██     ██ ██      ██      ██      ██    ██ ████  ████ ██             ██    ██    ██        ██    ██   ██ ██          ██       ██   ██ ████  ████ ██      ██ 
██  █  ██ █████   ██      ██      ██    ██ ██ ████ ██ █████          ██    ██    ██        ██    ███████ █████       ██   ███ ███████ ██ ████ ██ █████   ██ 
██ ███ ██ ██      ██      ██      ██    ██ ██  ██  ██ ██             ██    ██    ██        ██    ██   ██ ██          ██    ██ ██   ██ ██  ██  ██ ██         
 ███ ███  ███████ ███████  ██████  ██████  ██      ██ ███████        ██     ██████         ██    ██   ██ ███████      ██████  ██   ██ ██      ██ ███████ ██ 
                                                                                                                                                            
                                                                                                                                                            
 ██        ███████ ████████  █████  ██████  ████████      ██████   █████  ███    ███ ███████                                                               
███        ██         ██    ██   ██ ██   ██    ██        ██       ██   ██ ████  ████ ██                                                                    
 ██        ███████    ██    ███████ ██████     ██        ██   ███ ███████ ██ ████ ██ █████                                                                 
 ██             ██    ██    ██   ██ ██   ██    ██        ██    ██ ██   ██ ██  ██  ██ ██                                                                    
 ██ ██     ███████    ██    ██   ██ ██   ██    ██         ██████  ██   ██ ██      ██ ███████                                                               
                                                                                                                                                            
                                                                                                                                                            
██████         ███████ ██   ██ ██ ████████      ██████   █████  ███    ███ ███████                                                                         
     ██        ██       ██ ██  ██    ██        ██       ██   ██ ████  ████ ██                                                                              
 █████         █████     ███   ██    ██        ██   ███ ███████ ██ ████ ██ █████                                                                           
██             ██       ██ ██  ██    ██        ██    ██ ██   ██ ██  ██  ██ ██                                                                              
███████ ██     ███████ ██   ██ ██    ██         ██████  ██   ██ ██      ██ ███████                                                                         
                                                                                                                                                            
                                                                                                                                                            
 ██████ ██   ██  ██████   ██████  ███████ ███████      █████  ███    ██      ██████  ██████  ████████ ██  ██████  ███    ██                                 
██      ██   ██ ██    ██ ██    ██ ██      ██          ██   ██ ████   ██     ██    ██ ██   ██    ██    ██ ██    ██ ████   ██ ██                              
██      ███████ ██    ██ ██    ██ ███████ █████       ███████ ██ ██  ██     ██    ██ ██████     ██    ██ ██    ██ ██ ██  ██                                 
██      ██   ██ ██    ██ ██    ██      ██ ██          ██   ██ ██  ██ ██     ██    ██ ██         ██    ██ ██    ██ ██  ██ ██ ██                              
 ██████ ██   ██  ██████   ██████  ███████ ███████     ██   ██ ██   ████      ██████  ██         ██    ██  ██████  ██   ████                                
""";
System.out.println(asciiArt);
        
    }

    public static void main(String[] args) {
        renderMenu();
    }
}
// public class RenderMenu {

//      private static final String[] menuOptions = {"Start Game", "Exit"};
//      private static int currentSelection = 0;

//      public static void main(String[] args) throws Exception {
//          // Create the terminal and screen
//          Terminal terminal = new DefaultTerminalFactory().createTerminal();
//          Screen screen = new TerminalScreen(terminal);
//          screen.startScreen();

//          try {
//              // Main loop
//              while (true) {
//                  renderMenu(screen); // Render menu with animations
//                  KeyStroke keyStroke = screen.readInput();

//                  if (keyStroke != null) {
//                      switch (keyStroke.getKeyType()) {
//                          case ArrowUp:
//                              currentSelection = (currentSelection - 1 + menuOptions.length) %
//      menuOptions.length;
//                              break;
//                          case ArrowDown:
//                              currentSelection = (currentSelection + 1) % menuOptions.length;
//                              break;
//                          case Enter:
//                              if (currentSelection == 0) {
//                                  screen.clear();
//                                  TextGraphics textGraphics = screen.newTextGraphics();
//                                  // to use putString, you have generate a "textGraphics" object first
//                                  textGraphics.putString(10, 5, "Starting Game..."/*, TextColor.ANSI.GREEN*/);
//                                  screen.refresh();
//                                  Thread.sleep(1000);  // Simulate game start
//                                  return;  // Exit after "Starting Game..."
//                              } else {
//                                  screen.clear();
//                                  screen.putString(10, 5, "Exiting...", Terminal.Color.RED);
//                                  screen.refresh();
//                                  Thread.sleep(1000);  // Simulate exit
//                                  return;  // Exit the program
//                              }
//                      }
//                  }
//              }
//          } finally {
//              screen.stopScreen();
//          }
//      }

//      public static void renderMenu(Screen screen) throws InterruptedException {
//          screen.clear();

//          // Create border around the menu
//          screen.putString(0, 0, "+---------------------+", Terminal.Color.YELLOW);
//          screen.putString(0, 1, "|    Main Menu        |", Terminal.Color.YELLOW);
//          screen.putString(0, 2, "+---------------------+", Terminal.Color.YELLOW);

//          // Loop through menu options and animate highlighting
//          for (int i = 0; i < menuOptions.length; i++) {
//              if (i == currentSelection) {
//                  // Highlight the current option
//                  screen.putString(2, 3 + i, "> " + menuOptions[i], Terminal.Color.CYAN);
//              } else {
//                  // Regular options
//                  screen.putString(2, 3 + i, menuOptions[i], Terminal.Color.GREEN);
//              }
//          }

//          // Display instructions
//          screen.putString(0, 5 + menuOptions.length, "Use Arrow Keys to Navigate | Press Enter to Select", Terminal.Color.WHITE);
//          screen.refresh();
//          Thread.sleep(100);  // Small delay to simulate animation
//      }
// }
