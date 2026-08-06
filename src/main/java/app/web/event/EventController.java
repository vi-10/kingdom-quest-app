package app.web.event;

import app.model.dto.event.ActiveEventResponse;
import app.service.event.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Controller
public class EventController {
    private EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/event")
     public ModelAndView getActiveEvent(){

        ModelAndView modelAndView = new ModelAndView("event");
        modelAndView.addObject("event", eventService.getActiveEvent());
        return modelAndView;

     }

}
