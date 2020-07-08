package rsi.service;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rsi.model.EventInfo;
import rsi.resource.ExampleEventList;

public class EventInfoService {

    private final Map<Long, EventInfo> events = new HashMap<>();

    public EventInfoService() {
        List<EventInfo> exampleEvents = new ExampleEventList().getEvents();
        for (EventInfo event : exampleEvents) events.put(event.getId(), event);
    }

    public List<EventInfo> getAllEvents() {
        return new ArrayList<>(events.values());
    }

    public List<EventInfo> getEventsForWeek(int year, int week) {
        List<EventInfo> results = new ArrayList<>();
        for (Map.Entry<Long, EventInfo> entry : events.entrySet()) {
            EventInfo event = entry.getValue();
            if (event.getDate().getYear() == year && getEventWeekNumber(event) == week) {
                results.add(event);
            }
        }
        return results;
    }
    
    public List<EventInfo> getEventsForDate(int year, int month, int day) {
        List<EventInfo> results = new ArrayList<>();
        LocalDate date = LocalDate.of(year, month, day);
        for (Map.Entry<Long, EventInfo> entry : events.entrySet()) {
            EventInfo event = entry.getValue();
            if (event.getDate().equals(date)) {
                results.add(event);
            }
        }
        return results;
    }

    public EventInfo getEvent(Long id) {
        return events.get(id);
    }

    public EventInfo createEvent(EventInfo event) {
        Long id = events.size() + 1L;
        event.setId(id);
        events.put(id, event);
        return events.get(id);
    }

    public EventInfo updateEvent(EventInfo event) {
        Long id = event.getId();
        events.put(id, event);
        return events.get(id);
    }

    public void deleteEvent(Long id) {
        events.remove(id);
    }
    
    private int getEventWeekNumber(EventInfo event) {
        WeekFields weekFields = WeekFields.ISO; 
        int weekNumber = event.getDate().get(weekFields.weekOfWeekBasedYear());
        return weekNumber;
    }
    
}
