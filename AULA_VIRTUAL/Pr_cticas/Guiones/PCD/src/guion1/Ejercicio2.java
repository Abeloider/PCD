package guion1;

public class Ejercicio2 {
	public static void main(String[] args) {

		HiloHolaRunnable runnableHola = new HiloHolaRunnable();
		HiloMundoRunnable runnableMundo = new HiloMundoRunnable();

		Thread hola = new Thread(runnableHola);
		Thread mundo = new Thread(runnableMundo);

		hola.start();
		mundo.start();
		
		try {
			hola.join();
			mundo.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Fin hilo principal");
	}
}

class HiloHolaRunnable implements Runnable {
	public void run() {
		System.out.println("Hola");
	}
}

class HiloMundoRunnable implements Runnable {
	public void run() {
		System.out.println("Mundo");
	}
}

// Los mensajes "Hola" y "Mundo" se imprimen en orden no determinista,
// pero "Fin hilo principal" aparece siempre al final porque el hilo
// principal espera a ambos hilos mediante join().
