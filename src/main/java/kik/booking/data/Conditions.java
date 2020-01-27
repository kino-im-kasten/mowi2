package kik.booking.data;

import java.util.Objects;

/**
 * A class that is responsible for saving the condition data of a {@link Booking}.
 *
 * @author Erik Schönherr
 */
public class Conditions {
	private float minimumGuarantee;
	private float percentage;
	private float freight;
	private float advertisement;
	private float spio;
	private float other;

	/**
	 * Default constructor of {@link Conditions}.
	 */
	public Conditions(){

	}

	/**
	 * Smaller constructor that initializes all the attributes that are often 0 as 0.
	 *
	 * @param minimumGuarantee Minimum guarantee of the booking
	 * @param percentage Percentage of the booking, in percent
	 */
	public Conditions(float minimumGuarantee, float percentage) {
		this.minimumGuarantee = minimumGuarantee;
		this.percentage = percentage;

		this.freight = 0;
		this.advertisement = 0;
		this.spio = 0;
		this.other = 0;
	}

	/**
	 * Constructor for immediately setting all attributes
	 *
	 * @param minimumGuarantee Minimum guarantee of the booking
	 * @param percentage Percentage of the booking, in percent
	 * @param freight Freight costs of the booking
	 * @param advertisement Advertisement costs of the booking
	 * @param spio SPIO of the booking
	 * @param other Other costs of the booking
	 */
	public Conditions(float minimumGuarantee, float percentage, float freight, float advertisement, float spio,
					  float other){
		this.minimumGuarantee = minimumGuarantee;
		this.percentage = percentage;

		this.freight = freight;
		this.advertisement = advertisement;
		this.spio = spio;
		this.other = other;
	}

	/**
	 * Sets the minimum guarantee.
	 *
	 * @param minimumGuarantee the minimum guarantee
	 */
	public void setMinimumGuarantee(float minimumGuarantee) {
		this.minimumGuarantee = minimumGuarantee;
	}

	/**
	 * Sets the percentage.
	 *
	 * @param percentage the percentage
	 */
	public void setPercentage(float percentage) {
		this.percentage = percentage;
	}

	/**
	 * Sets the freight.
	 *
	 * @param freight the freight
	 */
	public void setFreight(float freight) {
		this.freight = freight;
	}

	/**
	 * Sets the advertisement.
	 *
	 * @param advertisement the advertisement
	 */
	public void setAdvertisement(float advertisement) {
		this.advertisement = advertisement;
	}

	/**
	 * Sets the spio.
	 *
	 * @param spio the spio
	 */
	public void setSpio(float spio) {
		this.spio = spio;
	}

	/**
	 * Sets the other costs.
	 *
	 * @param other the other costs
	 */
	public void setOther(float other) {
		this.other = other;
	}

	/**
	 * Gets the minimum guarantee.
	 *
	 * @return the minimum guarantee
	 */
	public float getMinimumGuarantee() {
		return minimumGuarantee;
	}

	/**
	 * Gets the percentage.
	 *
	 * @return the percentage
	 */
	public float getPercentage() {
		return percentage;
	}

	/**
	 * Gets the freight.
	 *
	 * @return the freight
	 */
	public float getFreight() {
		return freight;
	}

	/**
	 * Gets the advertisement.
	 *
	 * @return the advertisement
	 */
	public float getAdvertisement() {
		return advertisement;
	}

	/**
	 * Gets the spio.
	 *
	 * @return the spio
	 */
	public float getSpio() {
		return spio;
	}

	/**
	 * Gets the other costs.
	 *
	 * @return the other costs
	 */
	public float getOther() {
		return other;
	}

	@Override
	public String toString(){
		return "Conditions with minimum guarantee: " + minimumGuarantee + ", percentage: " + percentage + ", freight: "
			+ freight + ", advertisement: " + advertisement + ", spio: " + spio + ", other: " + other;
	}

	@Override
	public int hashCode(){
		return Objects.hash(minimumGuarantee, percentage, freight, advertisement, spio, other);
	}

	@Override
	public boolean equals(Object o){
		if(o.getClass() != Conditions.class) {
			return false;
		}

		Conditions c2 = (Conditions) o;

		float epsilon = 0.001f;

		//looks ugly but sonarqube likes it
		boolean equals = Math.abs(minimumGuarantee - c2.getMinimumGuarantee()) < epsilon ;

		equals = equals && Math.abs(percentage - c2.getPercentage()) < epsilon;
		equals = equals && Math.abs(freight - c2.getFreight()) < epsilon;
		equals = equals && Math.abs(advertisement - c2.getAdvertisement()) < epsilon;
		equals = equals && Math.abs(spio - c2.getSpio()) < epsilon;
		equals = equals && Math.abs(other - c2.getOther()) < epsilon;

		return equals;
	}
}
