package telem;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

public class AlquilerTest {

    private Alquiler crearAlquiler(LocalDate inicio, LocalDate fin) {
        Vehiculo vehiculo = new Turismo("TEST", Categoria.NORMAL, 2020, 5, 450.0);
        return new Alquiler(vehiculo, new Cliente("Cliente de prueba", "000"), inicio, fin);
    }

    @Test
    public void calculaPoliticasDePrecio() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        Alquiler alquiler = crearAlquiler(inicio, inicio.plusDays(2));

        assertEquals(150.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.Normal), 0.0);
        assertEquals(300.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.TemporadaAlta), 0.0);
        assertEquals(120.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.ClienteFrecuente), 0.0);
    }

    @Test
    public void aplicaDescuentoAdicionalDeOfertonCuandoSuperaSieteDias() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        Alquiler alquiler = crearAlquiler(inicio, inicio.plusDays(7));
        Alquiler alquilerDeSieteDias = crearAlquiler(inicio, inicio.plusDays(6));

        assertEquals(252.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.Oferton), 0.0);
        assertEquals(245.0, alquilerDeSieteDias.calcularPrecioAlquiler(PoliticasDePrecios.Oferton), 0.0);
    }

    @Test
    public void clienteFrecuenteRecibeDescuentoMayorDesdeElCuartoDia() {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        Alquiler alquiler = crearAlquiler(inicio, inicio.plusDays(3));

        assertEquals(150.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.ClienteFrecuente), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaFechasEnOrdenInvalido() {
        LocalDate inicio = LocalDate.of(2026, 1, 2);
        crearAlquiler(inicio, inicio.minusDays(1))
                .calcularPrecioAlquiler(PoliticasDePrecios.Normal);
    }
}
