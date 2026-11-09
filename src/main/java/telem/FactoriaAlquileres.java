package telem;

import org.json.JSONObject;
//Ejercicio 3: Añadimos las factorías de clientes y vehículos para poder reconstruir los objetos que el alquiler contiene.

/**
 * Método Factoría para deserializar alquileres. Valida que el JSON tenga la
 * estructura esperada y delega en las factorías de cliente y vehículo para
 * reconstruir los objetos que el alquiler contiene.
 */
public class FactoriaAlquileres {

    public static Alquiler getAlquiler(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        for (String campo : new String[] {"vehiculo", "cliente", "fechaInicio", "fechaFin"}) {
            if (!json.has(campo)) {
                throw new IllegalArgumentException(
                        "JSON de alquiler incompleto, falta el campo: " + campo);
            }
        }
        return new Alquiler(jsonString);
    }

    public static void main(String[] args) {
        Vehiculo v = new Motocicleta("MOTO001", Categoria.ESPECIAL, 2024, 600);
        Cliente c = new Cliente("Ana Lítica", "005");
        java.time.LocalDate hoy = java.time.LocalDate.now();
        Alquiler a1 = new Alquiler(v, c, hoy, hoy.plusDays(4));
        String serializado = a1.toString();
        Alquiler a2 = FactoriaAlquileres.getAlquiler(serializado);
        System.out.println("Original:      " + serializado);
        System.out.println("Deserializado: " + a2);
    }
}
