package kik.overview.management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import kik.event.data.event.EventRepository;
import kik.overview.data.OverviewMonth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.Month;
import java.util.ArrayList;

@SpringBootTest
@Transactional
public class OverviewManagementTest {
    private OverviewManagement ovManagement;
    private EventRepository eventRepository;

    @Autowired
    OverviewManagementTest(OverviewManagement ovManagement, EventRepository eventRepository) {
        this.ovManagement = ovManagement;
        this.eventRepository = eventRepository;
    }

    @Test
    void uOverviewManagementMonthSwitchP1() {
        assertEquals("overview.month.january", OverviewManagement.monthToString(Month.JANUARY));
        assertEquals("overview.month.february", OverviewManagement.monthToString(Month.FEBRUARY));
        assertEquals("overview.month.march", OverviewManagement.monthToString(Month.MARCH));
        assertEquals("overview.month.april", OverviewManagement.monthToString(Month.APRIL));
        assertEquals("overview.month.may", OverviewManagement.monthToString(Month.MAY));
        assertEquals("overview.month.june", OverviewManagement.monthToString(Month.JUNE));
        assertEquals("overview.month.july", OverviewManagement.monthToString(Month.JULY));
        assertEquals("overview.month.august", OverviewManagement.monthToString(Month.AUGUST));
        assertEquals("overview.month.september", OverviewManagement.monthToString(Month.SEPTEMBER));
        assertEquals("overview.month.october", OverviewManagement.monthToString(Month.OCTOBER));
        assertEquals("overview.month.november", OverviewManagement.monthToString(Month.NOVEMBER));
        assertEquals("overview.month.december", OverviewManagement.monthToString(Month.DECEMBER));
    }

    @Test
    void uOverviewManagementMonthSwitchN1() {
        assertEquals("overview.month.unknown", OverviewManagement.monthToString(null));
    
    }


    @Test
    void uOverviewManagementCreateNewP1() {

        assertTrue("The overview list should be empty", this.ovManagement.resetList().size() == 0);
        assertTrue("There should be events present", !this.eventRepository.findAll().isEmpty());
        
        ArrayList<OverviewMonth> ovList = this.ovManagement.getOverviewList();
        assertTrue("message", ovList.size() > 0);
    }

    @Test
    void uOverviewManagementRoundingP1() {
        assertEquals(1.235, OverviewManagement.round(1.23456789, 3), 3);
        assertEquals(1.6, OverviewManagement.round(1.5555555, 1), 1);
    }
}
