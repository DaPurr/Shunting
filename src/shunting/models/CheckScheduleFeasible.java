package shunting.models;

import java.util.List;

//tests whether all arrivals (combined) fit on the tracks

public class CheckScheduleFeasible {

	public boolean ScheduleFeasible(Schedule sc, ShuntingYard bink){

		int trackLength=0;
		List <ShuntTrack> tracks=bink.getShuntTracks();
		for(int i=0;i<tracks.size();i++){
			int length=tracks.get(i).getCapacity();
			trackLength=trackLength+length;
		}

		int trainLength=0;
		List <Arrival> arrivals=sc.arrivals();
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
