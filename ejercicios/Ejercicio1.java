package ejercicios;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Ejercicio1 {
	public static ReentrantLock lock = new ReentrantLock();

	public static void main(String[] args) {
		    Cuadrado c = new Cuadrado();
       		Suma s = new Suma();
			c.start();
			s.start();
			try {
				c.join();
				s.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


class Cuadrado extends Thread {
	private int[][] matriz;
	public Cuadrado() {
		this.matriz = new int[3][3];
	}

	public void run() {
		Random rand = new Random();
		for (int repeticion = 0; repeticion < 10; repeticion++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    matriz[i][j] = rand.nextInt(10) + 1;
                }
            }
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
						//Calculo del cuadrado
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
}

class Suma extends Thread {
	private int[][] matriz;
	public Suma() {
		this.matriz = new int[3][3];
	}

	public void run() {
		Random rand = new Random();
		for (int repeticion = 0; repeticion < 10; repeticion++) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    matriz[i][j] = rand.nextInt(10) + 1;
                }
            }
		Ejercicio1.lock.lock();
		try {
			System.out.println("A + A");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					System.out.print(matriz[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("+\n");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					System.out.print(matriz[i][j] + " ");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println("2A");
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					//Calculo de la suma
					int suma = matriz[i][j] + matriz[i][j];
					System.out.print(suma + " ");
				}
				System.out.println();
			}
			System.out.println();
		} finally {
			Ejercicio1.lock.unlock();
		}
	}
}
}