package org.sirma.sb.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class SseService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public void addEmitter(String clientId, SseEmitter emitter) {
        this.emitters.put(clientId, emitter);

        emitter.onCompletion(() -> emitters.remove(clientId));
        emitter.onTimeout(() -> emitters.remove(clientId));
        emitter.onError((ex) -> emitters.remove(clientId));
    }

    public void removeEmitter(String clientId) {
        emitters.remove(clientId);
    }

    public void sendEventToClient(String clientId, String eventName, Object event) throws IOException {
        SseEmitter emitter = emitters.get(clientId);
        if (emitter != null) {
            emitter.send(SseEmitter.event()
                    .name(eventName)
                    .data(event));
        } else {
            System.out.println("No emitter found for client: " + clientId);
        }
    }

    public void broadcastEvent(String eventName, Object event) {
        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(event));
            } catch (IOException e) {
                emitters.remove(clientId); // Clean up disconnected emitters
            }
        });
    }
}