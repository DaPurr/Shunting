package shunting.data;

import java.io.File;
import java.io.IOException;
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
	
	public ScheduleReader() {
		trainCache = new HashMap<>();
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
		// read compositions
		Map<String, Composition> compMap = new HashMap<>();
		NodeList trains = doc.getElementsByTagName("composition");
		int n = trains.getLength();
		for (int i = 0; i < n; i++) {
			Node m = trains.item(i);
			Composition comp = xmlToComposition(m);
			compMap.put(comp.getID(), comp);
		}
		
		// read events
		List<Arrival> arrivals = new ArrayList<>();
		List<Departure> departures = new ArrayList<>();
		NodeList events = doc.getElementsByTagName("events").item(0).getChildNodes();
		int k = events.getLength();
		for (int i = 0; i < k; i++) {
			Node m = events.item(i);
			if (m.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (m.getNodeName().equals("arrival"))
				arrivals.add((Arrival)xmlToEvent(m, compMap));
			else if (m.getNodeName().equals("departure"))
				departures.add((Departure)xmlToEvent(m, compMap));
			else
				throw new IllegalStateException("Event not defined: " + m.getNodeName());
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
		TrainFactory tf = new TrainFactory();
		boolean interchangeable = Boolean.parseBoolean(attr.getNamedItem("interchangeable").getNodeValue());
		boolean inspection = Boolean.parseBoolean(attr.getNamedItem("inspection").getNodeValue());
		boolean repair = Boolean.parseBoolean(attr.getNamedItem("repair").getNodeValue());
		boolean cleaning = Boolean.parseBoolean(attr.getNamedItem("cleaning").getNodeValue());
		boolean washing = Boolean.parseBoolean(attr.getNamedItem("inspection").getNodeValue());
		String type = attr.getNamedItem("type").getNodeValue();
		String subtype = attr.getNamedItem("subtype").getNodeValue();
		
		Train t = tf.createTrainByType(ID, type+"_"+subtype, interchangeable, 
				inspection, repair, cleaning, washing);
		trainCache.put(ID, t);
		
		return t;
	}
	
	private Composition xmlToComposition(Node n) {
		NodeList trains = n.getChildNodes();
		NamedNodeMap attr = n.getAttributes();
		String ID = attr.getNamedItem("ID").getNodeValue();
		List<Train> trainList = new ArrayList<>();
		int k = trains.getLength();
		for (int i = 0; i < k; i++) {
			Node m = trains.item(i);
			if (m.getNodeType() != Node.ELEMENT_NODE)
				continue;
			Train t = xmlToTrain(m);
			trainList.add(t);
		}
		return new Composition(ID, trainList);
	}
	
	private Event xmlToEvent(Node n, Map<String, Composition> compMap) {
		NamedNodeMap attr = n.getAttributes();
		String type = n.getNodeName();
		String compID = attr.getNamedItem("compID").getNodeValue();
		double time = Double.parseDouble(attr.getNamedItem("time").getNodeValue());
		if (type.equals("arrival"))
			return new Arrival(time, compMap.get(compID));
		else if (type.equals("departure"))
			return new Departure(time, compMap.get(compID));
		throw new IllegalStateException("Illegal event: " + type);
	}
}
