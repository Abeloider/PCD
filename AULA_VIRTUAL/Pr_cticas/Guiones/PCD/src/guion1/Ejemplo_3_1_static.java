package guion1;

class Hilo31 implements Runnable {
    public void run() {
        Ejemplo_3_1_static.x = 7;
    }
}

public class Ejemplo_3_1_static {

	// Variable compartida entre hilos SIN garantía de visibilidad
    static int x = 0;

    public static void main(String[] args) throws Exception {
    	
        System.out.println("Antes: x=" + x);
        
        Hilo31 a = new Hilo31();
        Thread t1 = new Thread(a);
        
        t1.start();
        t1.join();

        System.out.println("Después: x=" + x);
    }
}

//Una variable static pertenece a la clase, no a los objetos.
//Existe una única copia de la variable para toda la aplicación.
//Todos los hilos acceden a la misma variable.

//Permite compartir tipos primitivos (int, boolean, etc.) entre hilos.
//Es la forma más directa de hacer una variable global visible para todos los hilos.

//Resuelve problemas de compartición: todos los hilos pueden leer y escribir la misma
//variable.

//No garantiza orden ni atomicidad.
//No evita condiciones de carrera.
//No asegura que el resultado sea correcto cuando hay operaciones compuestas
//(++, leer-modificar-escribir).
//static permite compartir, pero no protege.