package com.barrio.infraestructura.bd;

import java.sql.Connection;
import java.sql.DriverManager;
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

    private static void inicializar() throws SQLException {
        for (int i = 0; i < TAMANIO_POOL; i++) {
            pool.offer(DriverManager.getConnection(URL, USUARIO, PASSWORD));
        }
        Connection conexion = pool.peek();
        crearSchema(conexion);
        inicializado = true;
    }

    private static void crearSchema(Connection conexion) throws SQLException {
        try (Statement st = conexion.createStatement()) {

            st.execute("CREATE TABLE IF NOT EXISTS LOTES ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "NUMERO INT NOT NULL,"
                    + "MANZANA VARCHAR(10) NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS PERSONAS ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "TIPO VARCHAR(30) NOT NULL,"
                    + "NOMBRE VARCHAR(100) NOT NULL,"
                    + "APELLIDO VARCHAR(100) NOT NULL,"
                    + "DNI VARCHAR(15) NOT NULL UNIQUE,"
                    + "EMAIL VARCHAR(150),"
                    + "NUMERO_LOTE INT,"
                    + "LEGAJO VARCHAR(20),"
                    + "ESPECIALIDAD VARCHAR(50),"
                    + "RESIDENTE_AUTORIZANTE_ID BIGINT,"
                    + "FOREIGN KEY (RESIDENTE_AUTORIZANTE_ID) REFERENCES PERSONAS(ID))");

            st.execute("CREATE TABLE IF NOT EXISTS SECTORES_COMUNES ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "NOMBRE VARCHAR(100) NOT NULL)");

            st.execute("CREATE TABLE IF NOT EXISTS VIVIENDAS ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "NUMERO VARCHAR(20) NOT NULL)");

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
                    + "FECHA_HORA_INGRESO TIMESTAMP NOT NULL,"
                    + "FECHA_HORA_EGRESO TIMESTAMP,"
                    + "PROTOCOLO VARCHAR(50) NOT NULL,"
                    + "PERMITIDO BOOLEAN NOT NULL DEFAULT TRUE,"
                    + "FOREIGN KEY (ACTOR_ID) REFERENCES PERSONAS(ID))");

            st.execute("CREATE TABLE IF NOT EXISTS AUTORIZACIONES_ACCESO ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "RESIDENTE_ID BIGINT NOT NULL,"
                    + "VALIDA_HASTA TIMESTAMP,"
                    + "FOREIGN KEY (RESIDENTE_ID) REFERENCES PERSONAS(ID))");

            st.execute("CREATE TABLE IF NOT EXISTS ORDENES_TRABAJO ("
                    + "ID BIGINT AUTO_INCREMENT PRIMARY KEY,"
                    + "ADMINISTRADOR_ID BIGINT,"
                    + "PROVEEDOR_ID BIGINT,"
                    + "FECHA_EMISION TIMESTAMP NOT NULL,"
                    + "FOREIGN KEY (ADMINISTRADOR_ID) REFERENCES PERSONAS(ID),"
                    + "FOREIGN KEY (PROVEEDOR_ID) REFERENCES PERSONAS(ID))");

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
                    + "AUTOR_ID BIGINT NOT NULL,"
                    + "DESCRIPCION VARCHAR(500) NOT NULL,"
                    + "FOREIGN KEY (AUTOR_ID) REFERENCES PERSONAS(ID))");
        }
    }
}
