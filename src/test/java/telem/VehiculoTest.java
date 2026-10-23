package telem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VehiculoTest {

    @Test
    public void exponeSusPropiedadesYPermiteCambiarDisponibilidad() {
        Vehiculo vehiculo = new Motocicleta("M-123", Categoria.ESPECIAL, 2024, 500);

        assertEquals("M-123", vehiculo.getMatricula());
        assertEquals(Categoria.ESPECIAL, vehiculo.getCategoria());
        assertEquals(2024, vehiculo.getAnnio());
        assertFalse(vehiculo.isDisponible());

        vehiculo.setDisponible(true);
        assertTrue(vehiculo.isDisponible());

        vehiculo.setDisponible(false);
        assertFalse(vehiculo.isDisponible());
    }
}
