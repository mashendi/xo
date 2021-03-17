package server.controllers;

import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import server.MainServer;
import server.db.DBConnection;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MainController {
    private final DBConnection dbConnection;
    ScheduledExecutorService scheduledExecutorService;

    {
        dbConnection = DBConnection.getDatabaseInstance();
    }

    static boolean isServerOn;

    @FXML
    private FontAwesomeIconView closeBtn;

    @FXML
    private FontAwesomeIconView minimizeBtn;


    @FXML
    private JFXToggleButton serverToggleBtn;

    @FXML
    private Label ip;

    @FXML
    void toggleState() throws IOException {
        if (serverToggleBtn.isSelected()) {
            MainServer.getInstance().start();
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            dbConnection.openConnection();
            isServerOn = true;
            ip.setText(String.valueOf(Inet4Address.getLocalHost().getHostAddress()));
        } else {
            ip.setText("");
            closingEverything();
        }
    }

    @FXML
    void closeWindow() throws IOException {
        ((Stage) closeBtn.getScene().getWindow()).close();
        closingEverything();
    }

    @FXML
    void minimizeWindow() {
        ((Stage) minimizeBtn.getScene().getWindow()).setIconified(true);
    }


    private void closingEverything() throws IOException {
        if (isServerOn) {
            scheduledExecutorService.shutdown();
        }
        MainServer.getInstance().stop();
        MainServer.mainSocket.close();
        MainServer.getInstance();
        MainServer.stopClients();
        MainServer.deleteInstance();
        isServerOn = false;
    }
}
