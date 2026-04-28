package guion4;

import messagepassing.MailBox; // Importamos la clase de la biblioteca JMP 
import java.util.Random;

public class Ejercicio4 {

    public static void main(String[] args) {
        // Creamos los buzones para el torno R y el L 
        MailBox tornoR = new MailBox();
        MailBox tornoL = new MailBox();
        MailBox pantalla = new MailBox(); // Buzón extra para exclusión mutua de la pantalla

        //introducimos un token inicial en cada buzon que se le cedera a cada aficionado cuando lo pida
        // para evitar que haya mas de un aficionado a la vez
        tornoR.send("tokenR"); 
        tornoL.send("tokenL");
        pantalla.send("tokenPantalla");

        // creamos los hilos para cada aficionado 
        Thread[] aficionados = new Thread[50];
        for (int i = 0; i < 50; i++) {
            aficionados[i] = new Thread(new Aficionado(i, tornoR, tornoL, pantalla));
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

// La clase implementa Runnable para ser ejecutada por un Thread 
class Aficionado implements Runnable {
    private int id;
    private MailBox tornoR;
    private MailBox tornoL;
    private MailBox pantalla;
    private Random rand;

    // Constructor que recibe las referencias a los buzones compartidos  1081]
    public Aficionado(int id, MailBox tornoR, MailBox tornoL, MailBox pantalla) {
        this.id = id;
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

                // El controlador estima el tiempo (1 a 10)
                int t = rand.nextInt(10) + 1; 
                String colaAsignada;
                MailBox tornoAsignado;

                // Asignación de cola según el tiempo
                if (t <= 5) {
                    colaAsignada = "R";
                    tornoAsignado = tornoR;
                } else {
                    colaAsignada = "L";
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