package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.fistofsteel.audio.AudioManager;
import com.fistofsteel.entities.Alexis;
import com.fistofsteel.entities.EnemyManager;
import com.fistofsteel.entities.Hugo;
import com.fistofsteel.entities.Player;
import com.fistofsteel.items.PotionManager;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.utils.Constants;
import com.fistofsteel.utils.HitboxDebugger;

/**
 * GameManager - DÉMARRE LA MUSIQUE LEVEL, arrête la musique menu
 */
public class GameManager implements Screen {
    private FistOfSteelGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player player;
    private InputHandler inputHandler;
    
    // ⭐ AudioManager partagé (reçu depuis CharactersChoice)
    private AudioManager audioManager;
    
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Array<Rectangle> collisionRects;
    
    private String selectedCharacter;
    
    private Texture backgroundTexture;
    private float mapWidthInPixels;
    private float mapHeightInPixels;
    
    private boolean debugMode = false;
    
    private PotionManager potionManager;
    private EnemyManager enemyManager;

    // ⭐ Constructeur avec AudioManager
    public GameManager(FistOfSteelGame game, String selectedCharacter, AudioManager audioManager) {
        this.game = game;
        this.selectedCharacter = selectedCharacter;
        this.audioManager = audioManager;
    }

    @Override
    public void show() {
        System.out.println("\n========================================");
        System.out.println("🎮 INITIALISATION DE GAMEMANAGER");
        System.out.println("========================================\n");
        
        // 1. CAMÉRA
        camera = new OrthographicCamera();
        float worldHeight = 20 * 64;
        float screenAspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        float viewportWidth = worldHeight * screenAspectRatio;
        camera.setToOrtho(false, viewportWidth, worldHeight);
        System.out.println("✅ Caméra initialisée");
        
        // 2. BATCH
        batch = new SpriteBatch();
        System.out.println("✅ SpriteBatch créé");
        
        // 3. INPUT HANDLER (avec AudioManager)
        inputHandler = new InputHandler(audioManager);
        Gdx.input.setInputProcessor(inputHandler);
        System.out.println("✅ InputHandler créé et connecté à AudioManager");
        
        // 4. TILED MAP (charge map + collisions)
        loadTiledMap();
        loadBackground();
        
        // 5. PLAYER (créer APRÈS les collisions)
        if ("Alexis".equals(selectedCharacter)) {
            player = new Alexis(inputHandler);
            System.out.println("✅ Personnage: Alexis (Hitbox: 75x110)");
        } else {
            player = new Hugo(inputHandler);
            System.out.println("✅ Personnage: Hugo (Hitbox: 60x100)");
        }
        
        // ⭐ IMPORTANT : Donner les collisions AVANT de positionner
        if (collisionRects != null && collisionRects.size > 0) {
            player.setCollisionRects(collisionRects);
            System.out.println("✅ Collisions configurées pour le joueur (" + collisionRects.size + " rectangles)");
        } else {
            System.err.println("⚠️ ATTENTION : Aucune collision chargée !");
        }
        
        // ⭐ PUIS charger le spawn
        loadSpawnFromTiled();
        
        // 6. ENEMY MANAGER
        enemyManager = new EnemyManager(player);
        loadEnemiesFromTiled();
        
        // ⭐ IMPORTANT : Donner les collisions à TOUS les ennemis
        if (collisionRects != null) {
            enemyManager.setCollisionRects(collisionRects);
            System.out.println("✅ Collisions configurées pour " + enemyManager.getTotalCount() + " ennemis");
        }
        
        // 7. POTIONS
        potionManager = new PotionManager();
        loadPotionsFromTiled();
        
        // 8. ⭐ MUSIQUE : Arrêter menu, démarrer level
        audioManager.startLevelMusic();
        System.out.println("🎵 GameManager : Musique level démarrée");
        
        // 9. DEBUG
        HitboxDebugger.setDebugEnabled(debugMode);
        
        // 10. FINALIZE
        updateCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        System.out.println("\n✅ GAMEMANAGER PRÊT !\n");
    }
    
    private void loadBackground() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("assets/maps/background_double.png"));
            backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            System.out.println("✅ Background chargé: " + mapWidthInPixels + "x" + mapHeightInPixels + " pixels");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur chargement background: " + e.getMessage());
        }
    }
    
    private void loadTiledMap() {
        try {
            tiledMap = new TmxMapLoader().load("maps/level1_example.tmx");
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            
            int mapWidthInTiles = tiledMap.getProperties().get("width", Integer.class);
            int mapHeightInTiles = tiledMap.getProperties().get("height", Integer.class);
            int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
            int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
            
            mapWidthInPixels = mapWidthInTiles * tileWidth;
            mapHeightInPixels = mapHeightInTiles * tileHeight;
            
            System.out.println("✅ Map Tiled: " + mapWidthInPixels + "x" + mapHeightInPixels + " pixels");
            
            loadCollisions();
        } catch (Exception e) {
            System.err.println("⚠️ Erreur chargement map: " + e.getMessage());
        }
    }
    
    /**
     * ⭐ VERSION RECTANGLES : Charge le spawn du joueur depuis un RECTANGLE Tiled
     */
    private void loadSpawnFromTiled() {
        if (tiledMap == null) return;
        
        MapLayer spawnLayer = tiledMap.getLayers().get("spawn");
        if (spawnLayer == null) {
            System.err.println("⚠️ Layer 'spawn' introuvable !");
            return;
        }
        
        for (MapObject object : spawnLayer.getObjects()) {
            String objectName = object.getName();
            String className = object.getProperties().get("class", String.class);
            
            boolean isSpawn = "spawn".equalsIgnoreCase(className) || 
                            "playerSpawn".equalsIgnoreCase(objectName);
            
            if (isSpawn) {
                // ⭐ RECTANGLE : Tiled donne directement le coin inférieur gauche
                float tiledX = object.getProperties().get("x", Float.class);
                float tiledY = object.getProperties().get("y", Float.class);
                
                // ✅ AUCUNE CONVERSION NÉCESSAIRE pour les rectangles !
                float libgdxX = tiledX;
                float libgdxY = tiledY;
                
                System.out.println("📍 DEBUG Spawn (Rectangle):");
                System.out.println("   Tiled X,Y: (" + (int)tiledX + ", " + (int)tiledY + ")");
                System.out.println("   LibGDX X,Y: (" + (int)libgdxX + ", " + (int)libgdxY + ")");
                System.out.println("   ✅ Pas de conversion Y (rectangle = coin inférieur)");
                
                player.setPosition(libgdxX, libgdxY);
                System.out.println("✅ Spawn: (" + (int)libgdxX + ", " + (int)libgdxY + ")");
                
                // ⭐ VÉRIFICATION : Le joueur est-il bien au sol ?
                boolean playerGrounded = false;
                if (collisionRects != null) {
                    Rectangle playerHitbox = player.getHitbox();
                    Rectangle testHitbox = new Rectangle(
                        playerHitbox.x,
                        playerHitbox.y - 5f,
                        playerHitbox.width,
                        playerHitbox.height
                    );
                    
                    for (Rectangle collRect : collisionRects) {
                        if (testHitbox.overlaps(collRect)) {
                            playerGrounded = true;
                            break;
                        }
                    }
                    
                    if (playerGrounded) {
                        System.out.println("   ✅ Joueur bien positionné au sol");
                    } else {
                        System.err.println("   ⚠️ ATTENTION : Joueur pas au sol ! Vérifiez le spawn dans Tiled");
                    }
                }
                
                return;
            }
        }
        
        System.err.println("⚠️ Aucun spawn trouvé dans le layer 'spawn' !");
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
            System.out.println("✅ Collisions: " + collisionRects.size + " rectangles");
        }
    }
    
    private void loadPotionsFromTiled() {
        if (tiledMap == null) return;
        
        MapLayer potionLayer = tiledMap.getLayers().get("Potions");
        if (potionLayer == null) {
            System.out.println("⚠️ Layer 'Potions' non trouvé");
            return;
        }
        
        int potionCount = 0;
        
        for (MapObject object : potionLayer.getObjects()) {
            float tiledX = object.getProperties().get("x", Float.class);
            float tiledY = object.getProperties().get("y", Float.class);
            
            float libgdxX = tiledX;
            float libgdxY = tiledY;
            
            potionManager.addPotion(libgdxX, libgdxY);
            potionCount++;
        }
        
        System.out.println("✅ Potions: " + potionCount + " chargées");
    }
    
    /**
     * ⭐ VERSION RECTANGLES : Charge les ennemis depuis des RECTANGLES Tiled
     */
    private void loadEnemiesFromTiled() {
        System.out.println("\n🔎 DEBUG - Chargement des ennemis...");
        
        if (tiledMap == null) {
            System.out.println("❌ tiledMap est NULL !");
            return;
        }
        
        MapLayer enemyLayer = tiledMap.getLayers().get("Enemies");
        if (enemyLayer == null) {
            System.out.println("⚠️ Layer 'Enemies' non trouvé");
            return;
        }
        
        System.out.println("✅ Layer 'Enemies' trouvé !");
        System.out.println("📦 Nombre d'objets dans la couche : " + enemyLayer.getObjects().getCount());
        
        int enemyCount = 0;
        
        for (MapObject object : enemyLayer.getObjects()) {
            System.out.println("\n🎯 Objet trouvé : " + object.getName());
            
            float tiledX = object.getProperties().get("x", Float.class);
            float tiledY = object.getProperties().get("y", Float.class);
            
            System.out.println("   Coord Tiled (rectangle): (" + (int)tiledX + ", " + (int)tiledY + ")");
            
            // ⭐ RECTANGLE : Pas de conversion nécessaire !
            float libgdxX = tiledX;
            float libgdxY = tiledY;
            
            System.out.println("   Coord LibGDX : (" + (int)libgdxX + ", " + (int)libgdxY + ")");
            System.out.println("   ✅ Pas de conversion Y (rectangle = coin inférieur)");
            
            String enemyType = object.getProperties().get("type", "Knight", String.class);
            System.out.println("   Type : " + enemyType);
            
            Float patrolMinObj = object.getProperties().get("patrolMin", Float.class);
            Float patrolMaxObj = object.getProperties().get("patrolMax", Float.class);
            
            if ("Knight".equalsIgnoreCase(enemyType)) {

                // Knight avec patrouille
                if (patrolMinObj != null && patrolMaxObj != null) {
                    enemyManager.addKnight(libgdxX, libgdxY,
                                        libgdxX + patrolMinObj,
                                        libgdxX + patrolMaxObj);
                } else {
                    enemyManager.addKnight(libgdxX, libgdxY);
                }

                enemyCount++;

            } else if ("Mage".equalsIgnoreCase(enemyType)) {

                // Mage avec patrouille aussi
                if (patrolMinObj != null && patrolMaxObj != null) {
                    enemyManager.addMage(libgdxX, libgdxY,
                                        libgdxX + patrolMinObj,
                                        libgdxX + patrolMaxObj);
                } else {
                    enemyManager.addMage(libgdxX, libgdxY);
                }

                enemyCount++;

            } else {

                System.out.println("   ⚠️ Type d'ennemi non reconnu : " + enemyType);
            }

        }
        
        System.out.println("\n🔧 Stabilisation des ennemis au sol...");
        stabilizeAllEnemies();
        
        System.out.println("\n✅ Ennemis: " + enemyCount + " chargés\n");
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // DEBUG TOGGLE
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            HitboxDebugger.setDebugEnabled(debugMode);
            System.out.println("🔧 Debug mode: " + (debugMode ? "ON" : "OFF"));
        }

        // ⭐ RETOUR MENU - Arrête musique level, redémarre musique menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            audioManager.stopLevelMusic();
            audioManager.startMenuMusic();
            System.out.println("🎵 ESC : Retour au menu (musique changée)");
            // Créer un nouveau MenuScreen avec audioManager
            game.setScreen(new MenuScreen(game, audioManager));
            return;
        }

        // UPDATE
        player.update(delta);
        enemyManager.update(delta);
        potionManager.update(delta);
        
        // Collisions potions
        potionManager.checkCollisions(player.getHitbox());
        potionManager.removeCollectedPotions();
        
        // Collisions ennemis
        enemyManager.checkEnemyAttacks(player);
        enemyManager.checkPlayerAttack(player);
        enemyManager.removeDeadEnemies();
        
        updateCamera();
        camera.update();

        // RENDER
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        renderBackground();
        batch.end();
        
        if (tiledMapRenderer != null) {
            tiledMapRenderer.setView(camera);
            tiledMapRenderer.render();
        }
        
        batch.begin();
        player.render(batch);
        enemyManager.render(batch);
        potionManager.render(batch);
        batch.end();
        
        // DEBUG : Hitbox du joueur et des ennemis (activé avec F3)
        if (debugMode) {
            HitboxDebugger.renderPlayerHitbox(player, camera);
            enemyManager.renderDebugHitboxes(camera);
        }
    }
    
    private void renderBackground() {
        if (backgroundTexture == null) return;
        batch.draw(backgroundTexture, 0, 0, mapWidthInPixels, mapHeightInPixels);
    }
    
    private void updateCamera() {
        float mapWidth = mapWidthInPixels;
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
        audioManager.pauseLevelMusic();
    }
    
    @Override 
    public void resume() {
        audioManager.resumeLevelMusic();
    }
    
    @Override 
    public void hide() {
        if (Gdx.input.getInputProcessor() == inputHandler) {
            Gdx.input.setInputProcessor(null);
        }
    }

    private void stabilizeAllEnemies() {
        if (enemyManager == null) return;
        
        int maxAttempts = 100;
        int stabilizationAttempts = 0;
        
        while (stabilizationAttempts < maxAttempts) {
            enemyManager.update(0.016f);
            stabilizationAttempts++;
        }
        
        System.out.println("✅ Tous les ennemis stabilisés après " + stabilizationAttempts + " frames");
    }

    @Override
    public void dispose() {
        System.out.println("\n🧹 Nettoyage GameManager...");
        
        if (batch != null) batch.dispose();
        if (potionManager != null) potionManager.dispose();
        if (enemyManager != null) enemyManager.dispose();
        if (player != null) player.dispose();
        if (tiledMap != null) tiledMap.dispose();
        if (tiledMapRenderer != null) tiledMapRenderer.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        
        // ⭐ NE PAS disposer audioManager ici !
        // Il est partagé entre tous les écrans et sera disposé par FistOfSteelGame
        // audioManager.dispose(); ← NE PAS FAIRE !
        
        HitboxDebugger.dispose();
        
        System.out.println("✅ GameManager dispose\n");
    }
}