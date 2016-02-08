package shunting.models;

import java.util.List;

public class TrackAssignment {
		
		private List<PriceNode> nodes;
		private ShuntTrack track;
		
		public TrackAssignment(List<PriceNode> nodes, ShuntTrack track) {
			this.nodes = nodes;
			this.track = track;
		}
		
		@Override
		public boolean equals(Object other) {
			if (other == null || !(other instanceof TrackAssignment))
				return false;
			TrackAssignment ass = (TrackAssignment) other;
			if (!nodes.equals(ass.nodes))
				return false;
			return track.equals(ass.track);
		}
		
		@Override
		public int hashCode() {
			return 3*nodes.hashCode() + 7*track.hashCode();
		}
	}