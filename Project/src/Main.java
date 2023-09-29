import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static AppData appData = AppData.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        final int minWidth = 300;
        final int minHeight = 600;
        final int windowWidth = 800;
        final int windowHeight = 600;

        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
        UtilsViews.addView(getClass(), "Desktop", "assets/layout_desktop.fxml");
        UtilsViews.addView(getClass(), "Mobile0", "assets/layout_mobile_0.fxml");

        Scene scene = new Scene(UtilsViews.parentContainer);
        
        // Listen to window width changes
        scene.widthProperty().addListener((ChangeListener<? super Number>) new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldWidth, Number newWidth) {
                _setLayout(newWidth.intValue());
            }
        });

        stage.setScene(scene);
        stage.setTitle("NintendoDB");
        stage.setMinWidth(minWidth);
        stage.setWidth(windowWidth);
        stage.setMinHeight(minHeight);
        stage.setHeight(windowHeight);
        stage.show();

        // Add icon only if not Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            Image icon = new Image("file:assets/icons/icon.png");
            stage.getIcons().add(icon);
        }
    }

    private void _setLayout(int width) {
        if (width < 600) {
            UtilsViews.setView("Mobile0");
        } else {
            UtilsViews.setView("Desktop");
        }
    }
}