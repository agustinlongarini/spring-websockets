package com.example.springwebsockets.service;

import com.example.springwebsockets.model.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ChatHandler extends TextWebSocketHandler {

    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final Map<WebSocketSession, Usuario> sessionUserMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);

        // Crear e asociar un usuario con la sesión
        Usuario user = new Usuario();
        sessionUserMap.put(session, user);

        // Enviar información del usuario al cliente
        sendMessageToUser(session, "info", user);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);

        // También elimina la asociación de usuario cuando se cierra la sesión
        Usuario user = sessionUserMap.remove(session);

        // Envía un mensaje de desconexión a los demás usuarios
        sendMessageToAllUsers(user, createDisconnectMessage(user));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Obtener información del usuario asociada con la sesión
        Usuario usuario = sessionUserMap.get(session);

        // Manejar el mensaje (puedes implementar la lógica según tus necesidades)
        // Por ahora, simplemente reenviamos el mensaje a todos los usuarios
        sendMessageToAllUsers(usuario, message);
    }

    private void sendMessageToUser(WebSocketSession session, String type, Object payload) throws IOException {
        Map<String, Object> message = new HashMap<>();
        message.put("type", type);
        message.put("payload", payload);
        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(message)));
    }

    private void sendMessageToAllUsers(Usuario sender, TextMessage message) throws IOException {
        // Envía el mensaje a todos los usuarios excepto al remitente
        for (WebSocketSession webSocketSession : sessions) {
            if (!webSocketSession.equals(sender)) {
                webSocketSession.sendMessage(message);
            }
        }
    }

    private TextMessage createDisconnectMessage(Usuario user) throws JsonProcessingException {
        Map<String, Object> disconnectMessage = new HashMap<>();
        disconnectMessage.put("type", "disconnect");
        disconnectMessage.put("payload", user);
        return new TextMessage(new ObjectMapper().writeValueAsString(disconnectMessage));
    }
}


