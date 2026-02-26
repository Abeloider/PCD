package guion2;

class VariableCompartida2a {
	
	// Variable del objeto que actúa como contador compartido entre los hilos.
	private int var;
	
	// Constructor que inicializa el valor del contador.
	public VariableCompartida2a(int val) {
		var = val;
	}

	// Método que devuelve el valor actual de la variable compartida.
	public int getVar2a() {
		return var;
	}

	// Método sincronizado que incrementa la variable en exclusión mutua.
	public synchronized void incrementa2a() {
//	public void incrementa2a() {
		
		int temp;
		temp = var;
		
		try {
			Thread.sleep((int) Math.round(Math.random()));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		temp++;
		
		var = temp;
		
	}
}

class Hilo2a extends Thread {
	
	// Atributos que almacenan el identificador del hilo y la referencia al objeto
	// compartido.
	private String id;
	private VariableCompartida2a compar;

	// Constructor que asigna el identificador del hilo y el objeto compartido.
	public Hilo2a(String _id, VariableCompartida2a a) {
		id = _id;
		compar = a;
	}

	// Código que ejecuta el hilo: incrementa la variable compartida 1000 veces e
	// imprime su valor.
	public void run() {
		for (int i = 0; i < 1000; i++) {
			compar.incrementa2a();
			System.out.println(id + " " + compar.getVar2a());
		}
	}
}

public class Ejercicio_2a_Synchronized {

	public static void main(String[] args) {
		
		// Crea el objeto compartido inicializado a 0.
		VariableCompartida2a compar = new VariableCompartida2a(0);
		
		// Crea dos hilos que comparten el mismo objeto.
		Hilo2a a = new Hilo2a("Hilo a", compar);
		Hilo2a b = new Hilo2a("Hilo b", compar);
		
		// Inicia la ejecución concurrente de los hilos.
		a.start();
		b.start();
		
		// Hace que el hilo principal espere a que terminen los hilos secundarios.
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Muestra el valor final del contador tras la ejecución de ambos hilos.
		System.out.println("Hilo principal " + compar.getVar2a());
	}
}

// Al usar synchronized en incrementa2a(), los dos hilos incrementan el contador
// compartido en exclusión mutua, evitando condiciones de carrera, y al final el
// hilo principal imprime un valor correcto de 2000, aunque las impresiones están
// intercaladas y su orden es no determinista.
