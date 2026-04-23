//no utilizamos el monitor con synchronized ya que este solo nos permite disponer de una cola de espera
/*
Para asegurarnos que los clientes elijan la cola con menor tiempo de espera,
el tiempo aleatorio de validacion de cada cliente se le será asignado
al momento de entrar a la cola. Ya que si lo hiciesemos
por numero de clientes en la cola, existe la posibilidad de que otra cola
con mas clientes tenga un tiempo de espera menor
*/
package ejercicios;

import java.util.concurrent.locks.*;


public class Ejercicio3{
	
	// creamos el candado lock 
	private ReentrantLock lock = new ReentrantLock(); 
	
	// creamos la zona de tornos, la zona de maquinas y la zona de bici premium 
    private Condition[] tornos = new Condition[3];
    private Condition[] maquinas = new Condition[4];
	private Condition biciPremium = lock.newCondition(); 
		
	// vemos si de los 3 tornos esta ocupado o no 
	private boolean[] tornoOcupado = new boolean[3]; 
	// vemos si en la zona de maquinas ha llegado a 5 maquinas ocupadas como maximo 
	private int[] maquinasOcupadas = new int[4]; 
	// de momento la bici esta libre
	private boolean biciPremiumOcupada = false; 

    // Contadores para las estadísticas 
    private final int[] filaTornos = new int[3]; // 
    private final int[] filaZonas = new int[4];
    private int filabici = 0;

    private Ejercicio3() {
        for (int i = 0; i < 3; i++) tornos[i] = lock.newCondition();
        for (int i = 0; i < 4; i++) maquinas[i] = lock.newCondition();
    }
	
	// metodo para acceder a los tornos 
	public int entrarTorno() throws InterruptedException {
		lock.lock(); 
		try {
			// implementamos la logica de entrar al torno torno 
            int mejorTorno = 0;
            // cogemos la que menor fila de tornos tenga
			for (int i = 0; i < 3; i++) {
                if (filaTornos[i] < filaTornos[mejorTorno]) {
                    mejorTorno = i;
                }
            }
            // incrementamos el contador de la fila del torno
            filaTornos[mejorTorno]++;
            // esperamos en la cola del torno elegido si esta ocupado, incrementando el contador de espera
            while (tornoOcupado[mejorTorno]) {
                tornos[mejorTorno].await();
            } 
            // al salir del bucle el hilo ha sido despertado y es su turno, decrementamos el contador de espera 
            filaTornos[mejorTorno]--;
            // ocupamos el torno elegido
            tornoOcupado[mejorTorno] = true;
            // devolvemos el numero del torno (1, 2 o 3)
            return mejorTorno + 1; // Devolvemos el número del torno (1, 2 o 3)
		}finally {
			lock.unlock();
		}
 	}
	

	// metodo para salir torno 
	public void salirTorno(int numTorno) {
        lock.lock();
        try {
            // implementamos la logica de salir del torno 
            tornoOcupado[numTorno - 1] = false;
            tornos[numTorno - 1].signal(); 

        } finally {
            lock.unlock();
        }
    }

	public void entrarZona(int zona) throws InterruptedException {
		lock.lock();
		try {
			filaZonas[zona]++;
			while(maquinasOcupadas[zona] >= 5){ {
				maquinas[zona].await();
			}
			filaZonas[zona]--;
			maquinasOcupadas[zona]++;
		}
	}finally{
			lock.unlock();
		}
	}

	public void salirZona(int zona) throws InterruptedException {
		lock.lock();
		try {
			maquinasOcupadas[zona]--;
			maquinas[zona].signal();
		} 
		finally {
			lock.unlock();
		}
	}

	public void usarBiciPremium() throws InterruptedException{
		lock.lock();
		try {
			filabici++;
			while(biciPremiumOcupada) {
				biciPremium.await();
			}
			filabici--;
			biciPremiumOcupada = true;
		}
		finally{
			lock.unlock();
		}
	}
	
	public void liberarBiciPremium() throws InterruptedException{
		lock.lock();
		try {
			biciPremiumOcupada = false;
			biciPremium.signal();
		}
		finally{
			lock.unlock();
		}
	}
}


// CLASE HILO CLIENTE 
class cliente extends Thread{
	
}





public class Ejercicio3 {
    public static void main(String[] args) {
        MonitorGimnasio monitor = new MonitorGimnasio();
        
        // Simulación con 50 hilos cliente
        for (int i = 1; i <= 50; i++) {
            new Cliente(i, monitor).start();
        }
    }
}