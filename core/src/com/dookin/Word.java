package com.dookin;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by David on 3/18/2018.
 */

public class Word {

    String word = "";
    float x,y;
    GlyphLayout layout;

    public Word(String word, float x, float y) {
        this.word = word;
        this.x = x;
        this.y = y;
        layout = new GlyphLayout();
    }
    public void update() {

    }
    public void render(BitmapFont font, SpriteBatch batch) {
        layout.setText(font, word);
        font.draw(batch, layout, x , y);

    }
}
