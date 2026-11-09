package telem;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GestorAlquileresTest {

    private static final LocalDate FECHA_INICIO = LocalDate.of(2026, 1, 10);

    @Rule
    public TemporaryFolder carpetaTemporal = new TemporaryFolder();

    private Alquiler crearAlquiler(Vehiculo vehiculo, Cliente cliente) {
        return new Alquiler(vehiculo, cliente, FECHA_INICIO, FECHA_INICIO.plusDays(2));
    }

    private Cliente clienteInicial() {
        return new Cliente("Clara Videncia", "001");
    }

    @Test
    public void inicializaVehiculosDisponiblesDeCadaTipo() {
        GestorAlquileres gestor = new GestorAlquileres();

        assertEquals(3, gestor.obtenerVehiculos("Turismo").length);
        assertEquals(3, gestor.obtenerVehiculos("Motocicleta").length);
        assertEquals(3, gestor.obtenerVehiculos("VehiculoIndustrial").length);
        for (Vehiculo vehiculo : gestor.obtenerVehiculos("Turismo")) {
            assertTrue("Los vehículos de ejemplo deben estar disponibles",
                    vehiculo.isDisponible());
        }
        for (Vehiculo vehiculo : gestor.obtenerVehiculos("Motocicleta")) {
            assertTrue("Los vehículos de ejemplo deben estar disponibles",
                    vehiculo.isDisponible());
        }
        for (Vehiculo vehiculo : gestor.obtenerVehiculos("VehiculoIndustrial")) {
            assertTrue("Los vehículos de ejemplo deben estar disponibles",
                    vehiculo.isDisponible());
        }
    }

    @Test
    public void filtraVehiculosPorTipoYCategoria() {
        GestorAlquileres gestor = new GestorAlquileres();

        Vehiculo[] turismosEspeciales = gestor.obtenerVehiculos("Turismo", Categoria.ESPECIAL);
        Vehiculo[] motocicletasSuperiores =
                gestor.obtenerVehiculos("Motocicleta", Categoria.SUPERIOR);

        assertEquals(1, turismosEspeciales.length);
        assertEquals("Turismo", turismosEspeciales[0].getTipo());
        assertEquals(Categoria.ESPECIAL, turismosEspeciales[0].getCategoria());
        assertEquals(1, motocicletasSuperiores.length);
        assertEquals("Motocicleta", motocicletasSuperiores[0].getTipo());
        assertEquals(Categoria.SUPERIOR, motocicletasSuperiores[0].getCategoria());
        assertEquals(0, gestor.obtenerVehiculos("Desconocido").length);
        assertEquals(0,
                gestor.obtenerVehiculos("Desconocido", Categoria.SUPERIOR).length);
    }

    @Test
    public void agregaAlquilerYLoFiltraPorDniDelCliente() {
        GestorAlquileres gestor = new GestorAlquileres();
        Vehiculo vehiculo = gestor.obtenerVehiculos("Turismo")[0];
        Cliente cliente = clienteInicial();
        Cliente mismoDni = new Cliente("Otro nombre", cliente.getDni());
        Alquiler primero = crearAlquiler(vehiculo, cliente);
        Alquiler segundo = crearAlquiler(gestor.obtenerVehiculos("Motocicleta")[0], cliente);

        assertTrue(gestor.agregarAlquiler(primero));
        assertTrue(gestor.agregarAlquiler(segundo));

        Alquiler[] alquileres = gestor.obtenerAlquileres(mismoDni);
        assertEquals(2, alquileres.length);
        assertSame(primero, alquileres[0]);
        assertSame(segundo, alquileres[1]);
        assertEquals(0, gestor.obtenerAlquileres(new Cliente("Sin historial", "999")).length);
        assertEquals(0, gestor.obtenerAlquileres(null).length);
    }

    @Test
    public void devuelveSoloLosAlquileresActivosMasRecientesPorVehiculo() {
        GestorAlquileres gestor = new GestorAlquileres();
        Cliente cliente = clienteInicial();
        Cliente otroCliente = new Cliente("Aitor Menta", "002");
        Vehiculo mismoVehiculo = gestor.obtenerVehiculos("Turismo")[0];
        Vehiculo otroVehiculo = gestor.obtenerVehiculos("Motocicleta")[0];
        Alquiler historico = crearAlquiler(mismoVehiculo, cliente);
        Alquiler alquilerActualDeOtroCliente = crearAlquiler(mismoVehiculo, otroCliente);
        Alquiler alquilerActivo = crearAlquiler(otroVehiculo, cliente);
        mismoVehiculo.setDisponible(false);
        otroVehiculo.setDisponible(false);
        gestor.agregarAlquiler(historico);
        gestor.agregarAlquiler(alquilerActualDeOtroCliente);
        gestor.agregarAlquiler(alquilerActivo);

        Alquiler[] activos = gestor.obtenerAlquileresActivos(cliente);

        assertEquals(1, activos.length);
        assertSame(alquilerActivo, activos[0]);
        assertEquals(0, gestor.obtenerAlquileresActivos(null).length);
        otroVehiculo.setDisponible(true);
        assertEquals(0, gestor.obtenerAlquileresActivos(cliente).length);
    }

    @Test
    public void entregarVehiculoLoMarcaComoDisponible() {
        GestorAlquileres gestor = new GestorAlquileres();
        Vehiculo vehiculo = gestor.obtenerVehiculos("Turismo")[0];
        vehiculo.setDisponible(false);
        Alquiler alquiler = crearAlquiler(vehiculo, clienteInicial());

        gestor.entregarVehiculo(alquiler);

        assertTrue(vehiculo.isDisponible());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noPermiteEntregarUnAlquilerNulo() {
        new GestorAlquileres().entregarVehiculo(null);
    }

    @Test
    public void reconoceClienteFrecuenteAlAlcanzarElUmbral() {
        GestorAlquileres gestor = new GestorAlquileres();
        Cliente cliente = clienteInicial();

        assertFalse(gestor.esClienteFrecuente(cliente, 3));
        for (int i = 0; i < 3; i++) {
            assertTrue(gestor.agregarAlquiler(
                    crearAlquiler(gestor.obtenerVehiculos("Turismo")[i], cliente)));
        }

        assertTrue(gestor.esClienteFrecuente(cliente, 3));
        assertFalse(gestor.esClienteFrecuente(new Cliente("Otro", "999"), 1));
    }

    @Test
    public void rechazaAlquileresAlLlenarLaCapacidadDelGestor() {
        GestorAlquileres gestor = new GestorAlquileres();
        Alquiler alquiler = crearAlquiler(
                gestor.obtenerVehiculos("Turismo")[0], clienteInicial());

        for (int i = 0; i < 1000; i++) {
            assertTrue("Debe aceptar alquileres hasta llegar a la capacidad",
                    gestor.agregarAlquiler(alquiler));
        }

        assertFalse("No debe aceptar alquileres por encima de la capacidad",
                gestor.agregarAlquiler(alquiler));
        assertEquals(1000, gestor.obtenerAlquileres(clienteInicial()).length);
    }

    @Test
    public void exportaClientesVehiculosYAlquileresEnJson() {
        GestorAlquileres gestor = new GestorAlquileres();

        JSONObject json = new JSONObject(gestor.exportarJSON());

        assertEquals(6, json.getJSONArray("clientes").length());
        assertEquals(9, json.getJSONArray("vehiculos").length());
        assertEquals(0, json.getJSONArray("alquileres").length());
        assertFalse(json.has("tipo"));
    }

    @Test
    public void importaElEstadoYEnlazaAlquilerConVehiculoImportado() {
        GestorAlquileres original = new GestorAlquileres();
        Vehiculo vehiculo = original.obtenerVehiculos("Turismo")[0];
        vehiculo.setDisponible(false);
        original.agregarAlquiler(crearAlquiler(vehiculo, clienteInicial()));
        GestorAlquileres reconstruido = new GestorAlquileres();

        reconstruido.importarJSON(original.exportarJSON());

        assertEquals(1, reconstruido.obtenerAlquileres(clienteInicial()).length);
        Alquiler alquilerReconstruido =
                reconstruido.obtenerAlquileres(clienteInicial())[0];
        assertEquals(vehiculo.getMatricula(),
                alquilerReconstruido.getVehiculo().getMatricula());
        assertFalse(alquilerReconstruido.getVehiculo().isDisponible());
        Vehiculo[] turismosReconstruidos = reconstruido.obtenerVehiculos("Turismo");
        Vehiculo vehiculoReconstruido = null;
        for (Vehiculo candidato : turismosReconstruidos) {
            if (candidato.getMatricula().equals(vehiculo.getMatricula())) {
                vehiculoReconstruido = candidato;
                break;
            }
        }
        assertNotNull(vehiculoReconstruido);
        assertSame(vehiculoReconstruido, alquilerReconstruido.getVehiculo());

        reconstruido.entregarVehiculo(alquilerReconstruido);
        assertTrue(vehiculoReconstruido.isDisponible());
    }

    @Test
    public void importarReemplazaElEstadoAnteriorDelGestor() {
        GestorAlquileres original = new GestorAlquileres();
        original.agregarAlquiler(crearAlquiler(
                original.obtenerVehiculos("Motocicleta")[0], clienteInicial()));
        GestorAlquileres destino = new GestorAlquileres();
        destino.agregarAlquiler(crearAlquiler(
                destino.obtenerVehiculos("Turismo")[0], clienteInicial()));

        destino.importarJSON(original.exportarJSON());

        assertEquals(1, destino.obtenerAlquileres(clienteInicial()).length);
        assertEquals("Motocicleta",
                destino.obtenerAlquileres(clienteInicial())[0].getVehiculo().getTipo());
    }

    @Test
    public void rechazaImportacionIncoherenteSinModificarEstadoActual() {
        GestorAlquileres destino = new GestorAlquileres();
        Vehiculo vehiculoDestino = destino.obtenerVehiculos("Turismo")[0];
        destino.agregarAlquiler(crearAlquiler(vehiculoDestino, clienteInicial()));
        String estadoAnterior = destino.exportarJSON();

        GestorAlquileres origen = new GestorAlquileres();
        origen.agregarAlquiler(crearAlquiler(
                origen.obtenerVehiculos("Turismo")[0], clienteInicial()));
        JSONObject importacionInvalida = new JSONObject(origen.exportarJSON());
        JSONArray clientes = importacionInvalida.getJSONArray("clientes");
        for (int i = 0; i < clientes.length(); i++) {
            if (clientes.getJSONObject(i).getString("dni").equals("001")) {
                clientes.remove(i);
                break;
            }
        }

        try {
            destino.importarJSON(importacionInvalida.toString());
            fail("La importación con un cliente referenciado inexistente debe fallar");
        } catch (IllegalArgumentException esperada) {
            assertEquals(estadoAnterior, destino.exportarJSON());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaClientesDuplicadosAlImportar() {
        JSONObject json = new JSONObject(new GestorAlquileres().exportarJSON());
        JSONArray clientes = json.getJSONArray("clientes");
        clientes.put(new JSONObject(clientes.getJSONObject(0).toString()));

        new GestorAlquileres().importarJSON(json.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaVehiculosDuplicadosAlImportar() {
        JSONObject json = new JSONObject(new GestorAlquileres().exportarJSON());
        JSONArray vehiculos = json.getJSONArray("vehiculos");
        vehiculos.put(new JSONObject(vehiculos.getJSONObject(0).toString()));

        new GestorAlquileres().importarJSON(json.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void rechazaDatosQueSuperanLaCapacidadAlImportar() {
        JSONObject json = new JSONObject();
        JSONArray clientes = new JSONArray();
        for (int i = 0; i < 11; i++) {
            clientes.put(new JSONObject()
                    .put("nombre", "Cliente " + i)
                    .put("dni", "DNI-" + i));
        }
        json.put("clientes", clientes);
        json.put("vehiculos", new JSONArray());
        json.put("alquileres", new JSONArray());

        new GestorAlquileres().importarJSON(json.toString());
    }

    @Test(expected = JSONException.class)
    public void rechazaJsonSinLosArraysNecesariosAlImportar() {
        new GestorAlquileres().importarJSON("{}");
    }

    @Test
    public void guardaYCargaElEstadoDesdeUnFicheroUtf8() throws IOException {
        GestorAlquileres original = new GestorAlquileres();
        original.agregarAlquiler(crearAlquiler(
                original.obtenerVehiculos("VehiculoIndustrial")[0], clienteInicial()));
        Path ruta = carpetaTemporal.newFile("alquileres.json").toPath();
        GestorAlquileres recuperado = new GestorAlquileres();

        original.guardarEnFichero(ruta.toString());
        recuperado.cargarDesdeFichero(ruta.toString());

        assertTrue(Files.size(ruta) > 0);
        assertEquals(1, recuperado.obtenerAlquileres(clienteInicial()).length);
        assertEquals("VehiculoIndustrial",
                recuperado.obtenerAlquileres(clienteInicial())[0]
                        .getVehiculo().getTipo());
    }

    @Test(expected = IOException.class)
    public void fallaAlCargarUnFicheroInexistente() throws IOException {
        Path ruta = carpetaTemporal.getRoot().toPath().resolve("no-existe.json");

        new GestorAlquileres().cargarDesdeFichero(ruta.toString());
    }
}
