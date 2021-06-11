package codigo;

import java.util.Random;


public class Politica {
	//Campos
	private static final int Transicion1 = 1;
	private static final int Transicion2 = 2;
	private static final int Plaza1=1;
	private static final int Plaza2=2;
	private int politica;
	public Politica(int politica){
		this.politica = politica;
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
		switch(politica) {
		case 1:
			m.setDato(Plaza1, Transicion1, 0);
			break;
		case 2:
			m.setDato(Plaza2, Transicion2, 0);
			break;
		}
	}

}
