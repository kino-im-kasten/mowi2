package kik.overview.data;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@Transactional
public class OverviewMonthTest {

    @Test
    void uOverviewMonthGetterTestP1() {
        OverviewMonth ov = new OverviewMonth(2000, "January", 2.0, 3.0, 1, 2, 3, 4, 5, 6);
        assertEquals(2000, ov.getYear());
        assertEquals("January", ov.getMonth());
        assertEquals(2.0, ov.getSales(), 0.00001);
        assertEquals(3.0, ov.getExpenses(), 0.00001);
        assertEquals(1, ov.getNrEvents());
        assertEquals(2, ov.getNrMovieEvents());
        assertEquals(3, ov.getVisitorsTotal());
        assertEquals(4, ov.getVisitorsNormal());
        assertEquals(5, ov.getVisitorsReduced());
        assertEquals(6, ov.getVisitorsFree());
    }
}