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

package kik.booking.management;

import kik.booking.data.Booking;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

/**
 * Class for handling all filter requests for bookings. Currently contains more features than needed.
 *
 * @author Erik Schönherr
 */
public class BookingFilter {
	/**
	 * Filters the given bookings by the given input String. It checks if the movie name (original or german) or the
	 * ditributor name contains the input String. Large / Lower case doesn't matter.
	 *
	 * @param bookings to be filtered bookings
	 * @param input filter String
	 * @return filtered bookings as {@link Streamable}
	 */
	public Streamable<Booking> filter(Streamable<Booking> bookings, String input) {
		Assert.notNull(input, "input must not be null!");

		final String searchString = input.toLowerCase();

		return bookings.filter(b -> {
			String germanMovieName = b.getMovie().getGermanName().toLowerCase();
			String distributorName = b.getDistributor().getName().toLowerCase();
			String tbNumber = b.getTbNumber();

			String originalMovieName = b.getMovie().getOriginalName();

			if(originalMovieName != null){
				originalMovieName = originalMovieName.toLowerCase();
			} else {
				originalMovieName = germanMovieName;
			}

			return
				germanMovieName.contains(searchString) ||
				originalMovieName.contains(searchString) ||
				distributorName.contains(searchString) ||
				tbNumber.contains(searchString);
		});
	}
}