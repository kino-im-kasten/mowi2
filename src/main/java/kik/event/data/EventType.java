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
 * An {@link EventType} to encapsulate the various
 * types of events
 *
 * @author Georg Lauterbach
 * @version 0.0.5
 */
public enum EventType {
	/**
	 * Movie event type.
	 */
	MOVIE,
	/**
	 * Special event type.
	 */
	SPECIAL,
	/**
	 * Intern event type.
	 */
	INTERN;
	
	/**
	 * Returns the {@link EventType} for a given {@link Event}
	 *
	 * @param typeAsString type formatted as {@link String}
	 * @return the corresponding {@link EventType}
	 */
	public static EventType toType(String typeAsString) {
		EventType eventType;
		if (typeAsString == null) {
			return EventType.SPECIAL;
		}
		
		if (typeAsString.equals("INTERN")) {
			eventType = EventType.INTERN;
		} else {
			eventType = EventType.SPECIAL;
		}
		
		return eventType;
	}
	
	/**
	 * Overwrite to output the status nicely formatted
	 *
	 * @return the status' name
	 */
	@Override
	public String toString() {
		switch (this.name()) {
			case "SPECIAL": return "Spezialveranstaltung";
			case "INTERN": return "Interne Veranstaltung";
			default: return "Film";
		}
	}
}
