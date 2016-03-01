package shunting;

import java.util.ArrayList;
import java.util.List;

import shunting.models.FreeShuntTrack;
import shunting.models.Platform;
import shunting.models.ShuntTrack;
import shunting.models.ShuntingYard;
import shunting.models.Washer;

public class Initialisation_procedure {
	int horizon;
	
	public  ShuntingYard initialisation(int horizon) {
		Washer s63 = new Washer(horizon);
		Platform s62 = new Platform(horizon);
		Platform s61 = new Platform(horizon);
		FreeShuntTrack s58 = new FreeShuntTrack(203);
		FreeShuntTrack s57 = new FreeShuntTrack(202);
		FreeShuntTrack s56 = new FreeShuntTrack(222);
		FreeShuntTrack s55 = new FreeShuntTrack(357);
		FreeShuntTrack s54 = new FreeShuntTrack(387);
		FreeShuntTrack s53 = new FreeShuntTrack(431);
		FreeShuntTrack s52 = new FreeShuntTrack(480);

		List<Washer> washers1 = new ArrayList<>();
		washers1.add(s63);

		List<Platform> platforms1 = new ArrayList<>();
		platforms1.add(s62);
		platforms1.add(s61);

		List<ShuntTrack> tracks1 = new ArrayList<>();
		tracks1.add(s58);
		tracks1.add(s57);
		tracks1.add(s56);
		tracks1.add(s55);
		tracks1.add(s54);
		tracks1.add(s53);
		tracks1.add(s52);

		ShuntingYard kb = new ShuntingYard(platforms1, washers1, tracks1);
		return kb;
	}

	
	

}
