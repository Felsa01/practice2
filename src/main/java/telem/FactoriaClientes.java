package telem;
//Ejercicio 3: Añadimos las factorías de clientes y vehículos para poder reconstruir los objetos que el alquiler contiene.

/**
 * Método Factoría para deserializar clientes a partir de su representación JSON.
 * Hoy solo existe un tipo de cliente, pero la factoría deja el punto de
 * creación centralizado por si en el futuro aparecen subtipos.
 */
public class FactoriaClientes {

    public static Cliente getCliente(String jsonString) {
        return new Cliente(jsonString);
    }

    public static void main(String[] args) {
        Cliente c1 = new Cliente("Juan Pérez", "12345678A");
        String serializado = c1.toString();
        Cliente c2 = FactoriaClientes.getCliente(serializado);
        System.out.println("Original:      " + serializado);
        System.out.println("Deserializado: " + c2);
    }
}
