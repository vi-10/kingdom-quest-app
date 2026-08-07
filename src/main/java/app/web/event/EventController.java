package app.web.event;

import app.model.dto.event.ActiveEventResponse;
import app.model.dto.event.CreateEventRequest;
import app.model.dto.quest.CreateQuestDTO;
import app.service.event.EventService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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

    @GetMapping("/admin/events/create")
    public ModelAndView getCreateEventPage() {

        ModelAndView modelAndView = new ModelAndView("create-event");
        CreateEventRequest request = CreateEventRequest.builder().build();
        modelAndView.addObject("eventData", request);

        return modelAndView;
    }

    @PostMapping("/admin/events/create")
    public String createEvent(
            @Valid @ModelAttribute("eventData") CreateEventRequest request,
            BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "create-event";
        }

        eventService.createEvent(request);

        return "redirect:/admin/events";
    }



}
