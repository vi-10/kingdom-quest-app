package app.service.event.client;

import app.model.dto.event.ActiveEventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "event-client", url = "http://localhost:8081/api/v1/event")
public interface EventClient {

    @GetMapping
    ResponseEntity<ActiveEventResponse> getActiveEvent();

}
