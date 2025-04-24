package ru.Krivi4.LinCut.web.aspect;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /** Обработка ошибки при вводе короткой ссылке, которой не существует или истек срок действия*/
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        // Динамически получаем хост и порт из текущего запроса
        String host = ServletUriComponentsBuilder.fromCurrentRequest().build().getHost();
        int port = ServletUriComponentsBuilder.fromCurrentRequest().build().getPort();
        String portString = (port == 80 || port == 443) ? "" : ":" + port;

        // Получаем путь из запроса (например, /lincut/cfg)
        String path = request.getDescription(false).replace("uri=", "");

        // Формируем полный URL (например, localhost:8080/lincut/cfg)
        String fullUrl = host + portString + path;

        // Обновляем сообщение исключения, заменяя ключ на полный URL
        String message = ex.getMessage().replaceFirst("Короткой ссылки \\w+ ", "Короткой ссылки " + fullUrl + " ");

        // Формируем тело ответа
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", message);
        body.put("path", path);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}