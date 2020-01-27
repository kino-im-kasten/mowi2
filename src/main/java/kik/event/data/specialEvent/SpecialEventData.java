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
package kik.event.data.specialEvent;

import kik.event.data.EventType;
import kik.event.data.event.EventData;

import javax.validation.constraints.NotNull;
import java.time.OffsetTime;

/**
 * A {@link SpecialEventData} model, containing every information
 * needed to create a {@link SpecialEvent}
 * <p>
 * Used to transport information across the initializers and management
 * and in the end to create a real {@link SpecialEvent}
 *
 * @author Georg Lauterbach
 * @version 0.0.2
 */
public class SpecialEventData extends EventData {
	
	private OffsetTime expectedEnd;
	private String description;
	
	/**
	 * Default constructor of an {@link SpecialEventData}
	 * <p>
	 * There are no default parameters there, as everything is
	 * managed via getters and setters.
	 */
	public SpecialEventData() {
		setEventType(EventType.SPECIAL);
	}
	
	public OffsetTime getExpectedEnd() {
		return this.expectedEnd;
	}
	
	public void setExpectedEnd(OffsetTime expectedEnd) {
		this.expectedEnd = expectedEnd;
	}
	
	/**
	 * Gets description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets description.
	 *
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Override to not accidentaly overwrite the actual
	 * {@link EventType}
	 *
	 * @param type type of an event
	 */
	@Override
	public void setEventType(@NotNull EventType type) {
		if (type == null || type.equals(EventType.MOVIE)) {
			return;
		}
		
		super.setEventType(type);
	}
}
