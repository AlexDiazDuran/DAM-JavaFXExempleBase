import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class AppData {

    private static AppData instance = null;

    public boolean readyConsoles = false;
    public boolean readyJocs = false;
    public boolean readyPersonatges = false;

    public JSONArray dataConsoles = null;
    public JSONArray dataJocs = null;
    public JSONArray dataPersonatges = null;

    private ChoiceBox<String> choiceBox;
    private VBox yPane;

    private AppData() {
    }

    // Resto de la clase

    public AppData(ChoiceBox<String> choiceBox, VBox yPane) {
        this.choiceBox = choiceBox;
        this.yPane = yPane;
    }

    public static AppData getInstance() {
        if (instance == null) {
            instance = new AppData();
        }
        return instance;
    }

    boolean dataReady(String type) {
        switch (type) {
            case "Consoles":
                return readyConsoles;
            case "Jocs":
                return readyJocs;
            case "Personatges":
                return readyPersonatges;
        }
        return false;
    }

    JSONArray getData(String type) {
        switch (type) {
            case "Consoles":
                return dataConsoles;
            case "Jocs":
                return dataJocs;
            case "Personatges":
                return dataPersonatges;
        }
        return null;
    }

    public JSONObject getItemData(String type, int index) {
        if (dataReady(type)) {
            JSONArray dataArray = getData(type);
            if (dataArray != null && index >= 0 && index < dataArray.length()) {
                return dataArray.getJSONObject(index);
            }
        }
        return null;
    }

    public void load(String type, Consumer<String> callBack) {
        // Si les dades ja estàn carregades, no cal fer res
        if (dataReady(type)) {
          callBack.accept("OK");
          return;
        }
      
        // Seleccionem l'arxiu d'on s'han de carregar les dades
        String arxiu = "";
        switch (type) {
          case "Consoles": arxiu = "/assets/data/consoles.json"; break;
          case "Jocs": arxiu = "/assets/data/jocs.json"; break;
          case "Personatges": arxiu = "/assets/data/personatges.json"; break;
          default: throw new IllegalArgumentException("Tipus desconegut: " + type);
        }
        loadData(arxiu, (receivedData) -> {
            if (receivedData == null) {
              callBack.accept(null);
              return;
            } else {
              // Guardem les dades carregades en format JSON
              JSONArray dadesArxiu = new JSONArray(receivedData);
              switch (type) {
                case "Consoles":
                  readyConsoles = true;
                  dataConsoles = dadesArxiu;
                  break;
                case "Jocs":
                  readyJocs = true;
                  dataJocs = dadesArxiu;
                  break;
                case "Personatges":
                  readyPersonatges = true;
                  dataPersonatges = dadesArxiu;
                  break;
              }
              callBack.accept("OK");
            }
          });
        }
        

    public void showList(String opcioCarregada) {

        // Si s'ha carregat una altra opció, no cal fer res
        // (perquè el callback pot arribar després de que l'usuari hagi canviat d'opció)
        String opcioSeleccionada = choiceBox.getValue();
        if (opcioCarregada.compareTo(opcioSeleccionada) != 0) {
            return;
        }

        // Obtenir una referència a l'ojecte AppData que gestiona les dades
        AppData appData = AppData.getInstance();

        // Obtenir les dades de l'opció seleccionada
        JSONArray dades = appData.getData(opcioCarregada);
        // Esborrar la llista actual
        yPane.getChildren().clear();

        // Carregar la llista amb les dades
        for (int i = 0; i < dades.length(); i++) {
            JSONObject consoleObject = dades.getJSONObject(i);
            if (consoleObject.has("nom")) {
                String nom = consoleObject.getString("nom");
                Label label = new Label(nom);
                yPane.getChildren().add(label);
                System.out.println(yPane.getChildren().add(label));
            }
        }
    }

    public void loadData(String dataFile, Consumer<String> callBack) {

        // Use a thread to avoid blocking the UI
        new Thread(() -> {
            try {
                // Wait a second to simulate a long loading time
                Thread.sleep(1000);

                // Load the data from the assets file
                InputStream is = getClass().getResourceAsStream(dataFile);
                Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                StringBuilder content = new StringBuilder();
                char[] buffer = new char[1024];
                int bytesRead;
                while ((bytesRead = reader.read(buffer)) != -1) {
                    content.append(buffer, 0, bytesRead);
                }

                // Call the callback function on the UI thread (Only works on JavaFX)
                Platform.runLater(() -> {
                    callBack.accept(content.toString());
                });

            } catch (InterruptedException e) {
                callBack.accept(null);
                e.printStackTrace();
            } catch (IOException e) {
                callBack.accept(null);
                e.printStackTrace();
            }
        }).start();
    }

}
