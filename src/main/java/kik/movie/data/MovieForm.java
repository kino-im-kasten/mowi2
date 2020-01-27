package kik.movie.data;

import org.springframework.web.multipart.MultipartFile;

/**
 * An {@link MovieForm} stores the user input for movie creations and edit's
 *
 * @author Felix Rülke
 * @version 0.1.1
 */
public class MovieForm {

	private String originalName;
	private String germanName;
	private String releaseYear;
	private String runTimeInMin;
	private String regie;
	private String descriptionText;
	private String trailer;
	private MultipartFile picture;

	/**
	 * Default constructor of an {@link MovieForm}
	 *
	 * @param originalName    Title in the native language
	 * @param germanName      German title
	 * @param releaseYear     Year when the movie was released
	 * @param regie           Name of the person who directed the movie
	 * @param descriptionText Sentences wich describe the Movie
	 * @param runTimeInMin    The runtime of the movie
	 * @param trailer	      A link to a movie trailer
	 * @param picture         A movie cover picture
	 */
	public MovieForm(String originalName, String germanName, String releaseYear, String regie, String descriptionText,
			String runTimeInMin, String trailer, MultipartFile picture) {

		this.originalName = originalName;
		this.germanName = germanName;
		this.releaseYear = releaseYear;
		this.regie = regie;
		this.descriptionText = descriptionText;
		this.runTimeInMin = runTimeInMin;
		this.picture = picture;
		this.trailer = trailer;
	}

	

	/**
	 * Getter for the German title
	 * 
	 * @return The German title
	 */
	public String getGermanName() {
		return this.germanName;
	}

	/**
	 * Getter for the original title
	 * 
	 * @return The original title
	 */
	public String getOriginalName() {
		return this.originalName;
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
	 * Getter for the release year
	 * 
	 * @return The release year
	 */
	public String getReleaseYear() {
		return this.releaseYear;
	}

	/**
	 * Getter for the runtime
	 * 
	 * @return The runtime in Minutes
	 */
	public String getRunTimeInMin() {
		return this.runTimeInMin;
	}

	/**
	 * Getter for the the picture from the user input
	 * 
	 * @return The picture file
	 */
	public MultipartFile getPicture() {
		return this.picture;
	}

	/**
	 * Setter for the the picture file
	 * 
	 * @param file The Picture data
	 */
	public void setPicture(MultipartFile file) {
		this.picture = file;
	}

	/**
	 * Setter for the railer link
	 * 
	 * @param trailer The trailer string
	 */
	public void setTrailer(String trailer) {
		this.trailer = trailer;
	}

	/**
	 * Getter for the trailer link
	 * 
	 * @return The trailer String
	 */
	public String getTrailer() {
		return this.trailer;
	}
}
