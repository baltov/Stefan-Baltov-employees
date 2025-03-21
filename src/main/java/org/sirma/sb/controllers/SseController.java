package org.sirma.sb.controllers;

import lombok.RequiredArgsConstructor;
import org.sirma.sb.services.SseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse/events")
public class SseController {

    private final SseService emitterService;

    @GetMapping
    public SseEmitter connect(@RequestParam String clientId) {
        SseEmitter emitter = new SseEmitter(30_000L);
        emitterService.addEmitter(clientId, emitter);
        return emitter;
    }
}