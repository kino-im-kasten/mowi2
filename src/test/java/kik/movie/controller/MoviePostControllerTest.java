package kik.movie.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.util.Streamable;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import kik.movie.data.Movie;
import kik.movie.data.MovieRepository;
import kik.movie.management.MovieManagement;
import kik.picture.data.Picture;
import kik.picture.data.PictureRepository;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MoviePostControllerTest {
    @Autowired
    MockMvc mvc;

    private MovieRepository movieRepository;
    private MovieManagement movieManagement;
    private PictureRepository pictureRepository;
    MessageSource message;
    Locale locale;

    @Autowired
    MoviePostControllerTest(MovieManagement movieManagement, MovieRepository movieRepository,
            PictureRepository pictureRepository, MessageSource message) {
        this.movieRepository = movieRepository;
        this.movieManagement = movieManagement;
        this.pictureRepository = pictureRepository;
        this.locale = LocaleContextHolder.getLocale();
        this.message = message;
    }

    @Test
    @WithMockUser(roles={"ORGA","ADMIN"})
    void uMoviePostControllerCreateMovieP1() throws Exception {


        String uniqueName = UUID.randomUUID().toString();
        String testPictureName = "noposter300.png";
        Path path = Paths.get(
                                System.getProperty("user.dir").toString() +
                                "/src/main/resources/static/img/" + testPictureName);
        
        MockMultipartFile picture = new MockMultipartFile(
                                                    "picture", path.getFileName().toString(), Files.probeContentType(path),
                                                    Files.readAllBytes(path));

        MvcResult response = mvc.perform(multipart("/createMovie")
                                .file(picture)
                                .param("germanName", uniqueName)
                                .param("releaseYear", "2000")
                                .param("regie", "regie")
                                .param("runTimeInMin", "1"))
                                .andReturn();


        // redirect to movie overview : 302
        assertEquals(302, response.getResponse().getStatus());
        
        Movie movie = null;
        try {
            movie = this.movieRepository.findByGermanName(uniqueName).get();
        } catch (NoSuchElementException e) {
            fail("The created movie with the name: " + uniqueName + " could not be found in the database");
        }

        assertEquals(uniqueName, movie.getGermanName());
        assertEquals("regie", movie.getRegie());
        assertEquals("2000", movie.getReleaseYear());
        assertEquals(1, movie.getRunTimeInMin());
        assertEquals(this.movieManagement.getPictureByMovieId(movie.getId()).getBytes().length, picture.getBytes().length);
    }

    @Test
    @WithMockUser(roles={"ORGA","ADMIN"})
    void  uMoviePostControllerCreateMovieN1() throws Exception {

        String uniqueName = UUID.randomUUID().toString();
        String testPictureName = "noposter300.png";
        Path path = Paths.get(
                                System.getProperty("user.dir").toString() +
                                "/src/main/resources/static/img/" + testPictureName);
        
        MockMultipartFile picture = new MockMultipartFile(
                                                    "picture", path.getFileName().toString(),  Files.probeContentType(path),
                                                    Files.readAllBytes(path));

        MockHttpServletResponse response = mvc.perform(multipart("/createMovie")
                                .file(picture)
                                .param("germanName", uniqueName)
                                .param("releaseYear", "2000")
                                .param("regie", "regie")
                                .param("runTimeInMin", "-60"))
                                .andReturn().getResponse();


        // accepts but raises errors
        assertEquals(200, response.getStatus());

        assertTrue("There should be an visible error on the website (error with the runtime input)", 
                    response.getContentAsString().contains(this.message.getMessage("movie.create.error.runTimeInMin", null, this.locale)));
    }

    @Test
    @WithMockUser(roles={"ORGA","ADMIN"})
    void uMoviePostControllerRemoveMovieP1() throws Exception {
        Picture picture = new Picture(System.getProperty("user.dir").toString() + "/src/main/resources/static/img/noposter300.png");
        this.pictureRepository.save(picture);
        Movie movie = new Movie("origName", "deName1", "0000", "regie", "description", 1,"-", picture.getId());
        this.movieRepository.save(movie);
        

        MvcResult response = mvc.perform(post("/movies/delete/" + movie.getId())).andReturn();
        
        // redirect to movie overview : 302
        assertEquals(302, response.getResponse().getStatus());
        
        assertFalse("message", this.movieManagement.deleteMovie(movie.getId()));
    }

    @Test
    @WithMockUser(roles={"ORGA","ADMIN"})
    // U-MoviePostController-editMovie-P-1
    void uMoviePostControllerEditMovieP1() throws Exception {
        Picture pictureA = new Picture(System.getProperty("user.dir").toString() + "/src/main/resources/static/img/noposter300.png");
        this.pictureRepository.save(pictureA);
        Movie movie = new Movie("original", "german1", "2000", "regie", "description", 1,"-", pictureA.getId());
        this.movieRepository.save(movie);

        Path path = Paths.get(
                                System.getProperty("user.dir").toString() +
                                "/src/main/resources/static/img/batman.jpg");
        
        MockMultipartFile pictureB = new MockMultipartFile(
                                                    "picture", path.getFileName().toString(), Files.probeContentType(path),
                                                    Files.readAllBytes(path));

        MvcResult response = mvc.perform(multipart("/movies/edit/" + movie.getId())
                                .file(pictureB)
                                .param("originalName", "😉") // U+1F609 \xF0\x9F\x98\x89
                                .param("germanName", "Test-german")
                                .param("releaseYear", "2001")
                                .param("regie", "Test-regie")
                                .param("descriptionText", "Test-description")
                                .param("runTimeInMin", "9"))
                                .andReturn();

        // redirect to movie overview : 302
        assertEquals(302, response.getResponse().getStatus());

        Movie databaseMovie = this.movieRepository.findById(movie.getId()).get();

        // U+1F609 \xF0\x9F\x98\x89
        assertEquals("😉", databaseMovie.getOriginalName());
        assertEquals("Test-german", databaseMovie.getGermanName());
        assertEquals("2001", databaseMovie.getReleaseYear());
        assertEquals("Test-regie", databaseMovie.getRegie());
        assertEquals("Test-description", databaseMovie.getDescriptionText());
        assertEquals(9.0, databaseMovie.getRunTimeInMin());
        assertEquals(pictureB.getOriginalFilename(), this.movieManagement.getPictureByMovieId(databaseMovie.getId()).getName());
    }

    @Test
    @WithMockUser(roles={"ORGA","ADMIN"})
    void uMoviePostControllerEditMovieN1() throws Exception {
        Picture pictureA = new Picture(System.getProperty("user.dir").toString() + "/src/main/resources/static/img/noposter300.png");
        this.pictureRepository.save(pictureA);
        Movie movie = new Movie("original", "german2", "2000", "regie", "description", 1,"-", pictureA.getId());
        this.movieRepository.save(movie);

        Path path = Paths.get(
                                System.getProperty("user.dir").toString() +
                                "/src/main/resources/static/img/unittestfile.txt");
        
        MockMultipartFile pictureB = new MockMultipartFile(
                                                    "picture", path.getFileName().toString(), Files.probeContentType(path),
                                                    Files.readAllBytes(path));

        MvcResult response = mvc.perform(multipart("/movies/edit/" + movie.getId())
                                .file(pictureB)
                                .param("originalName", "Test-originalName") 
                                .param("germanName", "Test-german")
                                .param("releaseYear", "2001")
                                .param("regie", "Test-regie")
                                .param("descriptionText", "Test-description")
                                .param("runTimeInMin", "9"))
                                .andReturn();

        // accepts but raises errors
        assertEquals(200, response.getResponse().getStatus());

        Movie databaseMovie = this.movieRepository.findById(movie.getId()).get();

        // not supposed to rename something
        assertEquals("original", databaseMovie.getOriginalName());
        assertEquals("german2", databaseMovie.getGermanName());
        assertEquals("2000", databaseMovie.getReleaseYear());
        assertEquals("regie", databaseMovie.getRegie());
        assertEquals("description", databaseMovie.getDescriptionText());
        assertEquals(1.0, databaseMovie.getRunTimeInMin());

        assertTrue("There should be an visible error on the website (invalid picture)", 
                    response.getResponse().getContentAsString().contains(this.message.getMessage("movie.create.file_type_error", null, this.locale)));
    }

    @Test
    @WithMockUser(roles={"USER","ORGA","ADMIN"})
    void uMoviePostControllerOverviewSearchP1() throws Exception {
        String uniqueName = UUID.randomUUID().toString();
        Picture picture = new Picture(System.getProperty("user.dir").toString() + "/src/main/resources/static/img/noposter300.png");
        this.pictureRepository.save(picture);
        Movie movie = new Movie("origName", uniqueName, "0000", "regie", "description", 1,"-", picture.getId());
        this.movieRepository.save(movie);

        MvcResult response =  mvc.perform(post("/movies?input=" + uniqueName)).andReturn();
        
        assertEquals(200, response.getResponse().getStatus());

        Object result = response.getModelAndView().getModelMap().get("movies");

        assertTrue("The server response should be a Streamable<Movie>", result instanceof Streamable<?>);

        List<Movie> resultData = ((Streamable<Movie>) result).toList();

        assertEquals(1, resultData.size());
        assertTrue("The server response should cointain the unique movie only", resultData.contains(movie));
    }
}
