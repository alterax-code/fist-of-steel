package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.fistofsteel.entities.Alexis;
import com.fistofsteel.entities.Hugo;
import com.fistofsteel.entities.Player;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.Constants;
import com.fistofsteel.utils.HitboxDebugger;

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
    
    // Stocker le personnage choisi
    private String selectedCharacter;
    
    // ===== GESTION DU BACKGROUND =====
    private Texture backgroundTexture;
    private float mapWidthInPixels;
    private float mapHeightInPixels;
    
    // ===== DEBUG VISUEL DES HITBOX =====
    private boolean debugMode = false;

    public GameManager(FistOfSteelGame game) {
        this(game, "Hugo");
    }
    
    public GameManager(FistOfSteelGame game, String selectedCharacter) {
        this.game = game;
        this.selectedCharacter = selectedCharacter;
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
        loadBackground();  // ← NOUVEAU : Charger le background
        
        // Instancier le bon personnage selon le choix
        if ("Alexis".equals(selectedCharacter)) {
            player = new Alexis(inputHandler, soundManager);
            System.out.println("✅ Personnage sélectionné : Alexis (Hitbox: 75x110)");
        } else {
            player = new Hugo(inputHandler, soundManager);
            System.out.println("✅ Personnage sélectionné : Hugo (Hitbox: 60x100)");
        }
        
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
        
        HitboxDebugger.setDebugEnabled(debugMode);
        if (debugMode) {
            System.out.println("🔧 Mode debug des hitbox activé (F3 pour toggle)");
        }
        
        updateCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }
    
    /**
     * Charge le background en fonction des dimensions de la map Tiled
     */
    private void loadBackground() {
        try {
            // Charger la texture du background depuis assets/maps
            backgroundTexture = new Texture(Gdx.files.internal("assets/maps/background_double.png"));
            
            // Option pour un meilleur rendu (si l'image est pixelisée)
            backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            
            System.out.println("✅ Background chargé : " + mapWidthInPixels + "x" + mapHeightInPixels + " pixels");
            System.out.println("📁 Fichier : assets/maps/background_double.png");
            
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du chargement du background : " + e.getMessage());
            System.err.println("💡 Assurez-vous que le fichier existe : assets/maps/background_double.png");
        }
    }
    
    private void loadTiledMap() {
        try {
            tiledMap = new TmxMapLoader().load("maps/level1_example.tmx");
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            
            // Récupérer les dimensions de la map pour le background
            int mapWidthInTiles = tiledMap.getProperties().get("width", Integer.class);
            int mapHeightInTiles = tiledMap.getProperties().get("height", Integer.class);
            int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
            int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
            
            mapWidthInPixels = mapWidthInTiles * tileWidth;
            mapHeightInPixels = mapHeightInTiles * tileHeight;
            
            System.out.println("📐 Dimensions de la map : " + mapWidthInPixels + "x" + mapHeightInPixels + " pixels");
            
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

        // Toggle debug mode avec F3
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            HitboxDebugger.setDebugEnabled(debugMode);
            System.out.println("🔧 Debug mode: " + (debugMode ? "ON" : "OFF"));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game));
            return;
        }

        player.update(delta);
        updateCamera();
        camera.update();

        // ===== ORDRE DE RENDU =====
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        // 1. D'abord le background (tout au fond)
        renderBackground();
        
        batch.end();
        
        // 2. Ensuite la map Tiled (collisions + décors)
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }
        
        // 3. Puis le joueur (au-dessus)
        batch.begin();
        player.render(batch);
        batch.end();
        
        // 4. Enfin le debug (tout au-dessus)
        HitboxDebugger.renderPlayerHitbox(player, camera);
    }
    
    /**
     * Affiche le background adapté aux dimensions de la map
     */
    private void renderBackground() {
        if (backgroundTexture == null) return;
        
        // Option 1 : Background statique qui couvre toute la map
        batch.draw(
            backgroundTexture,
            0, 0,                           // Position (coin bas-gauche de la map)
            mapWidthInPixels,               // Largeur (étirée pour couvrir la map)
            mapHeightInPixels               // Hauteur (étirée pour couvrir la map)
        );
        
        // Option 2 (commentée) : Background avec effet parallax (plus lent que la caméra)
        // Décommenter pour activer l'effet de profondeur
        /*
        float parallaxFactor = 0.5f;  // 0.5 = le background bouge 2x plus lentement
        float bgX = -camera.position.x * parallaxFactor;
        float bgY = 0;
        
        batch.draw(
            backgroundTexture,
            bgX, bgY,
            mapWidthInPixels,
            mapHeightInPixels
        );
        */
    }
    
    private void updateCamera() {
        float mapWidth = mapWidthInPixels;   // Utiliser les dimensions réelles
        float mapHeight = mapHeightInPixels;
        
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
        
        // ===== DISPOSE DU BACKGROUND =====
        if (backgroundTexture != null) {
            backgroundTexture.dispose();
        }
        
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
        }
        
        if (soundManager != null) {
            soundManager.dispose();
        }
        
        HitboxDebugger.dispose();
    }
}