package kik.distributor.data;


import kik.distributor.data.Distributor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * repository for {@link Distributor}s
 */
@Repository
public interface DistributorRepository extends CrudRepository<Distributor,Long> { }