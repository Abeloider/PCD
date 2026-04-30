package ejercicios;

import messagepassing.MailBox; // Importamos la clase de la biblioteca JMP 

import java.io.Serializable;
import java.util.Random;


class DatosAsignacion implements Serializable {
	int tiempo; 
	String cola; 

	public DatosAsignacion(int tiempo, String cola) {
		this.tiempo=tiempo; 
		this.cola=cola; 
	}
}



public class Ejercicio4 {

	public static void main(String[] args) {
		// BUZON PARA EL CONTROLADOR 
		MailBox solicitudes = new MailBox(); 
		MailBox[] respuestas = new MailBox[50]; 
		for (int i = 0; i < 50; i++) {
            respuestas[i] = new MailBox();
        }

		// Creamos los buzones para el torno R y el L
		MailBox tornoR = new MailBox();
		MailBox tornoL = new MailBox();
		MailBox pantalla = new MailBox(); // Buzón extra para exclusión mutua de la pantalla

		// introducimos un token inicial en cada buzon que se le cedera a cada
		// aficionado cuando lo pida
		// para evitar que haya mas de un aficionado a la vez
		tornoR.send("tokenR");
		tornoL.send("tokenL");
		pantalla.send("tokenPantalla");

		Thread controlador = new Thread(new Controlador(solicitudes, respuestas));
        controlador.setDaemon(true); // Para que el programa cierre cuando acaben los aficionados
        controlador.start();

		// creamos los hilos para cada aficionado
		Thread[] aficionados = new Thread[50];
		for (int i = 0; i < 50; i++) {
			aficionados[i] = new Thread(new Aficionado(i, solicitudes, respuestas[i], tornoR, tornoL, pantalla));
			aficionados[i].start();
		}

		// Esperamos a que terminen
		for (int i = 0; i < 50; i++) {
			try {
				aficionados[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class Controlador implements Runnable {
    private MailBox solicitudes;
    private MailBox[] respuestas;
    private Random rand;

    public Controlador(MailBox solicitudes, MailBox[] respuestas) {
        this.solicitudes = solicitudes;
        this.respuestas = respuestas;
        this.rand = new Random();
    }

    @Override
    public void run() {
        while (true) { 
            // 1. Recibe la petición del aficionado
            int id = (Integer) solicitudes.receive(); 

            // 2. El servidor estima el tiempo (1 a 10)
            int t = rand.nextInt(10) + 1;
            String colaAsignada;

            // FORMA INTUITIVA: Asignamos la cola según el tiempo
            if (t <= 5) {
                colaAsignada = "R";
            } else {
                colaAsignada = "L";
            }

            // 3. Envía la respuesta al buzón del aficionado
            DatosAsignacion datos = new DatosAsignacion(t, colaAsignada);
			respuestas[id].send(datos);
        }
    }
}



// La clase implementa Runnable para ser ejecutada por un Thread 
class Aficionado implements Runnable {
	private int id;
	private MailBox solicitudes;
	private MailBox miRespuesta;

	private MailBox tornoR;
	private MailBox tornoL;
	private MailBox pantalla;
	private Random rand;

	// Constructor que recibe las referencias a los buzones compartidos
	public Aficionado(int id,MailBox solicitudes, MailBox miRespuesta, MailBox tornoR, MailBox tornoL, MailBox pantalla) {
		this.id = id;
		this.solicitudes = solicitudes; 
		this.miRespuesta = miRespuesta; 
		this.tornoR = tornoR;
		this.tornoL = tornoL;
		this.pantalla = pantalla;
		this.rand = new Random();
	}

	@Override
	public void run() {
		for (int ciclo = 0; ciclo < 5; ciclo++) {
			try {
				// 1. Acción previa (caminar hacia los baños)
				Thread.sleep(rand.nextInt(500) + 20); // Simulamos el paseo

				// 2. Solicita turno al controlador
				solicitudes.send(id);// Enviamos nuestro ID para que el controlador sepa a quién responder
				DatosAsignacion datos = (DatosAsignacion) miRespuesta.receive(); // Esperamos la respuesta del controlador
				int t = datos.tiempo;
				String colaAsignada = datos.cola;
				MailBox tornoAsignado;
				// Asignación de cola según el tiempo
				if (colaAsignada.equals("R")) {
                    tornoAsignado = tornoR;
                } else {
                    tornoAsignado = tornoL;
                }

				// 2. Solicita ponerse en la cola asignada
				// El hilo se bloquea si el buzón está vacío (alguien está usando el torno)
				Object tokenTorno = tornoAsignado.receive();

				// 3. Realiza la validación en el torno asignado
				Thread.sleep(t * 100); // Multiplico por 100ms para simular el tiempo T

				// 4. Libera la cola enviando el token de vuelta
				tornoAsignado.send(tokenTorno);

				// 5. Imprime en pantalla (Protegido por exclusión mutua)
				Object tokenPantalla = pantalla.receive();
				System.out.println("Aficionado " + id + " ha usado la cola " + colaAsignada);
				System.out.println("Tiempo de validación = " + t);
				System.out.println("Thread.sleep(" + t + ")");
				System.out.println("Aficionado " + id + " liberando la cola " + colaAsignada);
				System.out.println("---------------------------------");
				pantalla.send(tokenPantalla);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}