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
package kik.event.data;

import kik.event.data.event.Event;

/**
 * An {@link Enum} to serve the planning status of
 * an {@link Event}
 *
 * @author Georg Lauterbach
 * @version 0.0.4
 */
public enum EventPlanningStatus {
	/**
	 * In planning event planning status.
	 */
	IN_PLANNING,
	/**
	 * Booked event planning status.
	 */
	BOOKED,
	/**
	 * Special event planning status.
	 */
	SPECIAL,
	/**
	 * Settled event planning status.
	 */
	SETTLED,
	/**
	 * Presented event planning status.
	 */
	PRESENTED,
	/**
	 * Canceled event planning status.
	 */
	CANCELED,
	/**
	 * Expired event planning status
	 */
	EXPIRED;
	
	/**
	 * Overwrite to output the status nicely formatted
	 *
	 * @return the status' name
	 */
	@Override
	public String toString() {
		String tmp = "";
		
		switch (this.name()) {
			case "IN_PLANNING": {
				tmp += "In Planung";
			} break;
			case "BOOKED": {
				tmp += "Gebucht";
			} break;
			case "SPECIAL": {
				tmp += "Spezial";
			} break;
			case "SETTLED": {
				tmp += "Abgebucht";
			} break;
			case "PRESENTED": {
				tmp += "Präsentiert";
			} break;
			case "CANCELED": {
				tmp += "Abgesagt";
			} break;
			case "EXPIRED": {
				tmp += "Abgelaufen";
			} break;
			default: {
				tmp += "Unknown! This should not have happened!";
			}
		}
		
		return tmp;
	}
}
