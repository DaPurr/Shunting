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

import shunting.models.Composition;
import shunting.models.Schedule;
import shunting.models.Train;
import shunting.models.TrainFactory;

public class ScheduleReader {

	private Schedule schedule;
	
	public ScheduleReader() {
		
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
		Map<String, Composition> compMap = new HashMap<>();
		NodeList trains = doc.getElementsByTagName("composition");
		int n = trains.getLength();
		for (int i = 0; i < n; i++) {
			Node m = trains.item(i);
			Composition comp = xmlToComposition(m);
			compMap.put(comp.getID(), comp);
		}
		
		return new Schedule();
	}
	
	private Train xmlToTrain(Node n) {
		NamedNodeMap attr = n.getAttributes();
		String ID = attr.getNamedItem("ID").getNodeValue();
		TrainFactory tf = new TrainFactory();
		boolean interchangeable = Boolean.parseBoolean(attr.getNamedItem("interchangeable").getNodeValue());
		boolean inspection = Boolean.parseBoolean(attr.getNamedItem("inspection").getNodeValue());
		boolean repair = Boolean.parseBoolean(attr.getNamedItem("repair").getNodeValue());
		boolean cleaning = Boolean.parseBoolean(attr.getNamedItem("cleaning").getNodeValue());
		boolean washing = Boolean.parseBoolean(attr.getNamedItem("inspection").getNodeValue());
		String type = attr.getNamedItem("type").getNodeValue();
		String subtype = attr.getNamedItem("subtype").getNodeValue();
		
		return tf.createTrainByType(ID, type+"_"+subtype, interchangeable, 
				inspection, repair, cleaning, washing);
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
}
