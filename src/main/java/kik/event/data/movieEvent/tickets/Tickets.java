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
package kik.event.data.movieEvent.tickets;

import kik.config.Configuration;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * {@link Tickets} to encapsulate the functionality of
 * calculations based on tickets.
 *
 * @author Georg Lauterbach
 * @version 0.1.1
 */
@Entity
public class Tickets {
	@Id
	@GeneratedValue
	private long id;
	
	private int cardFreeAmount;
	
	private int cardNormalStartNumber;
	private int cardNormalEndNumber;
	
	private int cardReducedStartNumber;
	private int cardReducedEndNumber;
	
	private double normalPrice;
	private double reducedPrice;
	
	private int normalCount;
	private double normalSum;
	
	private int reducedCount;
	private double reducedSum;
	
	private int freeAmount;
	
	/**
	 * Instantiates a new Tickets.
	 */
	public Tickets() {
		this.cardFreeAmount = 0;
		
		this.cardNormalStartNumber = 0;
		this.cardNormalEndNumber = 0;
		
		this.cardReducedStartNumber = 0;
		this.cardReducedEndNumber = 0;
		
		Configuration configuration = new Configuration();
		this.normalPrice = configuration.getDefaultNormalPrice();
		this.reducedPrice = configuration.getDefaultReducedPrice();
		
		this.normalCount = 0;
		this.normalSum = 0.0;
	}
	
	/**
	 * Gets card free amount.
	 *
	 * @return the card free amount
	 */
	public int getCardFreeAmount() {
		return this.cardFreeAmount;
	}
	
	/**
	 * Sets card free amount.
	 *
	 * @param cardFreeAmount the card free amount
	 */
	public void setCardFreeAmount(int cardFreeAmount) {
		this.cardFreeAmount = cardFreeAmount;
	}
	
	/**
	 * Gets card normal end number.
	 *
	 * @return the card normal end number
	 */
	public int getCardNormalEndNumber() {
		return this.cardNormalEndNumber;
	}
	
	/**
	 * Sets card normal end number.
	 *
	 * @param cardNormalEndNumber the card normal end number
	 */
	public void setCardNormalEndNumber(int cardNormalEndNumber) {
		this.cardNormalEndNumber = cardNormalEndNumber;
	}
	
	/**
	 * Gets card normal start number.
	 *
	 * @return the card normal start number
	 */
	public int getCardNormalStartNumber() {
		return this.cardNormalStartNumber;
	}
	
	/**
	 * Sets card normal start number.
	 *
	 * @param cardNormalStartNumber the card normal start number
	 */
	public void setCardNormalStartNumber(int cardNormalStartNumber) {
		this.cardNormalStartNumber = cardNormalStartNumber;
	}
	
	/**
	 * Gets card reduced end number.
	 *
	 * @return the card reduced end number
	 */
	public int getCardReducedEndNumber() {
		return this.cardReducedEndNumber;
	}
	
	/**
	 * Sets card reduced end number.
	 *
	 * @param cardReducedEndNumber the card reduced end number
	 */
	public void setCardReducedEndNumber(int cardReducedEndNumber) {
		this.cardReducedEndNumber = cardReducedEndNumber;
	}
	
	/**
	 * Gets card reduced start number.
	 *
	 * @return the card reduced start number
	 */
	public int getCardReducedStartNumber() {
		return this.cardReducedStartNumber;
	}
	
	/**
	 * Sets card reduced start number.
	 *
	 * @param cardReducedStartNumber the card reduced start number
	 */
	public void setCardReducedStartNumber(int cardReducedStartNumber) {
		this.cardReducedStartNumber = cardReducedStartNumber;
	}
	
	/**
	 * Gets normal price.
	 *
	 * @return the normal price
	 */
	public double getNormalPrice() {
		return this.normalPrice;
	}
	
	/**
	 * Sets normal price.
	 *
	 * @param normalPrice the normal price
	 */
	public void setNormalPrice(double normalPrice) {
		this.normalPrice = normalPrice;
	}
	
	/**
	 * Gets reduced price.
	 *
	 * @return the reduced price
	 */
	public double getReducedPrice() {
		return this.reducedPrice;
	}
	
	/**
	 * Sets reduced price.
	 *
	 * @param reducedPrice the reduced price
	 */
	public void setReducedPrice(double reducedPrice) {
		this.reducedPrice = reducedPrice;
	}
	
	/**
	 * Gets normal count.
	 *
	 * @return the normal count
	 */
	public int getNormalCount() {
		return this.normalCount;
	}
	
	/**
	 * Sets normal count.
	 *
	 * @param count the count
	 */
	public void setNormalCount(int count) {
		this.normalCount = count;
	}
	
	/**
	 * Gets normal sum.
	 *
	 * @return the normal sum
	 */
	public double getNormalSum() {
		return this.normalSum;
	}
	
	/**
	 * Sets normal sum.
	 *
	 * @param sum the sum
	 */
	public void setNormalSum(double sum) {
		this.normalSum = sum;
	}
	
	/**
	 * Gets reduced count.
	 *
	 * @return the reduced count
	 */
	public int getReducedCount() {
		return this.reducedCount;
	}
	
	/**
	 * Sets reduced count.
	 *
	 * @param reducedCount the reduced count
	 */
	public void setReducedCount(int reducedCount) {
		this.reducedCount = reducedCount;
	}
	
	/**
	 * Gets reduced sum.
	 *
	 * @return the reduced sum
	 */
	public double getReducedSum() {
		return this.reducedSum;
	}
	
	/**
	 * Sets reduced sum.
	 *
	 * @param reducedSum the reduced sum
	 */
	public void setReducedSum(double reducedSum) {
		this.reducedSum = reducedSum;
	}
	
	/**
	 * Gets free amount.
	 *
	 * @return the free amount
	 */
	public int getFreeAmount() {
		return this.freeAmount;
	}
	
	/**
	 * Sets free amount.
	 *
	 * @param freeAmount the free amount
	 */
	public void setFreeAmount(int freeAmount) {
		this.freeAmount = freeAmount;
	}
	
	/**
	 * Creates a new instance of {@link TicketsForm} from this
	 * object
	 *
	 * @return the tickets form
	 */
	public TicketsForm intoTicketsForm() {
		TicketsForm ticketsForm = new TicketsForm();
		
		ticketsForm.setCardFreeAmount(this.cardFreeAmount);
		ticketsForm.setCardNormalEndNumber(this.cardNormalEndNumber);
		ticketsForm.setCardNormalEndNumber(this.cardNormalEndNumber);
		ticketsForm.setCardNormalStartNumber(this.cardNormalStartNumber);
		ticketsForm.setCardReducedEndNumber(this.cardReducedEndNumber);
		ticketsForm.setCardReducedStartNumber(this.cardReducedStartNumber);
		
		ticketsForm.setNormalPrice(this.normalPrice);
		ticketsForm.setReducedPrice(this.reducedPrice);
		
		ticketsForm.setNormalCount(this.normalCount);
		ticketsForm.setNormalSum(this.normalSum);
		
		ticketsForm.setReducedCount(this.reducedCount);
		ticketsForm.setReducedSum(this.reducedSum);
		
		ticketsForm.setFreeAmount(this.freeAmount);
		
		return ticketsForm;
	}
	
	/**
	 * Updates the current instance with new data
	 *
	 * @param ticketsForm the tickets form
	 */
	public void update(TicketsForm ticketsForm) {
		setCardFreeAmount(ticketsForm.getCardFreeAmount());
		
		setCardNormalStartNumber(ticketsForm.getCardNormalStartNumber());
		setCardNormalEndNumber(ticketsForm.getCardNormalEndNumber());
		
		setCardReducedStartNumber(ticketsForm.getCardReducedStartNumber());
		setCardReducedEndNumber(ticketsForm.getCardReducedEndNumber());
		
		setNormalPrice(ticketsForm.getNormalPrice());
		setReducedPrice(ticketsForm.getReducedPrice());
		
		if (this.cardNormalStartNumber == 0 && this.cardNormalEndNumber == 0) {
			setNormalCount(0);
		} else {
			setNormalCount(this.cardNormalEndNumber - this.cardNormalStartNumber + 1);
		}
		setNormalSum(this.normalCount * this.normalPrice);
		
		if (this.cardReducedStartNumber == 0 && this.cardReducedEndNumber == 0) {
			setReducedCount(0);
		} else {
			setReducedCount(this.cardReducedEndNumber - this.cardReducedStartNumber + 1);
		}
		setReducedSum(this.reducedCount * this.reducedPrice);
		
		setCardFreeAmount(ticketsForm.getCardFreeAmount());
		setFreeAmount(ticketsForm.getFreeAmount());
	}
	
	/**
	 * Calculates the earned revenue from all tickets
	 *
	 * @return revenue / -1 of validation failed
	 */
	public double calculateRawTicketsRevenue() {
		if (!validate()) {
			return -1;
		}

		double normal = 0.0;
		double reduced = 0.0;

		if(cardNormalEndNumber != 0){
			normal = this.normalPrice * (this.cardNormalEndNumber - this.cardNormalStartNumber + 1);
		}
		if(cardReducedEndNumber != 0){
			reduced = this.reducedPrice * ((this.cardReducedEndNumber - this.cardReducedStartNumber + 1));
		}

		return normal + reduced;
	}
	
	/**
	 * Checks with simple logic whether ticket numbers have been inserted
	 *
	 * @return true when no errors occured, false otherwise
	 */
	public boolean validate() {
		boolean result = this.cardReducedEndNumber >= this.cardReducedStartNumber;
		result = result && this.cardNormalEndNumber >= this.cardNormalStartNumber;
		
		return result;
	}
	
	/**
	 * Checks tckets for events to be schown on the /index page.
	 *
	 * @return true if tickets okay
	 */
	public boolean validateForIndex() {
		boolean result = this.cardReducedEndNumber > 0 || this.cardNormalEndNumber > 0 || this.cardFreeAmount > 0;
		return !result;
	}
}
