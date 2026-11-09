package telem;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

public class SerializacionTest {

    @Test
    public void reconstruyeClienteDesdeJson() {
        // Arrange
        Cliente original = new Cliente("Ana García", "12345678A");

        // Act
        Cliente reconstruido = FactoriaClientes.getCliente(original.toString());

        // Assert
        assertEquals("Ana García", reconstruido.getNombre());
        assertEquals("12345678A", reconstruido.getDni());
    }

    @Test
    public void reconstruyeTurismoConTodosSusAtributos() {
        // Arrange
        Turismo original = new Turismo("1234ABC", Categoria.ESPECIAL, 2022, 5, 450.5);

        // Act
        Vehiculo reconstruido = FactoriaVehiculos.getVehiculo(original.toString());

        // Assert
        assertTrue("Debe reconstruirse como Turismo", reconstruido instanceof Turismo);
        Turismo turismo = (Turismo) reconstruido;
        assertEquals("1234ABC", turismo.getMatricula());
        assertEquals(Categoria.ESPECIAL, turismo.getCategoria());
        assertEquals(2022, turismo.getAnnio());
        assertEquals(5, turismo.getPlazas());
        assertEquals(450.5, turismo.getCapacidadMaletero(), 0.0001);
        assertEquals("Turismo", turismo.getTipo());
    }

    @Test
    public void reconstruyeMotocicletaConTodosSusAtributos() {
        // Arrange
        Motocicleta original = new Motocicleta("5678DEF", Categoria.NORMAL, 2023, 600);

        // Act
        Vehiculo reconstruido = FactoriaVehiculos.getVehiculo(original.toString());

        // Assert
        assertTrue("Debe reconstruirse como Motocicleta", reconstruido instanceof Motocicleta);
        Motocicleta motocicleta = (Motocicleta) reconstruido;
        assertEquals("5678DEF", motocicleta.getMatricula());
        assertEquals(Categoria.NORMAL, motocicleta.getCategoria());
        assertEquals(2023, motocicleta.getAnnio());
        assertEquals(600, motocicleta.getCilindrada());
        assertEquals("Motocicleta", motocicleta.getTipo());
    }

    @Test
    public void reconstruyeVehiculoIndustrialConTodosSusAtributos() {
        // Arrange
        VehiculoIndustrial original = new VehiculoIndustrial(
                "9012GHI", Categoria.SUPERIOR, 2021, 12000, 4);

        // Act
        Vehiculo reconstruido = FactoriaVehiculos.getVehiculo(original.toString());

        // Assert
        assertTrue("Debe reconstruirse como VehiculoIndustrial",
                reconstruido instanceof VehiculoIndustrial);
        VehiculoIndustrial industrial = (VehiculoIndustrial) reconstruido;
        assertEquals("9012GHI", industrial.getMatricula());
        assertEquals(Categoria.SUPERIOR, industrial.getCategoria());
        assertEquals(2021, industrial.getAnnio());
        assertEquals(12000, industrial.getCapacidadCarga());
        assertEquals(4, industrial.getNumeroEjes());
        assertEquals("VehiculoIndustrial", industrial.getTipo());
    }

    @Test
    public void conservaDisponibilidadTrueAlReconstruirVehiculo() {
        // Arrange
        Turismo original = new Turismo("1111AAA", Categoria.NORMAL, 2020, 5, 400.0);
        original.setDisponible(true);

        // Act
        Vehiculo reconstruido = FactoriaVehiculos.getVehiculo(original.toString());

        // Assert
        assertTrue("Debe conservar la disponibilidad true", reconstruido.isDisponible());
    }

    @Test
    public void conservaDisponibilidadFalseAlReconstruirVehiculo() {
        // Arrange
        Turismo original = new Turismo("2222BBB", Categoria.NORMAL, 2020, 5, 400.0);
        original.setDisponible(false);

        // Act
        Vehiculo reconstruido = FactoriaVehiculos.getVehiculo(original.toString());

        // Assert
        assertFalse("Debe conservar la disponibilidad false", reconstruido.isDisponible());
    }

    @Test
    public void reconstruyeAlquilerConTodosSusAtributos() {
        // Arrange
        Vehiculo vehiculo = new Motocicleta("3333CCC", Categoria.ESPECIAL, 2024, 500);
        vehiculo.setDisponible(true);
        Cliente cliente = new Cliente("Luis Pérez", "87654321B");
        LocalDate inicio = LocalDate.of(2026, 4, 10);
        LocalDate fin = LocalDate.of(2026, 4, 12);
        Alquiler original = new Alquiler(vehiculo, cliente, inicio, fin);

        // Act
        Alquiler reconstruido = FactoriaAlquileres.getAlquiler(original.toString());

        // Assert
        assertTrue("El vehículo debe reconstruirse como Motocicleta",
                reconstruido.getVehiculo() instanceof Motocicleta);
        Motocicleta motocicleta = (Motocicleta) reconstruido.getVehiculo();
        assertEquals("3333CCC", motocicleta.getMatricula());
        assertEquals(Categoria.ESPECIAL, motocicleta.getCategoria());
        assertEquals(2024, motocicleta.getAnnio());
        assertEquals(500, motocicleta.getCilindrada());
        assertTrue(motocicleta.isDisponible());
        assertEquals("Luis Pérez", reconstruido.getCliente().getNombre());
        assertEquals("87654321B", reconstruido.getCliente().getDni());
        assertEquals(inicio, reconstruido.getFechaInicio());
        assertEquals(fin, reconstruido.getFechaFin());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaUnTipoDeVehiculoDesconocido() {
        // Arrange
        JSONObject json = new JSONObject(new Turismo(
                "4444DDD", Categoria.NORMAL, 2020, 5, 400.0).toString());
        json.put("tipo", "Barco");

        // Act
        FactoriaVehiculos.getVehiculo(json.toString());
    }

    @Test(expected = JSONException.class)
    public void rechazaUnVehiculoSinCampoTipo() {
        // Arrange
        JSONObject json = new JSONObject(new Turismo(
                "5555EEE", Categoria.NORMAL, 2020, 5, 400.0).toString());
        json.remove("tipo");

        // Act
        FactoriaVehiculos.getVehiculo(json.toString());
    }

    @Test(expected = JSONException.class)
    public void rechazaUnClienteSinDni() {
        // Arrange
        JSONObject json = new JSONObject(new Cliente("Marta", "00000000C").toString());
        json.remove("dni");

        // Act
        FactoriaClientes.getCliente(json.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaUnaCategoriaInexistente() {
        // Arrange
        JSONObject json = new JSONObject(new Turismo(
                "6666FFF", Categoria.NORMAL, 2020, 5, 400.0).toString());
        json.put("categoria", "PREMIUM");

        // Act
        FactoriaVehiculos.getVehiculo(json.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaUnAlquilerAlQueLeFaltaUnCampo() {
        // Arrange
        Alquiler original = new Alquiler(
                new Turismo("7777GGG", Categoria.NORMAL, 2020, 5, 400.0),
                new Cliente("Nora", "11111111D"),
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 5, 2));
        JSONObject json = new JSONObject(original.toString());
        json.remove("fechaFin");

        // Act
        FactoriaAlquileres.getAlquiler(json.toString());
    }

    @Test(expected = DateTimeParseException.class)
    public void rechazaUnaFechaMalFormadaEnAlquiler() {
        // Arrange
        Alquiler original = new Alquiler(
                new Turismo("8888HHH", Categoria.NORMAL, 2020, 5, 400.0),
                new Cliente("Olga", "22222222E"),
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 2));
        JSONObject json = new JSONObject(original.toString());
        json.put("fechaInicio", "01-06-2026");

        // Act
        FactoriaAlquileres.getAlquiler(json.toString());
    }

    @Test
    public void jsonDeAlquilerNoIncluyeTipoPropio() {
        // Arrange
        Alquiler alquiler = new Alquiler(
                new Turismo("9999III", Categoria.NORMAL, 2020, 5, 400.0),
                new Cliente("Pablo", "33333333F"),
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 2));

        // Act
        JSONObject json = new JSONObject(alquiler.toString());

        // Assert
        assertFalse("El alquiler no debe tener un campo tipo propio", json.has("tipo"));
        assertEquals("Turismo", json.getJSONObject("vehiculo").getString("tipo"));
    }

    @Test
    public void alquilerReconstruidoCalculaElMismoPrecio() {
        // Arrange
        Alquiler original = new Alquiler(
                new Turismo("ABCD123", Categoria.ESPECIAL, 2020, 5, 400.0),
                new Cliente("Sara", "44444444G"),
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 5));

        // Act
        Alquiler reconstruido = FactoriaAlquileres.getAlquiler(original.toString());

        // Assert
        assertEquals(original.calcularPrecioAlquiler(PoliticasDePrecios.Normal),
                reconstruido.calcularPrecioAlquiler(PoliticasDePrecios.Normal), 0.0001);
    }
}
