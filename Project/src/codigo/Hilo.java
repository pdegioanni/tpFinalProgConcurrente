package codigo;

import java.util.concurrent.TimeUnit;

public class Hilo implements Runnable {
	private String nombre;
	private Monitor monitor;
	private int[] secuencia;
	private int siguienteTransicion;
	private boolean continuar = true;
	
	public Hilo(String nombre,Monitor monitor,int[] secuencia) {
		this.nombre = nombre;
		this.monitor = monitor;
		this.secuencia = secuencia;
		siguienteTransicion = secuencia[0];
	}
	public void run() {
		while(continuar) {
			for(int i=0;i<secuencia.length;i++) {
				siguienteTransicion = secuencia[i] - 1;
				monitor.dispararTransicion(siguienteTransicion);
				try {
            		TimeUnit.MILLISECONDS.sleep(1);
        		} catch (InterruptedException e) {
					//System.out.printf("Interrumpido hilo %s", Thread.currentThread().getName());
					continuar = false;
        		}
			}
		}
		//System.out.printf("Fin de hilo %s\n", Thread.currentThread().getName());
	}

	/*public void set_Fin()
	{
		continuar = false;
	}*/
}
