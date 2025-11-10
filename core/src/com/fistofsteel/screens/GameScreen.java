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
    
    // ===== AJOUT POUR TILED =====
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Array<Rectangle> collisionRects;
    // ============================
    
    // ===== MUSIQUE ET SONS =====
    private Music backgroundMusic;
    private SoundManager soundManager;
    // ===========================

    @Override
    public void show() {
        camera = new OrthographicCamera();
        // Caméra 1:1 avec la fenêtre (pas de zoom)
        camera.setToOrtho(false, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        batch = new SpriteBatch();

        inputHandler = new InputHandler();
        Gdx.input.setInputProcessor(inputHandler);

        // Créer le gestionnaire de sons
        soundManager = new SoundManager();
        
        // Créer le player avec le soundManager
        player = new Player(inputHandler, soundManager);
        
        // ===== CHARGEMENT DE LA MAP TILED =====
        loadTiledMap();
        
        // Passer les collisions au player
        if (collisionRects != null) {
            player.setCollisionRects(collisionRects);
        }
        // ======================================
        
        // ===== CHARGEMENT ET LECTURE DE LA MUSIQUE =====
        try {
            backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/background_music.mp3"));
            backgroundMusic.setLooping(true);  // Lecture en boucle
            backgroundMusic.setVolume(0.5f);   // Volume à 50%
            backgroundMusic.play();            // Démarrer la musique
            System.out.println("✓ Musique chargée et lancée !");
        } catch (Exception e) {
            System.err.println("⚠ Erreur lors du chargement de la musique : " + e.getMessage());
            e.printStackTrace();
            // Le jeu continue sans musique
        }
        // ===============================================
    }
    
    // ===== NOUVELLE MÉTHODE POUR CHARGER LA MAP =====
    private void loadTiledMap() {
        try {
            // Charger la map Tiled
            tiledMap = new TmxMapLoader().load("maps/level1_example.tmx");
            
            // Créer le renderer (pas de scale, on garde les pixels)
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            
            // Charger les collisions
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
        
        // Récupérer le layer "Collisions"
        MapLayer collisionLayer = tiledMap.getLayers().get("Collisions");
        
        if (collisionLayer != null) {
            // Parcourir tous les objets du layer
            for (MapObject object : collisionLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();
                    
                    // Les coordonnées sont déjà en pixels, on les garde telles quelles
                    collisionRects.add(new Rectangle(rect));
                }
            }
            System.out.println("✓ Collisions chargées : " + collisionRects.size + " rectangles");
        } else {
            System.out.println("⚠ Layer 'Collisions' non trouvé dans la map");
        }
    }
    // ================================================

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        player.update(delta);
        
        // ===== CAMÉRA QUI SUIT LE JOUEUR =====
        updateCamera();
        // ====================================

        camera.update();
        
        // ===== DESSINER LA MAP TILED EN ARRIÈRE-PLAN =====
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }
        // ==================================================
        
        batch.setProjectionMatrix(camera.combined);

        player.render(batch);
    }
    
    // ===== NOUVELLE MÉTHODE POUR SUIVRE LE JOUEUR =====
    private void updateCamera() {
        // Position du joueur
        float playerX = player.getX() + Constants.PLAYER_WIDTH / 2;
        float playerY = player.getY() + Constants.PLAYER_HEIGHT / 2;
        
        // La caméra suit le joueur
        camera.position.x = playerX;
        camera.position.y = playerY;
        
        // Limiter la caméra aux bords de la map
        float mapWidth = 60 * 64; // 60 tiles * 64 pixels
        float mapHeight = 20 * 64; // 20 tiles * 64 pixels
        
        float halfViewportWidth = camera.viewportWidth / 2;
        float halfViewportHeight = camera.viewportHeight / 2;
        
        // Limites horizontales
        if (camera.position.x < halfViewportWidth) {
            camera.position.x = halfViewportWidth;
        }
        if (camera.position.x > mapWidth - halfViewportWidth) {
            camera.position.x = mapWidth - halfViewportWidth;
        }
        
        // Limites verticales
        if (camera.position.y < halfViewportHeight) {
            camera.position.y = halfViewportHeight;
        }
        if (camera.position.y > mapHeight - halfViewportHeight) {
            camera.position.y = mapHeight - halfViewportHeight;
        }
    }
    // ==================================================

    @Override public void resize(int width, int height) {}
    
    @Override 
    public void pause() {
        // Mettre la musique en pause
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }
    
    @Override 
    public void resume() {
        // Reprendre la musique
        if (backgroundMusic != null) {
            backgroundMusic.play();
        }
    }
    
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        player.dispose();
        
        // ===== NETTOYAGE TILED =====
        if (tiledMap != null) {
            tiledMap.dispose();
        }
        if (tiledMapRenderer != null) {
            tiledMapRenderer.dispose();
        }
        // ===========================
        
        // ===== NETTOYAGE MUSIQUE =====
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        // =============================
        
        // ===== NETTOYAGE SONS =====
        if (soundManager != null) {
            soundManager.dispose();
        }
        // ==========================
    }
    
    // ===== MÉTHODE UTILITAIRE POUR LES COLLISIONS =====
    // Tu pourras l'utiliser dans ton Player plus tard
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
    // ==================================================
}