package kik.picture.data;

import java.io.IOException;
import java.util.List;

import org.salespointframework.core.DataInitializer;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(10)
public class PictureInitializer implements DataInitializer {

    private PictureRepository pictureRepository;

    /**
     * Default constructor of an PictureInitializer
     *
     * @param pictureRepository A repository containing all {@link Picture}'s
     */
    PictureInitializer(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    /**
     * Creates sample data for Pictures
     *
     * @see org.salespointframework.core.DataInitializer#initialize()
     */
    @Override
    public void initialize() {
		if (pictureRepository.findAll().iterator().hasNext()) {
			return;
		}


		try {
            List.of(new Picture(
                    System.getProperty("user.dir").toString() + "/src/main/resources/static/img/noposter300.png"),
                    new Picture(
                            System.getProperty("user.dir").toString() + "/src/main/resources/static/img/batman.jpg"),
                    new Picture(System.getProperty("user.dir").toString()
                            + "/src/main/resources/static/img/bladerunner.jpg"))
                    .forEach(this.pictureRepository::save);
        } catch (IOException e) {
            System.out.println("#####\n\n\n\n please check the given file paths in PictureInitializer \n\n\n\n#####");
        }
    }
}
