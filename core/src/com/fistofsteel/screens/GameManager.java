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
import com.fistofsteel.entities.WorldItemManager;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.ui.PlayerHUD;
import com.fistofsteel.utils.Constants;
import com.fistofsteel.utils.HitboxDebugger;

public class GameManager implements Screen {
    private FistOfSteelGame game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private Player player;
    private InputHandler inputHandler;
    private AudioManager audioManager;
    
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer tiledMapRenderer;
    private Array<Rectangle> collisionRects;
    
    private String selectedCharacter;
    
    private Texture backgroundTexture;
    private float mapWidthInPixels;
    private float mapHeightInPixels;
    
    private boolean debugMode = false;
    
    private WorldItemManager worldItemManager;
    private EnemyManager enemyManager;
    
    // ⭐ NOUVEAU : HUD du joueur
    private PlayerHUD playerHUD;

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
        
        // 3. INPUT HANDLER
        inputHandler = new InputHandler(audioManager);
        Gdx.input.setInputProcessor(inputHandler);
        System.out.println("✅ InputHandler créé et connecté à AudioManager");
        
        // 4. TILED MAP
        loadTiledMap();
        loadBackground();
        
        // 5. PLAYER
        if ("Alexis".equals(selectedCharacter)) {
            player = new Alexis(inputHandler);
            System.out.println("✅ Personnage: Alexis (Hitbox: 75x110)");
        } else {
            player = new Hugo(inputHandler);
            System.out.println("✅ Personnage: Hugo (Hitbox: 60x100)");
        }
        
        if (collisionRects != null && collisionRects.size > 0) {
            player.setCollisionRects(collisionRects);
            System.out.println("✅ Collisions configurées pour le joueur (" + collisionRects.size + " rectangles)");
        } else {
            System.err.println("⚠️ ATTENTION : Aucune collision chargée !");
        }
        
        loadSpawnFromTiled();
        
        // 6. ENEMY MANAGER
        enemyManager = new EnemyManager(player);
        loadEnemiesFromTiled();
        
        if (collisionRects != null) {
            enemyManager.setCollisionRects(collisionRects);
            System.out.println("✅ Collisions configurées pour " + enemyManager.getTotalCount() + " ennemis");
        }
        
        // 7. ITEMS
        worldItemManager = new WorldItemManager();
        loadPotionsFromTiled();
        
        // 8. ⭐ HUD
        playerHUD = new PlayerHUD();
        System.out.println("✅ HUD du joueur initialisé");
        
        // 9. MUSIQUE
        audioManager.startLevelMusic();
        System.out.println("🎵 Musique level démarrée");
        
        // 10. DEBUG
        HitboxDebugger.setDebugEnabled(debugMode);
        
        // 11. FINALIZE
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
                float tiledX = object.getProperties().get("x", Float.class);
                float tiledY = object.getProperties().get("y", Float.class);
                
                float libgdxX = tiledX;
                float libgdxY = tiledY;
                
                player.setPosition(libgdxX, libgdxY);
                System.out.println("✅ Spawn: (" + (int)libgdxX + ", " + (int)libgdxY + ")");
                
                if (collisionRects != null) {
                    Rectangle playerHitbox = player.getHitbox();
                    Rectangle testHitbox = new Rectangle(
                        playerHitbox.x,
                        playerHitbox.y - 5f,
                        playerHitbox.width,
                        playerHitbox.height
                    );
                    
                    boolean playerGrounded = false;
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
    
    private void loadPotionsFromTiled() {
        if (tiledMap == null || worldItemManager == null) return;
        
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
            
            worldItemManager.spawnHealPotion(libgdxX, libgdxY);
            potionCount++;
        }
        
        System.out.println("✅ Potions: " + potionCount + " chargées via WorldItemManager");
    }
    
    private void loadEnemiesFromTiled() {
        System.out.println("\n🔎 Chargement des ennemis...");
        
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
            float tiledX = object.getProperties().get("x", Float.class);
            float tiledY = object.getProperties().get("y", Float.class);
            
            float libgdxX = tiledX;
            float libgdxY = tiledY;
            
            String enemyType = object.getProperties().get("type", "Knight", String.class);
            
            Float patrolMinObj = object.getProperties().get("patrolMin", Float.class);
            Float patrolMaxObj = object.getProperties().get("patrolMax", Float.class);
            
            if ("Knight".equalsIgnoreCase(enemyType)) {
                if (patrolMinObj != null && patrolMaxObj != null) {
                    enemyManager.addKnight(libgdxX, libgdxY, 
                                          libgdxX + patrolMinObj, 
                                          libgdxX + patrolMaxObj);
                } else {
                    enemyManager.addKnight(libgdxX, libgdxY);
                }
                enemyCount++;
            } else if ("Mage".equalsIgnoreCase(enemyType)) {
                if (patrolMinObj != null && patrolMaxObj != null) {
                    enemyManager.addMage(libgdxX, libgdxY, 
                                        libgdxX + patrolMinObj, 
                                        libgdxX + patrolMaxObj);
                } else {
                    enemyManager.addMage(libgdxX, libgdxY);
                }
                enemyCount++;
            } else if ("Rogue".equalsIgnoreCase(enemyType)) {
                if (patrolMinObj != null && patrolMaxObj != null) {
                    enemyManager.addRogue(libgdxX, libgdxY, 
                                         libgdxX + patrolMinObj, 
                                         libgdxX + patrolMaxObj);
                } else {
                    enemyManager.addRogue(libgdxX, libgdxY);
                }
                enemyCount++;
            } else {
                System.out.println("⚠️ Type d'ennemi non reconnu : " + enemyType);
            }
        }
        
        stabilizeAllEnemies();
        
        System.out.println("✅ Ennemis: " + enemyCount + " chargés\n");
    }
    
    private void stabilizeAllEnemies() {
        if (enemyManager == null) return;
        
        int maxAttempts = 100;
        for (int i = 0; i < maxAttempts; i++) {
            enemyManager.update(0.016f);
        }
        
        System.out.println("✅ Tous les ennemis stabilisés après " + maxAttempts + " frames");
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // DEBUG TOGGLE (F3)
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            HitboxDebugger.setDebugEnabled(debugMode);
            System.out.println("🔧 Debug mode: " + (debugMode ? "ON" : "OFF"));
        }

        // RETOUR MENU (ESC)
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            audioManager.stopLevelMusic();
            audioManager.startMenuMusic();
            System.out.println("🎵 Retour au menu");
            game.setScreen(new MenuScreen(game, audioManager));
            return;
        }

        // UPDATE
        player.update(delta);
        enemyManager.update(delta);
        
        if (worldItemManager != null) {
            worldItemManager.update(delta);       
            worldItemManager.checkPlayerCollisions(player);
        }
        
        // ⭐ Update HUD
        if (playerHUD != null) {
            playerHUD.update(delta);
        }
        
        // COMBATS
        enemyManager.checkEnemyAttacks(player);
        enemyManager.checkPlayerAttack(player);
        enemyManager.removeDeadEnemies();
        
        updateCamera();
        camera.update();

        // RENDER WORLD
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
        
        if (worldItemManager != null) {
            worldItemManager.render(batch);
        }
        
        batch.end();
        
        // ⭐ BARRES DE VIE DES ENNEMIS
        enemyManager.renderHealthBars(new com.badlogic.gdx.graphics.glutils.ShapeRenderer(), camera);
        
        // DEBUG HITBOXES
        if (debugMode) {
            HitboxDebugger.renderPlayerHitbox(player, camera);
            enemyManager.renderDebugHitboxes(camera);
        }
        
        // ⭐ RENDER HUD (par-dessus tout le reste)
        if (playerHUD != null) {
            playerHUD.render(batch, player, enemyManager.getEnemiesKilled(), enemyManager.getTotalEnemiesSpawned());
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
        
        // ⭐ Mettre à jour la caméra du HUD
        if (playerHUD != null) {
            playerHUD.resize(width, height);
        }
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

    @Override
    public void dispose() {
        System.out.println("\n🧹 Nettoyage GameManager...");
        
        if (batch != null) batch.dispose();
        if (worldItemManager != null) worldItemManager.dispose();
        if (enemyManager != null) enemyManager.dispose();
        if (player != null) player.dispose();
        if (tiledMap != null) tiledMap.dispose();
        if (tiledMapRenderer != null) tiledMapRenderer.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (playerHUD != null) playerHUD.dispose();
        
        HitboxDebugger.dispose();
        
        System.out.println("✅ GameManager dispose\n");
    }
}