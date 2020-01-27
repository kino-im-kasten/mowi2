package kik.movie.management;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;

import kik.movie.data.*;
import kik.picture.data.Picture;
import kik.picture.data.PictureRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@SpringBootTest
@Transactional
public class MovieValidationTest {
    private MovieRepository movieRepository;
    private PictureRepository pictureRepository;
    private Picture picture;
    private MultipartFile pictureMult;

    @Autowired
    MovieValidationTest(MovieRepository movieRepository, PictureRepository pictureRepository) {
        this.movieRepository = movieRepository;
        this.pictureRepository = pictureRepository;
    }

    MultipartFile loadPictureFromSystem(String pathString) throws IOException {
        Path path = Paths.get(pathString);
        String name = path.getFileName().toString();

        MultipartFile result = new MockMultipartFile(name, name, Files.probeContentType(path),
                Files.readAllBytes(path));
        return result;
    }

    @BeforeEach
    void setup() {
        try {
            this.pictureMult = loadPictureFromSystem(
                    System.getProperty("user.dir").toString() + "/src/main/resources/static/img/batman.jpg");
            this.picture = new Picture(pictureMult);
        } catch (IOException e) {
            fail("Picture could not be loaded");
        }

        this.pictureRepository.save(this.picture);
    }

    @Test
    void uMovieValidationDenyExistingN1() {
        String uniqueName = UUID.randomUUID().toString();
        Movie movie = new Movie("test", uniqueName, "0000", "regie", "description", 1, "-", 0);
        this.movieRepository.save(movie);
        MovieForm movieForm = new MovieForm("test", uniqueName, "0000", "regie", "description", "1", "-", null);

        Errors errors = new BeanPropertyBindingResult(movieForm, "form");

        MovieValidation.validateWithoutPicture(movieForm, errors, movieRepository, false);
        assertEquals(3, errors.getErrorCount());
        assertEquals("movie.create.error.duplicate_movie", errors.getFieldError("germanName").getCode());
        assertEquals("movie.create.error.duplicate_movie", errors.getFieldError("releaseYear").getCode());
        assertEquals("movie.create.error.duplicate_movie", errors.getFieldError("runTimeInMin").getCode());
    }

    @Test
    void uMovieValidationRejectAllN1() {
        MovieForm movieForm = new MovieForm("", "", "year", "", "regie", "runtime", "description", null);
        Errors errors = new BeanPropertyBindingResult(movieForm, "form");
        MovieValidation.validateWithoutPicture(movieForm, errors, movieRepository, false);
        assertEquals(3, errors.getErrorCount());
        assertEquals("movie.create.error.title_de", errors.getFieldError("germanName").getCode());
        assertEquals("movie.create.release_year_error", errors.getFieldError("releaseYear").getCode());
        assertEquals("movie.create.error.runTimeInMin", errors.getFieldError("runTimeInMin").getCode());
    }

    @Test
    void uMovieValidationRejectAllN2() {
        MovieForm movieForm = new MovieForm(null, null, "3000", null, "regie", "111111", "description", null);
        Errors errors = new BeanPropertyBindingResult(movieForm, "form");
        MovieValidation.validateWithoutPicture(movieForm, errors, movieRepository, false);
        assertEquals(2, errors.getErrorCount());
        assertEquals("movie.create.error.title_de", errors.getFieldError("germanName").getCode());
        assertEquals("movie.create.error.runtimeToLong", errors.getFieldError("runTimeInMin").getCode());
    }
}