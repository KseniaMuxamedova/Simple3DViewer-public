package com.cgvsu;

import com.cgvsu.Rasterization.DrawUtilsJavaFX;
import com.cgvsu.Rasterization.GraphicsUtils;
import com.cgvsu.Rasterization.MyColor;
import com.cgvsu.Rasterization.Rasterization;
import com.cgvsu.math.Math.Vector.Vector3f;
import com.cgvsu.model.Polygon;
import com.cgvsu.model.TransformedModel;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.render_engine.RenderEngine;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.render_engine.Camera;

public class GuiController {
    private final static boolean willItWriteInformationToConsole = true;

    final private float TRANSLATION = 1F;

    final private float SCALE = 0.05F;

    final private float ROTATE_PARAM = 1F;

    static final float EPS = 1e-6f;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private  ColorPicker modelColorPicker;
    private Color colorModel;

    private ArrayList<TransformedModel> models = new ArrayList<>();
    @FXML
    private ComboBox listOfModels;
    
    private boolean isTriangulate = false;
    private boolean isRasterize = false;

    private boolean isKeepMesh = false;

    
    @FXML
    private CheckBox fillPolygons;
    @FXML
    private CheckBox triangulation;
    @FXML
    private CheckBox addLight;

    private ArrayList<ArrayList<KeyCode>> keyCodes = new ArrayList<>();
    private ArrayList<String> modelsNames = new ArrayList<>();

    private Camera camera = new Camera(
            new Vector3f(new float[]{0, 00, 100}),
            new Vector3f(new float[]{0, 0, 0}),
            1.0F, 1, 0.01F, 100);

    private Timeline timeline;



    @FXML
    private void initialize() {

        anchorPane.prefWidthProperty().addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue()));
        anchorPane.prefHeightProperty().addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        GraphicsUtils graphicsUtils = new DrawUtilsJavaFX(canvas);


        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);


        modelColorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                colorModel = modelColorPicker.getValue();
            }
        });


        KeyFrame frame = new KeyFrame(Duration.millis(30), event -> {
            double width = canvas.getWidth();
            double height = canvas.getHeight();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));


            if (models != null) {

                handleMouseActions();
                listOfModels.setItems(FXCollections
                        .observableArrayList(listToArr(modelsNames)));
                RenderEngine.render(canvas.getGraphicsContext2D(), camera, models,
                        (int) width, (int) height, isRasterize , graphicsUtils, isKeepMesh, modelColorPicker);
            }
        });
        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    FileChooser fileChooser = new FileChooser();



    //возможность сохранить транс и не транс модели
    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            Model mesh = ObjReader.read(fileContent, willItWriteInformationToConsole);
            TransformedModel transformedModel = new TransformedModel(mesh);
            transformedModel.setTransformedModel();
            models.add(transformedModel);
            modelsNames.add(file.getName());

            //проверить пустоту
            keyCodes.add(new ArrayList<>());
            // todo: обработка ошибок
        } catch (IOException exception) {

        }
    }

    private Object[] listToArr(ArrayList<String> arrayList) {
        String[] arr = new String[arrayList.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arrayList.get(i);
        }
        return arr;
    }

    private int getSelectIndex(ComboBox comboBox){
        SingleSelectionModel selectionModel = comboBox.getSelectionModel();
        return selectionModel.getSelectedIndex();
    }

    @FXML
    private void onWriteModelMenuItemClick() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");
        fileChooser.setInitialFileName("Saved Model");
        try {
            File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
            ObjWriter.writeTransformedModel(file.getAbsolutePath(), models.get(getSelectIndex(listOfModels)));
            // todo: обработка ошибок
        } catch (Exception exception) {

        }
    }

    @FXML
    private void onWriteTransformedModelMenuItemClick() throws IOException {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Save Model");
        fileChooser.setInitialFileName("Saved Model");
        try {
            File file = fileChooser.showSaveDialog((Stage) canvas.getScene().getWindow());
            ObjWriter.write(file.getAbsolutePath(), models.get(getSelectIndex(listOfModels)).getTransformedModel());
            // todo: обработка ошибок
        } catch (Exception exception) {

        }
    }
    public void FillingPolygons(){
          isRasterize = !isRasterize;
    }

    public void triangulationModel(){
            isTriangulate = !isTriangulate;
        Model.triangulate(models.get(getSelectIndex(listOfModels)).actualModel,isTriangulate);
    }
    public void addLight(){

    }

    public void keepMesh(){
        isKeepMesh = !isKeepMesh;
    }

    private void handleMouseActions() {
        if (keyCodes.isEmpty() || getSelectIndex(listOfModels) == -1 ||
                keyCodes.get(getSelectIndex(listOfModels)).isEmpty()) {
            canvas.setOnScroll(this::handleMouseWheelMoved);
            canvas.setOnMousePressed(this::handleMousePressed);
        } else {
            if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.S)) {
                handleModelScale();
            } else if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.R)) {
                handleModelRotation();
            } else if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.G)) {
                handleModelTranslation();
            }
        }
    }

    //камера
    private void handleMouseWheelMoved(ScrollEvent event) {
        final double notches = event.getDeltaY();
        final float x = camera.getPosition().getX();
        final float y = camera.getPosition().getY();
        final float z = camera.getPosition().getZ();
        final float max = Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));
        final float paramX = x < EPS ? -TRANSLATION : TRANSLATION;
        final float paramY = y < EPS ? -TRANSLATION : TRANSLATION;
        final float paramZ = z < EPS ? -TRANSLATION : TRANSLATION;

        if (notches < EPS) {
            if (max - Math.abs(x) < EPS) {
                camera.movePosition(new Vector3f(new float[]{paramX, 0, 0}));
            } else if (max - Math.abs(y) < EPS) {
                camera.movePosition(new Vector3f(new float[]{0, paramY, 0}));
            } else {
                camera.movePosition(new Vector3f(new float[]{0, 0, paramZ}));
            }
        } else {
            if (max - Math.abs(x) < EPS) {
                camera.movePosition(new Vector3f(new float[]{-paramX, 0, 0}));
            } else if (max - Math.abs(y) < EPS) {
                camera.movePosition(new Vector3f(new float[]{0, -paramY, 0}));
            } else {
                camera.movePosition(new Vector3f(new float[]{0, 0, -paramZ}));
            }
        }
    }
    
    private void handleModelScale() {
        canvas.setOnScroll(scrollEvent -> {
            final float sign = scrollEvent.getDeltaY() > EPS ? SCALE : -SCALE;
            if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.X)) {
                scaleByX(sign);
            } else if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.Y)) {
                scaleByY(sign);
            } else if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.Z)) {
                scaleByZ(sign);
            } else {
                scaleByX(sign);
                scaleByY(sign);
                scaleByZ(sign);
            }
        });
    }

    private void handleModelRotation() {
        canvas.setOnMousePressed(event -> {
            final float x = (float) (canvas.getWidth() / 2);
            final float y = (float) (canvas.getHeight() / 2);
            var ref = new Object() {
                float prevX = (float) event.getX();
                float prevY = (float) event.getY();
            };
            canvas.setOnMouseDragged(mouseEvent -> {
                final float actualX = (float) mouseEvent.getX();
                final float actualY = (float) mouseEvent.getY();
                final float dx = ref.prevX - actualX;
                final float dy = ref.prevY - actualY;
                float dz = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

                if (ref.prevY <= y && dx <= EPS) {
                    dz *= -1;
                } else if (ref.prevY >= y && dx >= EPS) {
                    dz *= -1;
                }

                ref.prevX = actualX;
                ref.prevY = actualY;
                rotateModel((Vector3f) new Vector3f(new float[]{dy, -dx, dz}).multiplicateVectorOnConstant(0.01f));
            });
        });
    }

    private void rotateModel(final Vector3f vector3f) {
        if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.X)) {
            rotateAroundX(vector3f.getX());
        } else if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.Y)) {
            rotateAroundY(vector3f.getY());
        } else if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.Z)) {
            rotateAroundZ(vector3f.getZ());
        } else {
            rotateAroundX(vector3f.getX());
            rotateAroundY(vector3f.getY());
            rotateAroundZ(vector3f.getZ());
        }
    }

    private void handleModelTranslation() {
        canvas.setOnMousePressed(event -> {
            var ref = new Object() {
                float prevX = (float) event.getX();
                float prevY = (float) event.getY();
            };
            canvas.setOnMouseDragged(mouseEvent -> {
                final float actualX = (float) mouseEvent.getX();
                final float actualY = (float) mouseEvent.getY();
                final float dx = ref.prevX - actualX;
                final float dy = ref.prevY - actualY;
                final float max = Math.max(Math.abs(dx), Math.abs(dy));
                final float dz = max - Math.abs(dx) <= EPS ? dx : -dy;

                ref.prevX = actualX;
                ref.prevY = actualY;
                translateModel((Vector3f) new Vector3f(new float[]{dx, dy, dz}).multiplicateVectorOnConstant(0.01f));
            });
        });
    }

    private void translateModel(final Vector3f vector3f) {
        if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.X)) {
            translateX(vector3f.getX());
        } else if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.Y)) {
            translateY(vector3f.getY());
        } else if (keyCodes.get(getSelectIndex(listOfModels)).contains(KeyCode.Z)) {
            translateZ(vector3f.getZ());
        } else {
            translateX(vector3f.getX());
            translateY(vector3f.getY());
            translateZ(vector3f.getZ());
        }
    }

    //камера
    private void handleMousePressed(javafx.scene.input.MouseEvent event) {
        var ref = new Object() {
            float prevX = (float) event.getX();
            float prevY = (float) event.getY();
        };
        canvas.setOnMouseDragged(mouseEvent -> {
            final float actualX = (float) mouseEvent.getX();
            final float actualY = (float) mouseEvent.getY();
            float dx = ref.prevX - actualX;
            final float dy = actualY - ref.prevY;
            final float dxy = Math.abs(dx) - Math.abs(dy);
            float dz = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

            if (dxy >= EPS && (camera.getPosition().getX() <= EPS && dx < EPS ||
                    camera.getPosition().getX() > EPS && dx > EPS)) {
                dz *= -1;
            } else if (dxy < EPS) { //если больше перемещаем по y, то по z не перемещаем
                dz = 0;
            }
            if (camera.getPosition().getZ() <= EPS) {
                dx *= -1;
            }

            ref.prevX = actualX;
            ref.prevY = actualY;
            camera.movePosition((Vector3f) new Vector3f(new float[]{dx, dy, dz}).multiplicateVectorOnConstant(0.01f));
        });
    }

    public void handleKeyEvent(KeyEvent e) {
        final KeyCode key = e.getCode();
        final Set<KeyCode> listSRG = Set.of(KeyCode.S, KeyCode.R, KeyCode.G);
        if (!listSRG.contains(key)) {
            checkXYZKeys(key);
        }
    }

    private void checkXYZKeys(final KeyCode key) {
        final Set<KeyCode> listXYZ = Set.of(KeyCode.X, KeyCode.Y, KeyCode.Z);
        final int modelIndex = getSelectIndex(listOfModels);
        if (!keyCodes.get(modelIndex).contains(key)) {
            if (keyCodes.get(modelIndex).size() > 1) {
                for (KeyCode keyCode : listXYZ) {
                    keyCodes.get(modelIndex).remove(keyCode);
                }
            }
            keyCodes.get(modelIndex).add(key);
        } else { // при повторном нажатии удаляет
            keyCodes.get(modelIndex).remove(key);
        }
    }

    private void checkSRGKeys(final KeyCode key) {
        final int modelIndex = getSelectIndex(listOfModels);
        if (!keyCodes.get(modelIndex).contains(key)) {
            keyCodes.get(modelIndex).clear();
            keyCodes.get(modelIndex).add(key);
        } else {
            keyCodes.get(modelIndex).clear();
        }
    }

    // 9 параметров
    @FXML
    public void handleCameraForward() {
        camera.movePosition(new Vector3f(new float[]{0, 0, -TRANSLATION}));
    }

    @FXML
    public void handleCameraBackward() {
        camera.movePosition(new Vector3f(new float[]{0, 0, TRANSLATION}));
    }

    @FXML
    public void handleCameraLeft() {
        camera.movePosition(new Vector3f(new float[]{TRANSLATION, 0, 0}));
    }

    @FXML
    public void handleCameraRight() {
        camera.movePosition(new Vector3f(new float[]{-TRANSLATION, 0, 0}));
    }

    @FXML
    public void handleCameraUp() {
        camera.movePosition(new Vector3f(new float[]{0, TRANSLATION, 0}));
    }

    @FXML
    public void handleCameraDown() {
        camera.movePosition(new Vector3f(new float[]{0, -TRANSLATION, 0}));
    }

    public void scaleModel(final int index, final float param) {
        final float scaleParam = models.get(getSelectIndex(listOfModels)).getScaleParams().get(index);
        if (scaleParam - 1 <= EPS) {
            models.get(getSelectIndex(listOfModels)).setScaleParams(index, scaleParam * param);
        } else {
            models.get(getSelectIndex(listOfModels)).setScaleParams(index, param);
        }
    }

    public void scaleByX(final float param) {
        scaleModel(0, param);
    }

    public void scaleByY(final float param) {
        scaleModel(1, param);
    }

    public void scaleByZ(final float param) {
        scaleModel(2, param);
    }

    public void rotateAroundX(final float param) {
        models.get(getSelectIndex(listOfModels)).setRotateXParam(param);
    }

    public void rotateAroundY(final float param) {
        models.get(getSelectIndex(listOfModels)).setRotateYParam(param);
    }

    public void rotateAroundZ(final float param) {
        models.get(getSelectIndex(listOfModels)).setRotateZParam(param);
    }

    private void translateX(final float param) {
        models.get(getSelectIndex(listOfModels)).setTranslateXParam(param);
    }

    private void translateY(final float param) {
        models.get(getSelectIndex(listOfModels)).setTranslateYParam(param);
    }

    private void translateZ(final float param) {
        models.get(getSelectIndex(listOfModels)).setTranslateZParam(param);
    }

    @FXML
    public void addScaleKey() {
        checkSRGKeys(KeyCode.S);
        handleModelScale();
    }

    @FXML
    public void addRotateKey() {
        checkSRGKeys(KeyCode.R);
        handleModelRotation();
    }

    @FXML
    public void addTranslateKey() {
        checkSRGKeys(KeyCode.G);
        handleModelTranslation();
    }


}
