package app.service.event;

import app.model.dto.event.ActiveEventResponse;
import app.model.dto.event.CreateEventRequest;
import app.model.dto.event.EditEventRequest;
import app.model.entity.quest.QuestType;
import app.service.event.client.EventClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class EventServiceUnitTest {
    @Mock
    private EventClient eventClient;

    @InjectMocks
    private EventService eventService;

    @Test
    void getActiveEvent_shouldReturnResponseBody() {

        ActiveEventResponse event = ActiveEventResponse.builder()
                .title("Warrior Festival")
                .bonusXp(20)
                .bonusGold(10)
                .affectedQuestType(QuestType.COMBAT)
                .build();

        ResponseEntity<ActiveEventResponse> response =
                ResponseEntity.ok(event);

        when(eventClient.getActiveEvent()).thenReturn(response);

        ActiveEventResponse result = eventService.getActiveEvent();

        assertNotNull(result);
        assertEquals("Warrior Festival", result.getTitle());
        assertEquals(20, result.getBonusXp());
        assertEquals(10, result.getBonusGold());

        verify(eventClient).getActiveEvent();
    }

    @Test
    void createEvent_shouldCallEventClient() {

        CreateEventRequest request = new CreateEventRequest();

        eventService.createEvent(request);

        verify(eventClient).createEvent(request);
    }

    @Test
    void editEvent_shouldCallEventClient() {

        EditEventRequest request = new EditEventRequest();

        eventService.editEvent(request);

        verify(eventClient).editEvent(request);
    }

    @Test
    void deleteEvent_shouldCallEventClient() {

        UUID eventId = UUID.randomUUID();

        eventService.deleteEvent(eventId);

        verify(eventClient).deleteEvent(eventId);
    }

}
