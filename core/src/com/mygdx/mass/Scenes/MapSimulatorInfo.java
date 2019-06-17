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
    private static long rayCount = 0, rayCollisionCount = 0;

    private Label fpsNameLabel, fpsLabel;
    private Label worldSpeedFactorNameLabel, worldSpeedFactorLabel;
    private Label simstepNameLabel, simstepLabel;
    private Label simTimeNameLabel, simTimeLabel;
    private Label rayCountNameLabel, rayCountLabel;
    private Label rayCollisionCountNameLabel, rayCollisionCountLabel;


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
        rayCountNameLabel = new Label("# Rays:", labelstyle);
        rayCountLabel = new Label(String.format("%09d", 0), labelstyle);
        rayCollisionCountNameLabel = new Label("# Ray collisions:", labelstyle);
        rayCollisionCountLabel = new Label(String.format("%09d", 0), labelstyle);


        currentLabel = new Label("Current:", labelstyle);
        builderLabel = new Label(currentBuildTool, labelstyle);

        table.add(fpsNameLabel).padTop(10).expandX();
        table.add(worldSpeedFactorNameLabel).padTop(10).expandX();
        table.add(simstepNameLabel).padTop(10).expandX();
        table.add(simTimeNameLabel).padTop(10).expandX();
        table.add(rayCountNameLabel).padTop(10).expandX();
        table.add(rayCollisionCountNameLabel).padTop(10).expandX();

        table.row();

        table.add(fpsLabel).padTop(5).expandX();
        table.add(worldSpeedFactorLabel).padTop(5).expandX();
        table.add(simstepLabel).padTop(5).expandX();
        table.add(simTimeLabel).padTop(5).expandX();
        table.add(rayCountLabel).padTop(5).expandX();
        table.add(rayCollisionCountLabel).padTop(5).expandX();

        stage.addActor(table);
    }

    public void update(float dt){
        fpsCount = Gdx.graphics.getFramesPerSecond();
        fpsLabel.setText(MASS.largeIntegerFormat.format(fpsCount) + " Hz");
        simstepLabel.setText(MASS.largeIntegerFormat.format(mapSimulatorScreen.getSimulationStep()));
        simTimeLabel.setText(MASS.largeDoubleFormat.format(mapSimulatorScreen.getSimulationTime()) + " s");
        worldSpeedFactorLabel.setText(String.format("%03d",mapSimulatorScreen.getWorldSpeedFactor()) + " X");
        rayCountLabel.setText(MASS.largeIntegerFormat.format(rayCount));
        rayCollisionCountLabel.setText(MASS.largeIntegerFormat.format(rayCollisionCount));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void updateBuildTool(String tool){
        builderLabel.setText(tool);
    }

    public static void addRayCount() {
        rayCount++;
    }

    public static void addRayCollisionCount() {
        rayCollisionCount++;
    }

    public static void resetRayCounters() {
        rayCount = 0;
        rayCollisionCount = 0;
    }

}
