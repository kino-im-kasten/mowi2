package kik.movie.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Streamable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import kik.movie.data.Movie;
import kik.movie.data.MovieForm;
import kik.movie.data.MovieRepository;
import kik.picture.data.Picture;
import kik.picture.data.PictureRepository;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MovieGetControllerTest {
    @Autowired
    MockMvc mvc;

    private MovieRepository movieRepository;
    private PictureRepository pictureRepository;

    @Autowired
    MovieGetControllerTest(MovieRepository movieRepository,
            PictureRepository pictureRepository) {
        this.movieRepository = movieRepository;
        this.pictureRepository = pictureRepository;
    }

    @Test
    @WithMockUser(roles={"ORGA","ADMIN"})
    //@WithUserDetails("Bruce")
    void uMovieGetControllerEditMovieP1() throws Exception {
        Picture picture = new Picture(System.getProperty("user.dir").toString() + "/src/main/resources/static/img/noposter300.png");
        Movie movie = new Movie("origName", "deName", "0000", "regie", "description", 1,"-",picture.getId());
        this.movieRepository.save(movie);
        this.pictureRepository.save(picture);

        MvcResult response =  mvc.perform(get("/movies/edit/"+ movie.getId())).andReturn();        
       
        assertEquals(200, response.getResponse().getStatus());
       
        Object resultForm = response.getModelAndView().getModelMap().get("movieForm");
        Object resultMovie = response.getModelAndView().getModelMap().get("movie");

        assertTrue("The server response should be a MovieForm object", resultForm instanceof MovieForm);
        assertTrue("The server response should be a Movie object", resultMovie instanceof Movie);

        MovieForm resultDataForm = (MovieForm) resultForm;
        Movie resultDataMovie = (Movie) resultMovie;

        assertEquals(movie, resultDataMovie);
        assertEquals(movie.getGermanName(), resultDataForm.getGermanName());
        assertEquals(movie.getOriginalName(), resultDataForm.getOriginalName());
        assertEquals(movie.getDescriptionText(), resultDataForm.getDescriptionText());
        assertEquals(movie.getRegie(), resultDataForm.getRegie());
        assertEquals(movie.getReleaseYear(), resultDataForm.getReleaseYear());
        assertEquals(Integer.toString(movie.getRunTimeInMin()), resultDataForm.getRunTimeInMin());
    }

    @Test
    @WithMockUser(roles={"USER","ORGA","ADMIN"})
    void uMovieGetControllerMovieOverviewP1() throws Exception {
        MvcResult response =  mvc.perform(get("/movies")).andReturn();
        List<Movie> dbMovies = this.movieRepository.findAll().toList();
        
        assertEquals(200, response.getResponse().getStatus());

        Object result = response.getModelAndView().getModelMap().get("movies");

        assertTrue("The server response should be a Streamable<Movie>", result instanceof Streamable<?>);

        List<Movie> resultData = ((Streamable<Movie>) result).toList();

        assertEquals(dbMovies.size(), resultData.size());
        
        for (Movie movie : dbMovies) {
            if(!resultData.contains(movie)) {
                fail("Movie :" + movie.getGermanName() + " with id:" + movie.getId() + " should be in the response but wasn't");
            }
        } 
    }

    @Test
    @WithMockUser(roles={"USER","ORGA","ADMIN"})
    void uMovieGetControllerDetailsMovieP1() throws Exception {
        Picture picture = new Picture(System.getProperty("user.dir").toString() + "/src/main/resources/static/img/noposter300.png");
        Movie movie = new Movie("origName", "deName", "0000", "regie", "description", 1,"-", picture.getId());
        this.movieRepository.save(movie);
        this.pictureRepository.save(picture);

        MvcResult response =  mvc.perform(get("/movies/details/" + movie.getId())).andReturn();

        assertEquals(200, response.getResponse().getStatus());

        Object result = response.getModelAndView().getModelMap().get("movie");
        
        assertTrue("The server response should be a Movie object", result instanceof Movie);

        assertEquals(movie , (Movie) result);
    }

    @Test
    @WithMockUser(roles={"ORGA","ADMIN"})
    void uMovieGetControllerCreateMovieP1() throws Exception {
        MvcResult response =  mvc.perform(get("/createMovie")).andReturn();

        assertEquals(200, response.getResponse().getStatus());

        Object result = response.getModelAndView().getModelMap().get("movieForm");
        
        assertTrue("The server response should be a MovieForm object", result instanceof MovieForm);
    }


    @Test
    @WithMockUser(roles={"USER","ORGA","ADMIN"})
    void uMovieGetControllerGetPictureP1() throws Exception {
        Picture picture = new Picture(System.getProperty("user.dir").toString() + "/src/main/resources/static/img/noposter300.png");
        Movie movie = new Movie("origName", "deName", "0000", "regie", "description", 1,"-", picture.getId());
        this.movieRepository.save(movie);
        this.pictureRepository.save(picture);

        MvcResult response =  mvc.perform(get("/movies/image/" + picture.getId())).andReturn();

        assertEquals(200, response.getResponse().getStatus());
    
        Object result = response.getResponse().getContentAsByteArray();

        assertTrue("The server response should be a byte[] object", result instanceof byte[]);

        assertEquals(picture.getBytes().length, ((byte[]) result).length);
    }
}