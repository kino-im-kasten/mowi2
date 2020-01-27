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

import kik.booking.data.BookingForm;
import kik.distributor.data.DistributorRepository;
import kik.movie.data.MovieRepository;
import org.springframework.validation.Errors;

/**
 * A Validation class for checking {@link BookingForm}s.
 *
 * @author Erik Schönherr
 */
class BookingValidation {
	/**
	 * Validates a given {@link BookingForm}.
	 *
	 * @param form the form that will get validated
	 * @param mr a {@link MovieRepository} to check if a movie with the given id exists
	 * @param dr a {@link DistributorRepository} to check if a distributor with the given id exists
	 * @param errors Possible errors during the filling of the form. If the validation isn't positive rejected values
	 *                  are added to this object.
	 */
	void validate(BookingForm form, MovieRepository mr, DistributorRepository dr, Errors errors) {
		//checks that the tb-number isn't blank
		if(form.getTbNumber().isBlank()) {
			errors.rejectValue("tbNumber", "booking.error.not_empty");
		}

		//checks that the start date isn't after the end date
		if(form.getEndDate().compareTo(form.getStartDate()) < 0) {
			errors.rejectValue("endDate", "booking.error.illegal_date");
		}

		if(form.getConditions().getPercentage() > 100) {
			errors.rejectValue("conditions.percentage", "booking.error.too_high_percentage");
		}

		//checks that all the conditions aren't negative
		if(form.getConditions().getMinimumGuarantee() < 0) {
			errors.rejectValue("conditions.minimumGuarantee", "booking.error.not_negative");
		}
		if(form.getConditions().getPercentage() < 0) {
			errors.rejectValue("conditions.percentage", "booking.error.not_negative");
		}
		if(form.getConditions().getFreight() < 0) {
			errors.rejectValue("conditions.freight", "booking.error.not_negative");
		}
		if(form.getConditions().getAdvertisement() < 0) {
			errors.rejectValue("conditions.advertisement", "booking.error.not_negative");
		}
		if(form.getConditions().getSpio() < 0) {
			errors.rejectValue("conditions.spio", "booking.error.not_negative");
		}
		if(form.getConditions().getOther() < 0) {
			errors.rejectValue("conditions.other", "booking.error.not_negative");
		}

		//checks if movie and distributor repositories contain an entity with the given id
		if(mr.findById(form.getMovieID()).isEmpty()) {
			errors.rejectValue("movieID", "booking.error.illegal_id");
		}
		if(dr.findById(form.getDistributorID()).isEmpty()) {
			errors.rejectValue("distributorID", "booking.error.illegal_id");
		}
	}
}
