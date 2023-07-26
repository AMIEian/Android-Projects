package com.badlogic.androidgames.framework.impl;

import com.badlogic.androidgames.framework.Game;
import com.badlogic.androidgames.framework.Screen;

public abstract class GLScreen extends Screen {
    protected final GLGraphics glGraphics;
    protected final GLGame glGame;
    protected int gameLevel = 0;
    
    public GLScreen(Game game) {
        super(game);
        glGame = (GLGame)game;
        glGraphics = ((GLGame)game).getGLGraphics();
    }
    
    public GLScreen(Game game, int level) {
    	super(game);
    	glGame = (GLGame)game;
    	glGraphics = ((GLGame)game).getGLGraphics();
    	gameLevel = level;
    }

}
