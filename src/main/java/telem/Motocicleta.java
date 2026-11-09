package telem;

import org.json.JSONObject;

public class Motocicleta extends Vehiculo {

    int cilindrada;

    public Motocicleta(String matricula, Categoria categoria, int annio,
        int cilindrada
    ) {
        super(matricula, categoria, annio);
        this.cilindrada = cilindrada;
    }

    //Ejercicio 3: En Motocicleta nos quedamos con cilindrada, que es el atributo propio de Motocicleta.
    public Motocicleta(String jsonString) {
        super(jsonString);
        JSONObject json = new JSONObject(jsonString);
        this.cilindrada = json.getInt("cilindrada");
    }

    public String getTipo(){
    return "Motocicleta";
    }

    public int getCilindrada() {
        return cilindrada;
    }

    @Override
    public int getPrecioBase() {
    int precioBase = 0;
    switch (getCategoria()) {
    case NORMAL:
    precioBase = 50;
    break;
    case ESPECIAL:
    precioBase = 80;
    break;
    case SUPERIOR:
    precioBase = 100;
    break;
    }
    return precioBase;
    }

}
