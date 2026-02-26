package guion1;

// Muestra el uso de una variable volatile para garantizar la visibilidad
// de cambios entre el hilo principal y otro hilo.
public class Ejemplo_3_3b_volatile {
	
	public static void main(String[] args) {
		
		Hilo33b a = new Hilo33b();
		a.start();
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		a.setFlag(true);
		
		try {
			a.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Fin del hilo principal");
	}
}

// Hilo que utiliza una variable volatile como condición de parada.
// El uso de volatile asegura que el cambio del flag realizado por
// el hilo principal sea visible para este hilo.
class Hilo33b extends Thread {
	
	private volatile Boolean flag;

	public Hilo33b() {
		flag = false;
	}

	public void setFlag(boolean b) {
		flag = b;
	}

	public void run() {
		flag = false;
		while (!flag) {
			System.out.println("Se ejecuta el hilo");
		}
	}
}

// El hilo secundario imprime "Se ejecuta el hilo" hasta que el principal cambia
// flag a true, y gracias a volatile ese cambio se ve inmediatamente, el bucle
// termina y el programa finaliza correctamente.
