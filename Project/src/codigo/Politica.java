package codigo;

import java.util.Random;


public class Politica {
	//Campos
	private int politica;
	
	private Matriz disparadas ;
	public Politica(RDP red){
		disparadas = new Matriz(red.numero_t(), 1);
	}
	/**
	 * Metodo que devuleve una transicion
	 * @param m matriz que contiene el resultado de Vc and Vs
	 * @return
	 */
	public int cual(Matriz m) {
		Random rand = new Random();
		int maximo = m.getNumColumnas();
		int aleatorio=0,siguiente=0;
		while(siguiente!=1) {
			aleatorio = rand.nextInt(maximo);
			siguiente=m.getDato(0, aleatorio);
		}
		return aleatorio;
	}
	/**
	 * Metodo que modifica la red para remover las prioridades
	 * @param m
	 * @return
	 */
	public void quitarPrioridad(Matriz m) {
		
	}
    public void set_D(int Transicion) {
    	
    }
}
