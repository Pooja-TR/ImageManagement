package sample;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

/**
 * Create a Image class for detailed usage
 */
public class Image extends Controller {

    private String format;
    private String path;
    private int height;
    private int width;
    private Metadata details;

    // constructor with details
    public Image(File file, String path, int height, int width, Metadata details) {
        this.path = path;
        this.height = height;
        this.width = width;
        this.details = details;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPath() {
        return path;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
