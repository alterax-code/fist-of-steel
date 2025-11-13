package com.fistofsteel.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;

public class SoundManager {
    private HashMap<String, Sound> sounds;
    private float volume = 1.0f;
    
    public SoundManager() {
        sounds = new HashMap<>();
        loadSounds();
    }
    
    private void loadSounds() {
        try {
            sounds.put("jump", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/jump.ogg")));
            sounds.put("attack", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/attack.ogg")));
            sounds.put("hit", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/hit.ogg")));
            sounds.put("death", Gdx.audio.newSound(Gdx.files.internal("assets/sounds/death.ogg")));
        } catch (Exception e) {
            System.err.println("⚠ Erreur chargement des sons : " + e.getMessage());
        }
    }
    
    public void play(String soundName) {
        Sound sound = sounds.get(soundName);
        if (sound != null) {
            sound.play(volume);
        }
    }
    
    public void play(String soundName, float customVolume) {
        Sound sound = sounds.get(soundName);
        if (sound != null) {
            sound.play(customVolume);
        }
    }
    
    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
    }
    
    public void dispose() {
        for (Sound sound : sounds.values()) {
            sound.dispose();
        }
        sounds.clear();
    }
}