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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@Transactional
public class MovieManagementTest {
    private MovieRepository movieRepository;
    private MovieManagement movieManagement;
    private PictureRepository pictureRepository;
    private Picture picture;
    private MultipartFile pictureMult;

    @Autowired
    MovieManagementTest(MovieManagement movieManagement, MovieRepository movieRepository,
            PictureRepository pictureRepository) {
        this.movieRepository = movieRepository;
        this.movieManagement = movieManagement;
        this.pictureRepository = pictureRepository;
    }

    MultipartFile loadPictureFromSystem(String pathString) throws IOException {
        Path path = Paths.get(pathString);
        String name = path.getFileName().toString();

        MultipartFile result = new MockMultipartFile(name, name, Files.probeContentType(path),
                Files.readAllBytes(path));
        return result;
    }

    /*
     * @BeforeAll void DatabaseReset() { this.pictureRepository.deleteAll();
     * this.movieEventRepository.deleteAll(); this.movieRepository.deleteAll();
     * 
     * new PictureInitializer(this.movieRepository,
     * this.pictureRepository).initialize(); new
     * MovieInitializer(this.movieRepository, this.pictureRepository).initialize();;
     * new SampleEventInitializer(new
     * MovieEventManagement(this.movieEventRepository)) }
     * 
     * @AfterClass tearDown
     * 
     * @Test void loadPictureFromSystemTest() { MultipartFile picture = null;
     * 
     * try { picture = loadPictureFromSystem(
     * System.getProperty("user.dir").toString() +
     * "/src/main/resources/static/img/batman.jpg"); } catch (IOException e) {
     * fail(); } assertEquals("batman.jpg", picture.getName()); }
     */

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
    void uMovieManagementCreateMovieP1() {
        String uniqueName = UUID.randomUUID().toString();
        MovieForm form = new MovieForm("original", uniqueName, "0000", "regie", "description", "100", "-",
                this.pictureMult);
        form.setPicture(this.pictureMult);

        Errors errors = new BeanPropertyBindingResult(form, "form");
        MovieValidation.validateWithPicture(form, errors, this.movieRepository, false);

        Movie movie = this.movieManagement.createMovie(form, this.pictureMult, errors);
        Movie fromDatabase = this.movieRepository.findById(movie.getId()).get();

        assertTrue("There should be no errors", errors.getErrorCount() == 0);
        assertTrue("The created movie should match the saved one in the database", movie.equals(fromDatabase));
    }

    @Test
    void uMovieManagementCreateMovieN1() {
        String uniqueName = UUID.randomUUID().toString();
        this.pictureRepository.save(this.picture);
        MovieForm form = new MovieForm("original", uniqueName, "2002", "regie", "description", "-100", "-",
                this.pictureMult);
        form.setPicture(this.pictureMult);

        Errors errors = new BeanPropertyBindingResult(form, "form");

        Movie movie = this.movieManagement.createMovie(form, this.pictureMult, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("movie.create.error.runTimeInMin", errors.getFieldError("runTimeInMin").getCode());
        assertEquals(null, movie);
    }

    @Test
    void uMovieManagementDeleteMovieP1() {
        String uniqueName = UUID.randomUUID().toString();
        Movie movie = new Movie("original", uniqueName, "0000", "regie", "description", 1, "-", this.picture.getId());
        this.movieRepository.save(movie);

        assertTrue("The moveManagement should return true after a movie is deleted correctly",
                this.movieManagement.deleteMovie(movie.getId()));
    }

    @Test
    void uMovieManagementDeleteMovieN1() {
        String uniqueName = UUID.randomUUID().toString();
        Movie movie = new Movie("original", uniqueName, "0000", "regie", "description", 1, "-", this.picture.getId());

        // id is irrelevant but has to exist in movie
        movie.addEventId(0);
        this.movieRepository.save(movie);

        assertFalse(this.movieManagement.deleteMovie(movie.getId()));
        assertEquals(movie, this.movieManagement.findMovieById(movie.getId()));
    }

    @Test
    void uMovieManagementEditMovieP1() {
        String uniqueName = UUID.randomUUID().toString();
        Movie movie = new Movie("original", uniqueName, "0000", "regie", "description", 1, "-", this.picture.getId());
        this.movieRepository.save(movie);

        MovieForm movieForm = new MovieForm("¡Hola", "hallo", "0000", "regie", "description", "1", "-", pictureMult);
        Errors errors = new BeanPropertyBindingResult(movieForm, "form");

        this.movieManagement.editMovie(movie.getId(), movieForm, this.pictureMult, errors);

        assertTrue("There should be no errors", errors.getErrorCount() == 0);
        assertEquals("¡Hola", this.movieRepository.findById(movie.getId()).get().getOriginalName());
    }

    @Test
    void uMovieManagementUploadPictureP1() throws IOException {
        MultipartFile pictureMultTwo = loadPictureFromSystem(
                System.getProperty("user.dir").toString() + "/src/main/resources/static/img/batman.jpg");
        Picture pictureTwo = new Picture(pictureMultTwo);

        String uniqueName = UUID.randomUUID().toString();
        Movie movie = new Movie("original", uniqueName, "0000", "regie", "description", 1, "-", this.picture.getId());
        this.movieRepository.save(movie);

        MovieForm movieForm = new MovieForm("original", "german", "0000", "regie", "description", "1", "-",
                pictureMultTwo);
        Errors errors = new BeanPropertyBindingResult(movieForm, "form");

        this.movieManagement.editMovie(movie.getId(), movieForm, pictureMultTwo, errors);

        assertTrue("There should be no errors", errors.getErrorCount() == 0);
        assertEquals(pictureTwo, this.movieManagement.getPictureByMovieId(movie.getId()));
    }

    @Test
    void uMovieManagementUploadPictureN1() throws IOException {
        MultipartFile pictureMultTwo = loadPictureFromSystem(
                System.getProperty("user.dir").toString() + "/src/main/resources/static/img/unittestfile.txt");

        String uniqueName = UUID.randomUUID().toString();
        Movie movie = new Movie("original", uniqueName, "0001", "regie", "description", 1, "-", this.picture.getId());
        this.movieRepository.save(movie);

        MovieForm movieForm = new MovieForm("hallo", "hallo", "0001", "regie", "description", "1", "-", pictureMultTwo);
        Errors errors = new BeanPropertyBindingResult(movieForm, "form");

        this.movieManagement.editMovie(movie.getId(), movieForm, pictureMultTwo, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("movie.create.file_type_error", errors.getFieldError("picture").getCode());
    }

    @Test
    void uMovieManagementGetInitialPictureP1() {
        List<Picture> p = this.pictureRepository.findAllByName("noposter300.png");
        this.pictureRepository.deleteAll(p);

        assertTrue("the picture should have been deleted but wasn't",
                this.pictureRepository.findAllByName("noposter300.png").size() == 0);
        this.movieManagement.getInitialPicture();
        assertTrue("The picture should have been restored but wasn't",
                this.pictureRepository.findAllByName("noposter300.png").size() >= 1);
    }

    @Test
    void uMovieManagementDeletePictureWithMovieP1() {

        String uniqueName = UUID.randomUUID().toString();
        MovieForm form = new MovieForm("original", uniqueName, "2002", "regie", "description", "100", "-",
                this.pictureMult);
        Errors errors = new BeanPropertyBindingResult(form, "form");
        Movie movie = this.movieManagement.createMovie(form, this.pictureMult, errors);

        Optional<Picture> p = this.pictureRepository.findById(movie.getPictureId());
        assertTrue("there should be a picture with that id", p.isPresent());
        assertEquals(1, p.get().getCountUsedByMovie());
        this.movieManagement.deleteMovie(movie.getId());
        assertTrue("The picture should not exist in the repo",
                !this.pictureRepository.findById(movie.getPictureId()).isPresent());

    }

    // cant recreate an IOException
    // @Test
    // void uMovieManagementLoadPictureN1() {
    // Errors errors = new BeanPropertyBindingResult(null, "test");
    // this.movieManagement.loadPicture(null, errors);
    // assertEquals(1, errors.getErrorCount());
    // assertEquals("movie.edit.unknown_file_error",
    // errors.getFieldError("picture").getCode());
    // }

    @Test
    void uMovieFilterSearchNameP1() {
        String uniqueName = UUID.randomUUID().toString();
        MovieForm form = new MovieForm("original", uniqueName, "2002", "regie", "description", "100", "-",
                this.pictureMult);
        Errors errors = new BeanPropertyBindingResult(form, "form");
        this.movieManagement.createMovie(form, this.pictureMult, errors);

        List<Movie> movies = MovieFilter.filter(this.movieRepository.findAll(), uniqueName).toList();
        assertEquals(1, movies.size());
        assertEquals(uniqueName, movies.get(0).getGermanName());
    }

}
