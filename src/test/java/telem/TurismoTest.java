package telem;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

public class TurismoTest {

    @Test
    public void devuelveSusCaracteristicasYTipo() {
        Turismo turismo = new Turismo("T-123", Categoria.NORMAL, 2020, 5, 450.0);

        assertEquals(5, turismo.getPlazas());
        assertEquals(450.0, turismo.getCapacidadMaletero(), 0.0);
        assertEquals("Turismo", turismo.getTipo());
    }

    @Test
    public void calculaPrecioBaseSegunCategoria() {
        assertEquals(50, crearTurismo(Categoria.NORMAL).getPrecioBase());
        assertEquals(80, crearTurismo(Categoria.ESPECIAL).getPrecioBase());
        assertEquals(100, crearTurismo(Categoria.SUPERIOR).getPrecioBase());
    }

    @Test
    public void serializaLasPropiedadesDelVehiculo() {
        JSONObject json = new JSONObject(
                crearTurismo(Categoria.NORMAL).toString());

        assertEquals("T-123", json.getString("matricula"));
        assertEquals("NORMAL", json.getString("categoria"));
        assertEquals(5, json.getInt("plazas"));
        assertEquals(450.0, json.getDouble("capacidadMaletero"), 0.0);
        assertEquals("Turismo", json.getString("tipo"));
    }

    private Turismo crearTurismo(Categoria categoria) {
        return new Turismo("T-123", categoria, 2020, 5, 450.0);
    }
}
