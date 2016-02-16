package shunting.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Schedule {
	private List<Departure> departures;
	private List<Arrival> arrivals;

	public Schedule() {
		departures = new ArrayList<>();
		arrivals = new ArrayList<>();
	}

	public Schedule(List<Arrival> arrivals, List<Departure> departures) {
		this.departures = departures;
		this.arrivals = arrivals;

		Collections.sort(departures);
		Collections.sort(arrivals);
	}

	public List<Arrival> arrivals() {
		return arrivals;
	}

	public List<Departure> departures() {
		return departures;
	}

	public Iterator<Event> events() {
		List<Event> events = new ArrayList<>(arrivals);
		events.addAll(departures);
		Collections.sort(events);
		return events.iterator();
	}

	@Override
	public String toString() {
		String s = "[";
		Iterator<Event> itEvents = events();
		while (itEvents.hasNext()) {
			Event ev = itEvents.next();
			s += ev.toString() + ", ";
		}
		if (s.length() > 1)
			s = s.substring(0, s.length()-2);
		s += "]";
		return s;
	}

	public static Schedule randomSchedule(int nrTrainUnits, int horizon, Random rn){ 

		List<Arrival> arrivals = new ArrayList<>();
		int unitsNow=0;

		while (unitsNow<nrTrainUnits){

			int arrivalTime=rn.nextInt(horizon)/2+1;

			CompositionFactory p=new CompositionFactory(rn);
			Composition virm=p.compVIRM();
			Composition ddz=p.compDDZ();
			Composition slt=p.compSLT();

			int answer = rn.nextInt(3) + 1;

			if (answer==1){
				unitsNow=unitsNow+virm.size();
				Arrival a=new Arrival(arrivalTime,virm);
				arrivals.add(a);}

			if (answer==2){
				unitsNow=unitsNow+ddz.size();
				Arrival b=new Arrival(arrivalTime,ddz);
				arrivals.add(b);}

			if (answer==3){
				unitsNow=unitsNow+slt.size();
				Arrival c=new Arrival(arrivalTime,slt);
				arrivals.add(c);}

		}

		List<Departure> departureVirm = new ArrayList<>();
		List<Departure> departureDdz = new ArrayList<>();
		List<Departure> departureSlt = new ArrayList<>();

		List<Train> listVirm = new ArrayList<>();
		List<Train> listDdz = new ArrayList<>();
		List<Train> listSlt = new ArrayList<>();

		for(Arrival i:arrivals){
			Composition comp=i.getComposition();

			for(int k=0;k<comp.size();k++){

				Train arrtrain=comp.getTrain(k);
				TrainType type=arrtrain.getTrainType();

				if(type.toString()=="VIRM_4" || type.toString()=="VIRM_6"){
					listVirm.add(arrtrain);
				}
				if(type.toString()=="DDZ_4" || type.toString()=="DDZ_6"){
					listDdz.add(arrtrain);
				}
				if(type.toString()=="SLT_4" || type.toString()=="SLT_6"){
					listSlt.add(arrtrain);
				}

			}	
		}

		while(listVirm.isEmpty()==false){
			int compSize = rn.nextInt(3) + 1;
			int departureTime=rn.nextInt(horizon+1)+horizon/2;

			if(compSize==1 || listVirm.size()==1){
				Train depTrain=listVirm.get(0);
				List <Train> trainComp= new ArrayList<>();
				trainComp.add(depTrain);
				Composition comp1=new Composition("id",trainComp);
				Departure depVIRM=new Departure(departureTime,comp1);
				departureVirm.add(depVIRM);
				listVirm.remove(0);
			}
			if(compSize==2 && listVirm.size()>1 || compSize==3 && listVirm.size()==2){
				Train depTrain1=listVirm.get(0);
				Train depTrain2=listVirm.get(1);
				List <Train> trainComp= new ArrayList<>();
				trainComp.add(depTrain1);
				trainComp.add(depTrain2);
				Composition comp1=new Composition("id",trainComp);
				Departure depVIRM=new Departure(departureTime,comp1);
				departureVirm.add(depVIRM);
				listVirm.remove(0);
				listVirm.remove(0);
			}
			if(compSize==3 && listVirm.size()>2){
				Train depTrain1=listVirm.get(0);
				Train depTrain2=listVirm.get(1);
				Train depTrain3=listVirm.get(2);
				List <Train> trainComp= new ArrayList<>();
				trainComp.add(depTrain1);
				trainComp.add(depTrain2);
				trainComp.add(depTrain3);
				Composition comp1=new Composition("id",trainComp);
				Departure depVIRM=new Departure(departureTime,comp1);
				departureVirm.add(depVIRM);
				listVirm.remove(0);
				listVirm.remove(0);
				listVirm.remove(0);
			}

		}
		
		while(listDdz.isEmpty()==false){
			int compSize = rn.nextInt(3) + 1;
			int departureTime=rn.nextInt(horizon+1)+horizon/2;

			if(compSize==1 || listDdz.size()==1){
				Train depTrain=listDdz.get(0);
				List <Train> trainComp= new ArrayList<>();
				trainComp.add(depTrain);
				Composition comp1=new Composition("id",trainComp);
				Departure depDDZ=new Departure(departureTime,comp1);
				departureDdz.add(depDDZ);
				listDdz.remove(0);
			}
			if(compSize==2 && listDdz.size()>1 || compSize==3 && listDdz.size()==2){
				Train depTrain1=listDdz.get(0);
				Train depTrain2=listDdz.get(1);
				List <Train> trainComp= new ArrayList<>();
				trainComp.add(depTrain1);
				trainComp.add(depTrain2);
				Composition comp1=new Composition("id",trainComp);
				Departure depDdz=new Departure(departureTime,comp1);
				departureDdz.add(depDdz);
				listDdz.remove(0);
				listDdz.remove(0);
			}
			if(compSize==3 && listDdz.size()>2){
				Train depTrain1=listDdz.get(0);
				Train depTrain2=listDdz.get(1);
				Train depTrain3=listDdz.get(2);
				List <Train> trainComp= new ArrayList<>();
				trainComp.add(depTrain1);
				trainComp.add(depTrain2);
				trainComp.add(depTrain3);
				Composition comp1=new Composition("id",trainComp);
				Departure depDdz=new Departure(departureTime,comp1);
				departureDdz.add(depDdz);
				listDdz.remove(0);
				listDdz.remove(0);
				listDdz.remove(0);
			}

		}
		
		while(listSlt.isEmpty()==false){
			int compSize = rn.nextInt(3) + 1;
			int departureTime=rn.nextInt(horizon+1)+horizon/2;

			if(compSize==1 || listSlt.size()==1){
				Train depTrain=listSlt.get(0);
				List <Train> trainComp= new ArrayList<>();
				trainComp.add(depTrain);
				Composition comp1=new Composition("id",trainComp);
				Departure depSlt=new Departure(departureTime,comp1);
				departureSlt.add(depSlt);
				listSlt.remove(0);
			}
			if(compSize==2 && listSlt.size()>1 || compSize==3 && listSlt.size()==2){
				Train depTrain1=listSlt.get(0);
				Train depTrain2=listSlt.get(1);
				List <Train> trainComp= new ArrayList<>();
				trainComp.add(depTrain1);
				trainComp.add(depTrain2);
				Composition comp1=new Composition("id",trainComp);
				Departure depSlt=new Departure(departureTime,comp1);
				departureSlt.add(depSlt);
				listSlt.remove(0);
				listSlt.remove(0);
			}
			if(compSize==3 && listSlt.size()>2){
				Train depTrain1=listSlt.get(0);
				Train depTrain2=listSlt.get(1);
				Train depTrain3=listSlt.get(2);
				List <Train> trainComp= new ArrayList<>();
				trainComp.add(depTrain1);
				trainComp.add(depTrain2);
				trainComp.add(depTrain3);
				Composition comp1=new Composition("id",trainComp);
				Departure depSlt=new Departure(departureTime,comp1);
				departureSlt.add(depSlt);
				listSlt.remove(0);
				listSlt.remove(0);
				listSlt.remove(0);
			}


		}
		List<Departure> departure = new ArrayList<Departure>();
		departure.addAll(departureVirm);
		departure.addAll(departureDdz);
		departure.addAll(departureSlt);
		
		Collections.sort(arrivals);
		Collections.sort(departure);
		
		Schedule randomSchedule=new Schedule(arrivals,departure);
		
		return randomSchedule;
		
	}
	
	public boolean ScheduleFeasible(ShuntingYard bink){

		int trackLength=0;
		List <ShuntTrack> tracks=bink.getShuntTracks();
		for(int i=0;i<tracks.size();i++){
			int length=tracks.get(i).getCapacity();
			trackLength=trackLength+length;
		}

		int trainLength=0;
		for(Arrival i:arrivals){
			Composition comp=i.getComposition();

			for(int k=0;k<comp.size();k++){
				Train arrtrain=comp.getTrain(k);
				int length=arrtrain.getTrainType().getTrainLength();
				trainLength=trainLength+length;
			}
		}

		if(trainLength<trackLength){
			return true;
		}
		else{
			return false;
		}
	}
	

}
