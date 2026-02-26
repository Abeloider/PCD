package guion2;

import java.util.concurrent.locks.ReentrantLock;

class VariableCompartida3c {
	
	private int var;
	private ReentrantLock l = new ReentrantLock();
	
	public VariableCompartida3c(int val) {
		var = val;
	}

	public int getVar() {
		return var;
	}

	public void incrementar() {
		
		int temp;
		l.lock();
		
		try {
			temp = var;
			try {
				Thread.sleep((int) Math.round(Math.random()));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			temp++;
			var = temp;
		} finally {
			l.unlock();
		}
	}
}

class Hilo3c extends Thread {
	
	private String id;
	VariableCompartida3c compar;

	public Hilo3c(String _id, VariableCompartida3c a) {
		id = _id;
		compar = a;
	}

	public void run() {
		for (int i = 0; i < 1000; i++) {
			compar.incrementar();
			System.out.println(id + " " + compar.getVar());
		}
	}
}

public class Ejercicio_3c_ReentrantLock {

	public static void main(String[] args) {
		
		VariableCompartida3c compar = new VariableCompartida3c(0);
		
		Hilo3c a = new Hilo3c("Hilo a", compar);
		Hilo3c b = new Hilo3c("Hilo b", compar);
		
		a.start();
		b.start();
		
		try {
			a.join();
			b.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Hilo principal " + compar.getVar());
		
	}
}

// El incremento del contador compartido se realiza en exclusión mutua usando ReentrantLock,
// con lock() / unlock() en finally, evitando condiciones de carrera y haciendo que el valor
// final impreso por el hilo principal sea correcto (2000), aunque las impresiones de ambos
// hilos se intercalen.
