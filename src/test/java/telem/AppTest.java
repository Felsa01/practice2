package telem;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class AppTest {

    @Test
    public void imprimeElMensajeDeBienvenida() {
        PrintStream salidaOriginal = System.out;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(salida, true, StandardCharsets.UTF_8));

            App.main(new String[0]);

            assertEquals("Hello World!" + System.lineSeparator(),
                    salida.toString(StandardCharsets.UTF_8));
        } finally {
            System.setOut(salidaOriginal);
        }
    }
}
