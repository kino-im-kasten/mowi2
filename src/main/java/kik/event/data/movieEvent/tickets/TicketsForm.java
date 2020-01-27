/*
 * Copyright 2019 the original author or authors.
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

/**
 * A {@link TicketsForm} to work with user-input
 * when creating new {@link Tickets}
 * <p>
 * There is no businell logic to be implemented here.
 *
 * @author Georg Laueterbach
 * @version 0.1.0
 */
public class TicketsForm {
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
	 * Gets card normal end number.
	 *
	 * @return the card normal end number
	 */
	public int getCardNormalEndNumber() {
		return cardNormalEndNumber;
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
	 * @param normalCount the normal count
	 */
	public void setNormalCount(int normalCount) {
		this.normalCount = normalCount;
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
	 * @param normalSum the normal sum
	 */
	public void setNormalSum(double normalSum) {
		this.normalSum = normalSum;
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
	 * Checks on equality
	 *
	 * @param obj {@link TicketsForm} to be checked
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TicketsForm)) {
			return false;
		}
		
		TicketsForm ticketsForm = (TicketsForm) obj;
		boolean ret = true;
		
		if (this.cardFreeAmount != ticketsForm.getCardFreeAmount()) {
			ret = false;
		} else if (this.cardReducedStartNumber != ticketsForm.getCardReducedStartNumber()) {
			ret = false;
		} else if (this.cardReducedEndNumber != ticketsForm.getCardReducedEndNumber()) {
			ret = false;
		} else if (this.cardNormalStartNumber != ticketsForm.getCardNormalStartNumber()) {
			ret = false;
		} else if (this.cardNormalEndNumber != ticketsForm.getCardNormalEndNumber()) {
			ret = false;
		}
		
		return ret;
	}
}
