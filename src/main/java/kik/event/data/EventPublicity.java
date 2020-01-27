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
 * An {@link Enum} to serve the {@link Event}'s publicity
 *
 * @author Georg Lauterbach
 * @version 0.0.4
 */
public enum EventPublicity {
	/**
	 * Public event publicity.
	 */
	PUBLIC,
	/**
	 * Private event publicity.
	 */
	PRIVATE;
	
	/**
	 * Returns the {@link EventPublicity} for a given {@link Event}
	 *
	 * @param publicityAsString publicity formatted as {@link String}
	 * @return the corresponding {@link EventPublicity}
	 */
	public static EventPublicity toPublicity(String publicityAsString) {
		if (publicityAsString == null || publicityAsString.equals("PRIVATE")) {
			return EventPublicity.PRIVATE;
		}
		
		return EventPublicity.PUBLIC;
	}
	
	/**
	 * Overwrite to output the status nicely formatted
	 *
	 * @return the status' name
	 */
	@Override
	public String toString() {
		if (this.name().equals("PUBLIC")) {
			return "Öffentlich";
		}
		
		return "Privat";
	}
}
