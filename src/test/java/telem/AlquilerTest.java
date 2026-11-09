package telem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.json.JSONObject;
import org.junit.Test;

public class AlquilerTest {

    private static final LocalDate FECHA_INICIO = LocalDate.of(2026, 1, 1);

    private Alquiler crearAlquiler(LocalDate inicio, LocalDate fin) {
        Vehiculo vehiculo = new Turismo("TEST", Categoria.NORMAL, 2020, 5, 450.0);
        return new Alquiler(vehiculo, new Cliente("Cliente de prueba", "000"), inicio, fin);
    }

    @Test
    public void devuelveLosDatosConLosQueSeCreo() {
        Vehiculo vehiculo = new Turismo("TEST", Categoria.NORMAL, 2020, 5, 450.0);
        Cliente cliente = new Cliente("Cliente de prueba", "000");
        LocalDate fechaFin = FECHA_INICIO.plusDays(2);
        Alquiler alquiler = new Alquiler(vehiculo, cliente, FECHA_INICIO, fechaFin);

        assertSame(vehiculo, alquiler.getVehiculo());
        assertSame(cliente, alquiler.getCliente());
        assertEquals(FECHA_INICIO, alquiler.getFechaInicio());
        assertEquals(fechaFin, alquiler.getFechaFin());
        

        System.out.println(alquiler.toString());
    }

    @Test
    public void agregaElAlquilerALaColeccion() {
        Alquiler alquiler = crearAlquiler(FECHA_INICIO, FECHA_INICIO);
        Alquiler[] alquileresAntesDeAgregar = Alquiler.obtenerAlquileres();

        Alquiler.agregarAlquiler(alquiler);

        assertSame(alquileresAntesDeAgregar, Alquiler.obtenerAlquileres());
        boolean encontrado = false;
        for (Alquiler alquilerGuardado : Alquiler.obtenerAlquileres()) {
            if (alquilerGuardado == alquiler) {
                encontrado = true;
                break;
            }
        }
        assertTrue("El alquiler agregado debe aparecer en la colección", encontrado);
    }

    @Test
    public void calculaPrecioNormalParaElIntervaloIndicado() {
        Alquiler alquiler = crearAlquiler(FECHA_INICIO, FECHA_INICIO);

        assertEquals(50, alquiler.calcularPrecioAlquiler(FECHA_INICIO, FECHA_INICIO));
        assertEquals(150, alquiler.calcularPrecioAlquiler(
                FECHA_INICIO, FECHA_INICIO.plusDays(2)));
    }

    @Test(expected = ArithmeticException.class)
    public void rechazaUnPrecioQueDesbordaUnEntero() {
        Vehiculo vehiculo = new Vehiculo("TEST", Categoria.NORMAL, 2020) {
            @Override
            public String getTipo() {
                return "Prueba";
            }

            @Override
            public int getPrecioBase() {
                return Integer.MAX_VALUE;
            }
        };
        Alquiler alquiler = new Alquiler(
                vehiculo, new Cliente("Cliente de prueba", "000"),
                FECHA_INICIO, FECHA_INICIO.plusDays(1));

        alquiler.calcularPrecioAlquiler(FECHA_INICIO, FECHA_INICIO.plusDays(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaFechaInicialNulaEnElCalculoPorIntervalo() {
        crearAlquiler(FECHA_INICIO, FECHA_INICIO)
                .calcularPrecioAlquiler(null, FECHA_INICIO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaFechaFinalNulaEnElCalculoPorIntervalo() {
        crearAlquiler(FECHA_INICIO, FECHA_INICIO)
                .calcularPrecioAlquiler(FECHA_INICIO, null);
    }

    @Test(expected = ArithmeticException.class)
    public void rechazaUnIntervaloConDemasiadosDiasParaUnEntero() {
        crearAlquiler(LocalDate.MIN, LocalDate.MAX)
                .calcularPrecioAlquiler(LocalDate.MIN, LocalDate.MAX);
    }

    @Test
    public void calculaPoliticasDePrecio() {
        Alquiler alquiler = crearAlquiler(FECHA_INICIO, FECHA_INICIO.plusDays(2));

        assertEquals(150.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.Normal), 0.0);
        assertEquals(300.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.TemporadaAlta), 0.0);
        assertEquals(120.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.ClienteFrecuente), 0.0);
    }

    @Test
    public void aplicaDescuentoAdicionalDeOfertonCuandoSuperaSieteDias() {
        Alquiler alquiler = crearAlquiler(FECHA_INICIO, FECHA_INICIO.plusDays(7));
        Alquiler alquilerDeSieteDias = crearAlquiler(FECHA_INICIO, FECHA_INICIO.plusDays(6));

        assertEquals(252.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.Oferton), 0.0);
        assertEquals(245.0, alquilerDeSieteDias.calcularPrecioAlquiler(PoliticasDePrecios.Oferton), 0.0);
    }

    @Test
    public void clienteFrecuenteRecibeDescuentoMayorDesdeElCuartoDia() {
        Alquiler alquiler = crearAlquiler(FECHA_INICIO, FECHA_INICIO.plusDays(3));

        assertEquals(150.0, alquiler.calcularPrecioAlquiler(PoliticasDePrecios.ClienteFrecuente), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaUnaPoliticaNula() {
        crearAlquiler(FECHA_INICIO, FECHA_INICIO)
                .calcularPrecioAlquiler((PoliticasDePrecios) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaFechasEnOrdenInvalido() {
        crearAlquiler(FECHA_INICIO.plusDays(1), FECHA_INICIO)
                .calcularPrecioAlquiler(PoliticasDePrecios.Normal);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaFechasNulasEnLaPoliticaDePrecio() {
        crearAlquiler(null, FECHA_INICIO)
                .calcularPrecioAlquiler(PoliticasDePrecios.Normal);
    }

    @Test
    public void serializaElAlquilerComoJson() {
        Alquiler alquiler = crearAlquiler(FECHA_INICIO, FECHA_INICIO.plusDays(1));

        JSONObject json = new JSONObject(alquiler.toString());

        assertNotNull(json.getJSONObject("vehiculo"));
        assertNotNull(json.getJSONObject("cliente"));
        assertEquals("2026-01-01", json.getString("fechaInicio"));
        assertEquals("2026-01-02", json.getString("fechaFin"));
        assertEquals("Turismo", json.getString("tipo"));
    }
}
