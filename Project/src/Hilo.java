public class Hilo implements Runnable {
	private final Monitor monitor;
	private final int[] secuencia;
	private int siguienteTransicion;
	private boolean continuar = true;

	/**
	 * Constructor de la clase Hilo
	 * @param monitor Monitor asociado
	 * @param secuencia Secuencia de transiciones que se va a disparar en el monitor asociado
	 */
	public Hilo(Monitor monitor,int[] secuencia) {
		
		this.monitor = monitor;
		this.secuencia = secuencia;
		siguienteTransicion = secuencia[0];
	}

	/**
	 * Ejecuta en bucle la secuencia de transiciones asignada
	 *
	 * */
	public void run() {
		while(continuar) {
			for (int j : secuencia) {
				siguienteTransicion = j - 1;
				monitor.dispararTransicion(siguienteTransicion);
			}
		}
	}

	/**
	 * Cambia el valor de la variable que va a interrumpir el disparo de transiciones
	 * */
	public void setFin()
	{
		continuar = false;
	}
}
