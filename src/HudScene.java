package com.spaceinvaders.game;

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

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by natha on 9/18/2017.
 */

public class HudScene implements Disposable
{

    public Stage stage;
    public Viewport viewport;

    private static int score;
    public static int lives;
    private static Label scoreLabel;
    private static Label livesLabel;

    public HudScene(SpriteBatch batch)
    {

        this.score = 0;
        this.lives = 3;

        this.viewport = new FitViewport(800, 480, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        scoreLabel = new Label("Score: " + NumberFormat.getNumberInstance(Locale.US).format(score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        livesLabel = new Label("Lives: " + lives, new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(scoreLabel).expandX().padTop(10);
        table.add(livesLabel).expandX().padTop(10);

        stage.addActor(table);

    }

    public static void addScore(int value)
    {

        score += value;
        scoreLabel.setText("Score: " + NumberFormat.getNumberInstance(Locale.US).format(score));


    }

    public static void addLife()
    {

        lives++;
        livesLabel.setText("Lives: " + lives);

    }

    public static void removeLife()
    {

        lives--;
        livesLabel.setText("Lives: " + lives);

    }

    @Override
    public void dispose() {
        this.stage.dispose();
    }
}
