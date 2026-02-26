package guion1;

class HiloConRunnable22b implements Runnable {
	private String id;

	public HiloConRunnable22b(String _id) {
		id = _id;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			System.out.println(id);
			try {
				Thread.sleep((int) (Math.random() * 10));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

public class Ejemplo_2_2b_runnable_join {

	public static void main(String[] args) {

		HiloConRunnable22b a = new HiloConRunnable22b("Hilo a");
		HiloConRunnable22b b = new HiloConRunnable22b("Hilo b");

		Thread t1 = new Thread(a);
		Thread t2 = new Thread(b);

		t1.start();
		t2.start();

		// Obligamos al hilo principa a esperar la finalización de los dos hilos t1 y t2
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Fin del hilo principal");

	}

}

// Los mensajes de los hilos a y b se intercalan de forma aleatoria, pero el
// mensaje del hilo principal solo se imprime cuando ambos hilos han finalizado
// gracias al uso de join().
