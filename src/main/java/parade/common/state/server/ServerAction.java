package parade.common.state.server;

import java.io.Serializable;

public enum ServerAction implements Serializable {
    // Connection actions
    CONNECT_ACK,

    // Lobby actions
    LOBBY_CREATE_ACK,
    LOBBY_JOIN_ACK,
    LOBBY_PLAYER_JOINED,
    LOBBY_PLAYER_LEFT,
    LOBBY_CLOSED,
    LOBBY_LIST,

    // Player actions
    PLAYER_TURN,
    PLAYER_DRAWN_CARD,
    PLAYER_PLAYED_CARD,
    PLAYER_RECEIVED_PARADE_CARDS,

    // Game actions
    GAME_START,
    GAME_FINAL_ROUND,
    GAME_END,

    /*REQUEST_GAME_STATE,
    REQUEST_PLAYER_LIST,
    REQUEST_CARD_LIST,
    REQUEST_PLAYER_INFO,
    REQUEST_GAME_INFO,
    REQUEST_PLAYER_ACTIONS,
    REQUEST_PLAYER_CARDS,
    REQUEST_PLAYER_HAND,*/
}
