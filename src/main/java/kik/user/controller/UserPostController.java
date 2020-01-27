package kik.user.controller;

import kik.user.data.exceptions.AdminDangerException;
import kik.user.data.exceptions.UserNotFoundException;
import kik.user.data.exceptions.UserTypeNotFoundException;
import kik.user.data.forms.*;
import kik.user.data.user.User;
import kik.user.management.UserManagement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Controller handling all post requests concerning users / usertyps
 *
 * @author Richard Müller
 * @version 0.0.1
 */
@Controller
public class UserPostController {

	private final UserManagement userManagement;

	/**
	 * default constructor for {@link UserPostController}
	 *
	 * @param userManagement management to forward the requests to
	 */
	public UserPostController(UserManagement userManagement) {
		this.userManagement = userManagement;
	}

	/**
	 * Handles the request for HTTP::POST on /user
	 * redirects to /user in get Controller
	 *
	 * @param input searchInput
	 * @return redirect:/user/{searchInput}
	 */
	@PostMapping("/user")
	@PreAuthorize("hasRole('ADMIN')")
	public String searchUser(@RequestParam(value = "input") String input){
		return "redirect:/user/" + input;
	}

	/**
	 * method to switch session attribute 'showDeletedUsers' on and off
	 *
	 * @param request to set and get session attributes
	 * @return redirect:(user
	 */
	@PostMapping("/user/showDeletedUsers")
	@PreAuthorize("hasRole('ADMIN')")
	public String showDeletedUsers(HttpServletRequest request){
		HttpSession session = request.getSession();
		if (session.getAttribute("showDeletedUsers") == null){
			session.setAttribute("showDeletedUsers", true);
		}else {
			session.setAttribute("showDeletedUsers", null);
		}
		return "redirect:/user";
	}

	/**
	 * Handles the request for HTTP::POST on /user/createNewUser
	 * if successful: creates and saves a new {@link User} with the given data
	 *
	 * @param form {@link CreateNewUserForm} holds data for creation
	 * @param errors to pass down to validation
	 * @param redirectAttributes for keeping errors over the redirect
	 * @return redirect:/user
	 */
	@PostMapping("/user/createNewUser")
	@PreAuthorize("hasRole('ADMIN')")
	public String createNewUser(CreateNewUserForm form, Errors errors, RedirectAttributes redirectAttributes){
		try {
			userManagement.createUser(form, errors);
		}catch (UserTypeNotFoundException e){
			redirectAttributes.addFlashAttribute("exception", "UserTypeNotFound");
		}
		redirectAttributes.addFlashAttribute("errors", errors);
		return "redirect:/user";
	}

	/**
	 * Handles the request for HTTP::POST on /user/changeUserType/{userName}
	 * if successful: changes the {@link kik.user.data.usertype.UserType} of an {@link User}
	 *
	 * @param form {@link ChangeUserTypeOfUserForm} holds data for {@link kik.user.data.usertype.UserType} change
	 * @param userName indentifier for {@link User} (userName saved in form is used during the changing process)
	 * @param errors to pass down to validation
	 * @param redirectAttributes for keeping errors over the redirect
	 * @return redirect:/user/details/any/{userName}
	 */
	@PostMapping("/user/changeUserType/{userName}")
	@PreAuthorize("hasRole('ADMIN')")
	public String changeUserTypeofUser(ChangeUserTypeOfUserForm form, @PathVariable(value = "userName") String userName,
									   Errors errors, RedirectAttributes redirectAttributes){

		try {
			this.userManagement.changeUserTypeOfUser(form, errors);
		}catch (UserNotFoundException e){
			redirectAttributes.addFlashAttribute("exception", "UserNotFound");
			return "redirect:/user/";
		}catch (UserTypeNotFoundException e){
			redirectAttributes.addFlashAttribute("exception", "UserTypeNotFound");
		}catch (AdminDangerException e){
			redirectAttributes.addFlashAttribute("exception", "AdminDanger");
		}
		redirectAttributes.addFlashAttribute("errors", errors);
		return "redirect:/user/details/any/" + userName;
	}

	/**
	 * Handles the request for HTTP::POST on /unlockAccount/own
	 * if successful: unlocks the {@link org.salespointframework.useraccount.UserAccount} of an {@link User} and refreshed the authentication
	 *
	 * @param form {@link UnlockAccountForm} hold data for unlocking Account (currentUserName)
	 * @param errors to pass down to validation
	 * @param model for adding errors as attributes
	 * @param session for adding the username to the session
	 * @return if successful: "redirect:/" , if not: "user/unlockAccount"
	 */
	@PostMapping("/unlockAccount/own")
	@PreAuthorize("isAuthenticated()")
	public String unlockAccount(UnlockAccountForm form, Errors errors, Model model, HttpSession session){

		try {
			userManagement.unlockUserAccount(form, errors);
		}catch (UserNotFoundException e){
			model.addAttribute("exception", "UserNotFound");
			return "user/login";
		}

		if (errors.hasErrors()){
			model.addAttribute("errors", errors);
			return "user/unlockAccount";
		}
		//refresh authentication
		userManagement.refreshCurrentAuthentication();
		if (userManagement.getCurrentUser().isPresent()){
			session.setAttribute("username", userManagement.getCurrentUser().get().getName());
		}
		return "redirect:/documentation";
	}

	/**
	 * Handles the request for HTTP::POST on /user/delete/{userName}/{deletionStep}
	 * if successful: deletes an {@link User} after confirming the input
	 *
	 * @param userName identifier for the {@link User} to be deleted
	 * @param deletionStep current deletion state the user is in ("try", "decline", "confirm", "confirmForever")
	 * @param request for setting session attributes
	 * @param redirectAttributes for keeping errors over the redirect
	 * @return redirect:/user
	 */
	@PostMapping("/user/delete/{userName}/{deletionStep}")
	@PreAuthorize("hasRole('ADMIN')")
	public String deleteUser(@PathVariable(value = "userName") String userName,
							 @PathVariable(value = "deletionStep") String deletionStep,
							 HttpServletRequest request,
							 RedirectAttributes redirectAttributes){
		HttpSession session = request.getSession();

		try{
			if (deletionStep.equals("confirm")) {
				userManagement.safeDeleteUserByName(userName);
			}else if(deletionStep.equals("confirmForever")) {
				userManagement.deleteUserForeverByName(userName);
			}
		}catch (UserNotFoundException e){
			redirectAttributes.addFlashAttribute("exception", "UserNotFound");
		}catch (AdminDangerException e){
			redirectAttributes.addFlashAttribute("exception", "AdminDanger");
		}

		return "redirect:/user";
	}

	/**
	 * Handles the request for HTTP::POST on /user/recoverUser/{userName}
	 * if successful: recovers a deleted {@link User}
	 *
	 * @param userName identifier for recovery
	 * @param request for setting session attributes
	 * @return redirect:/user
	 */
	@PostMapping("/user/recoverUser/{userName}")
	@PreAuthorize("hasRole('ADMIN')")
	public String recoverUser(@PathVariable(value = "userName") String userName, HttpServletRequest request,
							  RedirectAttributes redirectAttributes){
		try {
			userManagement.recoverDeletedUserByName(userName);
		}catch (UserNotFoundException e){
			redirectAttributes.addFlashAttribute("exception", "UserNotFound");
		}
		request.getSession().setAttribute("showDeletedUsers", null);
		return "redirect:/user";
	}

	/**
	 * Handles the request for HTTP::POST on /user/details/any/{userName}/resetPassword
	 * if successful: resets the password for an {@link User}, only for Admins possible
	 *
	 * @param userName identifier for the {@link User} to change the password for
	 * @param changePasswordForm holds data for password reset (only a new password)
	 * @param errors to pass down to validation
	 * @param redirectAttributes for keeping errors over the redirect
	 * @return redirect:/user/details/any/{userName}
	 */
	@PostMapping("/user/details/any/{userName}/resetPassword")
	@PreAuthorize("hasRole('ADMIN')")
	public String resetPassword(@PathVariable(value = "userName") String userName,
								ChangePasswordForm changePasswordForm,
								Errors errors,
								RedirectAttributes redirectAttributes){

		try {
			userManagement.resetPassword(userName, changePasswordForm.getNewPassword(), errors);
		}catch (UserNotFoundException e){
			redirectAttributes.addFlashAttribute("exception", "UserNotFound");
		}
		redirectAttributes.addFlashAttribute("errors", errors);
		return "redirect:/user/details/any/" + userName;
	}

	/**
	 * Handles the request for HTTP::POST on /user/details/any/{userName}/changeUserName
	 * if successful: changes the userName of an {@link User}
	 * and locks his {@link org.salespointframework.useraccount.UserAccount} in the process
	 *
	 * @param userName identifier for the {@link User} to change the userName of
	 * @param changeUserNameForm holds data for the userName change
	 * @param errors to pass down to validation
	 * @param redirectAttributes for keeping errors over the redirect
	 * @return if successful: "redirect:/user/details/any/{newUserName}" ,
	 * otherwise: "redirect:/user/details/any/{oldUserName}"
	 */
	@PostMapping("/user/details/any/{userName}/changeUserName")
	@PreAuthorize("hasRole('ADMIN')")
	public String changeUserName(@PathVariable(value = "userName") String userName,
								 ChangeUserNameForm changeUserNameForm,
								 Errors errors,
								 final RedirectAttributes redirectAttributes){
		try {
			userManagement.changeUserName(userName, changeUserNameForm, errors);
		}catch (UserNotFoundException e){
			redirectAttributes.addFlashAttribute("exception", "UserNotFound");
			return "redirect:/user";
		}catch (AdminDangerException e){
			redirectAttributes.addFlashAttribute("exception", "AdminDanger");
			return "redirect:/user/details/any/" + userName;
		}
		String returnName = changeUserNameForm.getNewUserName();
		if (errors.hasErrors()){
			redirectAttributes.addFlashAttribute("errors", errors);
			returnName = userName;
		}
		return "redirect:/user/details/any/" + returnName;
	}


	/**
	 * Handles the request for HTTP::POST on /user/details/own/changePassword
	 * if successful: changes the password of the current {@link User}
	 *
	 * @param changePasswordForm holds the data for the password change
	 * @param errors to pass down to validation
	 * @param redirectAttributes for keeping errors over the redirect
	 * @return redirect:/user/details/own
	 */
	@PostMapping("/user/details/own/changePassword")
	@PreAuthorize("hasRole('USER')")
	public String changePasswordOwn(ChangePasswordForm changePasswordForm,
									Errors errors, RedirectAttributes redirectAttributes){

		userManagement.changePassword(changePasswordForm, errors);
		redirectAttributes.addFlashAttribute("errors", errors);

		return "redirect:/user/details/own";
	}
}
