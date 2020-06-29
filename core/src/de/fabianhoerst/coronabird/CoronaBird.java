package de.fabianhoerst.coronabird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class CoronaBird extends ApplicationAdapter {
	Preferences prefs;
	SpriteBatch batch;
	BitmapFont font_score;
	BitmapFont font_virus;
	BitmapFont font_start;
	BitmapFont font_over;
	BitmapFont font_highscore;
	Texture background;
	Texture game_over;
	Texture player_frame1;
	Texture player_frame2;
	Texture virus_1;
	Texture virus_2;
	Texture top;
	Texture down;
	Texture player_over1;
	Texture player_over2;


	int obstacle_count = 4;

	float height_disp, width_disp;
	float scale_factor;
	float speed_player, gravity;
	float height_player, width_player;
	float player_x, player_y;
	float distance;
	float maxOffset;
	float obstacle_velocity;
	float obstacle_distance_x;
	float virus_y;
	float virus_x;

	float[] offset = new float[obstacle_count];
	float[] obstacle_x = new float[obstacle_count];

	int start;
	int frame;
	int virus_hit;
	int score;
	int tube_scoring;
	int frames_virus_font;
	int deadtime;
	int highscore;
	int start_time;

	Random randomOffset;
	Circle player_circle;
	Circle virus_circle;
	Rectangle[] top_rectangle;
	Rectangle[] down_rectangle;
	ShapeRenderer shapeRenderer;

	@Override
	public void create () {
		prefs = Gdx.app.getPreferences("myprefs");
		if(!prefs.contains("highscore")) prefs.putInteger("highscore", 0);
		highscore = prefs.getInteger("highscore");
		batch = new SpriteBatch();
		font_score = new BitmapFont(Gdx.files.internal("NordSudA-128.fnt"),
				Gdx.files.internal("fonts.png"),false);
		font_score.setColor(Color.WHITE);
		font_virus = new BitmapFont(Gdx.files.internal("NordSudA-128.fnt"),
				Gdx.files.internal("fonts.png"),false);
		font_virus.setColor(Color.WHITE);
		font_start = new BitmapFont(Gdx.files.internal("NordSudA-128.fnt"),
				Gdx.files.internal("fonts.png"),false);
		font_over = new BitmapFont(Gdx.files.internal("NordSudA-128.fnt"),
				Gdx.files.internal("fonts.png"),false);
		font_highscore = new BitmapFont(Gdx.files.internal("NordSudA-128.fnt"),
				Gdx.files.internal("fonts.png"),false);
		// set textures
		background = new Texture("bg2.png");
		player_frame1 = new Texture("fly1.png");
		player_frame2 = new Texture("fly2.png");
		player_over1 = new Texture("f1_over.png");
		player_over2 = new Texture("f2_over.png");
		top = new Texture("top.png");
		down = new Texture("down.png");
		virus_1 = new Texture("virus_1.png");
		virus_2 = new Texture("virus_2.png");
		game_over = new Texture("bg3.png");

		// other values
		height_disp = Gdx.graphics.getHeight();
        width_disp = Gdx.graphics.getWidth();
        scale_factor = height_disp/width_disp;
        frame = 0;
        player_y = 0;
        speed_player = 0;
        gravity = 2;
		height_player = Gdx.graphics.getHeight()/6*1/scale_factor;
		width_player = Gdx.graphics.getWidth()/6;
		player_x = width_disp/2-width_player/2;
		player_y = height_disp/2-height_player/2;
		start = 3;
		start_time = 180;
		distance = height_disp/4*(float)1.2;
		virus_hit = 0;
		score = 0;
		tube_scoring = 0;
		//Tubes
        maxOffset = Gdx.graphics.getHeight()/2-distance/2-100;
        randomOffset = new Random();
		obstacle_velocity = 6;
		obstacle_distance_x = ((float)Gdx.graphics.getWidth())/2*(float)1.5;

		top_rectangle = new Rectangle[obstacle_count];
		down_rectangle = new Rectangle[obstacle_count];
		for (int i = 0; i < obstacle_count; i++){
			offset[i] = (randomOffset.nextFloat() - 0.5f)*(Gdx.graphics.getHeight()- distance - 200);
			obstacle_x[i] = Gdx.graphics.getWidth() - down.getWidth()/2 + i*obstacle_distance_x;
			top_rectangle[i] = new Rectangle();
			down_rectangle[i] = new Rectangle();
		}
		player_circle = new Circle();
		shapeRenderer = new ShapeRenderer();
		virus_circle = new Circle();
		frames_virus_font = 0;
		deadtime = 60;
	}

	@Override
	// Render function: is running once per frame
	public void render () {
		batch.begin();
		// Draw background
		batch.draw(background,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (start == 1) {
			if (obstacle_x[tube_scoring] < Gdx.graphics.getWidth()/2-top.getWidth()/2){
				score ++;
				if (tube_scoring < obstacle_count - 1){
					tube_scoring = tube_scoring + 1;
				} else{
					tube_scoring = 0;
				}
			}
			if (Gdx.input.justTouched()) {
				speed_player = -30;
			}
			// Draw Tubes
			for (int i = 0; i < obstacle_count; i++){
				if (obstacle_x[i] <= - down.getWidth()){
					obstacle_x[i] += obstacle_count*obstacle_distance_x;
					offset[i] = (randomOffset.nextFloat() - 0.5f)*(Gdx.graphics.getHeight()- distance - 200);
				} else {
					obstacle_x[i] = obstacle_x[i] - obstacle_velocity;
				}
				batch.draw(top, obstacle_x[i], height_disp / 2 + distance / 2 + offset[i]);
				batch.draw(down, obstacle_x[i], height_disp / 2 - distance / 2 - (float) down.getHeight() + offset[i]);

				top_rectangle[i] = new Rectangle(obstacle_x[i], height_disp / 2 + distance / 2 + offset[i],
						top.getWidth(), top.getHeight());
				down_rectangle[i] = new Rectangle(obstacle_x[i],
						height_disp / 2 - distance / 2 - (float) down.getHeight() + offset[i],
						down.getWidth(), down.getHeight());
			}

			if (player_y > 0 || speed_player < 0) {
				speed_player = speed_player + gravity;
				player_y -= speed_player;
				if (player_y > (height_disp-height_player/8*7)){
					player_y = height_disp-height_player/8*7;
				}
			}
		}
		if (start == 2){
			if(score > highscore){
				highscore = score;
			}
			prefs.putInteger("highscore", highscore);
			prefs.flush();
			batch.draw(game_over,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			if (frame <= 8) {
				batch.draw(player_over1, width_disp / 2 - width_player,
						height_disp / 2 - height_player, width_player * 2, height_player * 2);
			}else{
				batch.draw(player_over2, width_disp / 2 - width_player,
						height_disp / 2 - height_player, width_player * 2, height_player * 2);
			}
			font_over.draw(batch, "TAP TO START", width_disp/20,
					height_disp/4);
			font_score.draw(batch, "GAME OVER\nSCORE: " + String.valueOf(score)+"\nHIGHSCORE: "
					+ String.valueOf(highscore), width_disp/20, height_disp-height_disp/25);
			deadtime = deadtime - 1;
			if (deadtime<=0) {
				if (Gdx.input.justTouched()) {
					frame = 0;
					speed_player = 0;
					virus_hit = 0;
					score = 0;
					tube_scoring = 0;
					start = 1;
					player_x = width_disp / 2 - width_player / 2;
					player_y = height_disp / 2 - height_player / 2;
					for (int i = 0; i < obstacle_count; i++) {
						offset[i] = (randomOffset.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - distance - 200);
						obstacle_x[i] = Gdx.graphics.getWidth() - down.getWidth() / 2 + i * obstacle_distance_x;
						top_rectangle[i] = new Rectangle();
						down_rectangle[i] = new Rectangle();
					}
					frames_virus_font = 0;
					deadtime = 60;
				}
			}
		}
		if (start == 0){
			font_virus.draw(batch, "TAP TO START", width_disp/20,height_disp/4);
			if (Gdx.input.justTouched()) {
				start = 1;
			}
		}

		if(score > highscore){
			highscore = score;
		}

        // Draw bird
		frame = (frame + 1)%16;
		// Choose which bird to fly
		if (start != 2) {
			if (frame <= 8) {
				// draw Virus
				virus_x = obstacle_x[3];
				virus_y = height_disp / 2 + offset[3] - height_player / 2;
				if (virus_hit == 0) {
					batch.draw(virus_1, virus_x, virus_y, width_player, height_player);
					virus_circle.set(virus_x + width_player / 2, virus_y + height_player / 2, width_player / (float) (2.3));
				} else if (virus_hit == 1 && virus_x >= width_disp) {
					virus_hit = 0;
					virus_circle.set(virus_x + width_player / 2, virus_y + height_player / 2, width_player / (float) (2.3));
				} else {
					virus_circle.set(0, 0, width_player / (float) (2.3));
				}
				draw_player(1, player_x, player_y);
			} else {
				// draw Virus
				virus_x = obstacle_x[3];
				virus_y = height_disp / 2 + offset[3] - height_player / 2;
				if (virus_hit == 0) {
					virus_hit = 0;
					batch.draw(virus_2, virus_x, virus_y, width_player, height_player);
					virus_circle.set(virus_x + width_player / 2, virus_y + height_player / 2, width_player / (float) (2.3));
				} else if (virus_hit == 1 && virus_x >= width_disp) {
					virus_circle.set(virus_x + width_player / 2, virus_y + height_player / 2, width_player / (float) (2.3));
				} else {
					virus_circle.set(0, 0, width_player / (float) (2.3));
				}
				draw_player(2, player_x, player_y);
			}
			font_score.draw(batch, String.valueOf(score),width_disp/20, height_disp-height_disp/25);
			font_highscore.draw(batch, "HIGHSCORE: " +String.valueOf(highscore),
					width_disp/20, height_disp/10);
		}
		if (frames_virus_font > 0){
			font_virus.draw(batch, "Virus killed!", width_disp/20,height_disp-height_disp/25-128);
			frames_virus_font = frames_virus_font - 1;
		}
		// Kollision
		player_circle.set(Gdx.graphics.getWidth()/2, player_y + height_player/2,
				width_player/(float)(2.3));
		if (start == 3) {
			batch.draw(game_over,0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			font_start.draw(batch, "#StayHome",width_disp/20, height_disp/2);
			if (start_time<=0){
				start = 0;
			}
			start_time = start_time - 1;
		}
		batch.end();
		for (int i = 0; i < obstacle_count; i++){
			if(Intersector.overlaps(player_circle,top_rectangle[i]) ||
					Intersector.overlaps(player_circle,down_rectangle[i])){
				Gdx.app.log("Collision", "Yes");
				start = 2;
			}
			if(Intersector.overlaps(player_circle, virus_circle)){
				Gdx.app.log("Points", "Hit Virus");
				virus_hit = 1;
				score = score + 1;
				frames_virus_font = 30;
			}
		}

		if (score>100){
			obstacle_velocity=7;
		}
		if (score>200){
			obstacle_velocity=8;
		}
		if (score>300){
			obstacle_velocity=9;
		}
		if (score>400){
			obstacle_velocity=10;
		}


    }
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}

	public void draw_player (int bird, float x, float y){
		if (bird == 1){
			batch.draw(player_frame1, x, y, width_player, height_player);
		}
		else{
			batch.draw(player_frame2, x, y, width_player, height_player);
		}
	}
}
