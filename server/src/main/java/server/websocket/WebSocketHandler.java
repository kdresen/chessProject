//package server.websocket;
//
//import com.google.gson.Gson;
//import dataaccess.daointerfaces.*;
//import dataaccess.DataAccessException;
//import org.eclipse.jetty.websocket.api.Session;
//import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
//import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
//import org.eclipse.jetty.websocket.api.annotations.WebSocket;
//import org.eclipse.jetty.websocket.client.io.ConnectionManager;
//import websocket.commands.*;
//import websocket.messages.*;
//
//import java.io.IOException;
//import java.util.Timer;
//
//@WebSocket
//public class WebSocketHandler {
//    private final ConnectionManager connections = new ConnectionManager();
//
//    @OnWebSocketMessage
//    public void onMessage(Session session, String message) throws IOException {
//        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
//        switch (serverMessage.getServerMessageType()) {
//            case LOAD_GAME -> loadGame(session, message);
//            case NOTIFICATION -> notification(session, message);
//            case ERROR -> sendError(session, message);
//        }
//    }
//
//    private void loadGame(Session session, String message) throws IOException {
//
//    }
//
//    private void notification(Session session, String message) throws IOException {
//
//    }
//
//    private void sendError(Session session, String message) throws IOException {
//
//    }
//}
