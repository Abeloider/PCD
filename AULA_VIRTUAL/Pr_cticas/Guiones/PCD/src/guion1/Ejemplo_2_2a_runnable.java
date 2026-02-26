package guion1;

//Clase que implementa la interfaz Runnable.
//Define la tarea que será ejecutada por un hilo independiente.
class HiloConRunnable22a implements Runnable {
	
	private String id;

	public HiloConRunnable22a(String _id) {
		id = _id;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			System.out.println(id);
		}
	}
}

// Clase principal del programa.
// Contiene el método main y crea dos hilos a partir de objetos que implementan Runnable.
public class Ejemplo_2_2a_runnable {

	public static void main(String[] args) {
		
		HiloConRunnable22a a = new HiloConRunnable22a("Hilo a");
		HiloConRunnable22a b = new HiloConRunnable22a("Hilo b");
		
		Thread t1 = new Thread(a);
		Thread t2 = new Thread(b);
		
		t1.start();
		t2.start();
		
		System.out.println("Fin del hilo principal");
		
		}
}

// El comportamiento es equivalente al de Thread: los mensajes de los
// hilos a, b y principal pueden aparecer en distinto orden en cada
// ejecución debido a la ejecución concurrente de los hilos.
