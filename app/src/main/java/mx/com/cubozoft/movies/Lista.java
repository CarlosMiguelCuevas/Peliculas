package mx.com.cubozoft.movies;

import java.util.ArrayList;

/**
 * Created by CarlosMiguel on 18/01/2016.
 */
public class Lista {
    private int noPagina;
    private ArrayList<String> lista;

    public Lista(int np, ArrayList<String> ls) {
        setNoPagina(np);
        setLista(ls);
    }

    public int getNoPagina() {
        return noPagina;
    }

    public void setNoPagina(int noPagina) {
        this.noPagina = noPagina;
    }

    public ArrayList<String> getLista() {
        return lista;
    }

    public void setLista(ArrayList<String> lista) {
        this.lista = lista;
    }

    public ArrayList<String> listaFinal() {

        lista.add("MAS!!");
        return lista;
    }
}
