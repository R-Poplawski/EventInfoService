package rsi.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventInfo implements Comparable< EventInfo > {

    Long id;
    String name;
    String description;
    EventType type;
    LocalDate eventDate;
    List<Link> links = new ArrayList<>();
    
    public enum EventType {
        Concert,
        Exhibition,
        Sport,
        Theater,
        Other
    }

    public EventInfo() { }
    
    public EventInfo(String name, String description, EventType type, LocalDate eventDate) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.eventDate = eventDate;
    }
    
    public EventInfo(Long id, String name, String description, EventType type, LocalDate eventDate) {
        this(name, description, type, eventDate);
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return eventDate;
    }

    public void setDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }
    
    public List<Link> getLinks() {
        return links;
    }
    
    public void setLinks(List<Link> links) {
        this.links = links;
    }
    
    public void addLink(String link, String rel) {
        boolean linkFound = false;
        for (Link l : links) {
            if (l.getRel().equals(rel)) {
                linkFound = true;
                break;
            }
        }
        
        if (!linkFound) links.add(new Link(link, rel));
    }
    
    public static EventType getTypeFromString(String typeStr) throws EventTypeException {
        typeStr = typeStr.toLowerCase();
        switch (typeStr) {
            case "concert":
                return EventType.Concert;
            case "exhibition":
                return EventType.Exhibition;
            case "sport":
                return EventType.Sport;
            case "theater":
                return EventType.Theater;
            case "other":
                return EventType.Other;
        }
        throw new EventTypeException();
    }
    
    public static String EventTypeToString(EventType type) {
        switch (type) {
            case Concert:
                return "koncert";
            case Exhibition:
                return "wystawa";
            case Sport:
                return "sport";
            case Theater:
                return "teatr";
            default:
                return "inne";
        }
    }
    
    @Override
    public int compareTo(EventInfo o) {
        return this.getDate().compareTo(o.getDate());
    }

}
