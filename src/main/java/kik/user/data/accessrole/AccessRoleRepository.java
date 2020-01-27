package kik.user.data.accessrole;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository interface to manage {@link AccessRole} instances.
 * Only used for saving.
 *
 * @author Richard Müller
 * @version 0.0.1
 */
public interface AccessRoleRepository extends CrudRepository<AccessRole,Long> {
}
