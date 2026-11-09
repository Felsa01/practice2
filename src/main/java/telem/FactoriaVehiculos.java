package telem;

import org.json.JSONObject;
//Ejercicio 3: Añadimos las factorías de clientes y vehículos para poder reconstruir los objetos que el alquiler contiene.

/**
 * Método Factoría para deserializar vehículos: a partir del campo "tipo" del
 * JSON decide qué subclase de Vehiculo hay que construir.
 */
public class FactoriaVehiculos {

    public static Vehiculo getVehiculo(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        String clase = json.getString("tipo");
        switch (clase) {
            case "Turismo":
                return new Turismo(jsonString);
            case "Motocicleta":
                return new Motocicleta(jsonString);
            case "VehiculoIndustrial":
                return new VehiculoIndustrial(jsonString);
            default:
                throw new IllegalArgumentException(
                        "Tipo de vehículo desconocido: " + clase);
        }
    }

    public static void main(String[] args) {
        Vehiculo v1 = new Turismo("1234ABC", Categoria.NORMAL, 2020, 5, 450.0);
        String serializado = v1.toString();
        Vehiculo v2 = FactoriaVehiculos.getVehiculo(serializado);
        System.out.println("Original:     " + serializado);
        System.out.println("Deserializado: " + v2);
        System.out.println("Misma clase: " + (v1.getClass() == v2.getClass()));
    }
}
