package com.seplag.musicapi.service;

import com.seplag.musicapi.dto.response.AlbumResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyNewAlbum(AlbumResponse album) {
        log.info("Notificando novo álbum via WebSocket: {}", album.getTitle());
        messagingTemplate.convertAndSend("/topic/albums", album);
    }
}
