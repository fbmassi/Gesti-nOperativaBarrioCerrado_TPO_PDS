package com.barrio.infraestructura.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public final class ConexionH2 {

    private static final String URL = "jdbc:h2:./data/barrio";
    private static final String USUARIO = "sa";
    private static final String PASSWORD = "";
    private static final int TAMANIO_POOL = 5;

    private static final BlockingQueue<Connection> pool = new ArrayBlockingQueue<>(TAMANIO_POOL);
    private static boolean inicializado = false;

    private ConexionH2() {
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (!inicializado) {
            inicializar();
        }
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrumpido esperando una conexión del pool", e);
        }
    }

    public static void liberarConexion(Connection conexion) {
        if (conexion != null) {
            pool.offer(conexion);
        }
    }
    
    public static synchronized void cerrar() {
        Connection conexion;
        while ((conexion = pool.poll()) != null) {
            try {
                conexion.close();
            } catch (SQLException ignored) {
                // Cierre best-effort.
            }
        }
        inicializado = false;
    }

    private static void inicializar() throws SQLException {
        for (int i = 0; i < TAMANIO_POOL; i++) {
            pool.offer(DriverManager.getConnection(URL, USUARIO, PASSWORD));
        }
        Connection conexion = pool.peek();
        crearSchema(conexion);
        inicializado = true;
        Runtime.getRuntime().addShutdownHook(new Thread(ConexionH2::cerrar));
    }

    private static void crearSchema(Connection conexion) throws SQLException {
        try (Statement st = conexion.createStatement()) {

            st.execute("CREATE TABLE IF NOT EXISTS VIVIENDAS ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "NUMERO VARCHAR(20) NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS PERSONAS ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "TIPO VARCHAR(30) NOT NULL,"
                    + "NOMBRE VARCHAR(100) NOT NULL,"
                    + "APELLIDO VARCHAR(100) NOT NULL,"
                    + "DNI VARCHAR(15) NOT NULL UNIQUE,"
                    + "EMAIL VARCHAR(150) UNIQUE,"
                    + "PASSWORD VARCHAR(100),"
                    + "NUMERO_LOTE INT,"
                    + "LEGAJO VARCHAR(20),"
                    + "ESPECIALIDAD VARCHAR(50),"
                    + "VIVIENDA_ID BIGINT,"
                    + "CANAL_NOTIFICACION VARCHAR(10) DEFAULT 'EMAIL',"
                    + "ACCESO_AUTORIZADO BOOLEAN DEFAULT FALSE,"
                    + "FOREIGN KEY (VIVIENDA_ID) REFERENCES VIVIENDAS(ID))");

            st.execute("CREATE TABLE IF NOT EXISTS SOLICITUDES ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "TIPO VARCHAR(30) NOT NULL,"
                    + "TITULO VARCHAR(150) NOT NULL,"
                    + "DESCRIPCION VARCHAR(1000),"
                    + "FECHA_CREACION TIMESTAMP NOT NULL,"
                    + "ESTADO VARCHAR(30) NOT NULL,"
                    + "CATEGORIA VARCHAR(30),"
                    + "PRIORIDAD VARCHAR(10),"
                    + "RESIDENTE_ID BIGINT,"
                    + "ADMINISTRADOR_ID BIGINT,"
                    + "GUARDIA_ID BIGINT,"
                    + "URGENCIA BOOLEAN,"
                    + "FOREIGN KEY (RESIDENTE_ID) REFERENCES PERSONAS(ID),"
                    + "FOREIGN KEY (ADMINISTRADOR_ID) REFERENCES PERSONAS(ID),"
                    + "FOREIGN KEY (GUARDIA_ID) REFERENCES PERSONAS(ID))");

            st.execute("CREATE TABLE IF NOT EXISTS TAREAS ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "TIPO VARCHAR(20) NOT NULL,"
                    + "TITULO VARCHAR(150) NOT NULL,"
                    + "DESCRIPCION VARCHAR(1000),"
                    + "ESTADO VARCHAR(30) NOT NULL,"
                    + "FECHA_CREACION TIMESTAMP NOT NULL,"
                    + "PROVEEDOR_ID BIGINT,"
                    + "TAREA_PADRE_ID BIGINT,"
                    + "FOREIGN KEY (PROVEEDOR_ID) REFERENCES PERSONAS(ID),"
                    + "FOREIGN KEY (TAREA_PADRE_ID) REFERENCES TAREAS(ID))");

            st.execute("CREATE TABLE IF NOT EXISTS ACCESOS ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "ACTOR_ID BIGINT NOT NULL,"
                    + "TIPO VARCHAR(20) NOT NULL,"
                    + "FECHA_HORA_INGRESO TIMESTAMP NOT NULL,"
                    + "FECHA_HORA_EGRESO TIMESTAMP,"
                    + "PERMITIDO BOOLEAN NOT NULL DEFAULT TRUE,"
                    + "FOREIGN KEY (ACTOR_ID) REFERENCES PERSONAS(ID))");

            st.execute("CREATE TABLE IF NOT EXISTS NOTIFICACIONES ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "DESTINATARIO_ID BIGINT NOT NULL,"
                    + "MENSAJE VARCHAR(1000) NOT NULL,"
                    + "FECHA_ENVIO TIMESTAMP,"
                    + "CANAL VARCHAR(30) NOT NULL,"
                    + "FOREIGN KEY (DESTINATARIO_ID) REFERENCES PERSONAS(ID))");

            st.execute("CREATE TABLE IF NOT EXISTS HISTORIAL_ACCIONES ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "FECHA TIMESTAMP NOT NULL,"
                    + "AUTOR_ID BIGINT,"
                    + "DESCRIPCION VARCHAR(500) NOT NULL,"
                    + "FOREIGN KEY (AUTOR_ID) REFERENCES PERSONAS(ID))");

            insertarUsuariosDePrueba(st);
        }
    }

    private static void insertarUsuariosDePrueba(Statement st) throws SQLException {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM PERSONAS")) {
            if (rs.next() && rs.getInt(1) > 0) {
                return;
            }
        }

        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, EMAIL, PASSWORD, LEGAJO) "
                + "VALUES ('ADMINISTRADOR', 'Admin', 'Sistema', '00000000', "
                + "'admin@barrio.com', 'admin123', 'LEG-ADM-001')");
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, EMAIL, PASSWORD, NUMERO_LOTE, ACCESO_AUTORIZADO) "
                + "VALUES ('RESIDENTE', 'Juan', 'Pérez', '12345678', "
                + "'residente@barrio.com', 'res123', 101, TRUE)");
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, EMAIL, PASSWORD, LEGAJO) "
                + "VALUES ('GUARDIA', 'Carlos', 'García', '87654321', "
                + "'guardia@barrio.com', 'guard123', 'LEG-GUA-001')");
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, EMAIL, PASSWORD, ESPECIALIDAD) "
                + "VALUES ('PROVEEDOR', 'Marco', 'Rodríguez', '11223344', "
                + "'proveedor@barrio.com', 'prov123', 'ELECTRICIDAD')");

        // Proveedores de prueba de distintas especialidades (para asignar tareas).
        insertarProveedor(st, "Pedro", "Verde", "30000001", "jardinero@barrio.com", "jard123", "JARDINERIA");
        insertarProveedor(st, "Luis", "Caño", "30000002", "plomero@barrio.com", "plom123", "PLOMERIA");
        insertarProveedor(st, "Ana", "Color", "30000003", "pintor@barrio.com", "pint123", "PINTURA");
        insertarProveedor(st, "Sofía", "Pan", "30000004", "alimentos@barrio.com", "alim123", "ALIMENTOS");
        insertarProveedor(st, "Marta", "Limpio", "30000005", "limpieza@barrio.com", "limp123", "LIMPIEZA");
        insertarProveedor(st, "Hugo", "Ladrillo", "30000006", "albanil@barrio.com", "alba123", "ALBANILERIA");
        insertarProveedor(st, "Raúl", "Gasista", "30000007", "gas@barrio.com", "gas123", "GAS");

        insertarDatosDeEjemplo(st);
    }

    /** Datos de ejemplo (más usuarios, viviendas, reclamos, incidentes y accesos) para ver reportes. */
    private static void insertarDatosDeEjemplo(Statement st) throws SQLException {
        // --- Más usuarios ---
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, EMAIL, PASSWORD, NUMERO_LOTE, ACCESO_AUTORIZADO) "
                + "VALUES ('RESIDENTE', 'María', 'González', '23456789', 'residente2@barrio.com', 'res123', 102, TRUE)");
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, EMAIL, PASSWORD, NUMERO_LOTE, ACCESO_AUTORIZADO) "
                + "VALUES ('RESIDENTE', 'Pedro', 'López', '34567890', 'residente3@barrio.com', 'res123', 103, TRUE)");
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, EMAIL, PASSWORD, LEGAJO) "
                + "VALUES ('GUARDIA', 'Laura', 'Díaz', '76543210', 'guardia2@barrio.com', 'guard123', 'LEG-GUA-002')");
        insertarProveedor(st, "Carlos", "Madera", "30000008", "carpintero@barrio.com", "carp123", "CARPINTERIA");
        insertarProveedor(st, "Vigil", "Seguro", "30000009", "seguridad@barrio.com", "segu123", "SEGURIDAD");
        insertarProveedor(st, "Otto", "Varios", "30000010", "otros@barrio.com", "otro123", "OTROS");
        // Visitante que ya egresó (acceso revocado) y familiar actualmente adentro (acceso vigente).
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, ACCESO_AUTORIZADO) "
                + "VALUES ('VISITANTE', 'Ana', 'Visita', '55667788', FALSE)");
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, ACCESO_AUTORIZADO) "
                + "VALUES ('FAMILIAR', 'Jorge', 'Pérez', '66778899', TRUE)");
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, ACCESO_AUTORIZADO) "
                + "VALUES ('EMERGENCIA', 'Ambulancia', 'SAME', '70000001', TRUE)");

        // --- Viviendas y asignación de residentes ---
        st.execute("INSERT INTO VIVIENDAS (NUMERO) VALUES ('Lote 101')");
        st.execute("INSERT INTO VIVIENDAS (NUMERO) VALUES ('Lote 102')");
        st.execute("UPDATE PERSONAS SET VIVIENDA_ID = (SELECT ID FROM VIVIENDAS WHERE NUMERO = 'Lote 101') "
                + "WHERE DNI = '12345678'");
        st.execute("UPDATE PERSONAS SET VIVIENDA_ID = (SELECT ID FROM VIVIENDAS WHERE NUMERO = 'Lote 102') "
                + "WHERE DNI = '23456789'");

        // --- Reclamos (residentes) ---
        insertarReclamo(st, "Pérdida de agua en la vereda", "Pierde agua hace 2 días", "EN_PROCESO", "MANTENIMIENTO", "ALTA", "12345678");
        insertarReclamo(st, "Ruidos molestos", "Música fuerte de madrugada", "PENDIENTE", "ADMINISTRATIVA", "MEDIA", "23456789");
        insertarReclamo(st, "Luminaria quemada en la plaza", "Foco del juego de niños", "RESUELTO", "MANTENIMIENTO", "BAJA", "34567890");
        insertarReclamo(st, "Portón lateral no cierra", "Queda abierto de noche", "CERRADO", "SEGURIDAD", "ALTA", "12345678");

        // --- Incidentes (guardia) ---
        insertarIncidente(st, "Intento de ingreso no autorizado", "Vehículo sin patente en el portón", "PENDIENTE", "SEGURIDAD", "ALTA", true, "87654321");
        insertarIncidente(st, "Alarma activada sector B", "Falsa alarma confirmada", "EN_PROCESO", "SEGURIDAD", "MEDIA", false, "87654321");

        // --- Accesos (ingresos/egresos registrados) ---
        insertarAcceso(st, "12345678", "FAMILIAR", true, true);    // residente, ingresó y salió
        insertarAcceso(st, "23456789", "FAMILIAR", true, false);   // residente, adentro
        insertarAcceso(st, "11223344", "PROVEEDOR", true, true);   // proveedor, ingresó y salió
        insertarAcceso(st, "55667788", "VISITANTE", true, true);   // visitante, ya egresó
        insertarAcceso(st, "66778899", "FAMILIAR", true, false);   // familiar, adentro
        insertarAcceso(st, "30000003", "PROVEEDOR", false, false); // proveedor sin autorización: denegado
        insertarAcceso(st, "70000001", "EMERGENCIA", true, false); // ingreso de emergencia
    }

    private static void insertarReclamo(Statement st, String titulo, String desc, String estado,
                                        String categoria, String prioridad, String dniResidente) throws SQLException {
        st.execute("INSERT INTO SOLICITUDES (TIPO, TITULO, DESCRIPCION, FECHA_CREACION, ESTADO, CATEGORIA, PRIORIDAD, RESIDENTE_ID, ADMINISTRADOR_ID) "
                + "SELECT 'RECLAMO', '" + titulo + "', '" + desc + "', CURRENT_TIMESTAMP, '" + estado + "', '"
                + categoria + "', '" + prioridad + "', p.ID, a.ID "
                + "FROM PERSONAS p, PERSONAS a WHERE p.DNI = '" + dniResidente + "' AND a.DNI = '00000000'");
    }

    private static void insertarIncidente(Statement st, String titulo, String desc, String estado,
                                          String categoria, String prioridad, boolean urgencia,
                                          String dniGuardia) throws SQLException {
        st.execute("INSERT INTO SOLICITUDES (TIPO, TITULO, DESCRIPCION, FECHA_CREACION, ESTADO, CATEGORIA, PRIORIDAD, GUARDIA_ID, URGENCIA) "
                + "SELECT 'INCIDENTE_SEGURIDAD', '" + titulo + "', '" + desc + "', CURRENT_TIMESTAMP, '" + estado + "', '"
                + categoria + "', '" + prioridad + "', g.ID, " + urgencia + " "
                + "FROM PERSONAS g WHERE g.DNI = '" + dniGuardia + "'");
    }

    private static void insertarAcceso(Statement st, String dni, String tipo,
                                       boolean permitido, boolean conEgreso) throws SQLException {
        String egreso = conEgreso ? "DATEADD('HOUR', 2, CURRENT_TIMESTAMP)" : "NULL";
        st.execute("INSERT INTO ACCESOS (ACTOR_ID, TIPO, FECHA_HORA_INGRESO, FECHA_HORA_EGRESO, PERMITIDO) "
                + "SELECT ID, '" + tipo + "', CURRENT_TIMESTAMP, " + egreso + ", " + permitido + " "
                + "FROM PERSONAS WHERE DNI = '" + dni + "'");
    }

    private static void insertarProveedor(Statement st, String nombre, String apellido, String dni,
                                          String email, String password, String especialidad) throws SQLException {
        st.execute("INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, EMAIL, PASSWORD, ESPECIALIDAD) "
                + "VALUES ('PROVEEDOR', '" + nombre + "', '" + apellido + "', '" + dni + "', '"
                + email + "', '" + password + "', '" + especialidad + "')");
    }
}
