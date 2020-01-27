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
package kik.event.management;

import kik.event.data.event.Event;

/**
 * A static checker class for {@link Event}s
 *
 * @author Georg Lauterbach
 * @version 0.0.3
 */
public interface Check {
	/**
	 * Checks whether given arguments (preferrably in
	 * a constructor) are null
	 *
	 * @param objects A list of objects that are to be checked
	 * @return The boolean value whether objects are null or not
	 */
	static boolean checkOnNull(Object... objects) {
		for (Object obj : objects) {
			if (obj == null) {
				return true;
			}
		}
		
		return false;
	}
}
