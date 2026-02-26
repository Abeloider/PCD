package guion1;

public class Ejercicio1 {

	public static void main(String[] args) {
		
		Thread hola = new HiloHola();
		Thread mundo = new HiloMundo();

		hola.start();
		mundo.start();

		System.out.println("Fin hilo principal");

	}
}

class HiloHola extends Thread {
	public void run() {
		System.out.println("Hola");
	}
}

class HiloMundo extends Thread {
	public void run() {
		System.out.println("Mundo");
	}
}

// Los mensajes "Hola", "Mundo" y "Fin hilo principal" pueden aparecer en
// distinto orden en cada ejecución, ya que los hilos se ejecutan de forma
// concurrente y sin sincronización.
