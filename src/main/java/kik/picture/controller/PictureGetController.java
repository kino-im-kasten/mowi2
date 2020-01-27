package kik.picture.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import kik.picture.data.Picture;
import kik.picture.data.PictureRepository;

/**
 * {@link PictureGetController} handling HTTP::GET requests for images
 *
 * @author Felix Rülke
 * @version 0.0.1
 */
@Controller
class PictureGetController {

    private PictureRepository pictureRepository;

    /**
     * Default Constructor of an {@link PictureGetController}
     * 
     * @param pictureRepository Stores all pictures
     */
    PictureGetController(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

     /**
     * Handles the request for HTTP::GET on /movies/image/$id
     *
     * @param id Id of Movie in the database
     * @return Image file
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    @GetMapping(value = "/movies/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable String id) throws IOException {
        Optional<Picture> o = pictureRepository.findById(Long.parseLong(id));
        if (!o.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entity not found");
        }

        byte[] imageContent = o.get().getBytes();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<byte[]>(imageContent, headers, HttpStatus.OK);
    }
}
