package lit.litfx.controls.icons;

import java.io.InputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconResourceProvider {
    public static Image loadIconFile(String iconName) {
        try {
            return new Image(IconResourceProvider.getResourceAsStream(iconName + ".png"));
        } catch (NullPointerException e) {
            return new Image(IconResourceProvider.getResourceAsStream("noimage.png"));
        }
    }

    public static ImageView loadIcon(String iconName, double FIT_WIDTH) {
        ImageView iv = new ImageView(loadIconFile(iconName));
        iv.setPreserveRatio(true);
        iv.setFitWidth(FIT_WIDTH);
        return iv;
    }
    public static InputStream getResourceAsStream(String name) {
        return IconResourceProvider.class.getResourceAsStream(name);
    }

}
