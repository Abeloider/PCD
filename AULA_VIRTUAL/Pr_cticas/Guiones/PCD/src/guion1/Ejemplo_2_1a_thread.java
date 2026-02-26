package guion1;

// Hilo creado mediante herencia de la clase Thread.
// Cada objeto de esta clase se ejecuta como un hilo independiente.
// Contiene un identificador y un contador en pantalla
class HiloConHerencia21a extends Thread {

    private String id;

    public HiloConHerencia21a(String _id) {
        id = _id;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.println(id);
        }
    }
}

// Clase principal del programa.
// Contiene el método main, que se ejecuta en el hilo principal.
// Crea dos hilos y los ejecuta.
public class Ejemplo_2_1a_thread {

    public static void main(String[] args) {

        Thread a = new HiloConHerencia21a("Hilo a");
        Thread b = new HiloConHerencia21a("Hilo b");

        a.start();
        b.start();

        System.out.println("Fin del hilo principal");
    }
}

// Los mensajes de los hilos a, b y principal pueden aparecer en distinto orden
// en cada ejecución, ya que los hilos se ejecutan de forma concurrente y su
// planificación no es determinista.
