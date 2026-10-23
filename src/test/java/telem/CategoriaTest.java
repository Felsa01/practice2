package telem;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class CategoriaTest {

    @Test
    public void contieneLasCategoriasDisponibles() {
        assertArrayEquals(
                new Categoria[] {Categoria.NORMAL, Categoria.ESPECIAL, Categoria.SUPERIOR},
                Categoria.values());
    }
}
