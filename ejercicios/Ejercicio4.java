package ejercicios;

import messagepassing.MailBox; // Importamos la clase de la biblioteca JMP 

import java.io.Serializable;
import java.util.Random;

/* 	Clase para almacenar los datos*/
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
		
		MailBox solicitudes = new MailBox(); 
		MailBox[] respuestas = new MailBox[50]; 
		// creamos buzones para cada afcionado 
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

		// hilo para el controlador 
		Thread controlador = new Thread(new Controlador(solicitudes, respuestas));
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
		// sin esto el controlador nunca terminaria 
		solicitudes.send(-1); // Enviamos una señal de terminación al controlador
	}
}

class Controlador implements Runnable {
	// el controlador recibe las solicitudes de los aficionados 
    private MailBox solicitudes; // buzón para recibir las solicitudes de los aficionados
    private MailBox[] respuestas; // para diferenciar a cada aficionado y enviarle la respuesta a su buzón correspondiente
    private Random rand;

    public Controlador(MailBox solicitudes, MailBox[] respuestas) {
        this.solicitudes = solicitudes;
        this.respuestas = respuestas;
        this.rand = new Random();
    }

    @Override
    public void run() {
        while (true) { 
            //Recibe la petición del aficionado
            int id = (Integer) solicitudes.receive();

			// si el id es -1 
			if (id == -1) { // Señal de terminación para el controlador 
				break;
			}
			
            // El servidor estima el tiempo (1 a 10)
            int t = rand.nextInt(10) + 1;
            String colaAsignada;
            // Asignación de cola según el tiempo
            if (t <= 5) {
                colaAsignada = "R";
            } else {
                colaAsignada = "L";
            }

            // Envía la respuesta al buzón del aficionado
            DatosAsignacion datos = new DatosAsignacion(t, colaAsignada);
			respuestas[id].send(datos);// enviamos las respuestas al buzon correspodiente de cada aficionado      
		}
    }
}

// implementamos el runable para crear los hilos de los aficionados
class Aficionado implements Runnable {
	private int id;
	private MailBox solicitudes;
	private MailBox miRespuesta;

	private MailBox tornoR;
	private MailBox tornoL;
	private MailBox pantalla;
	private Random rand;


	// Constructor 
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
				// simulamos el tiempo de paso por el aficionado 
				Thread.sleep(rand.nextInt(500) + 20); // Simulamos el paseo

				// Solicita turno al controlador
				solicitudes.send(id);// Enviamos nuestro ID 
				DatosAsignacion datos = (DatosAsignacion) miRespuesta.receive(); // Esperamos la respuesta del controlador
				
				int t = datos.tiempo;
				String x = datos.cola;

				// inicializamos el buzon del torno asignado 
				MailBox tornoAsignado= null;
				// Asignación de cola según el tiempo
				switch (x) {
					case "R":
						tornoAsignado = tornoR;
        				break;
    				case "L":
						tornoAsignado = tornoL;
       				    break;
				}

				// Solicita ponerse en la cola asignada
				// El hilo se bloquea si el buzón está vacío (sin token, entonces aficionado espera su turno )
				Object tokenTorno = tornoAsignado.receive();

				// simulamos el tiempo t
				Thread.sleep(t * 100); 

				// Libera la cola enviando el token de vuelta
				tornoAsignado.send(tokenTorno);

				//Imprime en pantalla exclusion mutua
				Object tokenPantalla = pantalla.receive();
				System.out.println("Aficionado " + id + " ha usado la cola " + x);
				System.out.println("Tiempo de validación = " + t);
				System.out.println("Thread.sleep(" + t + ")");
				System.out.println("Aficionado " + id + " liberando la cola " + x);
				System.out.println("---------------------------------");
				pantalla.send(tokenPantalla); // enviamos el token de la pantalla al buzon para que otro aficionado pueda imprimir su mensaje

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}