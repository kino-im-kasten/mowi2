package kik.user.data.accessrole;

/**
 * Enumeration of all {@link AccessRole}s that will be initialized.
 * The parts of the Enumeration represent all available values for {@link org.springframework.security.access.prepost.PreAuthorize} hasRole('VALUE').
 *
 * @author Richard Müller
 * @version 0.0.1
 */
public enum AccessRolesEnum {
	USER,ORGA,ADMIN
}
