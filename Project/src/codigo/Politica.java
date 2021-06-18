package codigo;

import java.util.*;


public class Politica {
	//Campos

	private int[][] invariantes;
	private List<Integer> vecesPorInvariante;

	public Politica(int[][] invariantes){
		this.invariantes = invariantes;
		vecesPorInvariante = new ArrayList<>();
		for(int i = 0; i < invariantes.length; i++){
			vecesPorInvariante.add(0);
		}
	}
	/**
	 * Metodo que devuleve una transicion
	 * @param m matriz que contiene el resultado de Vc and Vs
	 * @return
	 */
/*	public int cual(Matriz m) {
		List<Integer> transicionesHabilitadas = new ArrayList<>();
		TreeSet<Integer> invariantesPosibles = new TreeSet<Integer>() ;
		for(int i = 0; i<m.getNumFilas(); i++){
			if(m.getDato(i, 0) == 1) {
				transicionesHabilitadas.add(i);
				if(perteneceAInvariante(i)>=0) invariantesPosibles.add(i);
			}

		}
		if (transicionesHabilitadas.size() == 1) return transicionesHabilitadas.get(0); //Solo hay una transicion, no hay necesidad de decidir
		else{
			//System.out.println("Llamada a comparar invariantes con " + invariantesPosibles.size());
			int invarianteElegido = compararInvariantes(invariantesPosibles);
			int transicion = transicionesHabilitadas.get(0);
			for (int inv:transicionesHabilitadas){
				if(perteneceAInvariante(inv) == invarianteElegido) transicion = inv;
			}
			return transicion;
		}

	}*/
	public int cual(Matriz m) {
		List<Integer> transicionesHabilitadas = new ArrayList<>();
		for(int i = 0; i<m.getNumFilas(); i++){
			if(m.getDato(i, 0) == 1) {
				transicionesHabilitadas.add(i); //Saca las transiciones habilitadas a partir de la matriz m
			}
		}
		//if (transicionesHabilitadas.size() == 1) return transicionesHabilitadas.get(0); //Solo hay una transicion, no hay necesidad de decidir
		//else{
			int transicion = transicionesHabilitadas.get(0);
			int invarianteMenosRepetido = vecesPorInvariante.get(1);
			for(int v=0; v< vecesPorInvariante.size(); v++){
				if (vecesPorInvariante.get(v) < invarianteMenosRepetido) invarianteMenosRepetido = vecesPorInvariante.get(v);
			}
			System.out.println("Invariante menos repetido " + invarianteMenosRepetido);
			for (int t :transicionesHabilitadas) {
				System.out.println("Politica T" + (t+1));
				if (perteneceAInvariante(t) == invarianteMenosRepetido) return t;
			}
			return transicion;
		//}

	}

	public void registrarDisparo(int nTransicion){ //No considera a T1 ni a T6
		/*for(int i = 0; i< invariantes.length; i++){
			for (int j = 0; j < invariantes[i].length; j++){
				if(invariantes[i][j] == nTransicion) {
					vecesPorInvariante.set(i,vecesPorInvariante.get(i) +1);
				}
			}
		}*/
		//Hardcodeado - ver de hacer alguna forma mas general, tipo lo de arriba pero que funcione bien
		if(nTransicion == 3) {
			System.out.println("Se disparo primer invariante");
			vecesPorInvariante.set(0, (vecesPorInvariante.get(0)+1));
		}
		if(nTransicion == 4) {
			System.out.println("Se disparo segundo invariante");
			vecesPorInvariante.set(1, (vecesPorInvariante.get(1)+1));
		}
		if(nTransicion == 9) {
			System.out.println("Se disparo tercer invariante");
			vecesPorInvariante.set(2, (vecesPorInvariante.get(2)+1));
		}
	}

	public int perteneceAInvariante(int transicion){
		for(int i = 0; i < invariantes.length; i++){
			for (int j = 0; j < invariantes[i].length; j++){
				if(invariantes[i][j] == transicion) {
					return i; //Devuelve el numero de invariante al que pertenece la transicion
				}
			}
		}
		return -1; //Si la transicion no pertenece a ningun invariante
	}

	private int compararInvariantes(TreeSet<Integer> invariantesPosibles){
		int disparar = vecesPorInvariante.get(0);
		if(invariantesPosibles.size() == 1) disparar = vecesPorInvariante.get(invariantesPosibles.first());
		for(int i = 0; i<invariantesPosibles.size(); i++){
			if(vecesPorInvariante.get(i) < disparar) disparar = vecesPorInvariante.get(i);
		}
		return disparar;
	}

	public void imprimirVecesPorInvariante(){
		int i = 1;
		for(int veces: vecesPorInvariante){
			System.out.println("Invariante " + i + ": " + veces + " veces" );
			i++;
		}

	}
  
}
