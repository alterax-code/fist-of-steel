package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.fistofsteel.FistOfSteelGame;
import com.fistofsteel.audio.SoundManager;
import com.fistofsteel.entities.Hugo;
import com.fistofsteel.entities.Player;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.Constants;

public class GameManager implements Screen {
    private FistOfSteelGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player player;
    private InputHandler inputHandler;
    
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Array<Rectangle> collisionRects;
    
    private Music backgroundMusic;
    private SoundManager soundManager;

    public GameManager(FistOfSteelGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        
        float worldHeight = 20 * 64;
        float screenAspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        float viewportWidth = worldHeight * screenAspectRatio;
        
        camera.setToOrtho(false, viewportWidth, worldHeight);
        
        batch = new SpriteBatch();
        inputHandler = new InputHandler();
        Gdx.input.setInputProcessor(inputHandler);
        soundManager = new SoundManager();

        loadTiledMap();
        
        player = new Hugo(inputHandler, soundManager);
        
        if (collisionRects != null) {
            player.setCollisionRects(collisionRects);
        }
        
        loadSpawnFromTiled();
        
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/background_music.mp3"));
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f);
            backgroundMusic.play();
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du chargement de la musique : " + e.getMessage());
        }
        
        updateCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }
    
    private void loadTiledMap() {
        try {
            tiledMap = new TmxMapLoader().load("maps/level1_example.tmx");
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            loadCollisions();
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du chargement de la map Tiled : " + e.getMessage());
        }
    }
    
    private void loadSpawnFromTiled() {
        if (tiledMap == null) return;
        
        MapLayer spawnLayer = tiledMap.getLayers().get("spawn");
        if (spawnLayer == null) return;
        
        int mapHeightInTiles = tiledMap.getProperties().get("height", Integer.class);
        int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
        float mapHeightInPixels = mapHeightInTiles * tileHeight;
        
        for (MapObject object : spawnLayer.getObjects()) {
            String objectName = object.getName();
            String className = object.getProperties().get("class", String.class);
            
            boolean isSpawn = "spawn".equalsIgnoreCase(className) || 
                            "playerSpawn".equalsIgnoreCase(objectName);
            
            if (isSpawn) {
                float tiledX = object.getProperties().get("x", Float.class);
                float tiledY = object.getProperties().get("y", Float.class);
                
                float libgdxX = tiledX;
                float libgdxY = mapHeightInPixels - tiledY;
                
                player.setPosition(libgdxX, libgdxY);
                return;
            }
        }
    }
    
    private void loadCollisions() {
        collisionRects = new Array<>();
        if (tiledMap == null) return;
        
        MapLayer collisionLayer = tiledMap.getLayers().get("Collisions");
        if (collisionLayer != null) {
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();
                    collisionRects.add(new Rectangle(rect));
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        player.update(delta);
        updateCamera();
        camera.update();

        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        player.render(batch);
        batch.end();
    }
    
    private void updateCamera() {
        float mapWidth = 60 * 64;
        float mapHeight = 20 * 64;
        
        float playerX = player.getX() + Constants.PLAYER_WIDTH / 2;
        camera.position.x = playerX;
        camera.position.y = mapHeight / 2;
        
        float halfViewportWidth = camera.viewportWidth / 2;
        
        if (camera.position.x < halfViewportWidth) {
            camera.position.x = halfViewportWidth;
        }
        if (camera.position.x > mapWidth - halfViewportWidth) {
            camera.position.x = mapWidth - halfViewportWidth;
        }
    }

    @Override 
    public void resize(int width, int height) {
        float worldHeight = 20 * 64;
        float screenAspectRatio = (float) width / height;
        float viewportWidth = worldHeight * screenAspectRatio;
        
        camera.setToOrtho(false, viewportWidth, worldHeight);
        updateCamera();
        camera.update();
    }
    
    @Override 
    public void pause() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }
    
    @Override 
    public void resume() {
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
    }
    
    @Override 
    public void hide() {
        if (Gdx.input.getInputProcessor() == inputHandler) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (tiledMapRenderer != null) {
            tiledMapRenderer.dispose();
        }
        
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        
        if (soundManager != null) {
            soundManager.dispose();
        }
    }
}