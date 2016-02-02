package mx.com.cubozoft.movies;

/**
 * Created by CarlosMiguel on 18/01/2016.
 */
public class Lista {
    private int noPagina;
    private String[] lista;

    public Lista(int np, String[] ls)
    {
        setNoPagina(np);
        setLista(ls);
    }


    public int getNoPagina() {
        return noPagina;
    }

    public void setNoPagina(int noPagina) {
        this.noPagina = noPagina;
    }

    public String[] getLista() {
        return lista;
    }

    public void setLista(String[] lista) {
        this.lista = lista;
    }

    public String[] listaFinal()
    {
        String[] newList = new String[this.lista.length+1];

        for(int i = 0; i< this.lista.length; i++)
        {
            newList[i] = this.lista[i];
            if(i==this.lista.length-1)
            {
                newList[i] = "MAS!!!";
            }
        }

        return newList;
    }
}
