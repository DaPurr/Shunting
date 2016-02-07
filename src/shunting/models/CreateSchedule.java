package shunting.models;

import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CreateSchedule {

	private int nrComp;
	private int finalTime; // until when compositions can arrive

	public List<Composition> RandomCompositions(int nrComp){ //create compositions nrComp times

		List<Composition> compos = new ArrayList<>(); //list of arriving compositions

		for (int i = 0; i < nrComp; i++) {

			CompositionFactory p=new CompositionFactory(100);
			Composition virm=p.compVIRM();
			Composition ddz=p.compDDZ();
			Composition slt=p.compSLT();

			Random rn = new Random();
			int answer = rn.nextInt(4) + 1;

			if (answer==1)
				compos.add(virm);
			if (answer==2)
				compos.add(ddz);
			if (answer==3)
				compos.add(slt);
		}
		return compos;
	}



	public List<Integer> Arrivaltimes (int nrComp,int finalTime){ //create random arrival times corresponding to compositions

		List <Integer> times=new ArrayList<>();

		for (int i = 0; i < nrComp; i++) {
			Random rn = new Random();
			int a = rn.nextInt(finalTime+1) + 1;
			times.add(a);
		}

		return times;

	}

}


