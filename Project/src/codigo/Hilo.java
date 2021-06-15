package codigo;

public class Hilo implements Runnable {
	private String nombre;
	private Monitor monitor;
	private int secuencia[];
	private int siguienteTransicion;
	private boolean continuar = true;
	
	public Hilo(String nombre,Monitor monitor,int secuencia[]) {
		this.nombre = nombre;
		this.monitor = monitor;
		this.secuencia = secuencia;
		siguienteTransicion = secuencia[0];
	}
	public void run() {
		while(continuar) {
			for(int i=0;i<secuencia.length;i++) {
				//System.out.println("Hilo :"+nombre);
				siguienteTransicion = secuencia[i] - 1;
				monitor.dispararTransicion(siguienteTransicion);
			}
		}
	}
	//public int getSiguienteTransicion(){return siguienteTransicion;}
	public void set_Fin()
	{
		continuar = false;
	}
}
