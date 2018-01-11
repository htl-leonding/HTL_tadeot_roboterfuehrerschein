package at.htl.leonding.pimpedhotroad.desktop;

import at.htl.leonding.pimpedhotroad.model.Impulse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Simon Kepplinger on 29.12.16.
 */
public class Controller implements Initializable {

    @FXML
    private TextField tf_Ip;
    @FXML
    private TextField tf_Port;
    @FXML
    private Button btn_Connect;
    @FXML
    private Button btn_Disconnect;
    @FXML
    private Button btn_Forward;
    @FXML
    private Button btn_Left;
    @FXML
    private Button btn_Stop;
    @FXML
    private Button btn_Right;
    @FXML
    private Button btn_Backwards;
    @FXML
    private Button btn_FocusPanel;
    @FXML
    private TextArea tf_Log;

    private VehicleClient vehicleClient = new VehicleClient();
    private Stack<KeyCode> pressedKeys = new Stack<>();
    private boolean connecting = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setConnected(false);
        Platform.runLater(() -> btn_Connect.requestFocus());
    }

    @FXML
    void connectClient(ActionEvent actionEvent) {

        //region LISTENERS
        Main.getPrimaryStage().getScene().setOnKeyPressed(event -> {
            if (vehicleClient.isConnected()) {
                KeyCode kc = event.getCode();
                if (!pressedKeys.contains(kc)) {
                    pressedKeys.push(kc);
                    processKeyCode(kc);
                }
            }
        });

        Main.getPrimaryStage().getScene().setOnKeyReleased(event -> {
            if (!pressedKeys.empty() && pressedKeys.lastElement() != null) {
                KeyCode kc = pressedKeys.lastElement();
                pressedKeys.remove(event.getCode());

                if (kc == event.getCode() && !pressedKeys.isEmpty()) {
                    processKeyCode(pressedKeys.lastElement());
                } else if (pressedKeys.isEmpty() && kc != KeyCode.SPACE) {
                    send(Impulse.STOP);
                }
            }
        });

        Main.getPrimaryStage().setOnCloseRequest(event -> {
            try {
                if (vehicleClient.isConnected()) {
                    vehicleClient.disconnect();
                }
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                System.exit(0);
            }
        });

        tf_Log.textProperty().addListener((observable, oldValue, newValue) -> tf_Log.setScrollTop(Double.MAX_VALUE));

        btn_Connect.focusedProperty().addListener((observable, oldValue, newValue) -> btn_Stop.requestFocus());
        btn_Disconnect.focusedProperty().addListener((observable, oldValue, newValue) -> btn_Stop.requestFocus());
        btn_Forward.focusedProperty().addListener((observable, oldValue, newValue) -> btn_Stop.requestFocus());
        btn_Backwards.focusedProperty().addListener((observable, oldValue, newValue) -> btn_Stop.requestFocus());
        btn_Left.focusedProperty().addListener((observable, oldValue, newValue) -> btn_Stop.requestFocus());
        btn_Right.focusedProperty().addListener((observable, oldValue, newValue) -> btn_Stop.requestFocus());
        btn_FocusPanel.focusedProperty().addListener((observable, oldValue, newValue) -> btn_Stop.requestFocus());

        //endregion


        if (!connecting) {
            connecting = true;
            String ipAddress = tf_Ip.getText();
            String port = tf_Port.getText();

            log("Connecting to " + ipAddress + ":" + port + "...");

            Thread thread = new Thread(() -> {
                try {
                    vehicleClient.connect(ipAddress, Integer.parseInt(port));
                    log("Connected!");
                    log("==========");
                    setConnected(true);
                } catch (Exception ex) {

                    setConnected(false);
                    log(ex.getClass() + ": " + ex.getMessage());
                }
                connecting = false;
            });
            thread.start();

        }

    }

    @FXML
    void disconnectClient(ActionEvent event) {
        try {
            vehicleClient.disconnect();

            setConnected(false);

            log("=============");
            log("Disconnected!");
        } catch (Exception ex) {

            setConnected(false);

            log(ex.getClass() + ": " + ex.getMessage());
            log("Disconnected anyway, anarchy!");
        }
    }

    @FXML
    void focusPanel(ActionEvent event) {
        Main.getPrimaryStage().requestFocus();
    }

    @FXML
    void forwardMove(ActionEvent event) {
        send(Impulse.FORWARD);
    }

    @FXML
    void backwardsMove(ActionEvent event) {
        send(Impulse.BACKWARD);
    }

    @FXML
    void leftMove(ActionEvent event) {
        send(Impulse.LEFT);
    }

    @FXML
    void rightMove(ActionEvent event) {
        send(Impulse.RIGHT);
    }

    @FXML
    void stopMove(ActionEvent event) {
        send(Impulse.STOP);
    }

    private void log(String message) {
        tf_Log.appendText(message + "\n");
    }

    private void setConnected(boolean b) {
        tf_Ip.setDisable(b);
        tf_Port.setDisable(b);
        btn_Connect.setDisable(b);
        btn_Disconnect.setDisable(!b);

        btn_Forward.setDisable(!b);
        btn_Left.setDisable(!b);
        btn_Stop.setDisable(!b);
        btn_Right.setDisable(!b);
        btn_Backwards.setDisable(!b);

        btn_FocusPanel.setDisable(!b);
    }

    private void send(Impulse impulse) {
        boolean sent = false;
        while (!sent) {
            try {
                vehicleClient.send(impulse);
                sent = true;
                log("Sent: " + impulse.toString() + "!");
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                log(ex.getMessage());

                sent = false;
                vehicleClient.reconnect();
            }
        }
    }

    private void processKeyCode(KeyCode kc) {
        switch (kc) {
            case UP:
            case W:
                send(Impulse.FORWARD);
                break;
            case RIGHT:
            case D:
                send(Impulse.RIGHT);
                break;
            case DOWN:
            case S:
                send(Impulse.BACKWARD);
                break;
            case LEFT:
            case A:
                send(Impulse.LEFT);
                break;
            case SPACE:
                send(Impulse.STOP);
                break;
        }
    }
}
