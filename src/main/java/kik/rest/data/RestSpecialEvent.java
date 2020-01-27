package kik.rest.data;
import kik.event.data.event.Event;
import kik.event.data.specialEvent.SpecialEvent;

/**
 * {@link RestSpecialEvent} will be used by spring to create a json string
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
public class RestSpecialEvent extends RestEvent {
	private String description;
	/**
	 * Default constructor of {@link RestSpecialEvent}
	 * 
	 * @param event The {@link Event} to parse
	 */
    public RestSpecialEvent(Event event) {
		super(event);
        if (event instanceof SpecialEvent) {
            this.description = ((SpecialEvent) event).getDescription();
        }
		
    }

	/**
     * Getter for the description
     * 
     * @return The description
     */
    public String getDescription() {
		return this.description;
	}
}
