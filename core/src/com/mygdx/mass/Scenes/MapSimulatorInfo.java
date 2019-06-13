package com.mygdx.mass.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.mass.Data.MASS;
import com.mygdx.mass.Screens.MapSimulatorScreen;

public class MapSimulatorInfo implements Disposable {
    private MapSimulatorScreen mapSimulatorScreen;

    public Stage stage;
    private Viewport viewport;

    private int fpsCount;

    private Label fpsNameLabel;
    private Label fpsLabel;
    private Label worldSpeedFactorNameLabel;
    private Label worldSpeedFactorLabel;
    private Label simstepNameLabel;
    private Label simstepLabel;
    private Label simTimeNameLabel;
    private Label simTimeLabel;

    private Label currentLabel;
    private Label builderLabel;
    private String currentBuildTool;

    public MapSimulatorInfo(MapSimulatorScreen mapSimulatorScreen, SpriteBatch spritebatch) {
        this.mapSimulatorScreen = mapSimulatorScreen;

        fpsCount = 0;

        viewport = new FitViewport(MASS.WINDOW_WIDTH,MASS.WINDOW_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, spritebatch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        BitmapFont font = new BitmapFont();
        font.setFixedWidthGlyphs("0123456789");

        Label.LabelStyle labelstyle = new Label.LabelStyle(font, Color.YELLOW);

        fpsNameLabel = new Label("FPS:", labelstyle);
        fpsLabel = new Label(String.format("%03d", fpsCount), labelstyle);
        worldSpeedFactorNameLabel = new Label("Simulation speed factor:", labelstyle);
        worldSpeedFactorLabel = new Label(String.format("%02d", 1), labelstyle);
        simstepNameLabel = new Label("Simulation step:", labelstyle);
        simstepLabel = new Label(String.format("%09d", 0), labelstyle);
        simTimeNameLabel = new Label("Simulation time:", labelstyle);
        simTimeLabel = new Label(MASS.largeDoubleFormat.format(0), labelstyle);

        currentLabel = new Label("Current:", labelstyle);
        builderLabel = new Label(currentBuildTool, labelstyle);

        table.add(fpsNameLabel).padTop(10).expandX();
        table.add(worldSpeedFactorNameLabel).padTop(10).expandX();
        table.add(simstepNameLabel).padTop(10).expandX();
        table.add(simTimeNameLabel).padTop(10).expandX();

        table.row();

        table.add(fpsLabel).padTop(5).expandX();
        table.add(worldSpeedFactorLabel).padTop(5).expandX();
        table.add(simstepLabel).padTop(5).expandX();
        table.add(simTimeLabel).padTop(5).expandX();

        stage.addActor(table);
    }

    public void update(float dt){
        fpsCount = Gdx.graphics.getFramesPerSecond();
        fpsLabel.setText(MASS.largeIntegerFormat.format(fpsCount) + " Hz");
        simstepLabel.setText(MASS.largeIntegerFormat.format(mapSimulatorScreen.getSimulationStep()));
        simTimeLabel.setText(MASS.largeDoubleFormat.format(mapSimulatorScreen.getSimulationTime()) + " s");
        worldSpeedFactorLabel.setText(String.format("%03d",mapSimulatorScreen.getWorldSpeedFactor()) + " X");
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void updateBuildTool(String tool){
        builderLabel.setText(tool);
    }

}
