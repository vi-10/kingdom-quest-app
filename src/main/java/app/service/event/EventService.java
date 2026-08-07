package app.service.event;

import app.model.dto.event.ActiveEventResponse;
import app.model.dto.event.CreateEventRequest;
import app.model.dto.event.EditEventRequest;
import app.model.dto.event.EventDTO;
import app.service.event.client.EventClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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

    public void editEvent(EditEventRequest request){
        eventClient.editEvent(request);
    }

    public List<EventDTO> getAllEvents() {
        return eventClient.getAllEvents().getBody();
    }

    public void deleteEvent(UUID eventId) {
        eventClient.deleteEvent(eventId);
    }
}
