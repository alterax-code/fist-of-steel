package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
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
import com.fistofsteel.audio.SoundManager;
import com.fistofsteel.entities.Player;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.Constants;

public class GameScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player player;
    private InputHandler inputHandler;
    

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Array<Rectangle> collisionRects;

    private Music backgroundMusic;
    private SoundManager soundManager;


    @Override
    public void show() {
        camera = new OrthographicCamera();

        camera.setToOrtho(false, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        batch = new SpriteBatch();

        inputHandler = new InputHandler();
        Gdx.input.setInputProcessor(inputHandler);


        soundManager = new SoundManager();
        

        player = new Player(inputHandler, soundManager);
        

        loadTiledMap();
        

        if (collisionRects != null) {
            player.setCollisionRects(collisionRects);
        }

        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/background_music.mp3"));
            backgroundMusic.setLooping(true);  
            backgroundMusic.setVolume(0.5f);  
            backgroundMusic.play();    
            System.out.println("✓ Musique chargée et lancée !");
        } catch (Exception e) {
            System.err.println("⚠ Erreur lors du chargement de la musique : " + e.getMessage());
            e.printStackTrace();
           
        }
 
    }
    

    private void loadTiledMap() {
        try {
         
            tiledMap = new TmxMapLoader().load("maps/level1_example.tmx");
            
       
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            

            loadCollisions();
            
            System.out.println("✓ Map Tiled chargée avec succès !");
        } catch (Exception e) {
            System.err.println("⚠ Erreur lors du chargement de la map Tiled : " + e.getMessage());
            e.printStackTrace();
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
            System.out.println("✓ Collisions chargées : " + collisionRects.size + " rectangles");
        } else {
            System.out.println("⚠ Layer 'Collisions' non trouvé dans la map");
        }
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(delta);
        

        updateCamera();


        camera.update();
        
 
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }

        
        batch.setProjectionMatrix(camera.combined);

        player.render(batch);
    }
    

    private void updateCamera() {

        float playerX = player.getX() + Constants.PLAYER_WIDTH / 2;
        float playerY = player.getY() + Constants.PLAYER_HEIGHT / 2;
        

        camera.position.x = playerX;
        camera.position.y = playerY;
        

        float mapWidth = 60 * 64;
        float mapHeight = 20 * 64; 
        
        float halfViewportWidth = camera.viewportWidth / 2;
        float halfViewportHeight = camera.viewportHeight / 2;
        
     
        if (camera.position.x < halfViewportWidth) {
            camera.position.x = halfViewportWidth;
        }
        if (camera.position.x > mapWidth - halfViewportWidth) {
            camera.position.x = mapWidth - halfViewportWidth;
        }
        

        if (camera.position.y < halfViewportHeight) {
            camera.position.y = halfViewportHeight;
        }
        if (camera.position.y > mapHeight - halfViewportHeight) {
            camera.position.y = mapHeight - halfViewportHeight;
        }
    }


    @Override public void resize(int width, int height) {}
    
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
    
    @Override public void hide() {}

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
    
    
    public boolean checkCollision(Rectangle playerHitbox) {
        if (collisionRects == null) return false;
        
        for (Rectangle collisionRect : collisionRects) {
            if (playerHitbox.overlaps(collisionRect)) {
                return true;
            }
        }
        return false;
    }
    
    public Array<Rectangle> getCollisionRects() {
        return collisionRects;
    }

}