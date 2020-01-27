package kik.user.data.usertype;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

import java.util.Optional;

/**
 * A repository interface to manage {@link UserType} instances.
 *
 * @author Richard Müller
 * @version 0.0.1
 */
public interface UserTypeRepository extends CrudRepository<UserType, Long> {
	@Override
	Optional<UserType> findById(Long aLong);

	Optional<UserType> findByName(String name);

	@Override
	Streamable<UserType> findAll();

	void deleteByName(String UserTypeName);
}
