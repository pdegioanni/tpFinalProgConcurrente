import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

public class Cola {
	//Campos
	private final Set<Integer> transicionesEsperando;
	private final int cantTransiciones;
	private final Semaphore [] semaforos;
	
	/**
	 * Constructor de la clase Cola que inicia un semaforo para cada transición de la Red de Petri.
	 * @param cantTransiciones Cantidad de transiciones para armar array de semáforos
	 */
	public Cola(int cantTransiciones) {
		this.cantTransiciones = cantTransiciones;
		transicionesEsperando = new TreeSet<>();

		semaforos = new Semaphore[cantTransiciones]; 
		for (int i = 0; i < cantTransiciones; i++) {
			semaforos[i] = new Semaphore(0); //Se inicializa un semáforo binario para cada transición
        }
     }

	/**
	 * Metodo que debe devolver el vector con los hilos que estan en cola
	 * @return Vc matriz con los hilos que esperan en la cola
	 */
	public Matriz quienesEstan(){
		Matriz Vc = new Matriz(cantTransiciones,1);
		for(Integer transicion : transicionesEsperando){
			Vc.setDato(transicion, 0, 1);
		}
		return Vc;
	}
	
	/**
	 * Metodo que encola el hilo según la transición que está intentando disparar
	 * @param transicion transicion que intenta realizar el disparo
	 */
	public boolean ponerEnCola(Integer transicion) {
		boolean agregado = false;
		if(semaforos[transicion] != null) {
			try {
				agregado = transicionesEsperando.add(transicion);
				semaforos[transicion].acquire(); //se queda esperando
			}
			catch(InterruptedException e){
				System.out.println("Error al intentar poner transicion en cola");
				e.printStackTrace();
			}
		}
		return agregado;
	}

	/**
	 * Metodo que desencola el hilo según la transición indicada
	 * @param transicion transicion para la cual se quiere liberar el semáforo
	 */
	public void sacarDeCola(Integer transicion) {
		transicionesEsperando.remove(transicion);
		if(semaforos[transicion]!=null){
			semaforos[transicion].release();
		}
	}

	/**
	 * Metodo que imprime la cola de las transiciones esperando
	 *
	 */
	public String imprimirCola(){
		String esperando = "Esperando: ";
		
		for(Integer transicion : transicionesEsperando){
			esperando += " T"+(transicion+1);
		}
		return esperando;
	}

	/**
	 * @return la cantidad de transiciones esperando
	 */
	public int getCantTransicionesEsperando(){
		return transicionesEsperando.size();
	}

	/**
	 * @return 0 si transiciones esperando, 1 si la cola de espera está vacía
	 */
	public boolean isVacia(){
		return transicionesEsperando.isEmpty();
	}

}

