package kik.distributor.data;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * repository for ContactPersons
 */
@Repository
public interface ContactPersonRepository extends CrudRepository<ContactPerson,Long> {
}
