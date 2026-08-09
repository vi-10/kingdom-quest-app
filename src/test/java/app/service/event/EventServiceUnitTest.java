package app.service.event;

import app.service.event.client.EventClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventServiceUnitTest {
    @Mock
    private EventClient eventClient;

    @InjectMocks
    private EventService eventService;


}
