package telem;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class PoliticasDePreciosTest {

    @Test
    public void contieneLasPoliticasDisponibles() {
        assertArrayEquals(new PoliticasDePrecios[] {
                PoliticasDePrecios.Normal,
                PoliticasDePrecios.TemporadaAlta,
                PoliticasDePrecios.ClienteFrecuente,
                PoliticasDePrecios.Oferton
        }, PoliticasDePrecios.values());
    }
}
