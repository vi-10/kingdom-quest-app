package app.service.event;

import app.model.dto.event.ActiveEventResponse;
import app.model.dto.event.CreateEventRequest;
import app.service.event.client.EventClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventClient eventClient;

    public ActiveEventResponse getActiveEvent(){
        return eventClient.getActiveEvent().getBody();
    }

    public void createEvent(CreateEventRequest request){
        eventClient.createEvent(request);
    }
}
