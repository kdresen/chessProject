package ui.websocket;

import websocket.messages.*;

public interface ServerMessageHandler {
    void notify(ServerMessage serverMessage);
}
