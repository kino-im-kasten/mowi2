package kik.picture.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * A repository interface to manage {@link Picture} instances.
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
public interface PictureRepository extends CrudRepository<Picture, Long> {
    List<Picture> findAllByName(String name);
}
