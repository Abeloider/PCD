package ejercicios;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Ejercicio1 {
	//
	public static ReentrantLock lock = new ReentrantLock();

	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			Random rand = new Random();
			int v[] = new int[9];
			for (int j = 0; j < 9; j++) {
				v[j] = rand.nextInt(10) + 1;
			}
			Cuadrado c = new Cuadrado(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8]);
			Suma s = new Suma(v[0], v[1], v[2], v[3], v[4], v[5], v[6], v[7], v[8]);
			c.start();
			try {
				c.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			s.start();
			try {
				s.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Cuadrado extends Thread {
	private int matriz[][];

	public Cuadrado(int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9) {
		this.matriz = new int[3][3];
		this.matriz[0][0] = a1;
		this.matriz[0][1] = a2;
		this.matriz[0][2] = a3;
		this.matriz[1][0] = a4;
		this.matriz[1][1] = a5;
		this.matriz[1][2] = a6;
		this.matriz[2][0] = a7;
		this.matriz[2][1] = a8;
		this.matriz[2][2] = a9;
	}

	public void run() {
		Ejercicio1.lock.lock();
		try {
			int suma;
			int[][] resultado = new int[3][3];
			System.out.println("A x A");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					System.out.print(matriz[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("x\n");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					System.out.print(matriz[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("A2");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					suma = 0;
					for (int k = 0; k < 3; k++) {
						suma += matriz[i][k] * matriz[k][j];
					}
					resultado[i][j] = suma;
					System.out.print(resultado[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
		} finally {
			Ejercicio1.lock.unlock();
		}
	}
}

class Suma extends Thread {
	private int matriz[][];

	public Suma(int a1, int a2, int a3, int a4, int a5, int a6, int a7, int a8, int a9) {
		this.matriz = new int[3][3];
		this.matriz[0][0] = a1;
		this.matriz[0][1] = a2;
		this.matriz[0][2] = a3;
		this.matriz[1][0] = a4;
		this.matriz[1][1] = a5;
		this.matriz[1][2] = a6;
		this.matriz[2][0] = a7;
		this.matriz[2][1] = a8;
		this.matriz[2][2] = a9;
	}

	public void run() {
		Ejercicio1.lock.lock();
		try {
			System.out.println("A + A");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					matriz[i][j] = matriz[i][j];
					System.out.print(matriz[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("+\n");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					matriz[i][j] = matriz[i][j];
					System.out.print(matriz[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("2A");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					matriz[i][j] = matriz[i][j] * 2;
					System.out.print(matriz[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
		} finally {
			Ejercicio1.lock.unlock();
		}
	}
}