package codigo;

import java.util.concurrent.TimeUnit;

public class Hilo implements Runnable {
	private String tipo;
	private Monitor monitor;
	private int[] secuencia;
	private int siguienteTransicion;
	private boolean continuar = true;
	//private boolean compartido;
	
	public Hilo(String tipo,Monitor monitor,int[] secuencia) {
		this.tipo = tipo;
		this.monitor = monitor;
		this.secuencia = secuencia;
		//this.compartido = compartido;
		siguienteTransicion = secuencia[0];
	}
	public void run() {
		while(continuar) {
			for(int i=0;i<secuencia.length;i++) {
				//System.out.println("Hilo :"+nombre);
				siguienteTransicion = secuencia[i] - 1;
				monitor.dispararTransicion(siguienteTransicion);
				try {
            		TimeUnit.MILLISECONDS.sleep(500);
        		} catch (InterruptedException e) {
					continuar = false;
        		}
			}
		}
	}
	//public int getSiguienteTransicion(){return siguienteTransicion;}
	//public boolean isCompartido(){return compartido;}
	public int[] getSecuencia(){return secuencia;}
	public void set_Fin()
	{
		continuar = false;
	}
}
