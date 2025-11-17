package com.fistofsteel.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;

/**
 * Gestionnaire audio centralisé - VERSION 2 MUSIQUES
 * Gère séparément la musique du menu et la musique du level
 */
public class AudioManager {
    
    // Volumes
    private float soundVolume = 1.0f;
    private float musicVolume = 0.5f;
    
    // Collections
    private HashMap<String, Sound> sounds;
    
    // ⭐ DEUX MUSIQUES SÉPARÉES
    private Music menuMusic;      // Pour MenuScreen, OptionsScreen, CharactersChoice
    private Music levelMusic;     // Pour GameManager (en jeu)
    
    // Debug
    private boolean audioSystemReady = false;
    
    public AudioManager() {
        System.out.println("\n========================================");
        System.out.println("🔊 INITIALISATION AUDIOMANAGER");
        System.out.println("========================================");
        
        sounds = new HashMap<>();
        loadAllAudio();
        
        System.out.println("========================================\n");
    }
    
    /**
     * Charge TOUS les fichiers audio au démarrage
     */
    private void loadAllAudio() {
        long startTime = System.currentTimeMillis();
        
        // ===== SONS =====
        System.out.println("📂 Chargement des sons...");
        int soundsLoaded = 0;
        
        soundsLoaded += loadSound("jump", "assets/sounds/jump.ogg") ? 1 : 0;
        soundsLoaded += loadSound("attack", "assets/sounds/attack.ogg") ? 1 : 0;
        soundsLoaded += loadSound("hit", "assets/sounds/hit.ogg") ? 1 : 0;
        soundsLoaded += loadSound("death", "assets/sounds/death.ogg") ? 1 : 0;
        
        System.out.println("✅ " + soundsLoaded + "/4 sons chargés");
        
        // ===== MUSIQUE MENU =====
        System.out.println("\n🎵 Chargement de la musique menu...");
        try {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/Untitled.ogg"));
            menuMusic.setLooping(true);  // ⭐ BOUCLE AUTOMATIQUE
            menuMusic.setVolume(musicVolume);
            System.out.println("✅ Musique menu chargée : Untitled.ogg (looping activé)");
        } catch (Exception e) {
            System.err.println("❌ ERREUR musique menu : " + e.getMessage());
            System.err.println("   Fichier attendu : assets/music/Untitled.ogg");
            menuMusic = null;
        }
        
        // ===== MUSIQUE LEVEL =====
        System.out.println("\n🎵 Chargement de la musique level...");
        try {
            levelMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/Untitled-_1_.ogg"));
            levelMusic.setLooping(true);  // ⭐ BOUCLE AUTOMATIQUE
            levelMusic.setVolume(musicVolume);
            System.out.println("✅ Musique level chargée : Untitled-_1_.ogg (looping activé)");
        } catch (Exception e) {
            System.err.println("❌ ERREUR musique level : " + e.getMessage());
            System.err.println("   Fichier attendu : assets/music/Untitled-_1_.ogg");
            levelMusic = null;
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("\n⏱️ Temps de chargement : " + (endTime - startTime) + "ms");
        
        audioSystemReady = (soundsLoaded > 0);
        
        if (!audioSystemReady) {
            System.err.println("⚠️ ATTENTION : Système audio non fonctionnel !");
        }
    }
    
    /**
     * Charge un son individuel avec vérification
     */
    private boolean loadSound(String name, String path) {
        try {
            long start = System.nanoTime();
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            long end = System.nanoTime();
            
            sounds.put(name, sound);
            
            double timeMs = (end - start) / 1_000_000.0;
            System.out.println("  ✓ " + name + " (" + String.format("%.2f", timeMs) + "ms)");
            return true;
        } catch (Exception e) {
            System.err.println("  ✗ " + name + " : " + e.getMessage());
            return false;
        }
    }
    
    // ========================================
    // 🎵 GESTION MUSIQUE MENU
    // ========================================
    
    /**
     * Démarre la musique du MENU
     * Utilisé par : MenuScreen, OptionsScreen, CharactersChoice
     */
    public void startMenuMusic() {
        // Arrêter la musique du level si elle joue
        stopLevelMusic();
        
        // ⭐ Ne démarrer QUE si elle n'est pas déjà en train de jouer
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.play();
            System.out.println("🎵 Musique MENU démarrée");
        } else if (menuMusic != null && menuMusic.isPlaying()) {
            System.out.println("🎵 Musique MENU déjà en cours");
        }
    }
    
    /**
     * Arrête la musique du menu
     */
    public void stopMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
            System.out.println("🎵 Musique MENU arrêtée");
        }
    }
    
    /**
     * Met en pause la musique du menu
     */
    public void pauseMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.pause();
            System.out.println("🎵 Musique MENU en pause");
        }
    }
    
    /**
     * Reprend la musique du menu
     */
    public void resumeMenuMusic() {
        if (menuMusic != null) {
            menuMusic.play();
            System.out.println("🎵 Musique MENU reprise");
        }
    }
    
    // ========================================
    // 🎮 GESTION MUSIQUE LEVEL
    // ========================================
    
    /**
     * Démarre la musique du LEVEL (en jeu)
     * Utilisé par : GameManager
     */
    public void startLevelMusic() {
        // Arrêter la musique du menu si elle joue
        stopMenuMusic();
        
        // ⭐ Ne démarrer QUE si elle n'est pas déjà en train de jouer
        if (levelMusic != null && !levelMusic.isPlaying()) {
            levelMusic.play();
            System.out.println("🎵 Musique LEVEL démarrée");
        } else if (levelMusic != null && levelMusic.isPlaying()) {
            System.out.println("🎵 Musique LEVEL déjà en cours");
        }
    }
    
    /**
     * Arrête la musique du level
     */
    public void stopLevelMusic() {
        if (levelMusic != null && levelMusic.isPlaying()) {
            levelMusic.stop();
            System.out.println("🎵 Musique LEVEL arrêtée");
        }
    }
    
    /**
     * Met en pause la musique du level
     */
    public void pauseLevelMusic() {
        if (levelMusic != null && levelMusic.isPlaying()) {
            levelMusic.pause();
            System.out.println("🎵 Musique LEVEL en pause");
        }
    }
    
    /**
     * Reprend la musique du level
     */
    public void resumeLevelMusic() {
        if (levelMusic != null) {
            levelMusic.play();
            System.out.println("🎵 Musique LEVEL reprise");
        }
    }
    
    // ========================================
    // 🔊 GESTION SONS
    // ========================================
    
    /**
     * Joue un son IMMÉDIATEMENT
     */
    public void playSound(String soundName) {
        if (!audioSystemReady) {
            System.err.println("⚠️ Audio non prêt, impossible de jouer : " + soundName);
            return;
        }
        
        Sound sound = sounds.get(soundName);
        if (sound == null) {
            System.err.println("❌ Son introuvable : " + soundName);
            System.err.println("   Sons disponibles : " + sounds.keySet());
            return;
        }
        
        long playTime = System.nanoTime();
        long soundId = sound.play(soundVolume);
        long endTime = System.nanoTime();
        
        double delayMs = (endTime - playTime) / 1_000_000.0;
        
        if (soundId == -1) {
            System.err.println("❌ Échec lecture du son : " + soundName);
        } else {
            // Log seulement si le délai est anormal
            if (delayMs > 5.0) {
                System.err.println("⚠️ DÉLAI ANORMAL pour " + soundName + " : " + String.format("%.2f", delayMs) + "ms");
            }
        }
    }
    
    // ========================================
    // 🎛️ CONTRÔLES VOLUME
    // ========================================
    
    /**
     * Change le volume des effets sonores
     */
    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0f, Math.min(1f, volume));
        System.out.println("🔊 Volume sons : " + (int)(soundVolume * 100) + "%");
    }
    
    /**
     * Change le volume de TOUTES les musiques
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        if (menuMusic != null) {
            menuMusic.setVolume(musicVolume);
        }
        if (levelMusic != null) {
            levelMusic.setVolume(musicVolume);
        }
        System.out.println("🎵 Volume musique : " + (int)(musicVolume * 100) + "%");
    }
    
    // ========================================
    // 🔄 UPDATE (pour vérifications)
    // ========================================
    
    /**
     * Vérifie et relance les musiques si nécessaire
     * Note : Avec setLooping(true), ce n'est plus nécessaire
     */
    public void update() {
        // Les musiques sont en loop automatique, pas besoin de les relancer manuellement
    }
    
    // ========================================
    // 🧹 NETTOYAGE
    // ========================================
    
    /**
     * Libère toutes les ressources audio
     */
    public void dispose() {
        System.out.println("\n🧹 Nettoyage AudioManager...");
        
        // Arrêter et disposer la musique menu
        if (menuMusic != null) {
            if (menuMusic.isPlaying()) {
                menuMusic.stop();
            }
            menuMusic.dispose();
            menuMusic = null;
            System.out.println("  ✓ Musique menu disposée");
        }
        
        // Arrêter et disposer la musique level
        if (levelMusic != null) {
            if (levelMusic.isPlaying()) {
                levelMusic.stop();
            }
            levelMusic.dispose();
            levelMusic = null;
            System.out.println("  ✓ Musique level disposée");
        }
        
        // Disposer tous les sons
        int soundsDisposed = 0;
        for (Sound sound : sounds.values()) {
            if (sound != null) {
                sound.dispose();
                soundsDisposed++;
            }
        }
        sounds.clear();
        System.out.println("  ✓ " + soundsDisposed + " sons disposés");
        
        audioSystemReady = false;
        System.out.println("✅ AudioManager disposé\n");
    }
    
    // ========================================
    // 📊 GETTERS / STATUS
    // ========================================
    
    public float getSoundVolume() {
        return soundVolume;
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public boolean isMenuMusicPlaying() {
        return menuMusic != null && menuMusic.isPlaying();
    }
    
    public boolean isLevelMusicPlaying() {
        return levelMusic != null && levelMusic.isPlaying();
    }
    
    public boolean isAudioReady() {
        return audioSystemReady;
    }
    
    /**
     * Affiche le statut de l'audio (debug)
     */
    public void printStatus() {
        System.out.println("\n📊 STATUT AUDIO :");
        System.out.println("  Système prêt : " + audioSystemReady);
        System.out.println("  Sons chargés : " + sounds.size());
        System.out.println("  Musique menu : " + (menuMusic != null ? "OK" : "NULL"));
        System.out.println("  Musique level : " + (levelMusic != null ? "OK" : "NULL"));
        System.out.println("  Menu joue : " + isMenuMusicPlaying());
        System.out.println("  Level joue : " + isLevelMusicPlaying());
        System.out.println("  Volume sons : " + (int)(soundVolume * 100) + "%");
        System.out.println("  Volume musique : " + (int)(musicVolume * 100) + "%\n");
    }
}