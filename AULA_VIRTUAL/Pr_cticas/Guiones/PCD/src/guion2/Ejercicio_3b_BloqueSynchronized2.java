package guion2;

// Objeto compartido entre los hilos.
class RecursoCompartido3b {

	// Dos variables compartidas que serán modificadas por distintos métodos.
    private int x = 0;
    private int y = 0;

    // Objeto que actúa como cerrojo (semáforo binario).
    // Todos los bloques synchronized usan este mismo objeto para garantizar
    // exclusión mutua.
    private final Object cerrojo = new Object();

    // Método que modifica la variable x, protegido por "cerrojo".
    // Esto garantiza que nunca se ejecute simultáneamente con el que modifica y,
    // aunque trabajen con datos distintos.
    public void incrementaX() {
        synchronized (cerrojo) {
            x++;
            System.out.println("Incrementando X: " + x);
        }
    }

    // Método que modifica la variable y, protegido por "cerrojo".
    // Esto garantiza que nunca se ejecute simultáneamente con el que modifica x,
    // aunque trabajen con datos distintos.
    public void incrementaY() {
        synchronized (cerrojo) {
            y++;
            System.out.println("Incrementando Y: " + y);
        }
    }
}

// Definición del hilo encargado de incrementar la variable x
class HiloX3b extends Thread {

	// Referencia al objeto compartido que usan todos los hilos.
    private final RecursoCompartido3b recurso;

    public HiloX3b(RecursoCompartido3b recurso) {
        this.recurso = recurso;
        setName("Hilo X");
    }

    // Código que ejecuta el hilo.
    public void run() {
        for (int i = 0; i < 5; i++) {
            recurso.incrementaX();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

//Definición del hilo encargado de incrementar la variable y
class HiloY3b extends Thread {

	// Referencia al objeto compartido que usan todos los hilos.
    private final RecursoCompartido3b recurso;

    public HiloY3b(RecursoCompartido3b recurso) {
        this.recurso = recurso;
        setName("Hilo Y");
    }

 // Código que ejecuta el hilo.
    public void run() {
        for (int i = 0; i < 5; i++) {
            recurso.incrementaY();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Clase principal que arranca el programa.
public class Ejercicio_3b_BloqueSynchronized2 {

    public static void main(String[] args) {

    	// Creación del objeto compartido único conteniendo las variables y el cerrojo.
        RecursoCompartido3b recurso = new RecursoCompartido3b();

        // Creación de dos hilos, ambos con acceso al mismo recurso compartido.
        Thread hiloX = new HiloX3b(recurso);
        Thread hiloY = new HiloY3b(recurso);

        // Inicio de la ejecución concurrente de los hilos.
        hiloX.start();
        hiloY.start();
        
    }
}

// Al usar el mismo cerrojo en ambos métodos incrementaX() e incrementaY(), las
// operaciones sobre x e y quedan en exclusión mutua, por lo que las impresiones
// salen intercaladas (unas veces se incrementa X primero y luego Y, y otras veces
// va al revés), y hasta que una iteración no acaba no empieza la siguiente.
