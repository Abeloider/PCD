package ejercicios;

import messagepassing.MailBox;
import java.io.Serializable;

// Clase para la respuesta del controlador (debe ser Serializable)
class Respuesta implements Serializable {
    int tiempo;
    char cola; // 'R' o 'L'

    public Respuesta(int tiempo, char cola) {
        this.tiempo = tiempo;
        this.cola = cola;
    }
}

// Hilo controlador de accesos
class Controlador extends Thread {
    private MailBox requestBox;
    private MailBox[] replyBoxes;
    private int numAficionados;

    public Controlador(MailBox requestBox, MailBox[] replyBoxes, int numAficionados) {
        this.requestBox = requestBox;
        this.replyBoxes = replyBoxes;
        this.numAficionados = numAficionados;
    }

    @Override
    public void run() {
        while (true) {
            Object msg = requestBox.receive(); // bloqueante si no hay mensajes
            if (msg instanceof String && ((String) msg).equals("STOP")) {
                break; // fin del controlador
            }
            int id = (Integer) msg;
            int tiempo = (int) (Math.random() * 10) + 1; // 1..10
            char cola = (tiempo <= 5) ? 'R' : 'L';
            replyBoxes[id - 1].send(new Respuesta(tiempo, cola));
        }
    }
}

// Hilo aficionado
class Aficionado extends Thread {
    private int id;
    private MailBox requestBox;
    private MailBox myReplyBox;
    private MailBox tokenR, tokenL;
    private final int CICLOS = 5;

    public Aficionado(int id, MailBox requestBox, MailBox myReplyBox,
                      MailBox tokenR, MailBox tokenL) {
        this.id = id;
        this.requestBox = requestBox;
        this.myReplyBox = myReplyBox;
        this.tokenR = tokenR;
        this.tokenL = tokenL;
    }

    @Override
    public void run() {
        for (int i = 0; i < CICLOS; i++) {
            try {
                // 1. Acción previa (caminar hacia los baños)
                Thread.sleep((int) (Math.random() * 401) + 100); // 100-500 ms
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // 2. Solicitar asignación de cola al controlador
            requestBox.send(id); // envío no bloqueante
            Respuesta resp = (Respuesta) myReplyBox.receive(); // espera la respuesta
            int T = resp.tiempo;
            char cola = resp.cola;

            // 3. Obtener acceso exclusivo al torno (token)
            if (cola == 'R') {
                tokenR.receive(); // se bloquea si el torno R está ocupado
            } else {
                tokenL.receive();
            }

            // 4. Validar la entrada (usar el torno)
            System.out.println("Aficionado " + id + " ha usado la cola " + cola +
                               " Tiempo de validación = " + T);
            try {
                Thread.sleep(T); // simula el tiempo de validación
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Aficionado " + id + " liberando la cola " + cola);

            // 5. Liberar el torno (devolver token)
            if (cola == 'R') {
                tokenR.send("token");
            } else {
                tokenL.send("token");
            }
        }
    }
}

// Programa principal
public class Ejercicio4 {
    public static void main(String[] args) {
        final int NUM_AFICIONADOS = 50;

        // Buzón para peticiones al controlador (capacidad ilimitada)
        MailBox requestBox = new MailBox();

        // Buzones de respuesta, uno por aficionado (capacidad 1)
        MailBox[] replyBoxes = new MailBox[NUM_AFICIONADOS];
        for (int i = 0; i < NUM_AFICIONADOS; i++) {
            replyBoxes[i] = new MailBox(1);
        }

        // Buzones token para cada torno (inicialmente con un "token")
        MailBox tokenR = new MailBox();
        tokenR.send("token");
        MailBox tokenL = new MailBox();
        tokenL.send("token");

        // Crear e iniciar el controlador
        Controlador control = new Controlador(requestBox, replyBoxes, NUM_AFICIONADOS);
        control.start();

        // Crear e iniciar los aficionados
        Aficionado[] fans = new Aficionado[NUM_AFICIONADOS];
        for (int i = 0; i < NUM_AFICIONADOS; i++) {
            fans[i] = new Aficionado(i + 1, requestBox, replyBoxes[i], tokenR, tokenL);
            fans[i].start();
        }

        // Esperar a que todos los aficionados terminen
        for (Aficionado f : fans) {
            try {
                f.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Enviar señal de terminación al controlador
        requestBox.send("STOP");
        try {
            control.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Todos los aficionados han completado sus ciclos.");
    }
}