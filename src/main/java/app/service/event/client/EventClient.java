package app.service.event.client;

import app.model.dto.event.ActiveEventResponse;
import app.model.dto.event.CreateEventRequest;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "event-client", url = "http://localhost:8081/api/v1/event")
public interface EventClient {

    @GetMapping
    ResponseEntity<ActiveEventResponse> getActiveEvent();

    @PostMapping
    ResponseEntity<Void> createEvent(
            @RequestBody CreateEventRequest request
    );

}
