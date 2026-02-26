package guion1;

public class Ejemplo_3_3a_volatile {

    // Variable compartida entre hilos CON garantía de visibilidad
    static volatile int x = 0;

    static class Hilo33a implements Runnable {
        public void run() {
            x = 7;
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("Antes: x=" + x);

        Thread t1 = new Thread(new Hilo33a());
        
        t1.start();
        t1.join();

        System.out.println("Después: x=" + x);
    }
}

//Comunicación simple entre hilos.
//Muy útil para banderas de control (parar un hilo, cambiar de estado).

//Resuelve problemas de visibilidad: evita que un hilo trabaje con un valor "antiguo"
//almacenado en caché.
//Evita bucles infinitos causados por optimizaciones del compilador.

//No proporciona exclusión mutua.
//No hace atómicas operaciones como x++.
//No evita pérdidas de actualizaciones en operaciones compuestas.

//volatile hace que los hilos se vean, pero no que se coordinen.
