package com.baniares.laberinto;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Laberinto implements ApplicationListener {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture entrada, salida, pared, pasillo, cursor;
	private int tamaño, ex, ey, sx, sy, visitados, currentX, currentY,
			direccion,solx,soly;
	private float dimension;
	private String[][] laberinto;
	private boolean norte = false, sur = false, este = false, oeste = false,
			pasar, terminado = false,recorrido=false;
	private long lastact;

	@Override
	public void create() {
		lastact = TimeUtils.nanoTime();
		tamaño = 40;
		dimension = 720 / (tamaño * 2 + 1);
		ex = MathUtils.random(0, tamaño - 1);
		ey = MathUtils.random(0, tamaño - 1);
		sx = MathUtils.random(0, tamaño - 1);
		sy = MathUtils.random(0, tamaño - 1);
		currentX = (ex * 2) + 1;
		currentY = (ey * 2) + 1;
		solx=currentX;
		soly=currentY;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 1280, 720);
		// camera.zoom=(float)0.1;
		// camera.viewportHeight=dimension*tamaño;
		// camera.viewportWidth=(100*camera.viewportHeight)/(float)56.25;
		entrada = new Texture(Gdx.files.internal("data/entrada.png"));
		salida = new Texture(Gdx.files.internal("data/salida.png"));
		pared = new Texture(Gdx.files.internal("data/pared.png"));
		pasillo = new Texture(Gdx.files.internal("data/pasillo.png"));
		cursor = new Texture(Gdx.files.internal("data/cursor.png"));
		laberinto = new String[(tamaño * 2) + 1][(tamaño * 2) + 1];
		for (int i = 0; i <= tamaño * 2; i++) {
			for (int j = 0; j <= tamaño * 2; j++) {
				laberinto[i][j] = "P";
			}
		}
		laberinto[(ex * 2) + 1][(ey * 2) + 1] = "E";
		laberinto[(sx * 2) + 1][(sy * 2) + 1] = "S";
		batch = new SpriteBatch();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// batch.draw(pared,280,720-36);
		for (int i = 0; i <= tamaño * 2; i++) {
			for (int j = 0; j <= tamaño * 2; j++) {
				if (laberinto[i][j].equals("P")) {
					batch.draw(pared, 280 + (i * dimension), 720 - dimension
							- (j * dimension), dimension, dimension);
				}
				if (laberinto[i][j].equals("E")) {
					batch.draw(entrada, 280 + (i * dimension), 720 - dimension
							- (j * dimension), dimension, dimension);
				}
				if (laberinto[i][j].equals("S") || laberinto[i][j].equals("SV")) {
					batch.draw(salida, 280 + (i * dimension), 720 - dimension
							- (j * dimension), dimension, dimension);
				}
				if (laberinto[i][j].equals("L") || laberinto[i][j].equals("V")) {
					batch.draw(pasillo, 280 + (i * dimension), 720 - dimension
							- (j * dimension), dimension, dimension);
				}
				if (laberinto[i][j].equals("L+") || laberinto[i][j].equals("V+")){
					batch.draw(pasillo, 280 + (i * dimension), 720 - dimension - (j * dimension), dimension, dimension);
					batch.draw(cursor, 280 + (i * dimension), 720 - dimension - (j * dimension), dimension/2, dimension/2);
				}
			}
		}
		if (!terminado)
			batch.draw(cursor, 280 + (currentX * dimension), 720 - dimension
					- (currentY * dimension), dimension, dimension);
		batch.end();
		// camera.position.x=280+(currentX*dimension);
		// camera.position.y=720-dimension-(currentY*dimension);
		// camera.update();
		if (!terminado && TimeUtils.nanoTime() - lastact > 500000) {
			armar();
		} else if(!recorrido) {
			recorrido=recorrer(solx, soly);
		}
	}

	private boolean recorrer(int x, int y) {
		lastact=TimeUtils.nanoTime();
		if (x > 0 && x < (tamaño * 2) + 1 && y > 0 && y < (tamaño * 2) + 1) {
			if (laberinto[x][y].equals("SV")) {
				return true;
			} else {
				boolean camino=false;
				for(int i=0;i<4;i++){
					switch(i){
					case 0:
						if(laberinto[x][y-1].equals("L")){
							laberinto[x][y-1]="L+";
							if(!laberinto[x][y-2].equals("SV"))
								laberinto[x][y-2]="V+";
							camino=recorrer(x,y-2);
							if(!camino){
								laberinto[x][y-1]="L";
								laberinto[x][y-2]="V";
							}else{
								return camino;
							}
						}
						break;
					case 1:
						if(laberinto[x+1][y].equals("L")){
							laberinto[x+1][y]="L+";
							if(!laberinto[x+2][y].equals("SV"))
								laberinto[x+2][y]="V+";
							camino=recorrer(x+2,y);
							if(!camino){
								laberinto[x+1][y]="L";
								laberinto[x+2][y]="V";
							}else{
								return camino;
							}
						}
						break;
					case 2:
						if(laberinto[x][y+1].equals("L")){
							laberinto[x][y+1]="L+";
							if(!laberinto[x][y+2].equals("SV"))
								laberinto[x][y+2]="V+";
							camino=recorrer(x,y+2);
							if(!camino){
								laberinto[x][y+1]="L";
								laberinto[x][y+2]="V";
							}else{
								return camino;
							}
						}
						break;
					case 3:
						if(laberinto[x-1][y].equals("L")){
							laberinto[x-1][y]="L+";
							if(!laberinto[x-2][y].equals("SV"))
								laberinto[x-2][y]="V+";
							camino=recorrer(x-2,y);
							if(!camino){
								laberinto[x-1][y]="L";
								laberinto[x-2][y]="V";
							}else{
								return camino;
							}
						}
						break;
					}
				}
				if(!camino)
					return false;
			}
		} else {
			return false;
		}
		return true;
	}

	/*
 	private boolean generar(int x, int y){
		boolean avanzar=false;
		while(!avanzar){
			
		}
		return true;
	}
	*/
	private void armar() {
		pasar = false;
		while (norte && sur && este && oeste) {
			for (int i = 1; i <= tamaño * 2; i += 2) {
				for (int j = 1; j <= tamaño * 2; j += 2) {
					if (laberinto[i][j].equals("E")
							|| laberinto[i][j].equals("V")
							|| laberinto[i][j].equals("SV")) {
						if (i - 2 > 0
								&& (laberinto[i - 2][j].equals("P") || laberinto[i - 2][j]
										.equals("S"))) {
							currentX = i;
							currentY = j;
							oeste = false;
						} else if (i + 2 <= tamaño * 2
								&& (laberinto[i + 2][j].equals("P") || laberinto[i + 2][j]
										.equals("S"))) {
							currentX = i;
							currentY = j;
							este = false;
						} else if (j - 2 > 0
								&& (laberinto[i][j - 2].equals("P") || laberinto[i][j - 2]
										.equals("S"))) {
							currentX = i;
							currentY = j;
							norte = false;
						} else if (j + 2 <= tamaño * 2
								&& (laberinto[i][j + 2].equals("P") || laberinto[i][j + 2]
										.equals("S"))) {
							currentX = i;
							currentY = j;
							sur = false;
						}
					}
				}
			}
		}
		while (!pasar) {
			direccion = MathUtils.random(0, 3);
			switch (direccion) {
			case 0:
				if (!norte) {
					pasar = true;
				}
				break;
			case 1:
				if (!este) {
					pasar = true;
				}
				break;
			case 2:
				if (!sur) {
					pasar = true;
				}
				break;
			case 3:
				if (!oeste) {
					pasar = true;
				}
				break;
			}
		}
		switch (direccion) {
		case 0:
			if (currentY - 2 > 0) {
				if (laberinto[currentX][currentY - 2].equals("P")
						|| laberinto[currentX][currentY - 2].equals("S")) {
					laberinto[currentX][currentY - 1] = "L";
					if (laberinto[currentX][currentY - 2].equals("S")) {
						laberinto[currentX][currentY - 2] = "SV";
					} else {
						laberinto[currentX][currentY - 2] = "V";
					}
					currentY = currentY - 2;
					norte = false;
					sur = true;
					este = false;
					oeste = false;
				} else {
					norte = true;
				}
			} else {
				norte = true;
			}
			break;

		case 1:
			if (currentX + 2 <= tamaño * 2) {
				if (laberinto[currentX + 2][currentY].equals("P")
						|| laberinto[currentX + 2][currentY].equals("S")) {
					laberinto[currentX + 1][currentY] = "L";
					if (laberinto[currentX + 2][currentY].equals("S")) {
						laberinto[currentX + 2][currentY] = "SV";
					} else {
						laberinto[currentX + 2][currentY] = "V";
					}
					currentX = currentX + 2;
					norte = false;
					sur = false;
					este = false;
					oeste = true;
				} else {
					este = true;
				}
			} else {
				este = true;
			}
			break;

		case 2:
			if (currentY + 2 <= tamaño * 2) {
				if (laberinto[currentX][currentY + 2].equals("P")
						|| laberinto[currentX][currentY + 2].equals("S")) {
					laberinto[currentX][currentY + 1] = "L";
					if (laberinto[currentX][currentY + 2].equals("S")) {
						laberinto[currentX][currentY + 2] = "SV";
					} else {
						laberinto[currentX][currentY + 2] = "V";
					}
					currentY = currentY + 2;
					norte = true;
					sur = false;
					este = false;
					oeste = false;
				} else {
					sur = true;
				}
			} else {
				sur = true;
			}
			break;

		case 3:
			if (currentX - 2 > 0) {
				if (laberinto[currentX - 2][currentY].equals("P")
						|| laberinto[currentX - 2][currentY].equals("S")) {
					laberinto[currentX - 1][currentY] = "L";
					if (laberinto[currentX - 2][currentY].equals("S")) {
						laberinto[currentX - 2][currentY] = "SV";
					} else {
						laberinto[currentX - 2][currentY] = "V";
					}
					currentX = currentX - 2;
					norte = false;
					sur = false;
					este = true;
					oeste = false;
				} else {
					oeste = true;
				}
			} else {
				oeste = true;
			}
			break;
		}
		visitados = 0;
		for (int i = 1; i <= tamaño * 2; i += 2) {
			for (int j = 1; j <= tamaño * 2; j += 2) {
				if (laberinto[i][j].equals("V") || laberinto[i][j].equals("E")
						|| laberinto[i][j].equals("SV")) {
					visitados++;
					if (visitados >= tamaño * tamaño) {
						terminado = true;
					}
				}
			}
		}
		lastact = TimeUtils.nanoTime();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
