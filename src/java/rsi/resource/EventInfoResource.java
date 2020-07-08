package rsi.resource;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.ejb.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import rsi.model.EventInfo;
import rsi.service.EventInfoService;
import javax.ws.rs.NotFoundException;
import org.faceless.pdf2.*;

@Singleton
@Path("EventInfo")
public class EventInfoResource {
    
    @Context
    UriInfo context;
    
    private final EventInfoService eventService = new EventInfoService();
    private static final DateTimeFormatter PRINT_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy"); 

    public EventInfoResource() { }

    @GET
    @Path("/date/{year}/{month}/{day}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EventInfo> getEventsForDate(@PathParam("year") int year, @PathParam("month") int month, @PathParam("day") int day) {        
        return getEventListWithLinks(eventService.getEventsForDate(year, month, day));
    }

    @GET
    @Path("/week/{year}/{weekNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<EventInfo> getEventsForWeek(@PathParam("year") int year, @PathParam("weekNumber") int weekNumber) {
        List<EventInfo> events = eventService.getEventsForWeek(year, weekNumber);
        Collections.sort(events);
        return getEventListWithLinks(events);
    }
    
    @GET
    @Path("/{eventId}")
    @Produces(MediaType.APPLICATION_JSON)
    public EventInfo getEvent(@PathParam("eventId") Long id) {
        EventInfo event = eventService.getEvent(id);
        addLinksToEvent(event);
        return event;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public EventInfo createEvent(EventInfo event) {
        EventInfo newEvent = eventService.createEvent(event);
        addLinksToEvent(newEvent);
        return newEvent;
    }
    
    @PUT
    @Path("/{eventId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EventInfo updateEvent(@PathParam("eventId") Long id, EventInfo event) {
        EventInfo oldEvent = eventService.getEvent(id);
        if (oldEvent == null) {
            throw new NotFoundException();
        }
        event.setId(id);
        event = eventService.updateEvent(event);
        addLinksToEvent(event);
        return event;
    }
    
    @GET
    @Path("pdfForDate/{year}/{month}/{day}")
    public byte[] getPdfForDate(@PathParam("year") int year, @PathParam("month") int month, @PathParam("day") int day) throws IOException {
        LocalDate date = LocalDate.of(year, month, day);
        String time = PRINT_DATE_FORMATTER.format(date);
        List<EventInfo> events = getEventsForDate(year, month, day);
        return eventListToPdf(events, time);
    }

    @GET
    @Path("/pdfForWeek/{year}/{weekNumber}")
    public byte[] getPdfForWeek(@PathParam("year") int year, @PathParam("weekNumber") int weekNumber) throws IOException {
        LocalDate d1 = getFirstDayOfWeek(weekNumber, year);
        LocalDate d2 = getLastDayOfWeek(weekNumber, year);
        String time = PRINT_DATE_FORMATTER.format(d1) + " - " + PRINT_DATE_FORMATTER.format(d2);
        List<EventInfo> events = getEventsForWeek(year, weekNumber);
        return eventListToPdf(events, time);
    }
    
     private LocalDate getFirstDayOfWeek(int weekNumber, int year) {
        LocalDate date = LocalDate.of(year, 1, 1)
            .with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, weekNumber)
            .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        
        return date;
    }
    
    private LocalDate getLastDayOfWeek(int weekNumber, int year) {
        LocalDate firstDayOfWeek = getFirstDayOfWeek(weekNumber, year);
        return firstDayOfWeek.plusDays(6);
    }
    
    private byte[] eventListToPdf(List<EventInfo> events, String time) throws IOException {
        PDF pdf = new PDF();
        PDFPage page = pdf.newPage("A4");
        
        PDFStyle style = new PDFStyle();
        PDFFont font = new StandardFont(StandardFont.TIMES);
        style.setFont(font, 16);
        style.setFillColor(Color.black);
        
        LayoutBox flow = new LayoutBox(page.getWidth() - 100);
        flow.setStyle(style);
        
        flow.addText("Lista wydarzeń " + time, style, Locale.getDefault());
        flow.addLineBreak(style);
        flow.addLineBreak(style);
        
        for (EventInfo e : events) {
            
            flow.addText("Nazwa: " + (e.getName() == null ? "" : e.getName()), style, Locale.getDefault());
            flow.addLineBreak(style);
            flow.addText("Typ wydarzenia: " + EventInfo.EventTypeToString(e.getType()), style, Locale.getDefault());
            flow.addLineBreak(style);
            flow.addText("Data: " + PRINT_DATE_FORMATTER.format(e.getDate()), style, Locale.getDefault());
            flow.addLineBreak(style);
            flow.addText("Opis: ", style, Locale.getDefault());
            flow.addLineBreak(style);
            flow.addText("\"" + (e.getDescription() == null ? "" : e.getDescription()) + "\"", style, Locale.getDefault());
            flow.addLineBreak(style);
            flow.addLineBreak(style);
        }
        
        int top = (int)page.getHeight() - 50;
        page.drawLayoutBox(flow, 50, top);
        
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        pdf.render(byteStream);
        byteStream.close();
        
        return byteStream.toByteArray();
    }
    
    private List<EventInfo> getEventListWithLinks(List<EventInfo> events) {
        for (EventInfo event : events) {
            addLinksToEvent(event);
        }
        return events;
    }
    
    private void addLinksToEvent(EventInfo event) {
        if (event == null) return;
        String uri = context.getBaseUriBuilder().path(EventInfoResource.class)
                .path(String.valueOf(event.getId()))
                .build().toString();
        event.addLink(uri, "self");
    }
}
