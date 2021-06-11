package codigo;

import java.util.concurrent.Semaphore;
public class Cola {

	//Campos
	private Semaphore semaforos[];
	private int tam;
	/**
	 * Constructor de la clase Cola que inicia un semaforo para cada elemento 
	 * que se encuentre en la misma, de esta manera el hilo se queda esperando en la cola.
	 * @param tam tama�o de la cola
	 */
	public Cola(int tam) {
		this.tam=tam;
		semaforos = new Semaphore[tam];
		for(int i=0; i<tam ;i++) {
			semaforos[i] =new Semaphore(0,true);
		}
	}
	/**
	 * Metodo que debe devolver el vector con los hilos que estan en cola 
	 * @return Vc matriz con los hilos que esperan 
	 */
	public Matriz quienesEstan(){
		Matriz Vc = new Matriz(1,tam);
		for(int i=0; i<tam; i++){
			if(semaforos[i].hasQueuedThreads()) {
				Vc.setDato(0, i, 1);
			}
			else {
				Vc.setDato(0, i, 0);
			}
		}
		return Vc;
	}
	/**
	 * Metodo que saca el siguiente hilo a ejecutar
	 * @param Transicion 
	 */
	public void sacarDeCola(int Transicion) {
		if(semaforos[Transicion]!=null){
			semaforos[Transicion].release();
		}
	}
	/**
	 * Metodo que debe poner en cola el hilo en una ubicacion determinada para esa transicion
	 * @param Transicion transicion que intento realizar el disparo
	 */
	 
	public void ponerEnCola(int Transicion) {
		if(semaforos[Transicion]!=null) {
			try {
				semaforos[Transicion].acquire();
			}
			catch(InterruptedException e){
				System.out.println("Error al intentar poner en cola");
				e.printStackTrace();
			}
		}
	}
}
