package parade.common.state.client;

import java.io.Serializable;

public enum ClientAction implements Serializable {
    // Connection actions
    CONNECT,

    // Lobby actions
    LOBBY_CREATE,
    LOBBY_JOIN,
    LOBBY_LEAVE,
    LOBBY_START,
    LOBBY_CLOSE,
    LOBBY_REQUEST_LIST,

    // Game actions
    CARD_PLAY,

    // This is used within the server to signal that a client has disconnected
    POISON_PILL,

    /*REQUEST_GAME_STATE,
    REQUEST_PLAYER_LIST,
    REQUEST_CARD_LIST,
    REQUEST_PLAYER_INFO,
    REQUEST_GAME_INFO,
    REQUEST_PLAYER_ACTIONS,
    REQUEST_PLAYER_CARDS,
    REQUEST_PLAYER_HAND,*/
}
