package telem;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.json.JSONObject;

/**
 * Modela un alquiler de un vehículo por parte de un cliente, con fechas de inicio y fin.
 * Almacena los alquileres realizados en un array estático y proporciona métodos para calcular el precio del alquiler.
 * Alquiler
 */
public class Alquiler {
    
    private Vehiculo vehiculo;
    private Cliente cliente;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private static Alquiler[] alquileres = new Alquiler[1000];

    public static final int PRECIO_BASE = 50;
    private static int contadorAlquileres = 0;

    public Alquiler(Vehiculo vehiculo, Cliente cliente, LocalDate fechaInicio, LocalDate fechaFin) {
        this.vehiculo = vehiculo;
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    
    public static Alquiler[] obtenerAlquileres() {
        return alquileres; 
    }

    public static void agregarAlquiler(Alquiler alquiler) {
        if (contadorAlquileres < alquileres.length) {
            alquileres[contadorAlquileres] = alquiler;
            contadorAlquileres++;
        } else {
            System.out.println("No se pueden agregar más alquileres. Capacidad máxima alcanzada.");
        }
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public String getTipo(){
    return "Turismo";
    }

    public int calcularPrecioAlquiler(LocalDate fechaInicio, LocalDate fechaFin) {
        int dias = calcularDias(fechaInicio, fechaFin);
        return Math.multiplyExact(dias, vehiculo.getPrecioBase());
    }

    public double calcularPrecioAlquiler(PoliticasDePrecios politica) {
        if (politica == null) {
            throw new IllegalArgumentException("La política de precios es obligatoria.");
        }

        int dias = calcularDias(fechaInicio, fechaFin);
        BigDecimal precioBase = BigDecimal.valueOf(vehiculo.getPrecioBase());
        BigDecimal total;

        switch (politica) {
            case Normal:
                total = precioBase.multiply(BigDecimal.valueOf(dias));
                break;
            case TemporadaAlta:
                total = precioBase.multiply(BigDecimal.valueOf(dias))
                        .multiply(BigDecimal.valueOf(2));
                break;
            case Oferton:
                total = precioBase.multiply(BigDecimal.valueOf(dias))
                        .multiply(new BigDecimal("0.70"));
                if (dias > 7) {
                    total = total.multiply(new BigDecimal("0.90"));
                }
                break;
            case ClienteFrecuente:
                int diasConDescuento20 = Math.min(dias, 3);
                int diasConDescuento40 = Math.max(0, dias - 3);
                total = precioBase.multiply(BigDecimal.valueOf(diasConDescuento20))
                        .multiply(new BigDecimal("0.80"))
                        .add(precioBase.multiply(BigDecimal.valueOf(diasConDescuento40))
                                .multiply(new BigDecimal("0.60")));
                break;
            default:
                throw new IllegalArgumentException("Política de precios no válida.");
        }

        return total.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private int calcularDias(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("El intervalo de fechas no es válido.");
        }

        return Math.toIntExact(ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1);
    }

    @Override 
    public String toString() {
        return new JSONObject(this).toString();
    }

    public static void main(String[] args) {

        // Crear instancias de Vehiculo y Cliente
        Vehiculo vehiculo1 = new Turismo("ABC123", Categoria.NORMAL, 2020, 5, 400.0);
        Cliente cliente1 = new Cliente("Juan Pérez", "123456789");
        LocalDate hoy = LocalDate.now();
        Alquiler alquiler1 = new Alquiler(vehiculo1, cliente1, hoy, hoy.plusDays(3)); // Alquiler de 3 días
        
        System.out.println(alquiler1.toString());
        System.out.println("--------------------------------------------------------");

        Vehiculo vehiculo2 = new VehiculoIndustrial("ABC123", Categoria.NORMAL, 2020, 1000, 3);
        Alquiler alquiler2 = new Alquiler(vehiculo2, cliente1, hoy, hoy.plusDays(3)); // Alquiler de 3 días

        System.out.println(alquiler2.toString());
        System.out.println("--------------------------------------------------------");
    }
}
