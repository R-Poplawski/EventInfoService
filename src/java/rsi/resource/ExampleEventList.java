package rsi.resource;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import rsi.model.EventInfo;

public class ExampleEventList {
    public List<EventInfo> getEvents() {
        List<EventInfo> events = new LinkedList<>();
        
        events.add(new EventInfo(1L,
                "Wydarzenie testowe",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent neque metus, finibus ac ligula eu, pretium porta metus. Nam consectetur massa metus, sit amet facilisis turpis finibus sed. Aenean facilisis vitae magna in accumsan. Mauris eleifend lectus ut justo convallis, eget volutpat libero ullamcorper. Maecenas nec faucibus risus. Sed tincidunt mi et viverra ultrices. Pellentesque in mauris vel nulla aliquam convallis. Phasellus sed accumsan nisl. Phasellus sed orci velit. ",
                EventInfo.EventType.Other,
                LocalDate.of(2020, 6, 16)));
        events.add(new EventInfo(2L,
                "Wydarzenie testowe 2",
                "Opis... ",
                EventInfo.EventType.Other,
                LocalDate.of(2020, 6, 28)));
        
        return events;
    }
}
