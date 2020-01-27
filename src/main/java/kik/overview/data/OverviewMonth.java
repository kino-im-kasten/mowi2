package kik.overview.data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * An {@link OverviewMonth} holds all relevant information of a month
 *
 * @author Felix Rülke
 * @version 0.1.2
 */
@Entity
public class OverviewMonth {
    @Id
    private long id;
    private String month;
    private int year;
    private double sales = 0;
    private double expenses = 0;
    private int nrEvents = 0;
    private int nrMovieEvents = 0;
    private int visitorsTotal = 0;
    private int visitorsNormal = 0;
    private int visitorsReduced = 0;
    private int visitorsFree = 0;

    /**
     * Default constructor for a month overview
     * 
     * @param year            The year
     * @param month           The overview month
     * @param sales           The sales made in that month
     * @param expenses        The expenses made in that month
     * @param nrEvents        The amount of Events
     * @param nrMovieEvents   The amount of movieEvents
     * @param visitorsTotal   The total amount of visitors
     * @param visitorsNormal  The amount of visitors wich payed a normal price
     * @param visitorsReduced The amount of visitors wich payed a reduced price
     * @param visitorsFree    The amount of visitors wich payed nothing
     */
    public OverviewMonth(int year, String month, double sales, double expenses, int nrEvents, int nrMovieEvents,
            int visitorsTotal, int visitorsNormal, int visitorsReduced, int visitorsFree) {
        this.year = year;
        this.month = month;
        this.sales = sales;
        this.expenses = expenses;
        this.nrEvents = nrEvents;
        this.nrMovieEvents = nrMovieEvents;
        this.visitorsTotal = visitorsTotal;
        this.visitorsNormal = visitorsNormal;
        this.visitorsReduced = visitorsReduced;
        this.visitorsFree = visitorsFree;
    }

    /**
	 * Needed, not-used, non-deafult constructor of an {@link OverviewMonth}
	 */
    public OverviewMonth() {}

    /**
     * Getter for the id
     * 
     * @return The overview id
     */
    public long getId() {
        return this.id;
    }

    /**
     * Getter for the month
     * 
     * @return The month
     */
    public String getMonth() {
        return month;
    }

    /**
     * Getter for the year
     * 
     * @return The Year
     */
    public int getYear() {
        return year;
    }

    /**
     * Getter for the sales
     * 
     * @return The sales
     */
    public int getSales() {
        return (int) sales;
    }

    /**
     * Getter for the expenses
     * 
     * @return the expenses
     */
    public int getExpenses() {
        return (int) expenses;
    }

    /**
     * Getter for amount of events
     * 
     * @return the amount of events
     */
    public int getNrEvents() {
        return nrEvents;
    }

    /**
     * Getter for the amount of movie events
     * 
     * @return The number of movie events
     */
    public int getNrMovieEvents() {
        return nrMovieEvents;
    }

    /**
     * Getter for the amount of total visitors
     * 
     * @return The amount
     */
    public int getVisitorsTotal() {
        return visitorsTotal;
    }

    /**
     * Getter the amount of visitors wich payed a normal price
     * 
     * @return The amount
     */
    public int getVisitorsNormal() {
        return visitorsNormal;
    }

    /**
     * Getter for the amount of visitors wich payed a reduced pric
     * 
     * @return The amount
     */
    public int getVisitorsReduced() {
        return visitorsReduced;
    }

    /**
     * Getter for the amount of visitors wich payed a reduced pric
     * 
     * @return The amount
     */
    public int getVisitorsFree() {
        return visitorsFree;
    }
}