package com.spaceinvaders.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
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

public class SpaceInvaders extends Game {

	public SpriteBatch batch;
	public static AssetManager manager;

	
	@Override
	public void create () {


		batch = new SpriteBatch();


        manager = new AssetManager();
        // Loading sounds
        manager.load("audio/sounds/explosion.wav", Sound.class);
        manager.load("audio/sounds/fastinvader2.wav", Sound.class);
        manager.load("audio/sounds/invaderkilled.wav", Sound.class);
        manager.load("audio/sounds/shoot.wav", Sound.class);
        manager.finishLoading();

        // Setting the main play screen
        this.setScreen(new PlayScreen(this));

	}



	@Override
	public void render () { super.render(); }
	
	@Override
	public void dispose () {
		batch.dispose();
        manager.dispose();
	}
}
