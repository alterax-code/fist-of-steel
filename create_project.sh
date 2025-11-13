#!/bin/bash

# === GRADLE WRAPPER ===
mkdir -p gradle/wrapper

cat > gradle/wrapper/gradle-wrapper.properties << 'EOF'
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-7.5.1-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF

# Télécharge le jar du wrapper
wget https://raw.githubusercontent.com/gradle/gradle/v7.5.1/gradle/wrapper/gradle-wrapper.jar -O gradle/wrapper/gradle-wrapper.jar

cat > gradlew << 'EOF'
#!/bin/sh
exec java -classpath gradle/wrapper/gradle-wrapper.jar org.gradle.wrapper.GradleWrapperMain "$@"
EOF

chmod +x gradlew

# === FICHIERS GRADLE ===
cat > settings.gradle << 'EOF'
include 'desktop', 'core'
EOF

cat > build.gradle << 'EOF'
buildscript {
    repositories {
        mavenCentral()
        google()
    }
}

allprojects {
    version = '1.0'
    ext {
        appName = "FistOfSteel"
        gdxVersion = '1.12.1'
    }
    repositories {
        mavenCentral()
        maven { url "https://oss.sonatype.org/content/repositories/releases/" }
    }
}

project(":desktop") {
    apply plugin: "java-library"
    dependencies {
        implementation project(":core")
        api "com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion"
        api "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop"
    }
}

project(":core") {
    apply plugin: "java-library"
    dependencies {
        api "com.badlogicgames.gdx:gdx:$gdxVersion"
    }
}
EOF

cat > gradle.properties << 'EOF'
org.gradle.daemon=true
org.gradle.jvmargs=-Xms128m -Xmx1500m
EOF

# === DESKTOP ===
mkdir -p desktop/src/com/fistofsteel

cat > desktop/build.gradle << 'EOF'
sourceCompatibility = 17
sourceSets.main.java.srcDirs = [ "src/" ]
sourceSets.main.resources.srcDirs = ["../assets"]
mainClassName = "com.fistofsteel.DesktopLauncher"

task run(dependsOn: classes, type: JavaExec) {
    mainClass = project.mainClassName
    classpath = sourceSets.main.runtimeClasspath
    standardInput = System.in
    workingDir = file("../assets")
    ignoreExitValue = true
}
EOF

cat > desktop/src/com/fistofsteel/DesktopLauncher.java << 'EOF'
package com.fistofsteel;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Fist of Steel: Marvin's Vengeance");
        config.setWindowedMode(1280, 720);
        config.setForegroundFPS(60);
        config.useVsync(true);
        new Lwjgl3Application(new FistOfSteelGame(), config);
    }
}
EOF

# === CORE ===
mkdir -p core/src/com/fistofsteel/{screens,entities,world,input,utils}

cat > core/build.gradle << 'EOF'
sourceCompatibility = 17
[compileJava, compileTestJava]*.options*.encoding = 'UTF-8'
sourceSets.main.java.srcDirs = [ "src/" ]
EOF

cat > core/src/com/fistofsteel/FistOfSteelGame.java << 'EOF'
package com.fistofsteel;

import com.badlogic.gdx.Game;
import com.fistofsteel.screens.GameScreen;

public class FistOfSteelGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
EOF

cat > core/src/com/fistofsteel/utils/Constants.java << 'EOF'
package com.fistofsteel.utils;

public class Constants {
    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;
    public static final float GRAVITY = 0.5f;
    public static final float MAX_FALL_SPEED = 10.0f;
    public static final float JUMP_FORCE = -12.0f;
    public static final float WALK_SPEED = 5.0f;
    public static final float PLAYER_WIDTH = 64;
    public static final float PLAYER_HEIGHT = 64;
}
EOF

cat > core/src/com/fistofsteel/screens/GameScreen.java << 'EOF'
package com.fistofsteel.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen implements Screen {
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    
    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
EOF

# === ASSETS ===
mkdir -p assets/sprites/hugo

echo "Projet créé!"
echo "Lance: ./gradlew desktop:run"
