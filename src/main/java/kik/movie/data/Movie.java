package kik.movie.data;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import kik.event.data.movieEvent.MovieEvent;
import kik.picture.data.PictureRepository;

/**
 * An {@link Movie} holds all basic movie information
 *
 * @author Felix Rülke
 * @version 0.1.2
 */
@Entity
public class Movie {

	@Id
	@GeneratedValue
	private long id;
	@Column(length=1001)
	private String originalName;
	@Column(length=1001)
	private String germanName;
	private String releaseYear;
	@Column(length=1001)
	private String regie;
	@Lob
	@Column(length=15001)
	private String descriptionText;
	@Column(length=1001)
	private String trailer;
	private int runTimeInMin;
	private long pictureId;
	@Lob
	private HashSet<Long> linkedEvents;

	/**
	 * Needed, not-used, non-deafult constructor of an {@link Movie}
	 */
	public Movie() {
	}

	/**
	 * Default constructor for a {@link Movie}
	 *
	 * @param form      Stores the user input
	 * @param pictureId Id of the picture from {@link PictureRepository}
	 */
	public Movie(MovieForm form, long pictureId) {
		editData(form, pictureId);

		if (this.originalName == null) {
			this.originalName = "";
		}
		if (this.regie == null || this.regie.equals("")) {
			this.regie = "diverse";
		}
		if (this.descriptionText == null || this.descriptionText.equals("")) {
			this.descriptionText = "-";
		}
		if (this.trailer == null || this.trailer.equals("")) {
			this.trailer = "-";
		}

		this.linkedEvents = new HashSet<>();
	}

	/**
	 * Alternative constructor for a {@link Movie}. Only used by the
	 * {@link MovieInitializer}
	 * 
	 * @param origName    The native moviename
	 * @param deName      The German name
	 * @param year        The release year
	 * @param regie       The person who directed the movie
	 * @param description A movie description
	 * @param runtime     Length of the movie in minutes
	 * @param trailer     A link to a movie trailer
	 * @param pictureId   Id of the picture from {@link PictureRepository}
	 */
	public Movie(String origName, String deName, String year, String regie, String description, int runtime,
			String trailer, long pictureId) {
		this.originalName = origName;
		this.germanName = deName;
		this.releaseYear = year;
		this.regie = regie;
		this.descriptionText = description;
		this.runTimeInMin = runtime;
		this.trailer = trailer;
		this.pictureId = pictureId;
		this.linkedEvents = new HashSet<>();
	}

	/**
	 * Used to edit all movie data
	 * 
	 * @param form      An {@link MovieForm} which stores the user input
	 * @param pictureId Id of the picture from {@link PictureRepository}
	 */
	public void editData(MovieForm form, long pictureId) {
		this.originalName = form.getOriginalName();
		this.germanName = form.getGermanName().strip();
		this.releaseYear = form.getReleaseYear().strip();
		this.regie = form.getRegie();
		this.descriptionText = form.getDescriptionText();
		this.runTimeInMin = Integer.parseInt(form.getRunTimeInMin());
		this.trailer = form.getTrailer();
		this.pictureId = pictureId;
	}

	/**
	 * Getter for the id
	 * 
	 * @return Movie id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Getter for the native moviename
	 * 
	 * @return The native moviename
	 */
	public String getOriginalName() {
		return this.originalName;
	}

	/**
	 * Getter for the German name
	 * 
	 * @return The German name
	 */
	public String getGermanName() {
		return this.germanName;
	}

	/**
	 * Getter for the release year
	 * 
	 * @return The release year
	 */
	public String getReleaseYear() {
		return this.releaseYear;
	}

	/**
	 * Getter for the regie
	 * 
	 * @return The director
	 */
	public String getRegie() {
		return this.regie;
	}

	/**
	 * Getter for the description Text
	 * 
	 * @return The description text
	 */
	public String getDescriptionText() {
		return this.descriptionText;
	}

	/**
	 * Getter for the runtime
	 * 
	 * @return The runtime in Minutes
	 */
	public int getRunTimeInMin() {
		return this.runTimeInMin;
	}

	/**
	 * Getter for the the id
	 * 
	 * @return The id
	 */
	public int getNId() {
		return (int) this.id;
	}

	public String getTrailer() {
		return this.trailer;
	}

	/**
	 * Getter for the the picture id from {@link PictureRepository}
	 * 
	 * @return The id
	 */
	public long getPictureId() {
		return this.pictureId;
	}

	/**
	 * Returns the id's of all events that show this movie
	 * 
	 * @return Set of id's
	 */
	public Set<Long> getLinkedEvents() {
		return this.linkedEvents;
	}

	/**
	 * Adds an id from an event
	 * 
	 * @param id The event id
	 */
	public void addEventId(long id) {
		this.linkedEvents.add(id);
	}

	/**
	 * Removes an event from a movie
	 * 
	 * @param id The event id
	 * @return Returns true if an existing id has been removed
	 */
	public Boolean removeEventId(long id) {
		if (this.linkedEvents.contains(id)) {
			return this.linkedEvents.remove(id);
		}

		return false;
	}

	/**
	 * Returns a statement if this {@link Movie} is connected to an
	 * {@link MovieEvent}
	 * 
	 * @param id The {@link MovieEvent} id
	 * @return Returns true if the given {@link MovieEvent} id is connected
	 */
	public boolean linkedWithEvent(long id) {
		return this.linkedEvents.contains(id);
	}

	/**
	 * Returns a statement if this {@link Movie} is connected to Events
	 * 
	 * @return True if there are {@link MovieEvent} connectes
	 */
	public boolean linkedWithEvents() {
		return !this.linkedEvents.isEmpty();
	}

	/**
	 * Compares a given {@link Movie} object
	 * 
	 * @param o The {@link Movie} object
	 * @return Returns true if both movies have the same attributes
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Movie)) {
			return false;
		}

		Movie movie = (Movie) o;

		// thanks to sonarqube for this masterpiece (otherwise to many equals)
		boolean equals = this.id == movie.id;
		equals = equals && this.originalName.equals(movie.getOriginalName());
		equals = equals && this.germanName.equals(movie.getGermanName());
		equals = equals && this.releaseYear.equals(movie.getReleaseYear());
		equals = equals && this.regie.equals(movie.getRegie());
		equals = equals && this.descriptionText == movie.getDescriptionText();
		equals = equals && Float.compare(this.runTimeInMin, movie.getRunTimeInMin()) == 0;
		equals = equals && Float.compare(this.pictureId, movie.getPictureId()) == 0;
		equals = equals && this.linkedEvents.equals(movie.getLinkedEvents());

		return equals;
	}
}
