package kik.movie.data;

import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

/**
 * A repository interface to manage {@link Movie} instances.
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
public interface MovieRepository extends CrudRepository<Movie, Long> {
    @Override
    /**
     * Returns all existing movies
     * 
     * @return All movies existing
     */
    Streamable<Movie> findAll();

    /**
     * Returns all movies sorted
     * 
     * @param sort Defines the way how to sort
     * @return All movies existing sorted
     */
    Streamable<Movie> findAll(Sort sort);

    /**
     * Searches for a movie with a specific name
     * 
     * @param name The german title
     * @return The movie if existend
     */
    Optional<Movie> findByGermanName(String name);
}
