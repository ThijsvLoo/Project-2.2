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

public class MapBuilderInfo implements Disposable {

    MASS mass;

    public Stage stage;
    private Viewport viewport;

    private int fpsCount;
    private int simStep;

    private Label fpsNameLabel;
    private Label fpsLabel;
    private Label simstepNameLabel;
    private Label simstepLabel;

    private Label currentLabel;
    private Label builderLabel;
    private String currentBuildTool;

    public MapBuilderInfo(MASS mass) {
        this.mass = mass;

        fpsCount = 0;
        simStep = 0;

        viewport = new FitViewport(MASS.WINDOW_WIDTH,MASS.WINDOW_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, mass.batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        Label.LabelStyle labelstyle = new Label.LabelStyle(new BitmapFont(), Color.YELLOW);

        fpsNameLabel = new Label("FPS:", labelstyle);
        fpsLabel = new Label(String.format("%03d", fpsCount), labelstyle);
        simstepNameLabel = new Label("Simulation step:", labelstyle);
        simstepLabel = new Label(String.format("%06d", simStep), labelstyle);

        currentLabel = new Label(null, labelstyle);
        builderLabel = new Label(currentBuildTool, labelstyle);

        table.add(fpsNameLabel).padTop(10).expandX();
        table.add(currentLabel).padTop(10).expandX();
        table.add(simstepNameLabel).padTop(10).expandX();

        table.row();

        table.add(fpsLabel).padTop(5).expandX();
        table.add(builderLabel).padTop(5).expandX();
        table.add(simstepLabel).padTop(5).expandX();

        stage.addActor(table);
    }

    public void update(float dt){
        fpsCount = Gdx.graphics.getFramesPerSecond();
        fpsLabel.setText(String.format("%03d", fpsCount));
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void updateBuildTool(String tool){
        currentLabel.setText("Selected: ");
        builderLabel.setText(tool);
    }

}
