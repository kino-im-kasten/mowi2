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

/**
 * An Enum that contains all the states a {@link Booking} may have.
 *
 * @author Erik Schönherr
 */
public enum BookingState {
	PENDING,
	ACTIVE,
	OVER,
	CANCELED,
	SETTLEDUP;

	/**
	 * Translates a {@link BookingState} to a String to show it in the frontend.
	 *
	 * @return state as string
	 */
	@Override
	public String toString() {
		String stateString;

		switch(this.name()){
			case "PENDING":
				stateString = "Anstehend";
				break;
			case "ACTIVE":
				stateString = "Aktiv";
				break;
			case "OVER":
				stateString = "Abgelaufen";
				break;
			case "CANCELED":
				stateString = "Abgesagt";
				break;
			case "SETTLEDUP":
				stateString = "Abgerechnet";
				break;
			default: throw new IllegalStateException();
		}
		return stateString;
	}
}
