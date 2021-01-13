package com.bigbass.reactiondiffusion.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.tommyettinger.anim8.AnimatedGif;
import com.github.tommyettinger.anim8.Dithered;
import com.github.tommyettinger.anim8.PaletteReducer;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class Simulation {
	public static final int SIZE = 512; // MUST BE A POWER OF TWO
	private Grid gridActive;
	private Grid gridTemp;

	private float dA = 1.0f;
	private float dB = 0.5f;
	private float feed = 0.0375f;
	private float kill = 0.062f;

	private int stepsPerFrame = 40; // Decrease if FPS is too low. Controls the number of generations per frame

	private float adj = 0.2f;
	private float diag = 0.05f;

	private int generations = 0;

	private boolean isRendering = false;

//	private SpriteBatch gifBatch;
//	private GifRecorder gifRecorder;

	private ForkJoinPool pool;

	private AnimatedGif gif;
	public Array<Pixmap> pixmaps;
	public Pixmap current;
	private boolean isRecording;

	public Simulation(){
		gridActive = new Grid(150, 50, SIZE, SIZE);
		gridTemp = new Grid(150, 50, SIZE, SIZE);
		gif = new AnimatedGif();
		int[] palette = new int[256];
		for (int i = 1; i < 256; i++) {
			palette[i] = 0x01010100 * i | 0xFF;
		}
		gif.palette = new PaletteReducer(palette);
		gif.setDitherAlgorithm(Dithered.DitherAlgorithm.NONE);
		pixmaps = new Array<>(128);
		current = new Pixmap(SIZE, SIZE, Pixmap.Format.RGBA8888);
		isRecording = true;
		pool = ForkJoinPool.commonPool();
	}

	public void updateAndRender(){
		isRendering = !Gdx.input.isKeyPressed(Keys.SPACE); // Disable rendering by holding SPACE

		for(int z = 0; z < stepsPerFrame; z++){

			UpdateWorld wu = new UpdateWorld(gridTemp, gridActive, 1, gridTemp.cells.length - 1, 1, gridTemp.cells[1].length - 1);
			pool.execute(wu);
			wu.join();

			if(isRendering && z == stepsPerFrame - 1){
				for (int i = 1; i < gridTemp.cells.length - 1; i++) {
					for (int j = 1; j < gridTemp.cells[i].length - 1; j++) {
						Cell t = gridTemp.cells[i][j];
						current.setColor(t.red, t.green, t.blue, 1f);
						current.drawPixel(i, j);
						//sr.setColor(t.red, t.green, t.blue, 1);
						//sr.point(gridActive.pos.x + i, gridActive.pos.y + j, 0);
					}
				}
				if(isRecording){
					pixmaps.add(current);
					if(pixmaps.size < 100)
						current = new Pixmap(SIZE, SIZE, Pixmap.Format.RGBA8888);
				}
			}

			// Copy gridTemp data into gridActive
			Grid swap = gridActive;
			gridActive = gridTemp;
			gridTemp = swap;

			generations += 1;
		}
		if(isRecording && pixmaps.size >= 90){
			isRecording = false;
			Gdx.files.local("gifexport").mkdirs();
			FileHandle file = Gdx.files.local("gifexport/recording"+(TimeUtils.millis() / 1000L)+".gif");
			gif.write(file, pixmaps, 30);
		}
	}

	public float laplacianA(int x, int y){
		return -gridActive.cells[x][y].a
				+ (gridActive.cells[x - 1][y].a + gridActive.cells[x + 1][y].a +  gridActive.cells[x][y - 1].a + gridActive.cells[x][y + 1].a) * adj
				+ (gridActive.cells[x - 1][y - 1].a + gridActive.cells[x - 1][y + 1].a + gridActive.cells[x + 1][y - 1].a + gridActive.cells[x + 1][y + 1].a) * diag;
	}

	public float laplacianB(int x, int y){
		return -gridActive.cells[x][y].b
				+ (gridActive.cells[x - 1][y].b + gridActive.cells[x + 1][y].b +  gridActive.cells[x][y - 1].b + gridActive.cells[x][y + 1].b) * adj
				+ (gridActive.cells[x - 1][y - 1].b + gridActive.cells[x - 1][y + 1].b + gridActive.cells[x + 1][y - 1].b + gridActive.cells[x + 1][y + 1].b) * diag;
	}

	public int getGenerations(){
		return generations;
	}

	public void dispose(){
		for(Pixmap p : pixmaps)
			p.dispose();
	}


	private class UpdateWorld extends RecursiveAction {
		private int threshold = 1024;
		private Grid tmp;
		private Grid active;
		private int startx;
		private int endx;
		private int starty;
		private int endy;

		public UpdateWorld(Grid tmp, Grid active, int startx, int endx, int starty, int endy) {
			this.tmp = tmp;
			this.active = active;
			this.startx = startx;
			this.endx = endx;
			this.starty = starty;
			this.endy = endy;
		}

		public void compute() {
			int work = (endx - startx) * (endy - starty);
			if (work > threshold) {
				int xdiff = (endx - startx) / 2;
				int ydiff = (endy - starty) / 2;

				UpdateWorld uwA = new UpdateWorld(tmp, active, startx, startx + xdiff, starty, starty + ydiff);
				UpdateWorld uwB = new UpdateWorld(tmp, active, startx + xdiff, endx, starty, starty + ydiff);
				UpdateWorld uwC = new UpdateWorld(tmp, active, startx, startx + xdiff, starty + ydiff, endy);
				UpdateWorld uwD = new UpdateWorld(tmp, active, startx + xdiff, endx, starty + ydiff, endy);

				invokeAll(uwA, uwB, uwC, uwD);
			} else {
				for (int i = startx; i < endx; i++) {
					for (int j = starty; j < endy; j++) {
						Cell c = active.cells[i][j];
						Cell t = tmp.cells[i][j];

						final float abb = (c.a * c.b * c.b);
						t.a = MathUtils.clamp(c.a + (dA * laplacianA(i, j)) - abb + (feed * (1 - c.a)), 0f, 1f);
						t.b = MathUtils.clamp(c.b + (dB * laplacianB(i, j)) + abb - ((kill + feed) * c.b), 0f, 1f);

						t.updateColor();
					}
				}
			}
		}
	}
}