package telem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

public class GestorAlquileres {

    private static final String FICHERO_POR_DEFECTO = "datos_alquileres.json";
    private static final int UMBRAL_CLIENTE_FRECUENTE = 3;

    private int contadorAlquileres = 0;
    private Alquiler[] alquileres = new Alquiler[1000];

    // Para simplificar, vamos a definir un array de clientes y 
    // otro de vehículos dentro del gestor.
    // En una aplicación real estarían en una base de datos.
    private Cliente[] clientes = new Cliente[10];
    private Vehiculo[] vehiculos = new Vehiculo[100];


    public GestorAlquileres() {
        inicializarClientes();
        inicializarVehiculos();
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // INICIALIZACIÓN DE CLIENTES Y VEHÍCULOS DE EJEMPLO

    // Definimos métodos privados para inicializar clientes y vehículos de ejemplo
    private void inicializarClientes() {
        clientes[0] = new Cliente("Clara Videncia", "001");
        clientes[1] = new Cliente("Aitor Menta", "002");
        clientes[2] = new Cliente("Lola Mento", "003");
        clientes[3] = new Cliente("Armando Guerra", "004");
        clientes[4] = new Cliente("Ana Lítica", "005");
        clientes[5] = new Cliente("Carmelo Cotón", "006");
    }

    private void inicializarVehiculos() {
        vehiculos[0] = new Turismo("TNA2020", Categoria.NORMAL, 2020, 5, 450.0);
        vehiculos[1] = new Motocicleta("MNA2021", Categoria.NORMAL, 2021, 200);
        vehiculos[2] = new VehiculoIndustrial("CNA2019", Categoria.NORMAL, 2019, 1400, 2);
        vehiculos[3] = new Turismo("TSA2024", Categoria.SUPERIOR, 2024, 5, 550.0);
        vehiculos[4] = new Motocicleta("MSA2021", Categoria.SUPERIOR, 2021, 500);
        vehiculos[5] = new VehiculoIndustrial("CSA2025", Categoria.SUPERIOR, 2025, 2400, 3);
        vehiculos[6] = new Turismo("TEA2026", Categoria.ESPECIAL, 2026, 7, 650.0);
        vehiculos[7] = new Motocicleta("MEA2026", Categoria.ESPECIAL, 2026, 1000);
        vehiculos[8] = new VehiculoIndustrial("CEA2025", Categoria.ESPECIAL, 2025, 4400, 5);

        // Los vehículos nacen con disponible=false (valor por defecto de boolean),
        // así que los marcamos como disponibles para que se puedan alquilar.
        for (Vehiculo v : vehiculos) {
            if (v != null) {
                v.setDisponible(true);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // MÉTODOS PARA GESTIONAR VEHÍCULOS

    public Vehiculo[] obtenerVehiculos(String tipo) {

        Vehiculo[] vehiculosFiltrados = new Vehiculo[vehiculos.length];
        int contador = 0;

        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo != null) {
                if (vehiculo.getTipo().equals(tipo)) {
                    vehiculosFiltrados[contador++] = vehiculo;
                }
            }
        }

        // Ajustar el tamaño del array para que solo contenga los vehículos encontrados
        Vehiculo[] resultado = new Vehiculo[contador];
        System.arraycopy(vehiculosFiltrados, 0, resultado, 0, contador);
        return resultado;
    }

    /**
     * Devuelve un array con los vehículos del tipo y categoría especificados.
     * @param tipo
     * @param categoria
     * @return lista de vehículos del tipo y categoría especificados (vacía si no hay ninguno)
     */
    public Vehiculo[] obtenerVehiculos(String tipo, Categoria categoria) {
        Vehiculo[] delTipo = obtenerVehiculos(tipo);
        Vehiculo[] filtrados = new Vehiculo[delTipo.length];
        int contador = 0;

        for (Vehiculo vehiculo : delTipo) {
            if (vehiculo.getCategoria() == categoria) {
                filtrados[contador++] = vehiculo;
            }
        }

        Vehiculo[] resultado = new Vehiculo[contador];
        System.arraycopy(filtrados, 0, resultado, 0, contador);
        return resultado;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // MÉTODOS PARA GESTIONAR ALQUILERES

    /**
     * Agrega un nuevo alquiler al array de alquileres.
     * @param alquiler
     * @return true si se ha agregado, false si se ha alcanzado la capacidad máxima
     */
    public boolean agregarAlquiler(Alquiler alquiler) {
        if (contadorAlquileres < alquileres.length) {
            alquileres[contadorAlquileres] = alquiler;
            contadorAlquileres++;
            return true;
        } else {
            System.out.println("No se pueden agregar más alquileres. Capacidad máxima alcanzada.");
            return false;
        }
    }

    /**
     * Devuelve un array con todos los alquileres registrados en el gestor.
     * @return
     */
    public Alquiler[] obtenerAlquileres() {
        return alquileres;
    }

    /**
     * Devuelve los alquileres de un cliente (identificado por su DNI), del más
     * antiguo al más reciente.
     * @param cliente
     * @return alquileres del cliente (array vacío si no tiene ninguno)
     */
    public Alquiler[] obtenerAlquileres(Cliente cliente) {
        if (cliente == null) {
            return new Alquiler[0];
        }

        Alquiler[] filtrados = new Alquiler[contadorAlquileres];
        int contador = 0;
        for (int i = 0; i < contadorAlquileres; i++) {
            if (alquileres[i].getCliente().getDni().equals(cliente.getDni())) {
                filtrados[contador++] = alquileres[i];
            }
        }

        Alquiler[] resultado = new Alquiler[contador];
        System.arraycopy(filtrados, 0, resultado, 0, contador);
        return resultado;
    }

    /**
     * Devuelve los alquileres de un cliente que siguen en curso, es decir, aquellos
     * cuyo vehículo no se ha entregado todavía. Como Alquiler no guarda si está
     * cerrado, se considera en curso el alquiler más reciente de un vehículo
     * no disponible, siempre que sea de ese cliente.
     */
    public Alquiler[] obtenerAlquileresActivos(Cliente cliente) {
        if (cliente == null) {
            return new Alquiler[0];
        }

        Alquiler[] activos = new Alquiler[contadorAlquileres];
        int contador = 0;
        for (int i = 0; i < contadorAlquileres; i++) {
            Alquiler a = alquileres[i];
            if (a.getVehiculo().isDisponible()) {
                continue;
            }
            if (!a.getCliente().getDni().equals(cliente.getDni())) {
                continue;
            }
            if (esUltimoAlquilerDelVehiculo(i)) {
                activos[contador++] = a;
            }
        }

        Alquiler[] resultado = new Alquiler[contador];
        System.arraycopy(activos, 0, resultado, 0, contador);
        return resultado;
    }

    private boolean esUltimoAlquilerDelVehiculo(int indice) {
        String matricula = alquileres[indice].getVehiculo().getMatricula();
        for (int j = indice + 1; j < contadorAlquileres; j++) {
            if (alquileres[j].getVehiculo().getMatricula().equals(matricula)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Entrega el vehículo de un alquiler: el vehículo vuelve a estar disponible.
     * @param alquiler alquiler que se da por terminado
     */
    public void entregarVehiculo(Alquiler alquiler) {
        if (alquiler == null) {
            throw new IllegalArgumentException("El alquiler es obligatorio.");
        }
        alquiler.getVehiculo().setDisponible(true);
    }

    /**
     * Informa si un cliente es cliente frecuente en función del número de alquileres previos.
     * @param cliente cliente a evaluar
     * @param umbral número mínimo de alquileres previos que se consideran para determinar si es cliente frecuente
     * @return true si el cliente tiene al menos "umbral" alquileres previos
     */
    boolean esClienteFrecuente(Cliente cliente, int umbral) {
        return obtenerAlquileres(cliente).length >= umbral;
    }

    /**
     * Precio de un alquiler con una política. Está aislado en un método para que,
     * si cambia la forma de calcular precios (patrón Estrategia), solo haya que
     * tocar esta línea.
     */
    private double calcularPrecio(Alquiler alquiler, PoliticasDePrecios politica) {
        return alquiler.calcularPrecioAlquiler(politica);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // PERSISTENCIA EN FORMATO JSON (ejercicio 5)

    /**
     * Serializa clientes, vehículos y alquileres del gestor en un único JSON con
     * tres arrays: "clientes", "vehiculos" y "alquileres".
     */
    public String exportarJSON() {
        JSONArray jsonClientes = new JSONArray();
        for (Cliente c : clientes) {
            if (c != null) {
                jsonClientes.put(new JSONObject(c.toString()));
            }
        }

        JSONArray jsonVehiculos = new JSONArray();
        for (Vehiculo v : vehiculos) {
            if (v != null) {
                jsonVehiculos.put(new JSONObject(v.toString()));
            }
        }

        JSONArray jsonAlquileres = new JSONArray();
        for (int i = 0; i < contadorAlquileres; i++) {
            jsonAlquileres.put(new JSONObject(alquileres[i].toString()));
        }

        JSONObject raiz = new JSONObject();
        raiz.put("clientes", jsonClientes);
        raiz.put("vehiculos", jsonVehiculos);
        raiz.put("alquileres", jsonAlquileres);
        return raiz.toString(2);
    }

    /**
     * Sustituye el contenido del gestor por el que describe el JSON. Si el JSON no
     * es válido o es incoherente se lanza una excepción y el gestor queda intacto.
     * Los alquileres se enlazan con los clientes (por DNI) y vehículos (por matrícula)
     * del propio gestor, de modo que entregar un vehículo afecte a todos sus alquileres.
     * @throws IllegalArgumentException si el contenido es incoherente o no cabe
     * @throws org.json.JSONException si el JSON está mal formado o le faltan campos
     */
    public void importarJSON(String json) {
        JSONObject raiz = new JSONObject(json);
        JSONArray jsonClientes = raiz.getJSONArray("clientes");
        JSONArray jsonVehiculos = raiz.getJSONArray("vehiculos");
        JSONArray jsonAlquileres = raiz.getJSONArray("alquileres");

        if (jsonClientes.length() > clientes.length
                || jsonVehiculos.length() > vehiculos.length
                || jsonAlquileres.length() > alquileres.length) {
            throw new IllegalArgumentException("El fichero contiene más datos de los que caben en el gestor.");
        }

        // Todo se construye en estructuras nuevas y solo al final se sustituye el estado.
        Cliente[] nuevosClientes = new Cliente[clientes.length];
        for (int i = 0; i < jsonClientes.length(); i++) {
            Cliente c = FactoriaClientes.getCliente(jsonClientes.getJSONObject(i).toString());
            if (buscarCliente(nuevosClientes, c.getDni()) != null) {
                throw new IllegalArgumentException("DNI de cliente repetido: " + c.getDni());
            }
            nuevosClientes[i] = c;
        }

        Vehiculo[] nuevosVehiculos = new Vehiculo[vehiculos.length];
        for (int i = 0; i < jsonVehiculos.length(); i++) {
            Vehiculo v = FactoriaVehiculos.getVehiculo(jsonVehiculos.getJSONObject(i).toString());
            if (buscarVehiculo(nuevosVehiculos, v.getMatricula()) != null) {
                throw new IllegalArgumentException("Matrícula repetida: " + v.getMatricula());
            }
            nuevosVehiculos[i] = v;
        }

        Alquiler[] nuevosAlquileres = new Alquiler[alquileres.length];
        for (int i = 0; i < jsonAlquileres.length(); i++) {
            Alquiler leido = FactoriaAlquileres.getAlquiler(jsonAlquileres.getJSONObject(i).toString());
            Cliente c = buscarCliente(nuevosClientes, leido.getCliente().getDni());
            Vehiculo v = buscarVehiculo(nuevosVehiculos, leido.getVehiculo().getMatricula());
            if (c == null || v == null) {
                throw new IllegalArgumentException("Un alquiler hace referencia a un cliente o vehículo inexistente.");
            }
            nuevosAlquileres[i] = new Alquiler(v, c, leido.getFechaInicio(), leido.getFechaFin());
        }

        this.clientes = nuevosClientes;
        this.vehiculos = nuevosVehiculos;
        this.alquileres = nuevosAlquileres;
        this.contadorAlquileres = jsonAlquileres.length();
    }

    private static Cliente buscarCliente(Cliente[] lista, String dni) {
        for (Cliente c : lista) {
            if (c != null && c.getDni().equals(dni)) {
                return c;
            }
        }
        return null;
    }

    private static Vehiculo buscarVehiculo(Vehiculo[] lista, String matricula) {
        for (Vehiculo v : lista) {
            if (v != null && v.getMatricula().equals(matricula)) {
                return v;
            }
        }
        return null;
    }

    /** Guarda el estado del gestor en un fichero de texto (UTF-8) en formato JSON. */
    public void guardarEnFichero(String ruta) throws IOException {
        Files.write(Paths.get(ruta), exportarJSON().getBytes(StandardCharsets.UTF_8));
    }

    /** Recupera el estado del gestor desde un fichero de texto en formato JSON. */
    public void cargarDesdeFichero(String ruta) throws IOException {
        String contenido = new String(Files.readAllBytes(Paths.get(ruta)), StandardCharsets.UTF_8);
        importarJSON(contenido);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // INTERFAZ DE USUARIO (CONSOLA) Y LÓGICA DE NEGOCIO

    // La interfaz de usuario y la lógica de negocio suelen implementarse por separado.
    // Aquí se integran ambas por simplicidad, pero en una aplicación real hay que separarlas.

    public void menuPrincipal(){

        boolean salir = false;

        while(!salir){

            System.out.println("Gestión de Alquileres");
            System.out.println("(1) identificarse como cliente");
            System.out.println("(2) guardar datos en un fichero");
            System.out.println("(3) cargar datos desde un fichero");
            System.out.println("(0) salir de la app");

            int opcion = KeyBoard.readInt();

            switch (opcion) {
                case 0:
                    System.out.println("Bye ...");
                    salir = true;
                    break;
                case 1:
                    // Primero se identifica al cliente:
                    Cliente cliente = identificarCliente();
                    if (cliente != null) {
                        menuCliente(cliente);
                    } else {
                        System.out.println("Debe identificarse correctamente o salir de la app");
                    }
                    break;
                case 2:
                    guardarDatos();
                    break;
                case 3:
                    cargarDatos();
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }
    }

    private String pedirRutaFichero() {
        System.out.println("Nombre del fichero (intro para \"" + FICHERO_POR_DEFECTO + "\"):");
        String ruta = KeyBoard.readString();
        if (ruta == null || ruta.trim().isEmpty()) {
            return FICHERO_POR_DEFECTO;
        }
        return ruta.trim();
    }

    private void guardarDatos() {
        String ruta = pedirRutaFichero();
        try {
            guardarEnFichero(ruta);
            System.out.println("Datos guardados en " + ruta);
        } catch (IOException e) {
            System.out.println("No se pudo guardar el fichero: " + e.getMessage());
        }
    }

    private void cargarDatos() {
        String ruta = pedirRutaFichero();
        try {
            cargarDesdeFichero(ruta);
            System.out.println("Datos cargados desde " + ruta);
        } catch (IOException e) {
            System.out.println("No se pudo leer el fichero: " + e.getMessage());
        } catch (RuntimeException e) {
            // JSON mal formado o incoherente: el gestor queda como estaba.
            System.out.println("El fichero no tiene un formato válido: " + e.getMessage());
        }
    }

    private void menuCliente(Cliente cliente) {
        // Ahora el cliente puede elegir una opción.
        System.out.println("Sr " + cliente.getNombre() + " Introduzca una opción");
        System.out.println("(1) seleccionar coche para alquilar");
        System.out.println("(2) entregar un coche alquilado");
        System.out.println("(3) listar su histórico de alquileres");
        System.out.println("(4) calcular precio de alquiler");

        int opcion = KeyBoard.readInt();
        System.out.println("opción escogida: " + opcion);

        switch (opcion) {
            case 1:
                alquilarVehiculo(cliente);
                break;
            case 2:
                entregarVehiculo(cliente);
                break;
            case 3:
                Alquiler[] alqs = obtenerAlquileres(cliente);
                if (alqs.length == 0) {
                    System.out.println("No tiene alquileres registrados.");
                }
                for (Alquiler q : alqs) {
                    System.out.println(q);
                }
                break;
            case 4:
                calcularPrecioAlquiler(cliente);
                break;
            default:
                break;
        }
    }

    private void alquilarVehiculo(Cliente cliente) {
        Vehiculo v = seleccionarVehiculo(cliente);
        if (v == null) {
            System.out.println("No se ha seleccionado ningún vehículo.");
            return;
        }
        if (!v.isDisponible()) {
            System.out.println("Debe seleccionar un vehículo disponible");
            return;
        }

        // para simplificar la IU se supone que el alquiler comienza "ahora"
        // y el usuario especifica duración en días.
        System.out.println("¿cuántos días desea alquilar el vehículo?");
        int dias = KeyBoard.readInt();
        if (dias <= 0 || dias == KeyBoard.INT_ERR) {
            System.out.println("Número de días no válido.");
            return;
        }
        LocalDate inicio = LocalDate.now();
        // El cálculo del precio cuenta ambos días (inicio y fin), por eso el fin
        // es inicio + (dias - 1): así N días de alquiler se cobran como N días.
        LocalDate fin = inicio.plusDays(dias - 1);

        // Se evalúa antes de registrar el alquiler: cuentan solo los alquileres previos.
        boolean usuarioFrecuente = this.esClienteFrecuente(cliente, UMBRAL_CLIENTE_FRECUENTE);

        // Creamos y registramos el alquiler
        Alquiler q = new Alquiler(v, cliente, inicio, fin);
        if (!this.agregarAlquiler(q)) {
            return;
        }
        v.setDisponible(false);

        // Política aplicable: cliente frecuente si tiene alquileres previos suficientes,
        // y normal en caso contrario.
        PoliticasDePrecios politica;
        if (usuarioFrecuente) {
            System.out.println("Alquiler realizado. Tarifa Usuario frecuente.");
            politica = PoliticasDePrecios.ClienteFrecuente;
        } else {
            System.out.println("Alquiler realizado. Tarifa Normal.");
            politica = PoliticasDePrecios.Normal;
        }

        System.out.printf(Locale.forLanguageTag("es-ES"),
                "Precio total (%d día(s), %s): %.2f €%n",
                dias, politica, calcularPrecio(q, politica));
    }

    private void entregarVehiculo(Cliente cliente) {
        Alquiler[] activos = obtenerAlquileresActivos(cliente);
        if (activos.length == 0) {
            System.out.println("No tiene vehículos pendientes de entrega.");
            return;
        }

        System.out.println("Vehículos pendientes de entrega:");
        for (int i = 0; i < activos.length; i++) {
            Vehiculo v = activos[i].getVehiculo();
            System.out.println(i + " ---> " + v.getTipo() + " | " + v.getMatricula()
                    + " | hasta " + activos[i].getFechaFin());
        }
        System.out.println("Seleccione el número del vehículo que entrega:");
        int seleccion = KeyBoard.readInt();
        if (seleccion < 0 || seleccion >= activos.length) {
            System.out.println("Selección no válida.");
            return;
        }

        entregarVehiculo(activos[seleccion]);
        System.out.println("Vehículo " + activos[seleccion].getVehiculo().getMatricula()
                + " entregado. Gracias.");
    }

    private void calcularPrecioAlquiler(Cliente cliente) {
        Vehiculo[] opciones = obtenerTodosLosVehiculos();
        if (opciones.length == 0) {
            System.out.println("No hay vehículos registrados.");
            return;
        }

        System.out.println("Vehículos registrados:");
        for (int i = 0; i < opciones.length; i++) {
            Vehiculo vehiculo = opciones[i];
            System.out.printf(Locale.ROOT, "%d ---> %s | %s | %s | %d €/día%n",
                    i, vehiculo.getTipo(), vehiculo.getMatricula(),
                    vehiculo.getCategoria(), vehiculo.getPrecioBase());
        }
        System.out.println("Seleccione el número del vehículo:");
        int seleccionVehiculo = KeyBoard.readInt();
        if (seleccionVehiculo < 0 || seleccionVehiculo >= opciones.length) {
            System.out.println("Selección de vehículo no válida.");
            return;
        }

        System.out.println("Introduzca la fecha de inicio (AAAA-MM-DD):");
        LocalDate fechaInicio;
        String textoFechaInicio = KeyBoard.readString();
        if (textoFechaInicio == null || textoFechaInicio.trim().isEmpty()) {
            System.out.println("Fecha de inicio no válida. Use el formato AAAA-MM-DD.");
            return;
        }
        try {
            fechaInicio = LocalDate.parse(textoFechaInicio);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha de inicio no válida. Use el formato AAAA-MM-DD.");
            return;
        }

        System.out.println("Introduzca la fecha de fin (AAAA-MM-DD):");
        LocalDate fechaFin;
        String textoFechaFin = KeyBoard.readString();
        if (textoFechaFin == null || textoFechaFin.trim().isEmpty()) {
            System.out.println("Fecha de fin no válida. Use el formato AAAA-MM-DD.");
            return;
        }
        try {
            fechaFin = LocalDate.parse(textoFechaFin);
        } catch (DateTimeParseException e) {
            System.out.println("Fecha de fin no válida. Use el formato AAAA-MM-DD.");
            return;
        }
        if (fechaFin.isBefore(fechaInicio)) {
            System.out.println("La fecha de fin no puede ser anterior a la fecha de inicio.");
            return;
        }

        System.out.println("Seleccione la política: 1. Normal, 2. Temporada alta, "
                + "3. Ofertón, 4. Cliente frecuente");
        int seleccionPolitica = KeyBoard.readInt();
        PoliticasDePrecios politica;
        switch (seleccionPolitica) {
            case 1:
                politica = PoliticasDePrecios.Normal;
                break;
            case 2:
                politica = PoliticasDePrecios.TemporadaAlta;
                break;
            case 3:
                politica = PoliticasDePrecios.Oferton;
                break;
            case 4:
                politica = PoliticasDePrecios.ClienteFrecuente;
                break;
            default:
                System.out.println("Selección de política no válida.");
                return;
        }

        Alquiler alquiler = new Alquiler(opciones[seleccionVehiculo], cliente,
                fechaInicio, fechaFin);
        long dias = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        System.out.printf(Locale.forLanguageTag("es-ES"),
                "Precio para %d día(s) con la política %s: %.2f €%n",
                dias, politica, alquiler.calcularPrecioAlquiler(politica));
    }

    private Vehiculo[] obtenerTodosLosVehiculos() {
        int cantidad = 0;
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo != null) {
                cantidad++;
            }
        }

        Vehiculo[] resultado = new Vehiculo[cantidad];
        int indice = 0;
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo != null) {
                resultado[indice++] = vehiculo;
            }
        }
        return resultado;
    }

    public Cliente identificarCliente(){

        boolean seleccionado = false;
        Cliente cliente = null;

        while (!seleccionado) {
        
            System.out.println("Bienvenido al Gestor de Alquileres");
            for (int i = 0; i < clientes.length; i++) {
                if (clientes[i] != null) {
                    System.out.println(i + " ---> " + clientes[i].getNombre() + " - " + clientes[i].getDni());
                }
            }
            System.out.println("Identífiquese como cliente con su número de cliente (-1 para salir):");
            int numeroCliente = KeyBoard.readInt();
            if (numeroCliente == -1) {
                return null;
            }
            else if (numeroCliente < 0 || numeroCliente >= clientes.length || clientes[numeroCliente] == null) {
                System.out.println("identificarCliente:Número de cliente inválido.");
                return null;
            }
            else{
                seleccionado = true;
                cliente = clientes[numeroCliente];
            }
        }
        return cliente;
    }

    public Vehiculo seleccionarVehiculo(Cliente cliente){

        System.out.println("Estimado Sr./Sra. " + cliente.getNombre());
        System.out.println("Introduzca el número correspondiente al tipo de vehículo que desea alquilar:");
        System.out.println("1. Turismo, 2. Motocicleta, 3. Vehículo Industrial: ");

        int tipoVehiculo = KeyBoard.readInt();
        String tipo;

        switch (tipoVehiculo) {
            case 1:
                System.out.println("Ha seleccionado Turismo.");
                tipo = "Turismo";
                break;
            case 2:
                System.out.println("Ha seleccionado Motocicleta.");
                tipo = "Motocicleta";
                break;
            case 3:
                System.out.println("Ha seleccionado Vehículo Industrial.");
                tipo = "VehiculoIndustrial";
                break;
            default:
                System.out.println("Opción inválida. Por favor, seleccione un tipo de vehículo válido.");
                return null;
        }

        System.out.println("Estimado Sr./Sra. " + cliente.getNombre() + "\n seleccione categoría.");
        System.out.println("Introduzca el número correspondiente a la categoría que desea alquilar:");
        System.out.println("1. Normal, 2. Superior, 3. Especial, 4.Cualquiera: ");

        int categoriaVehiculo = KeyBoard.readInt();
        Vehiculo[] elegibles;

        switch (categoriaVehiculo) {
            case 1:
                System.out.println("Ha seleccionado Categoría Normal.");
                elegibles = obtenerVehiculos(tipo, Categoria.NORMAL);
                break;
            case 2:
                System.out.println("Ha seleccionado Categoría Superior.");
                elegibles = obtenerVehiculos(tipo, Categoria.SUPERIOR);
                break;
            case 3:
                System.out.println("Ha seleccionado Categoría Especial.");
                elegibles = obtenerVehiculos(tipo, Categoria.ESPECIAL);
                break;
            case 4:
                System.out.println("Ha seleccionado cualquier categoría.");
                elegibles = obtenerVehiculos(tipo);
                break;
            default:
                System.out.println("Opción inválida. Por favor, seleccione una categoría válida.");
                return null;
        }

        if (elegibles.length == 0) {
            System.out.println("No hay vehículos de ese tipo y categoría.");
            return null;
        }

        System.out.println("Coches elegibles");
        for (int i = 0; i < elegibles.length; i++) {
            Vehiculo e = elegibles[i];
            System.out.println(i + " ---> " + e.getMatricula() + " | " + e.getCategoria()
                    + " | " + e.getPrecioBase() + " €/día | "
                    + (e.isDisponible() ? "disponible" : "NO disponible"));
        }

        System.out.println("Introduzca número coche (sólo si está disponible)");
        int seleccion = KeyBoard.readInt();
        if (seleccion < 0 || seleccion >= elegibles.length) {
            System.out.println("Selección no válida.");
            return null;
        }
        return elegibles[seleccion];
    }

    public void listarAlquileres (){
        System.out.println("Listado de alquileres");
        for (int i = 0; i < contadorAlquileres; i++){
            System.out.println(alquileres[i]);
        }
        System.out.println();
    }

    public void listarVehiculos(){
        System.out.println("Listado de vehículos");
        for (Vehiculo veh: vehiculos){
            if (veh != null) {
                System.out.println(veh);
            }
        }
        System.out.println();
    }


    public static void main(String [] args){
        GestorAlquileres app = new GestorAlquileres();
        app.menuPrincipal();
    }
}
