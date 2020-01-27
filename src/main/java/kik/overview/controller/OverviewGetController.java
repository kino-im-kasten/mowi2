package kik.overview.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kik.overview.management.OverviewManagement;

/**
 * {@link OverviewGetController} handling HTTP::GET requests for the overview
 *
 * @author Felix Rülke
 * @version 0.1.2
 */
@Controller
class OverviewGetController {

    private OverviewManagement management;

    /**
     * Default constructor of an {@link OverviewGetController}
     *
     * @param management Management layer to distribute functionality
     */
    OverviewGetController(OverviewManagement management) {
        this.management = management;
    }

    /**
     * Handles the request for HTTP::GET on /movies
     *
     * @param model Model of HTML5 page
     * @return Name of the html-file in the webserver's file-root folder
     */
    @GetMapping(value = "/overview")
    @PreAuthorize("hasRole('USER')")
    String movieOverview(Model model) {
        model.addAttribute("overviewList", this.management.getOverviewList());
        return "overview";
    }
}