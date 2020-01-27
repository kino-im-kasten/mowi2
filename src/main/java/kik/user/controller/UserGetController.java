package kik.user.controller;

import kik.user.data.forms.*;
import kik.user.data.user.User;
import kik.user.data.usertype.UserType;
import kik.user.management.UserManagement;
import kik.user.management.UserTypeManagement;
import org.springframework.data.util.Streamable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Controller to manage all get requests concerning the users / usertypes and login
 *
 * @author Richard Müller
 * @version 0.0.1
 */
@Controller
public class UserGetController {

	private final UserManagement userManagement;
	private final UserTypeManagement userTypeManagement;

	/**
	 * default constructor for {@link UserGetController}
	 *
	 * @param userManagement management layer for managing {@link User}s
	 * @param userTypeManagement management layer for managing {@link UserType}
	 */
	public UserGetController(UserManagement userManagement, UserTypeManagement userTypeManagement) {
		this.userManagement = userManagement;
		this.userTypeManagement = userTypeManagement;
	}

	/**
	 * handles requests to enter the login page
	 *
	 * @param request for setting an error session attribute
	 * @return custom login page with no displayed errors
	 */
	@GetMapping({"/login", "/login/{error}"})
	public String login(HttpServletRequest request,@PathVariable(value = "error") Optional<String> loginError) {
		request.getSession().setAttribute("loginError",
			(loginError.isPresent() && loginError.get().equals("error")));
		return "user/login";
	}

	/**
	 * handles requests to enter the documentation page
	 *
	 * @return "documentation.html"
	 */
	@GetMapping("/documentation")
	public String documentation(){
		return "documentation.html";
	}

	/**
	 * handles requests to "/user"
	 * shows an overview of all {@link User} and a form to create new {@link User}s
	 *
	 * @param model source of information for the html template
	 * @param form {@link CreateNewUserForm} for handling input for creating {@link User}s
	 * @param searchInput String to search for {@link User}s
	 * @return user/user.html template
	 */
	@GetMapping({"/user", "/user/{searchInput}"})
	@PreAuthorize("hasRole('ADMIN')")
	public String users(Model model, CreateNewUserForm form,
						@PathVariable(value = "searchInput") Optional<String> searchInput,
						HttpServletRequest request){
		Streamable<User> users;
		if(request.getSession().getAttribute("showDeletedUsers") == null){
			users = userManagement.getUserRepository().findByDeleted(false);
		}else{
			users = userManagement.getUserRepository().findByDeleted(true);
		}

		if (searchInput.isPresent()){
			model.addAttribute("userList",users.filter(user -> user.getName().contains(searchInput.get())));
		}else{
			model.addAttribute("userList", users);
		}

		model.addAttribute("userTypeList", userTypeManagement.getUserTypeRepository().findAll());
		model.addAttribute("createNewUserForm", form);
		return "user/user";
	}

	/**
	 * handles requests to see own {@link User} details
	 *
	 * @param model source of information for the html template
	 * @param changePasswordForm {@link ChangePasswordForm} for handling input for changing the own password
	 * @return user/userDetails.html
	 */
	@GetMapping("/user/details/own")
	@PreAuthorize("hasRole('USER')")
	public String ownDetails(Model model, ChangePasswordForm changePasswordForm){
		//in case the User does not exist anymore
		Optional<User> oCurrentUser = userManagement.getCurrentUser();
		if (oCurrentUser.isEmpty()) {
			userManagement.invalidateAuthentication();
			return "redirect:/";
		}
		//user exists
		model.addAttribute("user" ,oCurrentUser.get());
		model.addAttribute("changePasswordForm", changePasswordForm);
		return "user/userDetails";
	}

	/**
	 * handles requests of admins to see {@link User} details of any {@link User}
	 *
	 * @param model source of information for the html template
	 * @param userName name of the {@link User} whose details should be shown
	 * @param changeUserNameForm {@link ChangeUserNameForm} for handling input for user name changes
	 * @param changeUserTypeOfUserForm {@link ChangeUserTypeOfUserForm} for handling input for {@link UserType} changes
	 * @return user/userDetails.html
	 */
	@GetMapping("/user/details/any/{userName}")
	@PreAuthorize("hasRole('ADMIN')")
	public String userDetails(Model model, @PathVariable(value = "userName") String userName,
							  ChangeUserNameForm changeUserNameForm,
							  ChangeUserTypeOfUserForm changeUserTypeOfUserForm) {


		//in case the User does not exist anymore
		Optional<User> oAnyUser = userManagement.getUserRepository().findByNameIgnoreCase(userName);
		if (oAnyUser.isEmpty()){
			return "redirect:/user";
		}
		//user exists
		model.addAttribute("userTypeList", userTypeManagement.getUserTypeRepository().findAll());
		model.addAttribute("changeUserTypeOfUserForm", changeUserTypeOfUserForm);
		model.addAttribute("changeUserNameForm", changeUserNameForm);

		model.addAttribute("user", oAnyUser.get());
		model.addAttribute("anyUser", true);
		return "user/userDetails";
	}

}

