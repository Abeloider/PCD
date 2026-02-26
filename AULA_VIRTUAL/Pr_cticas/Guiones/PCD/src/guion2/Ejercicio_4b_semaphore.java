package guion2;

import java.util.concurrent.Semaphore;

class HiloHola4b extends Thread {
	
	public void run() {
		
		for (int i = 0; i < 10; i++) {
			
			try {
				Ejercicio_4b_semaphore.hola.acquire();
			} catch (InterruptedException e) {
				// manejar la excepción si se desea
			}
			
			System.out.print("Hola ");
			Ejercicio_4b_semaphore.mundo.release();
			
		}
	}
}

class HiloMundo4b extends Thread {
	
	public void run() {
		
		for (int i = 0; i < 10; i++) {
			
			try {
				Ejercicio_4b_semaphore.mundo.acquire();
			} catch (InterruptedException e) {
				// manejar la excepción si se desea
			}
			
			System.out.println("mundo");
			Ejercicio_4b_semaphore.hola.release();
			
		}
	}
}

public class Ejercicio_4b_semaphore {
	
	public static Semaphore hola = new Semaphore(1);
	public static Semaphore mundo = new Semaphore(0);

	public static void main(String[] args) {
		
		Thread a = new HiloHola4b();
		Thread b = new HiloMundo4b();
		
		a.start();
		b.start();
		
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Fin del hilo principal ");
	}
}

// Gracias al uso de semáforos, los hilos se sincronizan correctamente y se imprime
// "Hola mundo" de forma alternada y ordenada, finalizando después el hilo principal.
