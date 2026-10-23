package telem;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MotocicletaTest {

    @Test
    public void devuelveCilindradaYTipo() {
        Motocicleta motocicleta = new Motocicleta("M-123", Categoria.NORMAL, 2021, 500);

        assertEquals(500, motocicleta.getCilindrada());
        assertEquals("Motocicleta", motocicleta.getTipo());
    }

    @Test
    public void calculaPrecioBaseSegunCategoria() {
        assertEquals(50, crearMotocicleta(Categoria.NORMAL).getPrecioBase());
        assertEquals(80, crearMotocicleta(Categoria.ESPECIAL).getPrecioBase());
        assertEquals(100, crearMotocicleta(Categoria.SUPERIOR).getPrecioBase());
    }

    private Motocicleta crearMotocicleta(Categoria categoria) {
        return new Motocicleta("M-123", categoria, 2021, 500);
    }
}
