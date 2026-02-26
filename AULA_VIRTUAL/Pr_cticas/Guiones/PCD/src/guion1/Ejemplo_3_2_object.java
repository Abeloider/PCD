package guion1;

//Contenedor mutable para poder modificar el valor desde otro hilo
class Contenedor {
	int x;
	Contenedor(int x) {
		this.x = x;
	}
}

//El hilo recibe una referencia al objeto compartido y realiza una operación sobre él.
class Hilo32 implements Runnable {

	private final Contenedor cont;

	public Hilo32(Contenedor cont) {
		this.cont = cont;
	}

	public void run() {
		cont.x = 7;
	}
}

public class Ejemplo_3_2_object {

	// Se define un nuevo Contenedor con valor 0 y se inicia un hilo para que opere sobre él.
	public static void main(String[] args) throws Exception {

		Contenedor varCompartida = new Contenedor(0);

		System.out.println("Antes: x=" + varCompartida.x);

		Thread t1 = new Thread(new Hilo32(varCompartida));
		t1.start();
		t1.join();

		System.out.println("Después: x=" + varCompartida.x);
	}
}

//En Java, al pasar un objeto como parámetro, se pasa una copia de la referencia, no del objeto.
//Varios hilos pueden apuntar al mismo objeto en memoria.

//Permite compartir datos complejos (objetos, estructuras).
//Es la forma natural de compartir información entre hilos sin usar variables globales.

//Resuelve problemas de compartición estructurada: varios hilos trabajan sobre el mismo objeto.
//Permite agrupar varios datos relacionados en una sola entidad compartida.

//No evita condiciones de carrera.
//No hace atómicas las operaciones sobre los campos del objeto.

//Cambiar de static int a objeto.valor no arregla el problema del incremento concurrente.
//Compartir un objeto no implica acceso seguro.
