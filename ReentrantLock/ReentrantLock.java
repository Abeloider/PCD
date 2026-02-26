package ejercicios;

import java.util.concurrent.locks.ReentrantLock;

public class Ejercicio1 {
	
}

class Cuadrado extends Thread{
	private static ReentrantLock lock=new ReentrantLock();
	private int matriz[][];
	
	public Cuadrado(int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9) {
		this.matriz[0][0]=a1;
		this.matriz[1][0]=a2;
		this.matriz[2][0]=a3;
		this.matriz[0][1]=a4;
		this.matriz[1][1]=a5;
		this.matriz[2][1]=a6;
		this.matriz[0][2]=a7;
		this.matriz[1][2]=a8;
		this.matriz[2][2]=a9;
	}
	
	public void run() {
		lock.lock();
		try{
			for (int i = 0; i < 3; i++) {
	            for (int j = 0; j < 3; j++) {
	                // Suma de productos de la fila i por la columna j
	                for (int k = 0; k < 3; k++) {
	                    matriz[i][j] += matriz[i][k] * matriz[k][j];
	                }
	            }
	        }
		}
		finally {
			lock.unlock();
		}
	}
}

class Suma extends Thread{
	private static ReentrantLock lock=new ReentrantLock();
	private int matriz[][];
	
	public Suma(int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9) {
		this.matriz[0][0]=a1;
		this.matriz[1][0]=a2;
		this.matriz[2][0]=a3;
		this.matriz[0][1]=a4;
		this.matriz[1][1]=a5;
		this.matriz[2][1]=a6;
		this.matriz[0][2]=a7;
		this.matriz[1][2]=a8;
		this.matriz[2][2]=a9;
	}
	
	public void run() {
		lock.lock();
		try{
			for (int i = 0; i < 3; i++) {
	            for (int j = 0; j < 3; j++) {
	                    matriz[i][j] = matriz[i][j] + matriz[i][j];
	                }
	            }
	        }
		finally {
			lock.unlock();
		}
	}
	
}