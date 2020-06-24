package utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageComparision {

  static Logger log = LoggerFactory.getLogger(ImageComparision.class);


  /**
   *
   * @param image1
   * @param image2
   * @param mimMatchedPercentAllowed
   * @return return true when passed minimum percentage is less than the matched percentage from image
   * @throws IOException
   */
  public boolean imageComparision(File image1, File image2, int mimMatchedPercentAllowed)
      throws IOException {
    boolean matched = true;
    try {
      BufferedImage imgA = ImageIO.read(image1);
      BufferedImage imgB = ImageIO.read(image2);
      int width1 = imgA.getWidth();
      int width2 = imgB.getWidth();
      int height1 = imgA.getHeight();
      int height2 = imgB.getHeight();
      if ((width1 != width2) || (height1 != height2)) {
        log.info("Error: Images dimensions" + " mismatch");
        matched = false;
      } else {
        long difference = 0;
        for (int y = 0; y < height1; y++) {
          for (int x = 0; x < width1; x++) {
            int rgbA = imgA.getRGB(x, y);
            int rgbB = imgB.getRGB(x, y);
            int redA = (rgbA >> 16) & 0xff;
            int greenA = (rgbA >> 8) & 0xff;
            int blueA = (rgbA) & 0xff;
            int redB = (rgbB >> 16) & 0xff;
            int greenB = (rgbB >> 8) & 0xff;
            int blueB = (rgbB) & 0xff;
            difference += Math.abs(redA - redB);
            difference += Math.abs(greenA - greenB);
            difference += Math.abs(blueA - blueB);
          }
        }
        double total_pixels = width1 * height1 * 3;
        // Normalizing the value of different pixels
        // for accuracy(average pixels per color component)
        double avg_different_pixels = difference / total_pixels;

        // There are 255 values of pixels in total
        double percentage = (avg_different_pixels / 255) * 100;

        if (percentage > mimMatchedPercentAllowed) {
          matched = false;
        }
        log.info("Difference in Percentage-->" + percentage + " So returned : " + matched);
      }
    } catch (Exception e) {
      matched = true;
    }
    return matched;
  }
}
