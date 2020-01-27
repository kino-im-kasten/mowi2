package kik.config;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for the settings.html page
 */
@Controller
public class ConfigurationController {

	private final Configuration configuration;

	/**
	 * default constructor for {@link ConfigurationController}
	 *
	 * @param configuration {@link Configuration} holds data of configuration.xml file
	 */
	public ConfigurationController(Configuration configuration) {
		this.configuration = configuration;
	}


	/**
	 * handles get requests to /settings
	 *
	 * @param model data attribute model of HTML5 page
	 * @return "settings.html"
	 */
	@GetMapping("/settings")
	@PreAuthorize("hasRole('ADMIN')")
	public String settings(Model model){
		model.addAttribute("configuration", configuration);
		return "settings.html";
	}

	/**
	 * handles post requests to /settings/refresh
	 * loads the configuration.xml file into the {@link Configuration} object
	 *
	 * @param model data attribute model of HTML5 page
	 * @return "settings.html"
	 */
	@PostMapping("/settings/refresh")
	@PreAuthorize("hasRole('ADMIN')")
	public String refreshSettings(Model model){
		model.addAttribute("loadSuccess",configuration.loadFromFile());
		model.addAttribute("configuration", configuration);
		return "settings.html";
	}
}
