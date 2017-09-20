package com.spaceinvaders.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

/**
 * Created by natha on 9/19/2017.
 */

public class PlayScreen implements Screen
{

    private Random rand = new Random();

    private SpaceInvaders game;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Texture spaceship_texture;
    private Texture invader_texture;
    private Rectangle spaceship;
    private ShapeRenderer shape_renderer;
    private Rectangle[][] invaders = new Rectangle[6][10];
    private boolean fired = false;
    private Array<Rectangle> space_missiles;
    private Array<Rectangle> invader_missiles;
    private int[][] can_fire;
    private long lastFired;
    private long lastMoved = TimeUtils.nanoTime();
    private boolean moving = true;
    private HudScene hud;
    private double speed = 1;
    private boolean speedup = true;
    private int num_invaders = 60;
    private Rectangle mother_ship;

    public PlayScreen(SpaceInvaders game)
    {

        this.batch = game.batch;
        this.hud = new HudScene(batch);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        shape_renderer = new ShapeRenderer();
        invader_texture = new Texture("invader-01.png");
        space_missiles = new Array<Rectangle>();
        invader_missiles = new Array<Rectangle>();

        spaceship_texture = new Texture("badlogic.jpg");
        spaceship = new Rectangle();
        spaceship.x = 800 / 2 - 16 / 2;
        spaceship.y = 20;
        spaceship.width = 16;
        spaceship.height = 32;

        initInvaders();

    }

    private void initInvaders()
    {

        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 10; j++)
                invaders[i][j] = new Rectangle(800 / 2 - 3 * 64  + j * 48, 380 - i * 40, 32, 32);

        can_fire = new int[6][10];
        for (int k = 0; k < 10; k++)
            can_fire[5][k] = 1;

    }

    @Override
    public void show()
    {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        this.hud.stage.draw();

        if (HudScene.lives == 0)
        {

            game.setScreen(new GameOverScreen(game));
            dispose();

        }

        if (num_invaders == 0) {
            initInvaders();
            num_invaders = 60;
        }

        if (space_missiles.size > 0)
            if (space_missiles.get(0).y > 480) {
                space_missiles.removeIndex(0);
                fired = false;
            }

        if (invader_missiles.size > 0)
            if (invader_missiles.get(0).y < 0) {
                invader_missiles.removeIndex(0);
            }

        if (!fired && (Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) && space_missiles.size == 0) {
            fired = true;
            space_missiles.add(new Rectangle(spaceship.x, spaceship.y + spaceship.height, 2, 10));
            System.out.println("Fire!");
            lastFired = TimeUtils.nanoTime();
            SpaceInvaders.manager.get("audio/sounds/shoot.wav", Sound.class).play();
        }

        int prob = rand.nextInt(10);
        if (prob == 1 && invader_missiles.size == 0)
        {

            int invader_fire = rand.nextInt(10);
            for (int i = 0; i < can_fire.length; i++) {
                if (can_fire[i][invader_fire] == 1)
                    if (invaders[i][invader_fire] != null)
                        invader_missiles.add(new Rectangle(invaders[i][invader_fire].x + invaders[i][invader_fire].width / 2, invaders[i][invader_fire].y - invaders[i][invader_fire].height, 2, 10));

            }

        }


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int i = 0; i < invaders.length; i++)
            for (int j = 0; j < invaders[i].length; j++)
                if (invaders[i][j] != null)
                    batch.draw(invader_texture, invaders[i][j].x, invaders[i][j].y, 40, 40);
        batch.end();

        shape_renderer.setProjectionMatrix(camera.combined);
        shape_renderer.begin(ShapeRenderer.ShapeType.Filled);
        shape_renderer.setColor(0, 1, 0, 1);
        shape_renderer.rect(spaceship.x, spaceship.y, spaceship.width, spaceship.height);
        shape_renderer.setColor(1, 1, 1, 1);

        for (Rectangle space_missile : space_missiles) {
            shape_renderer.rect(space_missile.x + (spaceship.width - space_missile.width) / 2, space_missile.y, space_missile.width, space_missile.height);
            space_missile.y += 400 * Gdx.graphics.getDeltaTime();
            for (int i = 0; i < invaders.length; i++)
                for (int j = 0; j < invaders[i].length; j++)
                    if (invaders[i][j] != null) {
                        double dist = Math.sqrt(Math.pow(invaders[i][j].x + invaders[i][j].width / 2 - space_missile.x + space_missile.width / 2, 2) + Math.pow(invaders[i][j].y + invaders[i][j].height / 2 - space_missile.y + space_missile.height / 2, 2));

                        if (dist < invaders[i][j].width / 2 + space_missile.width || dist < invaders[i][j].height / 2 + space_missile.height) {
                            invaders[i][j] = null;
                            space_missiles.removeValue(space_missile, false);
                            SpaceInvaders.manager.get("audio/sounds/invaderkilled.wav", Sound.class).play();
                            this.hud.addScore(20);
                            fired = false;
                            num_invaders--;
                            if (can_fire[i][j] == 1)
                                for (int k = i - 1; k >= 0; k--)
                                    if (invaders[k][j] != null)
                                    {
                                        can_fire[k][j] = 1;
                                        break;
                                    }

                            can_fire[i][j] = 0;

                        }

                    }
        }
        for (Rectangle invader_missile : invader_missiles) {
            shape_renderer.rect(invader_missile.x, invader_missile.y, invader_missile.width, invader_missile.height);
            invader_missile.y -= 400 * Gdx.graphics.getDeltaTime();

            double dist = Math.sqrt(Math.pow(spaceship.x + spaceship.width / 2 - invader_missile.x + invader_missile.width / 2, 2) + Math.pow(spaceship.y + spaceship.height / 2 - invader_missile.y + invader_missile.height / 2, 2));

            if (dist < spaceship.width / 2 + invader_missile.width || dist < spaceship.height / 2 + invader_missile.height) {
                invader_missiles.removeValue(invader_missile, false);
                SpaceInvaders.manager.get("audio/sounds/explosion.wav", Sound.class).play();
                this.hud.addScore(20);
                fired = false;
                this.hud.removeLife();
            }

        }

        shape_renderer.end();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) spaceship.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) spaceship.x += 200 * Gdx.graphics.getDeltaTime();

        if (spaceship.x < 0) spaceship.x = 0;
        if (spaceship.x > 800 - 16) spaceship.x = 800 - 16;

        // if (TimeUtils.nanoTime() - lastFired > 700000000)
        //   fired = false;

        if ((TimeUtils.nanoTime() - lastMoved)*0.5 > 900000000 * speed)
        {

            if (moving) {
                for (int i = 0; i < invaders.length; i++)
                    for (int j = 0; j < invaders[i].length; j++)
                        if (invaders[i][j] != null)
                            invaders[i][j].x += 40;

                if (!speedup)
                    speedup = true;

            }
            else {
                for (int i = 0; i < invaders.length; i++)
                    for (int j = 0; j < invaders[i].length; j++)
                        if (invaders[i][j] != null)
                            invaders[i][j].x -= 40;

                if (speedup && speed > 0.10) {
                    speed -= 0.15;
                    speedup = false;
                }

            }

            lastMoved = TimeUtils.nanoTime();
            SpaceInvaders.manager.get("audio/sounds/fastinvader2.wav", Sound.class).play();


        }

        for (int i = 0; i < invaders.length; i++)
            for (int j = 0; j < invaders[i].length; j++)
                if (invaders[i][j] != null)
                {

                    if (invaders[i][j].x < 0)
                    {

                        for (int k = 0; k < invaders.length; k++)
                            for (int l = 0; l < invaders[k].length; l++)
                                if (invaders[k][l] != null) {
                                    invaders[k][l].x += 10;
                                    invaders[k][l].y -= 10;
                                }

                        moving = true;

                    }
                    else if (invaders[i][j].x > 800 - 40)
                    {

                        for (int k = 0; k < invaders.length; k++)
                            for (int l = 0; l < invaders[k].length; l++)
                                if (invaders[k][l] != null)
                                    invaders[k][l].x -= 100;

                        moving = false;

                    }

                }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        hud.dispose();
    }

}
