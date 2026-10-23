package telem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

public class GestorAlquileresTest {

    @Test
    public void filtraVehiculosPorTipo() {
        GestorAlquileres gestor = new GestorAlquileres();

        Vehiculo[] turismos = gestor.obtenerVehiculos("Turismo");
        Vehiculo[] motocicletas = gestor.obtenerVehiculos("Motocicleta");
        Vehiculo[] industriales = gestor.obtenerVehiculos("VehiculoIndustrial");

        assertEquals(3, turismos.length);
        assertEquals(3, motocicletas.length);
        assertEquals(3, industriales.length);
        for (Vehiculo vehiculo : turismos) {
            assertEquals("Turismo", vehiculo.getTipo());
        }
        for (Vehiculo vehiculo : motocicletas) {
            assertEquals("Motocicleta", vehiculo.getTipo());
        }
        for (Vehiculo vehiculo : industriales) {
            assertEquals("VehiculoIndustrial", vehiculo.getTipo());
        }
    }

    @Test
    public void devuelveListaVaciaCuandoNoHayVehiculosDelTipo() {
        Vehiculo[] resultado = new GestorAlquileres().obtenerVehiculos("Desconocido");

        assertEquals(0, resultado.length);
    }

    @Test
    public void conservaLosAlquileresAgregadosEnSuLista() {
        GestorAlquileres gestor = new GestorAlquileres();
        Vehiculo vehiculo = gestor.obtenerVehiculos("Turismo")[0];
        Cliente cliente = new Cliente("Cliente de prueba", "000");
        Alquiler alquiler = new Alquiler(
                vehiculo, cliente, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 2));
        Alquiler[] alquileresAntesDeAgregar = gestor.obtenerAlquileres();

        gestor.agregarAlquiler(alquiler);

        assertSame(alquileresAntesDeAgregar, gestor.obtenerAlquileres());
        boolean encontrado = false;
        for (Alquiler alquilerGuardado : gestor.obtenerAlquileres()) {
            if (alquilerGuardado == alquiler) {
                encontrado = true;
                break;
            }
        }
        assertTrue("El alquiler debe aparecer en la lista del gestor", encontrado);
    }
}
