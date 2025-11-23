package com.fistofsteel.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;

/**
 * Gestionnaire audio centralisé pour le jeu.
 * Gère séparément la musique du menu, la musique du level et la musique de victoire/défaite.
 * Gère également tous les effets sonores du jeu.
 */
public class AudioManager {
    
    private float soundVolume = 1.0f;
    private float musicVolume = 0.5f;
    
    private HashMap<String, Sound> sounds;
    
    private Music menuMusic;
    private Music levelMusic;
    private Music victoryMusic;
    
    private boolean audioSystemReady = false;
    
    /**
     * Constructeur. Initialise et charge tous les fichiers audio.
     */
    public AudioManager() {
        System.out.println("\n========================================");
        System.out.println("INITIALISATION AUDIOMANAGER");
        System.out.println("========================================");
        
        sounds = new HashMap<>();
        loadAllAudio();
        
        System.out.println("========================================\n");
    }
    
    /**
     * Charge tous les fichiers audio au démarrage.
     */
    private void loadAllAudio() {
        long startTime = System.currentTimeMillis();
        
        System.out.println("Chargement des sons...");
        int soundsLoaded = 0;
        
        soundsLoaded += loadSound("jump", "assets/sounds/jump.ogg") ? 1 : 0;
        soundsLoaded += loadSound("attack", "assets/sounds/attack.ogg") ? 1 : 0;
        soundsLoaded += loadSound("hit", "assets/sounds/hit.ogg") ? 1 : 0;
        soundsLoaded += loadSound("death", "assets/sounds/death.ogg") ? 1 : 0;
        
        System.out.println(soundsLoaded + "/4 sons charges");
        
        System.out.println("\nChargement de la musique menu...");
        try {
            menuMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/Untitled.ogg"));
            menuMusic.setLooping(true);
            menuMusic.setVolume(musicVolume);
            System.out.println("Musique menu chargee : Untitled.ogg (looping active)");
        } catch (Exception e) {
            System.err.println("ERREUR musique menu : " + e.getMessage());
            System.err.println("   Fichier attendu : assets/music/Untitled.ogg");
            menuMusic = null;
        }
        
        System.out.println("\nChargement de la musique level...");
        try {
            levelMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/Untitled-_1_.ogg"));
            levelMusic.setLooping(true);
            levelMusic.setVolume(musicVolume);
            System.out.println("Musique level chargee : Untitled-_1_.ogg (looping active)");
        } catch (Exception e) {
            System.err.println("ERREUR musique level : " + e.getMessage());
            System.err.println("   Fichier attendu : assets/music/Untitled-_1_.ogg");
            levelMusic = null;
        }
        
        System.out.println("\nChargement de la musique victoire...");
        try {
            victoryMusic = Gdx.audio.newMusic(Gdx.files.internal("assets/music/EPIC-VICTORY-FANFARE_-explosive-triumpha.ogg"));
            victoryMusic.setLooping(true);
            victoryMusic.setVolume(musicVolume);
            System.out.println("Musique victoire chargee : EPIC-VICTORY-FANFARE_-explosive-triumpha.ogg (looping active)");
        } catch (Exception e) {
            System.err.println("ERREUR musique victoire : " + e.getMessage());
            System.err.println("   Fichier attendu : assets/music/EPIC-VICTORY-FANFARE_-explosive-triumpha.ogg");
            victoryMusic = null;
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("\nTemps de chargement : " + (endTime - startTime) + "ms");
        
        audioSystemReady = (soundsLoaded > 0);
        
        if (!audioSystemReady) {
            System.err.println("ATTENTION : Systeme audio non fonctionnel !");
        }
    }
    
    /**
     * Charge un son individuel avec vérification.
     * 
     * @param name Le nom du son
     * @param path Le chemin du fichier
     * @return true si le chargement a réussi, false sinon
     */
    private boolean loadSound(String name, String path) {
        try {
            long start = System.nanoTime();
            Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
            long end = System.nanoTime();
            
            sounds.put(name, sound);
            
            double timeMs = (end - start) / 1_000_000.0;
            System.out.println("  " + name + " (" + String.format("%.2f", timeMs) + "ms)");
            return true;
        } catch (Exception e) {
            System.err.println("  " + name + " : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Démarre la musique du menu.
     * Utilisé par MenuScreen, OptionsScreen, CharactersChoice.
     */
    public void startMenuMusic() {
        stopLevelMusic();
        stopVictoryMusic();
        
        if (menuMusic != null && !menuMusic.isPlaying()) {
            menuMusic.play();
            System.out.println("Musique MENU demarree");
        } else if (menuMusic != null && menuMusic.isPlaying()) {
            System.out.println("Musique MENU deja en cours");
        }
    }
    
    /**
     * Arrête la musique du menu.
     */
    public void stopMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.stop();
            System.out.println("Musique MENU arretee");
        }
    }
    
    /**
     * Met en pause la musique du menu.
     */
    public void pauseMenuMusic() {
        if (menuMusic != null && menuMusic.isPlaying()) {
            menuMusic.pause();
            System.out.println("Musique MENU en pause");
        }
    }
    
    /**
     * Reprend la musique du menu.
     */
    public void resumeMenuMusic() {
        if (menuMusic != null) {
            menuMusic.play();
            System.out.println("Musique MENU reprise");
        }
    }
    
    /**
     * Démarre la musique du level (en jeu).
     * Utilisé par GameManager.
     */
    public void startLevelMusic() {
        stopMenuMusic();
        stopVictoryMusic();
        
        if (levelMusic != null && !levelMusic.isPlaying()) {
            levelMusic.play();
            System.out.println("Musique LEVEL demarree");
        } else if (levelMusic != null && levelMusic.isPlaying()) {
            System.out.println("Musique LEVEL deja en cours");
        }
    }
    
    /**
     * Arrête la musique du level.
     */
    public void stopLevelMusic() {
        if (levelMusic != null && levelMusic.isPlaying()) {
            levelMusic.stop();
            System.out.println("Musique LEVEL arretee");
        }
    }
    
    /**
     * Met en pause la musique du level.
     */
    public void pauseLevelMusic() {
        if (levelMusic != null && levelMusic.isPlaying()) {
            levelMusic.pause();
            System.out.println("Musique LEVEL en pause");
        }
    }
    
    /**
     * Reprend la musique du level.
     */
    public void resumeLevelMusic() {
        if (levelMusic != null) {
            levelMusic.play();
            System.out.println("Musique LEVEL reprise");
        }
    }
    
    /**
     * Démarre la musique de victoire/défaite.
     * Utilisé par GameOverScreen, WinnerScreen.
     */
    public void startVictoryMusic() {
        stopMenuMusic();
        stopLevelMusic();
        
        if (victoryMusic != null && !victoryMusic.isPlaying()) {
            victoryMusic.play();
            System.out.println("Musique VICTOIRE demarree");
        } else if (victoryMusic != null && victoryMusic.isPlaying()) {
            System.out.println("Musique VICTOIRE deja en cours");
        }
    }
    
    /**
     * Arrête la musique de victoire.
     */
    public void stopVictoryMusic() {
        if (victoryMusic != null && victoryMusic.isPlaying()) {
            victoryMusic.stop();
            System.out.println("Musique VICTOIRE arretee");
        }
    }
    
    /**
     * Joue un son immédiatement.
     * 
     * @param soundName Le nom du son à jouer
     */
    public void playSound(String soundName) {
        if (!audioSystemReady) {
            System.err.println("Audio non pret, impossible de jouer : " + soundName);
            return;
        }
        
        Sound sound = sounds.get(soundName);
        if (sound == null) {
            System.err.println("Son introuvable : " + soundName);
            System.err.println("   Sons disponibles : " + sounds.keySet());
            return;
        }
        
        long playTime = System.nanoTime();
        long soundId = sound.play(soundVolume);
        long endTime = System.nanoTime();
        
        double delayMs = (endTime - playTime) / 1_000_000.0;
        
        if (soundId == -1) {
            System.err.println("Echec lecture du son : " + soundName);
        } else {
            if (delayMs > 5.0) {
                System.err.println("DELAI ANORMAL pour " + soundName + " : " + String.format("%.2f", delayMs) + "ms");
            }
        }
    }
    
    /**
     * Change le volume des effets sonores.
     * 
     * @param volume Le nouveau volume (0.0 à 1.0)
     */
    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0f, Math.min(1f, volume));
        System.out.println("Volume sons : " + (int)(soundVolume * 100) + "%");
    }
    
    /**
     * Change le volume de toutes les musiques.
     * 
     * @param volume Le nouveau volume (0.0 à 1.0)
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0f, Math.min(1f, volume));
        if (menuMusic != null) {
            menuMusic.setVolume(musicVolume);
        }
        if (levelMusic != null) {
            levelMusic.setVolume(musicVolume);
        }
        if (victoryMusic != null) {
            victoryMusic.setVolume(musicVolume);
        }
        System.out.println("Volume musique : " + (int)(musicVolume * 100) + "%");
    }
    
    /**
     * Vérifie et relance les musiques si nécessaire.
     * Note : Avec setLooping(true), ce n'est plus nécessaire.
     */
    public void update() {
        // Les musiques sont en loop automatique
    }
    
    /**
     * Libère toutes les ressources audio.
     */
    public void dispose() {
        System.out.println("\nNettoyage AudioManager...");
        
        if (menuMusic != null) {
            if (menuMusic.isPlaying()) {
                menuMusic.stop();
            }
            menuMusic.dispose();
            menuMusic = null;
            System.out.println("  Musique menu disposee");
        }
        
        if (levelMusic != null) {
            if (levelMusic.isPlaying()) {
                levelMusic.stop();
            }
            levelMusic.dispose();
            levelMusic = null;
            System.out.println("  Musique level disposee");
        }
        
        if (victoryMusic != null) {
            if (victoryMusic.isPlaying()) {
                victoryMusic.stop();
            }
            victoryMusic.dispose();
            victoryMusic = null;
            System.out.println("  Musique victoire disposee");
        }
        
        int soundsDisposed = 0;
        for (Sound sound : sounds.values()) {
            if (sound != null) {
                sound.dispose();
                soundsDisposed++;
            }
        }
        sounds.clear();
        System.out.println("  " + soundsDisposed + " sons disposes");
        
        audioSystemReady = false;
        System.out.println("AudioManager dispose\n");
    }
    
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
    
    public boolean isVictoryMusicPlaying() {
        return victoryMusic != null && victoryMusic.isPlaying();
    }
    
    public boolean isAudioReady() {
        return audioSystemReady;
    }
    
    /**
     * Affiche le statut de l'audio pour le debug.
     */
    public void printStatus() {
        System.out.println("\nSTATUT AUDIO :");
        System.out.println("  Systeme pret : " + audioSystemReady);
        System.out.println("  Sons charges : " + sounds.size());
        System.out.println("  Musique menu : " + (menuMusic != null ? "OK" : "NULL"));
        System.out.println("  Musique level : " + (levelMusic != null ? "OK" : "NULL"));
        System.out.println("  Musique victoire : " + (victoryMusic != null ? "OK" : "NULL"));
        System.out.println("  Menu joue : " + isMenuMusicPlaying());
        System.out.println("  Level joue : " + isLevelMusicPlaying());
        System.out.println("  Victoire joue : " + isVictoryMusicPlaying());
        System.out.println("  Volume sons : " + (int)(soundVolume * 100) + "%");
        System.out.println("  Volume musique : " + (int)(musicVolume * 100) + "%\n");
    }
}