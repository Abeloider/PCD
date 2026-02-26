package guion1;

class HiloConHerencia21b extends Thread {
	
	private String id;

	public HiloConHerencia21b(String _id) {
		id = _id;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			System.out.println(id);
			// Sleep que introduce aleatoriedad
			try {
				Thread.sleep((int) (Math.random() * 10));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

public class Ejemplo_2_1b_thread_sleep {
	
	public static void main(String[] args) {

		Thread a = new HiloConHerencia21b("Hilo a");
		Thread b = new HiloConHerencia21b("Hilo b");

		a.start();
		b.start();

		// Sleep que introduce aleatoriedad
		try {
			Thread.sleep((int) (Math.random() * 50));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Fin del hilo principal");
	}
}

// La inclusión de un retardo en la ejecución de los hilos provoca
// mayor grado de aleatoriedad en la salida.