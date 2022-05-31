import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ControlsAlert {
    public static void showWarningAlert(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("/resources/icon.png"));

        alert.getDialogPane().getStylesheets().add("resources/stylesheet.css");
        if(ControlsCSS.isBroken) alert.getDialogPane().getStylesheets().add("resources/customColor.css");
        else alert.getDialogPane().getStylesheets().add(ControlsCSS.pathToCSS);

        ControlsCSS.refreshParent(alert.getDialogPane());

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showErrorAlert(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image("/resources/icon.png"));

        alert.getDialogPane().getStylesheets().add("resources/stylesheet.css");
        if(ControlsCSS.isBroken) alert.getDialogPane().getStylesheets().add("resources/customColor.css");
        else alert.getDialogPane().getStylesheets().add(ControlsCSS.pathToCSS);

        ControlsCSS.refreshParent(alert.getDialogPane());

        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
