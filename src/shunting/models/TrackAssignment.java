package shunting.models;

public class TrackAssignment {
		
		private ShuntTrack track;
		private Path path;
		
		public TrackAssignment(ShuntTrack track, Path path) {
			this.track = track;
			this.path = path;
		}
		
		public ShuntTrack getTrack() {
			return track;
		}
		
		public Path getPath() {
			return path;
		}
		
		@Override
		public String toString() {
			return "{" + track.toString() + ", " + path.toString() + "}";
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof TrackAssignment))
				return false;
			TrackAssignment other = (TrackAssignment) o;
			return this.track.equals(other.track) &&
					this.path.equals(path);
		}
		
		@Override
		public int hashCode() {
			return 3*track.hashCode() + 7*path.hashCode();
		}
	}