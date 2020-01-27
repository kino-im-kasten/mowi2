package kik.picture.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.springframework.web.multipart.MultipartFile;

/**
 * An {@link Picture} holds all picture data
 *
 * @author Felix Rülke
 * @version 0.1.2
 */
@Entity
public class Picture {

    @Id
    @GeneratedValue
    private long id;
    @Lob
    private byte[] bytes;
    private String name;
    private int countUsedByMovie = 0;

    /**
     * Default constructor of a Picture
     *
     * @param file The picture itself
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public Picture(MultipartFile file) throws IOException {
        this.name = file.getOriginalFilename();
        this.bytes = file.getBytes();
    }

    /**
     * Constructor to create {@link Picture} with a given path
     *
     * @param path Absolute path to the image
     * @throws IOException Signals that an I/O exception of some sort has occurred.
     */
    public Picture(String path) throws IOException {
        Path p = Paths.get(path);
        this.name = p.getFileName().toString();
        this.bytes = Files.readAllBytes(p);
    }

    /**
     * Needed, not-used, non-deafult constructor of an {@link Picture}
     */
    public Picture() {
    }

    /**
     * Getter for the id
     * 
     * @return The picture id
     */
    public long getId() {
        return this.id;
    }

    /**
     * Getter for the picture name
     * 
     * @return The picture Name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for the Picture file itself
     * 
     * @return The picture in bytes
     */
    public byte[] getBytes() {
        return this.bytes;
    }

    /**
     * Returns the amount of movies wich use this picture
     * 
     * @return Number of movies
     */
    public int getCountUsedByMovie() {
        return this.countUsedByMovie;
    }

    /**
     * Increases the number, which shows how many movies are using this pictures
     */
    public void increaseCount() {
        this.countUsedByMovie++;
    }

    /**
     * Decreases the number, which shows how many movies are using this pictures
     */
    public void decreaseCount() {
        this.countUsedByMovie--;
    }

    /**
     * Compares a given {@link Picture} object
     * 
     * @param o The picture object
     * @return Returns true if both pictures have the same attributes
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Picture)) {
            return false;
        }

        Picture picture = (Picture) o;

        if (this.name.equals(picture.getName()) && this.countUsedByMovie == picture.getCountUsedByMovie() &&
        // risky but byte compare wont work
                this.bytes.length == picture.getBytes().length) {
            return true;
        }

        return false;
    }
}