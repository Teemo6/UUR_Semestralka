
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;

public class AppearanceStage {
    private static final AppearanceStage INSTANCE = new AppearanceStage();

    private final double TIMER_WINDOW_MIN_WIDTH = 300;
    private final double TIMER_WINDOW_MIN_HEIGHT = 250;

    private GridPane colorGrid;

    private Stage appearanceStage;
    private Scene appearanceScene;

    private ObjectProperty<Color> mainColorSelected = new SimpleObjectProperty<>();
    private BooleanProperty darkModeSelected = new SimpleBooleanProperty();

    public static AppearanceStage getInstance(){
        return INSTANCE;
    }

    public void createAppearanceStage(){
        if (appearanceStage != null && appearanceStage.isShowing()){
            appearanceStage.close();
        }
        Parent parentAppearance = getAppearancePane();
        parentAppearance.getStylesheets().addAll("resources/stylesheet.css");
        ControlsCSS.setParentAppearance(parentAppearance);
        ControlsCSS.refreshCSS();

        appearanceScene = new Scene(parentAppearance);

        appearanceStage = new Stage();
        appearanceStage.setTitle("Vzhled");
        appearanceStage.setScene(appearanceScene);
        appearanceStage.setMinWidth(TIMER_WINDOW_MIN_WIDTH);
        appearanceStage.setMinHeight(TIMER_WINDOW_MIN_HEIGHT);
        appearanceStage.getIcons().add(new Image("/resources/icon.png"));
        appearanceStage.setResizable(false);
        appearanceStage.show();

        appearanceStage.setOnCloseRequest(e -> closeRequest());

        setMainColorSelected(ControlsCSS.getMainColor());
        setDarkModeSelected(ControlsCSS.getDarkMode());

    }

    private Parent getAppearancePane() {
        colorGrid = new GridPane();

        Label colorLabel = new Label("Hlavní barva");
        ColorPicker colorPicker = new ColorPicker();
        mainColorSelected.bindBidirectional(colorPicker.valueProperty());

        Label modeLabel = new Label("Tmavý režim");
        CheckBox modeBox = new CheckBox();
        darkModeSelected.bindBidirectional(modeBox.selectedProperty());

        HBox buttonWrapper = new HBox();
        Button btnConfirm = new Button("Použít");
        btnConfirm.setOnAction(e -> overwriteFile());
        Button btnCancel = new Button("Zavřít");
        btnCancel.setOnAction(e -> closeRequest());

        colorGrid.add(colorLabel, 0, 0);
        colorGrid.add(colorPicker, 1, 0);
        colorGrid.add(modeLabel, 0, 1);
        colorGrid.add(modeBox, 1, 1);
        colorGrid.add(buttonWrapper, 0, 2, 2, 1);

        buttonWrapper.getChildren().addAll(btnConfirm, btnCancel);
        buttonWrapper.getChildren().forEach(n -> ((Region)n).setPrefWidth(100));
        buttonWrapper.getChildren().forEach(n -> ((Region)n).setPrefHeight(20));
        buttonWrapper.setSpacing(10);
        buttonWrapper.setPadding(new Insets(10, 0, 10, 0));
        buttonWrapper.setAlignment(Pos.TOP_CENTER);

        colorGrid.setHgap(5);
        colorGrid.setVgap(10);
        colorGrid.setAlignment(Pos.CENTER);

        return colorGrid;
    }

    public void overwriteFile(){
        try {
            ControlsCSS.setMainColor(getMainColorSelected());
            ControlsCSS.setDarkMode(getDarkModeSelected());

            FileWriter myWriter = new FileWriter("src" + File.separator + "resources" + File.separator + "customColor.css");

            StringBuilder outputText = new StringBuilder(".root{-mainColor:");
            StringBuilder previewText = new StringBuilder("-mainColor:");
            outputText.append(colorToHex(getMainColorSelected()));
            previewText.append(colorToHex(getMainColorSelected()));
            if(getDarkModeSelected()){
                outputText.append(";-primaryColor:#101010;-secondaryColor:#303030;-tertiaryColor:#A0A0A0}");
                previewText.append(";-primaryColor:#101010;-secondaryColor:#303030;-tertiaryColor:#A0A0A0");
            } else {
                outputText.append(";-primaryColor:#FFFFFF;-secondaryColor:#DDDDDD;-tertiaryColor:#606060}");
                previewText.append(";-primaryColor:#FFFFFF;-secondaryColor:#DDDDDD;-tertiaryColor:#606060");
            }
            myWriter.write(outputText.toString());
            myWriter.close();

            ControlsCSS.setStyleCSS(previewText.toString());
            ControlsCSS.refreshCSS();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Chyba při zápisu");
            alert.setHeaderText("Nepodařilo se uložit vybranou barvu");
            alert.showAndWait();
        }
    }

    public String colorToHex(Color color){
        int r = ((int) Math.round(color.getRed()     * 255)) << 16;
        int g = ((int) Math.round(color.getGreen()   * 255)) << 8;
        int b = ((int) Math.round(color.getBlue()    * 255));
        return String.format("#%06X", (r + g + b));
    }

    public void closeRequest(){
        ControlsCSS.mainColorProperty().unbind();
        ControlsCSS.darkModeProperty().unbind();

        appearanceStage.close();
    }

    public Color getMainColorSelected() {
        return mainColorSelected.get();
    }

    public ObjectProperty<Color> mainColorSelectedProperty() {
        return mainColorSelected;
    }

    public void setMainColorSelected(Color mainColorSelected) {
        this.mainColorSelected.set(mainColorSelected);
    }

    public boolean getDarkModeSelected() {
        return darkModeSelected.get();
    }

    public BooleanProperty darkModeSelectedProperty() {
        return darkModeSelected;
    }

    public void setDarkModeSelected(boolean darkModeSelected) {
        this.darkModeSelected.set(darkModeSelected);
    }
}
