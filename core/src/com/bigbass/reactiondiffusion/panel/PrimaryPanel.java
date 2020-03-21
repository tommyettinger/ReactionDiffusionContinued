package com.bigbass.reactiondiffusion.panel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.bigbass.reactiondiffusion.Main;
import com.bigbass.reactiondiffusion.skins.SkinManager;
import com.bigbass.reactiondiffusion.world.Simulation;

public class PrimaryPanel extends Panel {

	private Camera cam;
	private Stage stage;
	private ShapeRenderer sr;
	
	private Label infoLabel;
	
	private float scalar = 1;
	
	private Simulation sim;
	private Texture texture;
	
	public PrimaryPanel() {
		super();
		
		cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(cam.viewportWidth / 2, cam.viewportHeight / 2, 0);
		cam.update();
		
		stage = new Stage();
		Main.inputMultiplexer.addProcessor(stage);
		Main.inputMultiplexer.addProcessor(new ScrollwheelInputAdapter(){
			@Override
			public boolean scrolled(int amount) {
				if(amount == 1){
					changeCameraViewport(0.1f);
				} else if(amount == -1){
					changeCameraViewport(-0.1f);
				}
				return true;
			}
		});
		
		infoLabel = new Label("", SkinManager.getSkin("fonts/droid-sans-mono.ttf", 10));
		infoLabel.setColor(Color.WHITE);
		stage.addActor(infoLabel);
		
		sr = new ShapeRenderer(514 * 514);
		sr.setAutoShapeType(true);
		sr.setProjectionMatrix(cam.combined);
		texture = new Texture(Simulation.SIZE, Simulation.SIZE, Pixmap.Format.RGBA8888);
		Image image = new Image(texture);
		image.setPosition(0, 0);
		stage.addActor(image);
		sim = new Simulation();
	}
	
	public void render() {
		sr.begin(ShapeType.Filled);
		sr.setColor(Color.BLACK);
		sr.rect(-(cam.viewportWidth * 0.5f), -(cam.viewportHeight * 0.5f), Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
		sr.end();
		sim.updateAndRender();

		texture.draw(sim.pixmaps.peek(), 0, 0);
		
		panelGroup.render();
		
		stage.draw();
		
		/*sr.begin(ShapeType.Filled);
		sr.setColor(Color.FIREBRICK);
		renderDebug(sr);
		sr.end();*/
	}
	
	public void update(float delta) {
		panelGroup.update(delta);
		
		stage.act(delta);
		
		String info = String.format("FPS: %s%nGens: %d",
				Gdx.graphics.getFramesPerSecond(),
				sim.getGenerations()
			);
		
		infoLabel.setText(info);
		infoLabel.setPosition(10, Gdx.graphics.getHeight() - (infoLabel.getPrefHeight() / 2) - 5);
	}
	
	public boolean isActive() {
		return true; // Always active
	}
	
	public void dispose(){
		stage.dispose();
		sr.dispose();
		panelGroup.dispose();
		sim.dispose();
	}
	
	private void changeCameraViewport(float dscalar){
		scalar += dscalar;
		
		cam.viewportWidth = Gdx.graphics.getWidth() * scalar;
		cam.viewportHeight = Gdx.graphics.getHeight() * scalar;
		cam.update();

		sr.setProjectionMatrix(cam.combined);
	}
}
