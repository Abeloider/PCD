package ejercicios;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Ejercicio2 {
	public static void main(String[] args) {
		// Crear y lanzar 10 hilos (equipos) con identificador único
		Thread[] equipos = new Thread[10];
		for (int i = 0; i < 10; i++) {
			equipos[i] = new Equipo(i);
			equipos[i].start();
		}

		// Esperar a que todos terminen
		for (int i = 0; i < 10; i++) {
			try {
				equipos[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Todos los equipos han terminado.");
	}
}

class Equipo extends Thread {
	private static Semaphore s = new Semaphore(3);
	private static Semaphore impresion = new Semaphore(1);
	private int id;

	public Equipo(int id) {
		this.id = id;
	}

	public void run() {
		try {
			s.acquire();
			impresion.acquire();
			System.out.println("Equipo: " + id);
			Random rand = new Random();
			int resultado[][] = new int[4][5];
			for (int deportista = 0; deportista < 4; deportista++) { // deportistas por hilos
				int ejercicios[][] = new int[5][5];
				// creamos una tabla de ejercicios con puntuaciones
				for (int i = 0; i < 5; i++) { // fila
					for (int j = 0; j < 5; j++) { // columna
						ejercicios[i][j] = rand.nextInt(100);
						if (ejercicios[i][j] > resultado[deportista][i]) {
							resultado[deportista][i] = ejercicios[i][j];
						}
					}
					System.out.print(resultado[deportista][i] + " ");
				}
				System.out.println();
			}
			System.out.println();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			impresion.release();
			s.release();
		}
	}
}
