package shunting.data;

import shunting.models.*;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class ScheduleWriter {

	private Schedule schedule;
	
	public ScheduleWriter(Schedule schedule) {
		this.schedule = schedule;
	}
	
	public void toXML(File f) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(f));
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bw.newLine();
		bw.write("<schedule>");
		bw.newLine();
		Iterator<Event> itEvents = schedule.events();
		while (itEvents.hasNext()) {
			Event event = itEvents.next();
			bw.write(eventToXML(event));
			bw.newLine();
		}
		bw.write("</schedule>");
		bw.newLine();
		
		bw.close();
	}
	
	private String eventToXML(Event event) {
		String s = "";
		if (event instanceof Arrival)
			s += "<arrival";
		else if (event instanceof Departure)
			s += "<departure";
		else
			throw new IllegalStateException("There can't be another Event type!");
		LocalTime base = LocalTime.parse("00:00:00");
		LocalTime localTime = intToTime(event.getTime(), base);
		String time = localTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		s += " time=\"" + time + "\"";
		s += " compID=\"" + event.getComposition().getID() + "\"";
		s += " track=\"104\">\r\n";
		
		for (int i = 0; i < event.getComposition().size(); i++) {
			s += "  ";
			s += trainToXML(event.getComposition().getTrain(i));
			s += "\r\n";
		}
		s += "</";
		if (event instanceof Arrival)
			s += "arrival";
		else
			s += "departure";
		s += ">";
		
		return s;
	}
	
	private LocalTime intToTime(int time, LocalTime base) {
		return base.plusMinutes(time);
	}
	
	private String trainToXML(Train train) {
		String s = "<train";
		s += " ID=\"" + train.getID() + "\"";
		s += " type=\"" + train.getTrainType().getType() + "\"";
		s += " interchangeable=\"" + train.getInterchange() + "\"";
		s += " inspection=\"" + train.getInspection() + "\"";
		s += " repair=\"" + train.getRepair() + "\"";
		s += " cleaning=\"" + train.getCleaning() + "\"";
		s += " washing=\"" + train.getWashing() + "\"";
		s += " />";
		
		return s;
	}
}
