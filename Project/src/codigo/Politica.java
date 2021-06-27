package codigo;

import java.util.*;


public class Politica {
	//Campos

	private int[][] invariantes;
	private List<Integer> vecesPorInvariante;
	private List<Integer> disparos;

	public Politica(int[][] invariantes){
		this.invariantes = invariantes;
		vecesPorInvariante = new ArrayList<>();
		disparos = new ArrayList<Integer>(Collections.nCopies(10, 0));
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
	public int cual(Matriz m, Matriz estan , Matriz esperando) {
	   /*System.out.println("===============TOMAR UNA DECISION==================");
		estan.getTranspuesta().imprimirMatriz();
	    m.getTranspuesta().imprimirMatriz();*/
	    List<Integer> aux_1 =  new ArrayList<>();
	    List<Integer> aux_2 =  new ArrayList<>();
	    int tmp;
		int k=0;
	    
		for(int i = 0 ; i<10 ; i++)
	    {
	    	if(m.getDato(i, 0)==1)
	    	{
	    		aux_1.add(k,i);
	    		k++;
	    	}
	    }
		tmp = disparos.get(aux_1.get(0));
	    for(int i = 0 ; i<aux_1.size();i++)
	    {
	    	if( disparos.get(aux_1.get(i)) < tmp)
	     	{
	     			tmp = disparos.get(aux_1.get(i));
	     	}
	    }
	    
	    for(int j = 0; j<aux_1.size();j++)
	    {
	    	if(tmp !=  disparos.get(aux_1.get(j)))
	    	{
	    		aux_1.set(j, -1);
	    	}
	    }
	    
	    for (Integer candidato : aux_1) {
		       if (candidato != -1) {
		          aux_2.add(candidato);
		       }
		    }
	    
	    for (Integer deleteCandidate : aux_2) {
		       aux_1.remove(deleteCandidate);
		    }
	    int n = aux_2.size();
	    if(n == 1)
	    {
	    	return aux_2.get(0);
	    }
	    else
	    {
	    	int number = (int) (Math.random() * n);
	    	return aux_2.get(number);
	    }
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
		//VecesPorInvariante contiene la cantidad de veces que se dispararon los invariantes
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
		
		disparos.set(nTransicion , (disparos.get(nTransicion)+1));
	   /*
	    System.out.println("Tamanio de inv " + invariantes.length);
		System.out.println("0 - " + invariantes[0][0]);
		System.out.println("0 - " + invariantes[0][1]+"\n");
		System.out.println("1 - " + invariantes[1][0]);
		System.out.println("1 - " + invariantes[1][1]+"\n");
		System.out.println("2 - " + invariantes[2][0]);
		System.out.println("2 - " + invariantes[2][1]);
		System.out.println("2 - " + invariantes[2][2]);
		System.out.println("2 - " + invariantes[2][3]+"\n");
		
		System.out.println("Transiscion = " + (nTransicion+1)+" Disparo = " + disparos.get(nTransicion));
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		*/
		//imprimirVecesPorInvariante();
		
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
	public void imprimirDisparos(){
		System.out.println("=================================");
	   for(int i=0;i< disparos.size();i++)
		System.out.println("Transicion: "+(i+1)+ " disparos: "+disparos.get(i));
	   // System.out.println("Mayor " + Collections.max(disparos)) ;
	}
}
