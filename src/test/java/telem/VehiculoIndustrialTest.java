package telem;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

public class VehiculoIndustrialTest {

    @Test
    public void devuelveCaracteristicasYTipo() {
        VehiculoIndustrial vehiculo = new VehiculoIndustrial(
                "I-123", Categoria.NORMAL, 2020, 1400, 2);

        assertEquals(1400, vehiculo.getCapacidadCarga());
        assertEquals(2, vehiculo.getNumeroEjes());
        assertEquals("VehiculoIndustrial", vehiculo.getTipo());
    }

    @Test
    public void calculaPrecioBaseSegunCategoria() {
        assertEquals(50, crearVehiculo(Categoria.NORMAL).getPrecioBase());
        assertEquals(80, crearVehiculo(Categoria.ESPECIAL).getPrecioBase());
        assertEquals(100, crearVehiculo(Categoria.SUPERIOR).getPrecioBase());
    }

    @Test
    public void serializaLasPropiedadesDelVehiculo() {
        JSONObject json = new JSONObject(crearVehiculo(Categoria.NORMAL).toString());

        assertEquals("I-123", json.getString("matricula"));
        assertEquals("NORMAL", json.getString("categoria"));
        assertEquals(1400, json.getInt("capacidadCarga"));
        assertEquals(2, json.getInt("numeroEjes"));
        assertEquals("VehiculoIndustrial", json.getString("tipo"));
    }

    private VehiculoIndustrial crearVehiculo(Categoria categoria) {
        return new VehiculoIndustrial("I-123", categoria, 2020, 1400, 2);
    }
}
