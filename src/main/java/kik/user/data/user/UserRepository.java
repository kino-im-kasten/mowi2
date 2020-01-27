package kik.user.data.user;

import kik.user.data.usertype.UserType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;
import java.util.UUID;

/**
 * A repository interface to manage {@link User} instances.
 *
 * @author Richard Müller
 * @version 0.0.1
 */
public interface UserRepository extends CrudRepository<User, Long> {
	@Override
	Optional<User> findById(Long aLong);

	Optional<User> findByNameIgnoreCase(String name);

	Optional<User> findByUuid(UUID uuid);

	Streamable<User> findByUserType(UserType userType);

	Streamable<User> findByUnlocked(Boolean unlocked);

	Streamable<User> findByDeleted(Boolean deleted);
	@Override
	Streamable<User> findAll();

	void deleteByName(String name);
}

