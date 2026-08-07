package app.web.event;

import app.model.dto.event.ActiveEventResponse;
import app.model.dto.event.CreateEventRequest;
import app.model.dto.event.EditEventRequest;
import app.model.dto.event.EventDTO;
import app.model.dto.quest.CreateQuestDTO;
import app.model.dto.quest.EditQuestDTO;
import app.model.dto.quest.QuestDTO;
import app.service.event.EventService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
    public ModelAndView createEvent(
            @Valid @ModelAttribute("eventData") CreateEventRequest request,
            BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return new ModelAndView("create-event");
        }

        eventService.createEvent(request);

        return new ModelAndView("redirect:/admin/events");
    }

    @GetMapping("/admin/events/edit")
    public ModelAndView getEditEventPage() {

        ModelAndView modelAndView = new ModelAndView("edit-event");
        EditEventRequest request = EditEventRequest.builder().build();
        modelAndView.addObject("eventData", request);
        modelAndView.addObject("events", eventService.getAllEvents());

        return modelAndView;
    }

    @PutMapping("/admin/events/edit")
    public ModelAndView editEvent(
            @Valid @ModelAttribute("eventData") EditEventRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {

            ModelAndView modelAndView = new ModelAndView("edit-event");
            modelAndView.addObject("events", eventService.getAllEvents());

            return modelAndView;
        }

        eventService.editEvent(request);

        return new ModelAndView("redirect:/admin/events");
    }

    @GetMapping("/admin/events/delete")
    public ModelAndView getDeleteEventPage() {

        ModelAndView modelAndView = new ModelAndView("delete-event");
        List<EventDTO> events = eventService.getAllEvents();
        modelAndView.addObject("events", events);
        modelAndView.addObject("noEvents", events.isEmpty());

        return modelAndView;
    }

    @DeleteMapping("/admin/events/delete")
    public ModelAndView deleteEvent(@RequestParam UUID eventId) {

        eventService.deleteEvent(eventId);

        return new ModelAndView("redirect:/admin/events");
    }



}
