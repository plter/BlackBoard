package com.plter.blackboard.controllers;

import com.plter.blackboard.core.Pointer;
import com.plter.blackboard.graphics.ChalkPathCommands;
import com.plter.blackboard.graphics.ColorTool;
import com.plter.blackboard.res.Images;
import com.plter.blackboard.views.ViewLoader;
import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {


    public StackPane spBlackBoard;
    public HBox hbTop;
    public Pane pnLeft;
    public ImageView ivTopLeft;
    public Pane pnCenter;
    public Pane pnRight;
    public ImageView ivTopRight;
    public HBox hbBottom;
    public ImageView ivBottomLeft;
    public ImageView ivBottomRight;
    public ColorPicker cpChalkColor;
    public Canvas canvas;
    public Pane canvasContainer;
    public Button btnChalk;
    public Button btnEraser;
    private GraphicsContext context2D;
    private ChalkPathCommands commands = new ChalkPathCommands();
    private Pointer currentPointer = Pointer.Chalk;

    public static Scene createScene() {
        return new Scene(ViewLoader.loadView("MainView.fxml").getView(), 800, 500);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupBackground();
        setupCanvas();

        resetChalkPathCommands();
    }

    private void setupCanvas() {
        //bind canvas width and height to it's parent
        canvas.widthProperty().bind(canvasContainer.widthProperty());
        canvas.heightProperty().bind(canvasContainer.heightProperty());

        //get canvas context 2d
        context2D = canvas.getGraphicsContext2D();

        //add listeners
        canvas.widthProperty().addListener(canvasResizeHandler);
        canvas.heightProperty().addListener(canvasResizeHandler);
    }

    private ChangeListener<? super Number> canvasResizeHandler = (observable, oldValue, newValue) -> {
        commands.runDrawCommands();
    };

    private void setupBackground() {
        hbTop.setBackground(new Background(new BackgroundImage(Images.getImage("Top.png"), null, null, null, null)));
        ivTopLeft.setImage(Images.getImage("TopLeft.png"));
        ivTopRight.setImage(Images.getImage("TopRight.png"));
        pnLeft.setBackground(new Background(new BackgroundImage(Images.getImage("Left.png"), null, null, null, null)));
        pnRight.setBackground(new Background(new BackgroundImage(Images.getImage("Right.png"), null, null, null, null)));
        hbBottom.setBackground(new Background(new BackgroundImage(Images.getImage("Bottom.png"), null, null, null, null)));
        ivBottomLeft.setImage(Images.getImage("BottomLeft.png"));
        ivBottomRight.setImage(Images.getImage("BottomRight.png"));
    }

    private void resetChalkPathCommands() {
        commands.clear();
        commands.addCommand(() -> context2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()));

        context2D.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void btnClearBlackBoardClickHandler(ActionEvent actionEvent) {
        resetChalkPathCommands();
    }

    public void canvasMousePressedHandler(MouseEvent event) {

        //set init values
        Color currentColorClone;
        double currentLineWidth;
        switch (currentPointer){
            case Eraser:
                currentColorClone = ColorTool.cloneColor(Color.DARKSLATEGREY);
                currentLineWidth = 30;
                break;
            default:
                currentColorClone = ColorTool.cloneColor(cpChalkColor.getValue());
                currentLineWidth = 2;
                break;
        }



        ChalkPathCommands cmds = new ChalkPathCommands();

        cmds.addCommand(() -> {
            context2D.beginPath();
            context2D.setLineWidth(currentLineWidth);
            context2D.setLineCap(StrokeLineCap.ROUND);
            context2D.setStroke(currentColorClone);
            context2D.moveTo(event.getX(), event.getY());
        });

        cmds.runDrawCommands();

        //record to commands
        commands.addCommands(cmds);
    }


    public void canvasMouseDraggedHandler(MouseEvent event) {
        //make draw command
        ChalkPathCommands cmds = new ChalkPathCommands();
        cmds.addCommand(() -> context2D.lineTo(event.getX(), event.getY()));

        //draw to canvas
        cmds.runDrawCommands();
        context2D.stroke();

        //record path to commands
        commands.addCommands(cmds);
    }


    public void canvasMouseReleasedHandler(MouseEvent event) {
        commands.addCommand(() -> context2D.stroke());
        commands.addCommand(() -> context2D.closePath());

        context2D.closePath();
    }

    public void btnSaveBackBoardClickHandler(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("选择保存路径");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("png图片文件", "*.png"));
        File file = fc.showSaveDialog(spBlackBoard.getScene().getWindow());
        if (file != null) {
            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.DARKSLATEGREY);
            WritableImage image = canvas.snapshot(sp, new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight()));
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();

                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("提醒");
                dialog.setContentText("无法保存图片");
                dialog.getDialogPane().getButtonTypes().add(new ButtonType("OK", ButtonBar.ButtonData.OK_DONE));
                dialog.show();
            }
        }
    }

    public void btnChalkClickedHandler(ActionEvent actionEvent) {
        currentPointer = Pointer.Chalk;
    }

    public void btnEraserClickedHandler(ActionEvent actionEvent) {
        currentPointer = Pointer.Eraser;
    }
}
