import javafx.beans.property.*;
import javafx.css.CssParser;
import javafx.css.Rule;
import javafx.css.Stylesheet;
import javafx.css.converter.ColorConverter;
import javafx.scene.Parent;
import javafx.scene.paint.Color;

public class ControlsCSS {
    public static ObjectProperty<Color> mainColor = new SimpleObjectProperty<>();
    public static BooleanProperty darkMode = new SimpleBooleanProperty();

    private static Color previewColor;
    private static Boolean previewMode;

    private static String styleCSS;

    private static Parent parentMain;
    private static Parent parentAppearance;
    private static Parent parentLoader;
    private static Parent parentTimer;
    private static Parent parentAbout;

    public static void refreshCSS(){
        if(parentMain != null) parentMain.setStyle(styleCSS);
        if(parentAppearance != null) parentAppearance.setStyle(styleCSS);
        if(parentLoader != null) parentLoader.setStyle(styleCSS);
        if(parentTimer != null) parentTimer.setStyle(styleCSS);
        if(parentAbout != null) parentAbout.setStyle(styleCSS);

    }

    public static Color parseColor(String colorName) {
        CssParser parser = new CssParser();
        try {
            Stylesheet cssFile = parser.parse(Main.class.getClassLoader().getResource("resources/stylesheet.css").toURI().toURL());
            Rule rootRule = cssFile.getRules().get(0);
            return rootRule.getDeclarations().stream()
                    .filter(c -> c.getProperty().equals(colorName))
                    .findFirst()
                    .map(m -> ColorConverter.getInstance().convert(m.getParsedValue(), null))
                    .get();
        } catch (Exception ignore) {}
        if(colorName.equals("-mainColor")){
            return Color.ORANGE;
        }
        else {
            return Color.valueOf("#101010");
        }
    }

    public static void parseCSSFile(){
        setMainColor(parseColor("-maincolor"));
        previewColor = getMainColor();

        Color isDark = Color.valueOf("#101010");
        setDarkMode(isDark.equals(parseColor("-primarycolor")));
        previewMode = getDarkMode();
    }

    public static void setStyleCSS(String style){
        styleCSS = style;
    }

    public static void setParentMain(Parent parent){
        parentMain = parent;
    }

    public static void setParentAppearance(Parent parent){
        parentAppearance = parent;
    }

    public static void setParentLoader(Parent parent){
        parentLoader = parent;
    }

    public static void setParentTimer(Parent parent){
        parentTimer = parent;
    }

    public static void setParentAbout(Parent parent){
        parentAbout = parent;
    }

    public static Color getMainColor() {
        return mainColor.get();
    }

    public static ObjectProperty<Color> mainColorProperty() {
        return mainColor;
    }

    public static void setMainColor(Color mainColor) {
        ControlsCSS.mainColor.set(mainColor);
    }

    public static boolean getDarkMode() {
        return darkMode.get();
    }

    public static BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public static void setDarkMode(boolean darkMode) {
        ControlsCSS.darkMode.set(darkMode);
    }

    public static Color getPreviewColor() {
        return previewColor;
    }

    public static void setPreviewColor(Color previewColor) {
        ControlsCSS.previewColor = previewColor;
    }

    public static Boolean getPreviewMode() {
        return previewMode;
    }

    public static void setPreviewMode(Boolean previewMode) {
        ControlsCSS.previewMode = previewMode;
    }
}
