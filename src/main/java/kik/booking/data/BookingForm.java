/*
 * Copyright 2014-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kik.booking.data;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * A {@link BookingForm} to store and work with the data the user
 * puts in during the creation or the editing of a booking.
 *
 * @author Erik Schönherr
 */
public class BookingForm {
	private String tbNumber;

	private long movieID;
	private long distributorID;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;

	private Conditions conditions;

	public BookingForm() {

	}

	/**
	 * Constructor of a booking form
	 *
	 * @param tbNumber tb-number of the {@link Booking}
	 * @param movieID id of the movie related to the {@link Booking}
	 * @param distributorID distributor related to the {@link Booking}
	 * @param startDate starting time and date (as {@link LocalDate})
	 * @param endDate ending time and date (as {@link LocalDate})
	 * @param conditions conditions of the {@link Booking})
	 */
	public BookingForm(String tbNumber, long movieID, long distributorID, LocalDate startDate,
					   LocalDate endDate, Conditions conditions) {
		this.tbNumber = tbNumber;
		this.movieID = movieID;
		this.distributorID = distributorID;
		this.startDate = startDate;
		this.endDate = endDate;
		this.conditions = conditions;
	}

	/**
	 * Sets tb number.
	 *
	 * @param tbNumber the tb number
	 */
	public void setTbNumber(String tbNumber) {
		this.tbNumber = tbNumber;
	}

	/**
	 * Sets movie id.
	 *
	 * @param movieID the movie id
	 */
	public void setMovieID(long movieID) {
		this.movieID = movieID;
	}

	/**
	 * Sets distributor id.
	 *
	 * @param distributorID the distributor id
	 */
	public void setDistributorID(long distributorID) {
		this.distributorID = distributorID;
	}

	/**
	 * Sets start date.
	 *
	 * @param startDate the start date
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * Sets the end date.
	 *
	 * @param endDate the end date
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	/**
	 * Sets the conditions.
	 *
	 * @param conditions the conditions
	 */
	public void setConditions(Conditions conditions) {
		this.conditions = conditions;
	}

	/**
	 * Gets the tb number.
	 *
	 * @return the tb number
	 */
	public String getTbNumber() {
		return tbNumber;
	}

	/**
	 * Gets the movie id.
	 *
	 * @return the movie id
	 */
	public long getMovieID() {
		return movieID;
	}

	/**
	 * Gets distributor id.
	 *
	 * @return the distributor id
	 */
	public long getDistributorID() {
		return distributorID;
	}

	/**
	 * Gets the start date.
	 *
	 * @return the start date
	 */
	public LocalDate getStartDate() {
		return startDate;
	}

	/**
	 * Gets the end date.
	 *
	 * @return the end date
	 */
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * Gets the conditions.
	 *
	 * @return the conditions
	 */
	public Conditions getConditions() {
		return conditions;
	}

	@Override
	public String toString(){
		return "tbNumber: " + tbNumber + ", movieID: " + movieID + ", distributorID: " + distributorID + ", startDate: "
			+ startDate + ", endDate: " + endDate + ", conditions: " + conditions;
	}
}
