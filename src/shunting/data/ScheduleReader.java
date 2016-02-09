package shunting.data;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import shunting.models.Arrival;
import shunting.models.Composition;
import shunting.models.Departure;
import shunting.models.Event;
import shunting.models.Schedule;
import shunting.models.Train;
import shunting.models.TrainFactory;

public class ScheduleReader {
	
	private Map<String, Train> trainCache;
	private LocalTime base;
	private TrainFactory tf = new TrainFactory(0);
	
	public ScheduleReader() {
		trainCache = new HashMap<>();
		base = null;
	}
	
	public Schedule parseXML(File f) {
		DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder db = df.newDocumentBuilder();
			doc = db.parse(f);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		if (doc == null)
			throw new IllegalStateException("Something went horribly wrong!");
		
		// real code begins here
		// read events
		List<Arrival> arrivals = new ArrayList<>();
		List<Departure> departures = new ArrayList<>();
		NodeList events = doc.getElementsByTagName("schedule").item(0).getChildNodes();
		int prevTime = 0;
		for (int i = 0; i < events.getLength(); i++) {
			Node m = events.item(i);
			if (m.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (m.getNodeName().equals("arrival")) {
				Arrival a = (Arrival) xmlToEvent(m, prevTime);
				prevTime = a.getTime();
				arrivals.add(a);
			} else if (m.getNodeName().equals("departure")) {
				Departure d = (Departure) xmlToEvent(m, prevTime);
				prevTime = d.getTime();
				departures.add(d);
			}
		}
		
		Schedule schedule = new Schedule(arrivals, departures);
		return schedule;
	}
	
	private Train xmlToTrain(Node n) {
		NamedNodeMap attr = n.getAttributes();
		String ID = attr.getNamedItem("ID").getNodeValue();
		if (trainCache.containsKey(ID)) {
			return trainCache.get(ID);
		}
		boolean interchangeable = Boolean.parseBoolean(attr.getNamedItem("interchangeable").getNodeValue());
		boolean inspection = Boolean.parseBoolean(attr.getNamedItem("inspection").getNodeValue());
		boolean repair = Boolean.parseBoolean(attr.getNamedItem("repair").getNodeValue());
		boolean cleaning = Boolean.parseBoolean(attr.getNamedItem("cleaning").getNodeValue());
		boolean washing = Boolean.parseBoolean(attr.getNamedItem("inspection").getNodeValue());
		String type = attr.getNamedItem("type").getNodeValue();		
		
//		Train t = tf.createTrainByType(ID, type, interchangeable, 
//				inspection, repair, cleaning, washing);
		Train t = tf.createTrainByType(ID, type);
		trainCache.put(ID, t);
		
		return t;
	}
	
	private Event xmlToEvent(Node n, int prevTime) {
		NamedNodeMap attr = n.getAttributes();
		String type = n.getNodeName();
		String time = attr.getNamedItem("time").getNodeValue();
		LocalTime eventTime = LocalTime.parse(time);
		if (base == null)
			base = eventTime;
		Duration timeToInt = Duration.between(base, eventTime);
		int intTime = Math.abs((int) timeToInt.toMinutes());
		if (intTime < prevTime)
			intTime  = 24*60 - intTime;
		
		Composition comp = new Composition(attr.getNamedItem("compID").getNodeValue());
		NodeList trains = n.getChildNodes();
		for (int i = 0; i < trains.getLength(); i++) {
			Node m = trains.item(i);
			if (m.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Train t = xmlToTrain(m);
			comp.addTrain(t);
		}
		
		if (type.equals("arrival"))
			return new Arrival(intTime, comp);
		else if (type.equals("departure"))
			return new Departure(intTime, comp);
		throw new IllegalStateException("Illegal event: " + type);
	}
}
