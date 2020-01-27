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

package kik.booking.controller;

import kik.booking.data.Booking;
import kik.booking.data.BookingForm;
import kik.booking.management.BookingManagement;
import kik.overview.management.OverviewManagement;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * The controller handling all the post requests for bookings.
 *
 * @author Erik Schönherr
 */
@Controller
public class BookingPostController {

	private final BookingManagement bookingManagement;
	private final OverviewManagement overviewManagement;

	/**
	 * Default constructor for the BookingPostController.
	 *
	 * @param bookingManagement  Management layer to distribute functionality
	 * @param overviewManagement Management layer of the overview
	 */
	public BookingPostController(BookingManagement bookingManagement, OverviewManagement overviewManagement) {
		Assert.notNull(bookingManagement, "bookingManagement must not be null!");
		Assert.notNull(overviewManagement, "overviewManagement must not be null!");

		this.bookingManagement = bookingManagement;
		this.overviewManagement = overviewManagement;
	}

	/**
	 * Handles the request for HTTP::POST on /createBooking
	 * To create a new {@link Booking}
	 *
	 * @param bookingForm Holds neccessary parameters for creation of an {@link Booking}
	 * @param binding Possible, propagated errors during the filling of the form
	 * @param redirectAttributes {@link RedirectAttributes} to add model attributes before redirect
	 * @return A redirect to the detail page or to /createBooking when there are errors.
	 */
	@PostMapping(value = "/createBooking")
	@PreAuthorize("hasRole('ORGA')")
	String createBooking(@Valid @ModelAttribute("bookingForm") BookingForm bookingForm,
						 BindingResult binding, RedirectAttributes redirectAttributes) {
		Booking booking = bookingManagement.createBooking(bookingForm, binding);

		if (binding.hasErrors()) {
			redirectAttributes.addFlashAttribute("bookingForm", bookingForm);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookingForm", binding);

			return "redirect:/createBooking";
		}

		this.overviewManagement.update();
		return "redirect:/bookings/booking?id=" + booking.getId();
	}

	/**
	 * Handles the request for HTTP::POST on /bookings/edit
	 * To create a new {@link Booking}
	 *
	 * @param id Id of the booking that will be edited
	 * @param bookingForm Holds neccessary parameters for creation of an {@link Booking}
	 * @param binding Possible, propagated errors during the filling of the form
	 * @param redirectAttributes {@link RedirectAttributes} to add model attributes before redirect
	 * @return A redirect to the detail page or to /bookings/edit when there are errors.
	 */
	@PostMapping(value = "bookings/edit")
	@PreAuthorize("hasRole('ORGA')")
	String editBooking(@RequestParam("id") long id, @Valid @ModelAttribute("bookingForm") BookingForm bookingForm,
					   BindingResult binding, RedirectAttributes redirectAttributes) {
		bookingManagement.editBooking(id, bookingForm, binding);

		if (binding.hasErrors()) {
			redirectAttributes.addFlashAttribute("bookingForm", bookingForm);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.bookingForm", binding);

			return "redirect:/bookings/edit?id=" + id;
		}

		this.overviewManagement.update();
		return "redirect:/bookings/booking?id=" + id;
	}


	/**
	 * Handles the request for HTTP::POST on /bookings/cancel
	 *
	 * @param id Id of the booking that will get canceled
	 * @param request {@link HttpServletRequest} to check for user role in management
	 * @param redirectAttributes {@link RedirectAttributes} to add model attributes before redirect
	 * @return redirect to the booking detail page
	 */
	@PostMapping(value= "bookings/cancel")
	@PreAuthorize("hasRole('ORGA')")
	public String cancelBooking(@RequestParam long id, HttpServletRequest request,
								RedirectAttributes redirectAttributes) {
		String errorMessage = bookingManagement.cancelBooking(id, request);

		if(!errorMessage.isEmpty()) {
			redirectAttributes.addFlashAttribute("cancelError", errorMessage);
		}

		this.overviewManagement.update();
		return "redirect:/bookings/booking?id=" + id;
	}

	/**
	 * Handles the request for HTTP::POST on /bookings to show all the bookings filtered by an input string
	 *
	 * @param input String input by the user to filter by
	 * @return redirect to the booking overview page
	 */
	@PostMapping(value = "/bookings")
	@PreAuthorize("hasRole('USER')")
	String filterBookings(@RequestParam String input) {
		return "redirect:/bookings?input=" + input;
	}

	/**
	 * Handles the request for HTTP::POST on /bookings/current to show all the current bookings filtered by an input string
	 *
	 * @param input String input by the user to filter by
	 * @return redirect to the current booking overview page
	 */
	@PostMapping(value = "/bookings/current")
	@PreAuthorize("hasRole('USER')")
	String filterCurrentBookings(@RequestParam String input) {
		return "redirect:/bookings/current?input=" + input;
	}
}