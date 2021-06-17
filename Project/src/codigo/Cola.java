package codigo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Cola {
	//Campos
	private Set<Integer> transicionesEsperando;
	private int cantTransiciones;
	/**
	 * Constructor de la clase Cola que inicia un semaforo para cada elemento
	 * que se encuentre en la misma, de esta manera el hilo se queda esperando en la cola.
	 * @param cantTransiciones Cantidad de transiciones para armar vector Vc
	 */
	public Cola(int cantTransiciones) {
		this.cantTransiciones = cantTransiciones;
		//transicionesEsperando = new ArrayList<>();
		transicionesEsperando = new TreeSet<>();
	}
	/**
	 * Metodo que debe devolver el vector con los hilos que estan en cola 
	 * @return Vc matriz con los hilos que esperan 
	 */
	public Matriz quienesEstan(){
		Matriz Vc = new Matriz(cantTransiciones,1);
		for(Integer transicion : transicionesEsperando){
			Vc.setDato(transicion, 0, 1);
		}
		return Vc;
	}
	/**
	 * Metodo que saca el siguiente hilo a ejecutar
	 * @param transicion
	 */
	public void sacarDeCola(Integer transicion) {
		transicionesEsperando.remove(transicion);
	}
	/**
	 * Metodo que debe poner en cola el hilo en una ubicacion determinada para esa transicion
	 * @param transicion transicion que intento realizar el disparo
	 */
	 
	public boolean ponerEnCola(Integer transicion) {
		boolean agregado;
		agregado = transicionesEsperando.add(transicion);
		//agregado = transicionesEsperando.add(3);
		System.out.println(agregado);
		return agregado;
	}
	public String imprimirCola(){
		String esperando = "Esperando: ";
		for(Integer transicion : transicionesEsperando){
			esperando += " T"+(transicion+1);
		}
		return esperando;
	}

	public boolean isVacia(){
		return transicionesEsperando.isEmpty();
	}
}
