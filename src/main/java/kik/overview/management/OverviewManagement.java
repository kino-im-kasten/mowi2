package kik.overview.management;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import kik.event.data.event.EventRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import kik.booking.data.Booking;
import kik.booking.data.BookingRepository;
import kik.booking.data.BookingState;
import kik.booking.data.Conditions;
import kik.event.data.EventPlanningStatus;
import kik.event.data.event.Event;
import kik.event.data.movieEvent.MovieEvent;
import kik.event.data.movieEvent.tickets.Tickets;
import kik.overview.data.OverviewMonth;

/**
 * {@link OverviewManagement} Manages the data from the controller
 *
 * @author Felix Rülke
 * @version 0.1.1
 */
@Service
public class OverviewManagement {

    private EventRepository eventRepository;
    private BookingRepository bookingRepository;
    private ArrayList<OverviewMonth> overviewList;

    /**
     * Default constructor of an {@link OverviewManagement}
     * 
     * @param eventRepository   A repository containing all {@link Event}'s
     * @param bookingRepository A repository containing all {@link Booking}'s
     */
    OverviewManagement(EventRepository eventRepository, BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.overviewList = new ArrayList<>();
    }

    // called every two hours
    /**
     * Creates overviews for everything in the database
     */
    @Scheduled(cron = "0 0 */2 * * *")
    public void createOverview() {
        this.overviewList.clear();

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        int amountYears = 1; // 1 year back

        for (int offset = amountYears; offset >= 0; offset--) {
            for (Month month : Month.values()) {
                // creates overviews offset years back until the current month
                if (offset == 0 && month.getValue() <= currentMonth + 1) {
                    createOverviewSpecific(month, year - offset);
                } else if (offset > 0) {
                    createOverviewSpecific(month, year - offset);
                }
            }
        }

        // for thymeleaf to get the right order
        Collections.reverse(this.overviewList);
    }

    /**
     * Creates an overview for a specific month in a year
     * 
     * @param month The month
     * @param year  The year
     */
    private void createOverviewSpecific(Month month, int year) {
        double sales = 0;
        double expenses = 0;
        int nrEvents = 0;
        int nrMovieEvents = 0;
        int visitorsTotal = 0;
        int visitorsNormal = 0;
        int visitorsFree = 0;
        int visitorsReduced = 0;

        // sometimes findAll returns duplicates
        // Set<Long> eventIds = new HashSet<>();
        // Set<Long> bookingIds = new HashSet<>();

        for (Event event : this.eventRepository.findAll()) {
            // if(eventIds.contains(event.getId())) {
            // continue;
            // }
            // eventIds.add(event.getId());

            LocalDate date = event.getDate();

            // if event lies in the given month and year then continue
            if (date.getMonth() == month && date.getYear() == year
                    && event.getEventPlanningStatus() != EventPlanningStatus.CANCELED) {

                nrEvents++;

                if (event instanceof MovieEvent) {
                    nrMovieEvents++;

                    Tickets tickets = ((MovieEvent) event).getTickets();
                    visitorsFree += tickets.getCardFreeAmount();
                    visitorsNormal += tickets.getNormalCount();
                    visitorsReduced += tickets.getReducedCount();

                    double revenue = ((MovieEvent) event).calculateRawTicketsRevenue();
                    sales += revenue >= 0 ? revenue : 0;
                }

                // stuff for other event types goes here
            }
        }

        visitorsTotal = visitorsFree + visitorsReduced + visitorsNormal;
        expenses = calculateBookingExpenses(month, year);

        // dont create an overview if nothing happened
        if (nrEvents != 0) {
            this.overviewList.add(new OverviewMonth(year, monthToString(month), sales, expenses, nrEvents,
                    nrMovieEvents, visitorsTotal, visitorsNormal, visitorsReduced, visitorsFree));
        }
    }

    /**
     * Rounds a double
     * 
     * @param value         The number to round
     * @param decimalPoints Round by how many
     */
    public static double round(double value, int decimalPoints) {
        double d = Math.pow(10, decimalPoints);
        return Math.round(value * d) / d;
    }

    /**
     * Resolves month into strings which that can be resolved in the
     * messages.properties
     * 
     * @param month The month to resolve
     * @return The string
     */
    static public String monthToString(Month month) {
        // thx sonarqube 
        String m;

        if (month == null) {
            return "overview.month.unknown";
        }

        switch (month) {
            case JANUARY:
                m = "overview.month.january";
                break;
            case FEBRUARY:
                m = "overview.month.february";
                break;
            case MARCH:
                m = "overview.month.march";
                break;
            case APRIL:
                m = "overview.month.april";
                break;
            case MAY:
                m = "overview.month.may";
                break;
            case JUNE:
                m = "overview.month.june";
                break;
            case JULY:
                m = "overview.month.july";
                break;
            case AUGUST:
                m = "overview.month.august";
                break;
            case SEPTEMBER:
                m = "overview.month.september";
                break;
            case OCTOBER:
                m = "overview.month.october";
                break;
            case NOVEMBER:
                m = "overview.month.november";
                break;
            case DECEMBER:
                m = "overview.month.december";
                break;
            default:
                m = "overview.month.unknown";
        }

        return m;
    }

    /**
     * Returns all Overviews
     * 
     * @return A list of {@link OverviewMonth}
     */
    public ArrayList<OverviewMonth> getOverviewList() {
        if (this.overviewList.size() == 0) {
            this.createOverview();
        }

        return this.overviewList;
    }

    /**
     * Is called when a data change happened to update the overviews
     */
    public void update() {
        this.createOverview();
    }

    private double calculateBookingExpenses(Month month, int year) {
        double expenses = 0;

        for (Booking booking : this.bookingRepository.findAll()) {
            // if (bookingIds.contains(booking.getId())) {
            // continue;
            // }
            // bookingIds.add(booking.getId());

            Month bookingMonth = booking.getEndDate().getMonth();
            int bookingYear = booking.getEndDate().getYear();

            // expenses will be assigned to the month the booking was created
            if (bookingMonth == month && bookingYear == year) {
                Conditions conditions = booking.getConditions();
                expenses += conditions.getAdvertisement() + conditions.getFreight() + conditions.getSpio()
                        + conditions.getOther();
            }

            // in the month with the booking end date, the rest will be calculated
            if (bookingMonth == month && bookingYear == year && booking.getState() != BookingState.CANCELED) {

                double nettoSum = booking.getTotalRevenue();

                if (nettoSum > 0) {
                    // calculate the remaining expenses according to the conditions

                    double MwStBruttoNetto = (float) round(nettoSum * 0.07f, 2);
                    double bruttoSum = nettoSum - MwStBruttoNetto; // round error here
                    bruttoSum = round(bruttoSum, 2);

                    float percent = booking.getConditions().getPercentage() / 100f;
                    double minimum = booking.getConditions().getMinimumGuarantee();
                    double percentageOfBrutto = bruttoSum * percent;
                    expenses += minimum > percentageOfBrutto ? minimum : percentageOfBrutto;

                } else {
                    // if there was no income, just add the minimum guarantee
                    expenses += booking.getConditions().getMinimumGuarantee();
                }
            }
        }

        return expenses;
    }


    /**
     * Function for testing purposes only
     */
    public ArrayList<OverviewMonth> resetList() {
        this.overviewList.clear();
        return this.overviewList;
    }
}