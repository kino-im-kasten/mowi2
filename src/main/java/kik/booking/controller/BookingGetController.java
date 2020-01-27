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
import kik.booking.data.BookingState;
import kik.booking.management.BookingFilter;
import kik.booking.management.BookingManagement;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * The controller handling all the get requests for bookings.
 *
 * @author Erik Schönherr
 */
@Controller
public class BookingGetController {

	/**
	 * Defines how many bookings will get shown per page.
	 */
	private final int bookingsPerPage = 15;

	private final BookingManagement bookingManagement;

	/**
	 * Default constructor for the BookingGetController.
	 *
	 * @param bookingManagement Management layer to distribute functionality
	 */
	public BookingGetController(BookingManagement bookingManagement) {
		Assert.notNull(bookingManagement, "bookingManagement must not be null! " +
			"[CLASS::METHOD kik.booking.controller.BookingGetController::BookingGetController\"");

		this.bookingManagement = bookingManagement;
	}

	/**
	 * Handles the request for HTTP::GET on /createBooking
	 *
	 * @param model Model of HTML5 page
	 * @return name of the html-file in the webserver's file-root folder
	 */
	@GetMapping(value = "/createBooking")
	@PreAuthorize("hasRole('ORGA')")
	public String createBooking(Model model) {
		if(!model.containsAttribute("bookingForm")) {
			model.addAttribute("bookingForm", new BookingForm());
		}

		model.addAttribute("movies", bookingManagement.getAllMovies());
		model.addAttribute("distributors", bookingManagement.getAllDistributors());

		return "booking/createBooking";
	}

	/**
	 * Handles the request for HTTP::GET on /bookings/edit
	 *
	 * @param id Id of the booking that will be edited
	 * @param model Model of HTML5 page
	 * @return name of the html-file in the webserver's file-root folder
	 */
	@GetMapping(value = "bookings/edit")
	@PreAuthorize("hasRole('ORGA')")
	public String editBooking(@RequestParam long id, Model model) {
		Booking booking = bookingManagement.getById(id).orElseThrow();

		if(!model.containsAttribute("bookingForm")) {
			BookingForm bookingForm = bookingManagement.fillBookingForm(booking);

			model.addAttribute("bookingForm", bookingForm);
		}

		model.addAttribute("booking", booking);
		model.addAttribute("movies", bookingManagement.getAllMovies());
		model.addAttribute("distributors", bookingManagement.getAllDistributors());

		return "booking/editBooking";
	}

	/**
	 * Handles the request for HTTP::GET on /bookings
	 *
	 * @param model Model of HTML5 page
	 * @param sort name of the attribute to sort by
	 * @param asc decides whether to sort in ascending or descending order
	 * @param page current-page
	 * @param input filter String given by the User
	 * @return name of the html-file in the webserver's file-root folder
	 */
	@GetMapping(value = "/bookings")
	@PreAuthorize("hasRole('USER')")
	public String showBookings(Model model,
							   @RequestParam(required = false) String sort,
							   @RequestParam(required = false) Boolean asc,
							   @RequestParam(required = false) String page,
							   @RequestParam(required = false) String input) {
		Streamable<Booking> bookings;
		int pageNumber = getPageNumber(page);

		if(sort == null){
			model.addAttribute("asc", true);
			model.addAttribute("sortedBy", "movie.germanName");
			bookings = bookingManagement.getAllBookings("movie.germanName", true);
		} else {
			model.addAttribute("asc", asc);
			model.addAttribute("sortedBy", sort);
			bookings = bookingManagement.getAllBookings(sort, asc);
		}

		bookings = putAllFinishedButNotSettledUpBookingsFirst(bookings);
		addBookingListModelAttributes(model, bookings, input, pageNumber);

		return "booking/bookings";
	}

	/**
	 * Handles the request for HTTP::GET on /bookings/current
	 *
	 * @param model Model of HTML5 page
	 * @param sort name of the attribute to sort by
	 * @param asc decides whether to sort in ascending or descending order
	 * @param page current-page
	 * @param input filter String given by the User
	 * @return name of the html-file in the webserver's file-root folder
	 */
	@GetMapping(value = "bookings/current")
	@PreAuthorize("hasRole('USER')")
	public String showCurrentBookings(Model model,
									  @RequestParam(required = false) String sort,
									  @RequestParam(required = false) Boolean asc,
									  @RequestParam(required = false) String page,
									  @RequestParam(required = false) String input) {
		Streamable<Booking> bookings;
		int pageNumber = getPageNumber(page);

		if(sort == null) {
			model.addAttribute("asc", true);
			model.addAttribute("sortedBy", "startDate");
			bookings = bookingManagement.getAllNotArchivedBookings("startDate", true);
		} else {
			model.addAttribute("asc", asc);
			model.addAttribute("sortedBy", sort);
			bookings = bookingManagement.getAllNotArchivedBookings(sort, asc);
		}

		bookings = putAllFinishedButNotSettledUpBookingsFirst(bookings);
		addBookingListModelAttributes(model, bookings, input, pageNumber);

		return "booking/current-bookings";
	}

	/**
	 * Handles the request for HTTP::GET on /bookings/booking
	 *
	 * @param model Model of HTML5 page
	 * @param id Id of the booking that will get opened
	 * @return name of the html-file in the webserver's file-root folder
	 */
	@GetMapping(value= "bookings/booking")
	@PreAuthorize("hasRole('USER')")
	public String showBookingDetails(Model model, @RequestParam long id) {
		model.addAttribute("booking", bookingManagement.getById(id).orElseThrow());

		return "booking/booking-details";
	}

	/**
	 * Handles the request for HTTP::GET on /bookings/settleup
	 *
	 * @param id Id of the booking that will get opened
	 * @param redirectAttributes {@link RedirectAttributes} to add model attributes before redirect
	 * @return redirect to the booking detail page
	 */
	@GetMapping(value= "bookings/settleup")
	@PreAuthorize("hasRole('USER')")
	public String settleUp(@RequestParam long id, RedirectAttributes redirectAttributes) {
		String errorMessage = bookingManagement.settleUp(id);

		if(!errorMessage.isEmpty()) {
			redirectAttributes.addFlashAttribute("settleUpError", errorMessage);
		}

		return "redirect:/bookings/booking?id=" + id;
	}

	/**
	 * Handles the request for HTTP::GET on /bookings/undo-settleup
	 *
	 * @param id Id of the booking that will get opened
	 * @return redirect to the booking detail page
	 */
	@GetMapping(value= "bookings/undo-settleup")
	@PreAuthorize("hasRole('USER')")
	public String undoSettleUp(@RequestParam long id) {
		bookingManagement.undoSettleUp(id);

		return "redirect:/bookings/booking?id=" + id;
	}

	/**
	 * Handles the request for HTTP::GET on /undoCancelBooking
	 *
	 * @param id Id of the booking that will get canceled
	 * @return redirect to the booking detail page
	 */
	@GetMapping(value= "bookings/undoCancel")
	@PreAuthorize("hasRole('ADMIN')")
	public String undoCancelBooking(@RequestParam long id){
		bookingManagement.undoCancelBooking(id);

		return "redirect:/bookings/booking?id=" + id;
	}


	/**
	 * Adds multiple attributes to the model. Used in booking overview pages.
	 *
	 * @param model Model
	 * @param bookings unfiltered bookings
	 * @param input a String that the user has put in to filter the bookings
	 * @param page current page number
	 */
	private void addBookingListModelAttributes(Model model, Streamable<Booking> bookings, String input, int page){
		if(input != null){
			BookingFilter filter = new BookingFilter();
			bookings = filter.filter(bookings, input);
		} else {
			input = "";
		}

		model.addAttribute("input", input);
		model.addAttribute("lastPage", lastPage(bookings, page));
		model.addAttribute("bookings", pageSizeFilter(bookings, page));
		model.addAttribute("page", page);
	}

	/**
	 * Returns the page-number as int by a given String. Handles edge cases where the page should just be set to 1.
	 *
	 * @param page current page
	 * @return the current page as int
	 */
	private int getPageNumber(String page){
		int pageNumber;

		if(page != null){
			pageNumber = Integer.parseInt(page);
			if(pageNumber < 1){
				pageNumber = 1;
			}
		} else {
			pageNumber = 1;
		}

		return pageNumber;
	}

	/**
	 * Filters a given {@link Streamable} of {@link Booking}s to only show bookings of the specified page.
	 *
	 * @param bookings bookings to be filtered
	 * @param page count of the page
	 * @return filtered bookings
	 */
	private Streamable<Booking> pageSizeFilter(Streamable<Booking> bookings, int page){
		int pageIndex = page - 1;

		if(pageIndex < 0){
			return Streamable.empty();
		}

		int bookingAmount = bookings.toList().size();

		int firstIndex = pageIndex * bookingsPerPage;
		int lastIndex = (pageIndex + 1) * bookingsPerPage;

		if(firstIndex >= bookingAmount){
			return Streamable.empty();
		}
		if(lastIndex > bookingAmount){
			lastIndex = bookingAmount;
		}

		List<Booking> bookingsOnPage = bookings.toList().subList(firstIndex, lastIndex);

		return Streamable.of(bookingsOnPage);
	}

	/**
	 * Checks if the currently opened page is the last.
	 *
	 * @param bookings all bookings
	 * @param page current page number
	 * @return true if it's the last page
	 */
	private boolean lastPage(Streamable<Booking> bookings, int page){
		int pageIndex = page - 1;


		int bookingAmount = bookings.toList().size();

		int lastIndex = (pageIndex + 1) * bookingsPerPage;

		return lastIndex > bookingAmount;
	}

	/**
	 * Takes a {@link Streamable} of bookings and puts all finished but not settled up bookings at the start.
	 *
	 * @param bookings bookings to be sorted
	 * @return sorted bookings
	 */
	private Streamable<Booking> putAllFinishedButNotSettledUpBookingsFirst(Streamable<Booking> bookings){
		List<Booking> bookingList = new ArrayList<>(bookings.toList());
		List<Booking> fittingBookings = new ArrayList<>();

		// from back to start of list to keep the sorting at the end when adding the elements back in
		for(int i = bookingList.size() - 1; i >= 0; i--){
			Booking b = bookingList.get(i);
			if(b.getState() == BookingState.OVER){
				fittingBookings.add(b);
			}
		}

		for(Booking b : fittingBookings){
			bookingList.remove(b);
			bookingList.add(0, b);
		}

		return Streamable.of(bookingList);
	}
}