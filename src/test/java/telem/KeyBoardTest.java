package telem;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class KeyBoardTest {

    @Test
    public void leeTiposPrimitivosYTextoDesdeLaEntrada() {
        InputStream entradaOriginal = System.in;
        String datos = "no-byte\n-12\n123\n2.5\n1.25\ntexto de prueba\nZ\n";
        try {
            System.setIn(new ByteArrayInputStream(datos.getBytes(StandardCharsets.UTF_8)));

            assertEquals(KeyBoard.BYTE_ERR, KeyBoard.readByte());
            assertEquals((short) -12, KeyBoard.readShort());
            assertEquals(123, KeyBoard.readInt());
            assertEquals(2.5, KeyBoard.readDouble(), 0.0);
            assertEquals(1.25, KeyBoard.readFloat(), 0.0);
            assertEquals("texto de prueba", KeyBoard.readString());
            assertEquals('Z', KeyBoard.readChar());
        } finally {
            System.setIn(entradaOriginal);
        }
    }
}
