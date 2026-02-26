package guion2;

import java.util.concurrent.Semaphore;

class Hilo4a extends Thread {
	
	private String id;

	public Hilo4a(String _id) {
		id = _id;
	}

	public void run() {
		
		int temp;
		
		for (int i = 0; i < 1000; i++) {
			
			try {
				Ejercicio_4a_semaphore.mutex.acquire();
			} catch (InterruptedException e) {
				// manejar la excepción si se desea
			}
			
			temp = Ejercicio_4a_semaphore.n;
			temp++;
			Ejercicio_4a_semaphore.n = temp;
			Ejercicio_4a_semaphore.mutex.release();
		}
		
		System.out.println(id + " " + Ejercicio_4a_semaphore.n);
		
	}
}

public class Ejercicio_4a_semaphore {
	
	public static int n = 0;
	public static Semaphore mutex = new Semaphore(1);

	public static void main(String[] args) {
		
		Thread a = new Hilo4a("Hilo a");
		Thread b = new Hilo4a("Hilo b");
		
		a.start();
		b.start();
		
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Fin del hilo principal " + n);
		
	}
}

// El contador n se incrementa en exclusión mutua usando un Semaphore(1) como mutex,
// evitando la condición de carrera en el incremento y haciendo que el hilo principal
// termine mostrando un valor final correcto (2000). El valor de cada hilo no coincide
// porque la impresión de cada hilo no está protegida por el semáforo y se hace fuera
// de la sección crítica.
