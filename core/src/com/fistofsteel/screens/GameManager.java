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
import com.fistofsteel.entities.player.Alexis;
import com.fistofsteel.entities.player.Hugo;
import com.fistofsteel.entities.player.Player;
import com.fistofsteel.entities.managers.EnemyManager;
import com.fistofsteel.entities.managers.LevelExitManager;
import com.fistofsteel.entities.managers.WorldItemManager;
import com.fistofsteel.entities.managers.ProjectileManager;
import com.fistofsteel.input.InputHandler;
import com.fistofsteel.ui.PlayerHUD;
import com.fistofsteel.utils.EntityConstants;
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
    private Array<Rectangle> deathRects;
    
    private String selectedCharacter;
    private String currentLevel;
    
    private Texture backgroundTexture;
    private float mapWidthInPixels;
    private float mapHeightInPixels;
    
    private boolean debugMode = false;
    
    private WorldItemManager worldItemManager;
    private EnemyManager enemyManager;
    private LevelExitManager levelExitManager;
    private ProjectileManager projectileManager;
    
    private PlayerHUD playerHUD;

    public GameManager(FistOfSteelGame game, String selectedCharacter, AudioManager audioManager) {
        this(game, selectedCharacter, audioManager, "level1_example");
    }
    
    public GameManager(FistOfSteelGame game, String selectedCharacter, AudioManager audioManager, String levelName) {
        this.game = game;
        this.selectedCharacter = selectedCharacter;
        this.audioManager = audioManager;
        this.currentLevel = levelName;
    }

    @Override
    public void show() {
        System.out.println("\n========================================");
        System.out.println("🎮 INITIALISATION DE GAMEMANAGER");
        System.out.println("📍 Niveau: " + currentLevel);
        System.out.println("========================================\n");
        
        camera = new OrthographicCamera();
        float worldHeight = 20 * 64;
        float screenAspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        float viewportWidth = worldHeight * screenAspectRatio;
        camera.setToOrtho(false, viewportWidth, worldHeight);
        System.out.println("✅ Caméra initialisée");
        
        batch = new SpriteBatch();
        System.out.println("✅ SpriteBatch créé");
        
        inputHandler = new InputHandler(audioManager);
        Gdx.input.setInputProcessor(inputHandler);
        System.out.println("✅ InputHandler créé et connecté à AudioManager");
        
        loadTiledMap();
        loadBackgroundFromTiled();
        
        if ("Alexis".equals(selectedCharacter)) {
            player = new Alexis(inputHandler, audioManager);
            System.out.println("✅ Personnage: Alexis (Hitbox: 75x110)");
        } else {
            player = new Hugo(inputHandler, audioManager);
            System.out.println("✅ Personnage: Hugo (Hitbox: 60x100)");
        }
        
        if (collisionRects != null && collisionRects.size > 0) {
            player.setCollisionRects(collisionRects);
            System.out.println("✅ Collisions configurées pour le joueur (" + collisionRects.size + " rectangles)");
        } else {
            System.err.println("⚠️ ATTENTION : Aucune collision chargée !");
        }
        
        loadSpawnFromTiled();
        
        enemyManager = new EnemyManager(player);
        loadEnemiesFromTiled();
        
        if (collisionRects != null) {
            enemyManager.setCollisionRects(collisionRects);
            System.out.println("✅ Collisions configurées pour " + enemyManager.getTotalCount() + " ennemis");
        }
        
        projectileManager = new ProjectileManager(mapWidthInPixels);
        
        if (player instanceof Hugo) {
            ((Hugo) player).setProjectileManager(projectileManager);
            System.out.println("✅ ProjectileManager configuré pour Hugo");
        }
        
        worldItemManager = new WorldItemManager();
        loadPotionsFromTiled();
        loadArmorFromTiled();
        loadWeaponsFromTiled();
        
        levelExitManager = new LevelExitManager();
        loadExitsFromTiled();
        
        playerHUD = new PlayerHUD();
        System.out.println("✅ HUD du joueur initialisé");
        
        audioManager.startLevelMusic();
        System.out.println("🎵 Musique level démarrée");
        
        HitboxDebugger.setDebugEnabled(debugMode);
        
        updateCamera();
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        System.out.println("\n✅ GAMEMANAGER PRÊT !\n");
    }
    
    private void loadBackgroundFromTiled() {
        try {
            backgroundTexture = new Texture(Gdx.files.internal("assets/maps/Gemini_Generated_Image_3ijzal3ijzal3ijz_2 (1).png"));
            backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
            System.out.println("✅ Background chargé avec répétition X");
        } catch (Exception e) {
            System.err.println("⚠️ Erreur chargement background : " + e.getMessage());
        }
    }
    
    private void loadTiledMap() {
        try {
            String mapPath = "maps/" + currentLevel + ".tmx";
            tiledMap = new TmxMapLoader().load(mapPath);
            tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
            
            int mapWidthInTiles = tiledMap.getProperties().get("width", Integer.class);
            int mapHeightInTiles = tiledMap.getProperties().get("height", Integer.class);
            int tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
            int tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);
            
            mapWidthInPixels = mapWidthInTiles * tileWidth;
            mapHeightInPixels = mapHeightInTiles * tileHeight;
            
            System.out.println("✅ Map Tiled: " + mapWidthInPixels + "x" + mapHeightInPixels + " pixels");
            
            loadCollisions();
            loadDeathZones();
        } catch (Exception e) {
            System.err.println("⚠️ Erreur chargement map: " + e.getMessage());
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
            System.out.println("✅ Collisions: " + collisionRects.size + " rectangles");
        }
    }
    
    private void loadDeathZones() {
        deathRects = new Array<>();
        if (tiledMap == null) return;
        
        MapLayer deathLayer = tiledMap.getLayers().get("Death");
        if (deathLayer != null) {
            for (MapObject object : deathLayer.getObjects()) {
                if (object instanceof RectangleMapObject) {
                    RectangleMapObject rectObject = (RectangleMapObject) object;
                    Rectangle rect = rectObject.getRectangle();
                    deathRects.add(new Rectangle(rect));
                }
            }
            System.out.println("✅ Zones de mort: " + deathRects.size + " rectangles");
        } else {
            System.out.println("ℹ️ Aucune layer 'Death' trouvée dans la map");
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

    private void loadArmorFromTiled() {
        if (tiledMap == null || worldItemManager == null) return;

        MapLayer armorLayer = tiledMap.getLayers().get("Armor");
        if (armorLayer == null) {
            System.out.println("⚠️ Layer 'Armor' non trouvé");
            return;
        }

        int armorCount = 0;

        for (MapObject object : armorLayer.getObjects()) {
            float x = object.getProperties().get("x", Float.class);
            float y = object.getProperties().get("y", Float.class);

            String type = object.getProperties().get("type", String.class);
            if (type == null) {
                type = "light";
            }

            if ("heavy".equalsIgnoreCase(type)) {
                worldItemManager.spawnArmorHeavy(x, y);
            } else {
                worldItemManager.spawnArmorLight(x, y);
            }

            armorCount++;
        }

        System.out.println("✅ Armures: " + armorCount + " chargées via WorldItemManager");
    }

    private void loadWeaponsFromTiled() {
        if (tiledMap == null || worldItemManager == null) return;

        MapLayer weaponLayer = tiledMap.getLayers().get("Weapons");
        if (weaponLayer == null) {
            System.out.println("⚠️ Layer 'Weapons' non trouvé dans Tiled");
            return;
        }

        int weaponCount = 0;

        for (MapObject object : weaponLayer.getObjects()) {
            float tiledX = object.getProperties().get("x", Float.class);
            float tiledY = object.getProperties().get("y", Float.class);

            float x = tiledX;
            float y = tiledY;

            String weaponType = object.getProperties().get("type", String.class);
            if (weaponType == null) {
                weaponType = "sword1";
            }

            System.out.println("🔎 Weapon Tiled: type=" + weaponType + " à (" + x + ", " + y + ")");

            switch (weaponType) {
                case "sword1" -> worldItemManager.spawnSword1(x, y);
                case "sword2" -> worldItemManager.spawnSword2(x, y);
                case "sword3" -> worldItemManager.spawnSword3(x, y);
                default -> System.out.println("⚠️ weaponType inconnu: " + weaponType);
            }

            weaponCount++;
        }

        System.out.println("✅ Weapons: " + weaponCount + " chargées via WorldItemManager");
    }
    
    private void loadExitsFromTiled() {
        if (tiledMap == null || levelExitManager == null) return;
        
        MapLayer exitsLayer = tiledMap.getLayers().get("Exits");
        if (exitsLayer == null) {
            System.out.println("⚠️ Layer 'Exits' non trouvé - Aucune porte de sortie");
            return;
        }
        
        int exitCount = 0;
        
        for (MapObject object : exitsLayer.getObjects()) {
            float tiledX = object.getProperties().get("x", Float.class);
            float tiledY = object.getProperties().get("y", Float.class);
            
            float libgdxX = tiledX;
            float libgdxY = tiledY;
            
            String targetLevel = object.getProperties().get("targetLevel", "level2_example", String.class);
            
            levelExitManager.addExit(libgdxX, libgdxY, targetLevel);
            exitCount++;
        }
        
        System.out.println("✅ Portes de sortie: " + exitCount + " chargées");
    }
    
    private void loadEnemiesFromTiled() {
        System.out.println("\n🔍 Chargement des ennemis...");
        
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

            System.out.println("🔎 Ennemi trouvé dans Tiled :");
            System.out.println("   - Nom objet : " + object.getName());
            System.out.println("   - Propriétés : " + object.getProperties());
            System.out.println("   - enemyType lu = '" + enemyType + "'");
            
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
            } else if ("Boss".equalsIgnoreCase(enemyType)) {
                enemyManager.addBoss(libgdxX, libgdxY);
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

        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugMode = !debugMode;
            HitboxDebugger.setDebugEnabled(debugMode);
            System.out.println("🔧 Debug mode: " + (debugMode ? "ON" : "OFF"));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            audioManager.stopLevelMusic();
            audioManager.startMenuMusic();
            System.out.println("🎵 Retour au menu");
            game.setScreen(new MenuScreen(game, audioManager));
            return;
        }

        player.update(delta);
        enemyManager.update(delta);
        
        if (projectileManager != null) {
            projectileManager.update(delta);
            projectileManager.checkPlayerCollisions(player);
            projectileManager.checkEnemyCollisions(enemyManager);
            projectileManager.removeInactiveProjectiles();
        }
        
        if (worldItemManager != null) {
            worldItemManager.update(delta);
            worldItemManager.checkPlayerCollisions(player);
        }
        
        if (playerHUD != null) {
            playerHUD.update(delta);
        }
        
        if (checkGameOver()) {
            game.setScreen(new GameOverScreen(game, audioManager));
            return;
        }
        
        if (levelExitManager != null) {
            levelExitManager.update(enemyManager.getEnemiesKilled(), enemyManager.getTotalEnemiesSpawned());
            
            String nextLevel = levelExitManager.checkPlayerOnExit(player);
            if (nextLevel != null) {
                System.out.println("🚪 Changement de niveau -> " + nextLevel);
                loadNextLevel(nextLevel);
                return;
            }
        }
        
        if (checkVictory()) {
            game.setScreen(new WinnerScreen(game, audioManager));
            return;
        }
        
        enemyManager.checkEnemyAttacks(player);
        enemyManager.checkPlayerAttack(player);
        enemyManager.removeDeadEnemies();
        
        updateCamera();
        camera.update();

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
        
        if (projectileManager != null) {
            projectileManager.render(batch);
        }
        
        if (worldItemManager != null) {
            worldItemManager.render(batch);
        }
        
        if (levelExitManager != null) {
            levelExitManager.render(batch, camera);
        }
        
        batch.end();
        
        enemyManager.renderHealthBars(new com.badlogic.gdx.graphics.glutils.ShapeRenderer(), camera);
        
        if (debugMode) {
            HitboxDebugger.renderPlayerHitbox(player, camera);
            enemyManager.renderDebugHitboxes(camera);
        }
        
        if (playerHUD != null) {
            playerHUD.render(batch, player, enemyManager.getEnemiesKilled(), enemyManager.getTotalEnemiesSpawned());
        }
    }
    
    private boolean checkGameOver() {
        if (player.getHealth() <= 0) {
            System.out.println("💀 GAME OVER - Le joueur est mort (0 PV)");
            return true;
        }
        
        if (deathRects != null && deathRects.size > 0) {
            Rectangle playerHitbox = player.getHitbox();
            for (Rectangle deathRect : deathRects) {
                if (playerHitbox.overlaps(deathRect)) {
                    System.out.println("💀 GAME OVER - Le joueur est tombé dans une zone de mort");
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean checkVictory() {
        if (System.currentTimeMillis() % 1000 < 16) {
            System.out.println("🔍 Debug victoire - Niveau: " + currentLevel + 
                              " | Ennemis vivants: " + enemyManager.getAliveCount() + 
                              " | Total ennemis: " + enemyManager.getTotalEnemiesSpawned());
        }
        
        boolean isLevel4 = currentLevel.contains("level4") || currentLevel.contains("4");
        boolean allEnemiesDead = enemyManager.getAliveCount() == 0;
        boolean hasEnemies = enemyManager.getTotalEnemiesSpawned() > 0;
        
        if (isLevel4 && allEnemiesDead && hasEnemies) {
            System.out.println("🎉 VICTOIRE - Tous les ennemis du niveau 4 sont vaincus !");
            System.out.println("   📊 Stats finales:");
            System.out.println("      - Ennemis tués: " + enemyManager.getEnemiesKilled());
            System.out.println("      - Ennemis total: " + enemyManager.getTotalEnemiesSpawned());
            return true;
        }
        
        return false;
    }
    
    private void loadNextLevel(String nextLevelName) {
        System.out.println("\n========================================");
        System.out.println("🔄 CHANGEMENT DE NIVEAU");
        System.out.println("Ancien: " + currentLevel + " -> Nouveau: " + nextLevelName);
        System.out.println("========================================\n");
        
        game.setScreen(new GameManager(game, selectedCharacter, audioManager, nextLevelName));
    }
    
    private void renderBackground() {
        if (backgroundTexture == null) return;
        
        float bgWidth = backgroundTexture.getWidth();
        float bgHeight = backgroundTexture.getHeight();
        
        for (float x = 0; x < mapWidthInPixels; x += bgWidth) {
            batch.draw(backgroundTexture, x, 0f, bgWidth, bgHeight);
        }
    }
    
    private void updateCamera() {
        float mapWidth = mapWidthInPixels;
        float mapHeight = mapHeightInPixels;
        
        float playerX = player.getX() + EntityConstants.PLAYER_WIDTH / 2;
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
        if (projectileManager != null) projectileManager.dispose();
        if (player != null) player.dispose();
        if (tiledMap != null) tiledMap.dispose();
        if (tiledMapRenderer != null) tiledMapRenderer.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (playerHUD != null) playerHUD.dispose();
        if (levelExitManager != null) levelExitManager.dispose();
        
        HitboxDebugger.dispose();
        
        System.out.println("✅ GameManager dispose\n");
    }
}