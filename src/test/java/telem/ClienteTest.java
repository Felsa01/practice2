package telem;

import static org.junit.Assert.assertEquals;

import org.json.JSONObject;
import org.junit.Test;

public class ClienteTest {

    @Test
    public void devuelveNombreYDni() {
        Cliente cliente = new Cliente("Ada Lovelace", "12345678A");

        assertEquals("Ada Lovelace", cliente.getNombre());
        assertEquals("12345678A", cliente.getDni());
    }

    @Test
    public void serializaSusPropiedadesComoJson() {
        JSONObject json = new JSONObject(new Cliente("Ada Lovelace", "12345678A").toString());

        assertEquals("Ada Lovelace", json.getString("nombre"));
        assertEquals("12345678A", json.getString("dni"));
    }
}
