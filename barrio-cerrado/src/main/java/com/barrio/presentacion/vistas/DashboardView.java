package com.barrio.presentacion.vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.barrio.dominio.personas.EspecialidadProveedor;
import com.barrio.dominio.personas.Persona;
import com.barrio.presentacion.dto.AccesoDTO;
import com.barrio.presentacion.dto.NotificacionDTO;
import com.barrio.presentacion.dto.PersonaDTO;
import com.barrio.presentacion.dto.SolicitudDTO;
import com.barrio.presentacion.dto.TareaDTO;
import com.barrio.presentacion.dto.ViviendaDTO;

/**
 * Ventana principal por rol. Cada botón ejecuta un caso de uso a través de los controladores.
 */
public class DashboardView extends JFrame {

    private static final Color PRIMARIO = new Color(0, 134, 190);

    private final transient Contexto ctx;
    private final transient Persona usuario;
    private final JTextArea salida = new JTextArea();
    private final JPanel botones = new JPanel();

    public DashboardView(Contexto ctx, Persona usuario) {
        this.ctx = ctx;
        this.usuario = usuario;

        setTitle("Barrio Cerrado - " + usuario.getClass().getSimpleName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(760, 480);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(crearEncabezado(), BorderLayout.NORTH);

        botones.setLayout(new BoxLayout(botones, BoxLayout.Y_AXIS));
        botones.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
        botones.setPreferredSize(new Dimension(240, 0));
        add(botones, BorderLayout.WEST);

        salida.setEditable(false);
        salida.setFont(new Font("Monospaced", Font.PLAIN, 13));
        salida.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(new JScrollPane(salida), BorderLayout.CENTER);

        construirBotonesPorRol();
    }

    private JPanel crearEncabezado() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARIO);
        header.setPreferredSize(new Dimension(760, 60));

        JLabel info = new JLabel("  " + usuario.getNombreCompleto()
                + "  (" + usuario.getClass().getSimpleName() + ")");
        info.setForeground(Color.WHITE);
        info.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.add(info, BorderLayout.WEST);

        JButton logout = new JButton("Cerrar sesión");
        logout.setFocusPainted(false);
        logout.addActionListener(e -> cerrarSesion());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(logout);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private void construirBotonesPorRol() {
        if (ctx.autenticacion.esResidente()) {
            agregar("Registrar reclamo", this::registrarReclamo);
            agregar("Ver mis reclamos", this::verMisReclamos);
            agregar("Ver notificaciones", this::verNotificaciones);
            agregar("Cambiar mi contraseña", this::cambiarMiPassword);
            agregar("Cambiar mi método de notificación", this::cambiarMiNotificacion);
        } else if (ctx.autenticacion.esAdministrador()) {
            // CU-01 Residentes y viviendas
            agregar("Registrar residente", this::registrarResidente);
            agregar("Registrar vivienda", this::registrarVivienda);
            agregar("Asignar residente a vivienda", this::asignarResidenteVivienda);
            agregar("Ver viviendas", this::verViviendas);
            // CU-02 Proveedores y personal
            agregar("Registrar proveedor", this::registrarProveedor);
            agregar("Registrar guardia", this::registrarGuardia);
            // CU-05 Accesos (autorización previa)
            agregar("Autorizar ingreso", this::autorizarIngreso);
            // CU-04 Reclamos
            agregar("Ver todos los reclamos", this::verTodosLosReclamos);
            agregar("Cambiar estado de reclamo", this::cambiarEstadoReclamo);
            // CU-07 / CU-08 Mantenimiento
            agregar("Registrar tarea", this::registrarTarea);
            agregar("Asignar proveedor a una tarea", this::asignarProveedorTarea);
            agregar("Ver tareas de mantenimiento", this::verTareasAdmin);
            agregar("Cambiar estado de tarea", this::cambiarEstadoTarea);
            // CU-09 / CU-10 / CU-11
            agregar("Ver notificaciones", this::verNotificaciones);
            agregar("Ver historial", this::verHistorial);
            agregar("Generar reporte", this::generarReporte);
            // Otros
            agregar("Asignar contraseña", this::asignarPassword);
            agregar("Cambiar mi método de notificación", this::cambiarMiNotificacion);
        } else if (ctx.autenticacion.esGuardia()) {
            agregar("Registrar ingreso", this::registrarIngreso);
            agregar("Registrar egreso", this::registrarEgreso);
            agregar("Validar acceso", this::validarAcceso);
            agregar("Registrar emergencia", this::registrarEmergencia);
            agregar("Registrar incidente de seguridad", this::registrarIncidente);
            agregar("Ver incidentes de seguridad", this::verIncidentes);
        } else if (ctx.autenticacion.esProveedor()) {
            agregar("Ver mis tareas", this::verMisTareas);
            agregar("Cambiar estado de tarea", this::cambiarEstadoTarea);
            agregar("Ver historial de trabajos", this::verHistorialTrabajos);
            agregar("Cambiar mi contraseña", this::cambiarMiPassword);
        }
    }

    // ===================== Acciones: Residente =====================

    private void registrarReclamo() {
        String titulo = pedir("Título del reclamo:");
        if (titulo == null) return;
        String desc = pedir("Descripción:");
        if (desc == null) return;
        String categoria = elegir("Categoría:", new String[]{"MANTENIMIENTO", "SEGURIDAD", "ADMINISTRATIVA"});
        if (categoria == null) return;
        String prioridad = elegir("Prioridad:", new String[]{"BAJA", "MEDIA", "ALTA"});
        if (prioridad == null) return;

        SolicitudDTO dto = new SolicitudDTO();
        dto.setTitulo(titulo);
        dto.setDescripcion(desc);
        dto.setCategoria(categoria);
        dto.setPrioridad(prioridad);
        dto.setDniResidente(usuario.getDni());
        SolicitudDTO creado = ctx.reclamos.registrarReclamo(dto);
        info("Reclamo registrado (id=" + creado.getId() + ")");
    }

    private void verMisReclamos() {
        StringBuilder sb = new StringBuilder("=== Mis reclamos ===\n");
        boolean alguno = false;
        for (SolicitudDTO s : ctx.reclamos.listarReclamos()) {
            if (usuario.getDni() != null && usuario.getDni().equals(s.getDniResidente())) {
                alguno = true;
                sb.append("#").append(s.getId()).append("  ").append(s.getTitulo())
                        .append("  [").append(s.getEstado()).append("]\n");
            }
        }
        if (!alguno) sb.append("(no tenés reclamos)\n");
        mostrar(sb.toString());
    }

    private void verNotificaciones() {
        StringBuilder sb = new StringBuilder("=== Notificaciones ===\n");
        List<NotificacionDTO> notis = ctx.notificaciones.listarNotificacionesDe(usuario.getDni());
        if (notis.isEmpty()) sb.append("(no tenés notificaciones)\n");
        for (NotificacionDTO n : notis) {
            sb.append("- [").append(n.getCanal()).append("] ").append(n.getMensaje()).append("\n");
        }
        mostrar(sb.toString());
    }

    // ===================== Acciones: Administrador =====================

    private void registrarResidente() {
        String nombre = pedir("Nombre:");
        if (nombre == null) return;
        String apellido = pedir("Apellido:");
        if (apellido == null) return;
        String dni = pedir("DNI:");
        if (dni == null) return;
        String email = pedir("Email:");
        if (email == null) return;
        String password = pedir("Contraseña:");
        if (password == null) return;
        Integer lote = pedirEntero("Número de lote:");
        if (lote == null) return;

        PersonaDTO dto = new PersonaDTO();
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setDni(dni);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setNumeroLote(lote);
        PersonaDTO creado = ctx.personas.registrarResidente(dto);
        info("Residente registrado (id=" + creado.getId()
                + ").\nYa puede iniciar sesión con " + creado.getEmail());
    }

    private void registrarProveedor() {
        String nombre = pedir("Nombre:");
        if (nombre == null) return;
        String apellido = pedir("Apellido:");
        if (apellido == null) return;
        String dni = pedir("DNI:");
        if (dni == null) return;
        String email = pedir("Email:");
        if (email == null) return;
        String password = pedir("Contraseña:");
        if (password == null) return;
        EspecialidadProveedor[] vals = EspecialidadProveedor.values();
        String[] opciones = new String[vals.length];
        for (int i = 0; i < vals.length; i++) {
            opciones[i] = vals[i].getDescripcion();
        }
        String elegida = elegir("Especialidad:", opciones);
        if (elegida == null) return;
        String especialidad = EspecialidadProveedor.OTROS.name();
        for (EspecialidadProveedor e : vals) {
            if (e.getDescripcion().equals(elegida)) {
                especialidad = e.name();
            }
        }

        PersonaDTO dto = new PersonaDTO();
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setDni(dni);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setEspecialidad(especialidad);
        PersonaDTO creado = ctx.personas.registrarProveedor(dto);
        info("Proveedor registrado (id=" + creado.getId()
                + ").\nYa puede iniciar sesión con " + creado.getEmail());
    }

    private void asignarPassword() {
        String dni = pedir("DNI del usuario:");
        if (dni == null) return;
        String password = pedir("Nueva contraseña:");
        if (password == null) return;
        PersonaDTO p = ctx.personas.asignarPassword(dni, password);
        info("Contraseña asignada a " + p.getNombre() + " " + p.getApellido());
    }

    private void registrarGuardia() {
        String nombre = pedir("Nombre:");
        if (nombre == null) return;
        String apellido = pedir("Apellido:");
        if (apellido == null) return;
        String dni = pedir("DNI:");
        if (dni == null) return;
        String email = pedir("Email:");
        if (email == null) return;
        String password = pedir("Contraseña:");
        if (password == null) return;
        String legajo = pedir("Legajo:");
        if (legajo == null) return;
        PersonaDTO dto = new PersonaDTO();
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setDni(dni);
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setLegajo(legajo);
        PersonaDTO creado = ctx.personas.registrarGuardia(dto);
        info("Guardia registrado (id=" + creado.getId() + ").\nYa puede iniciar sesión con " + creado.getEmail());
    }

    private void asignarProveedorTarea() {
        String idStr = pedir("ID de la tarea:");
        if (idStr == null) return;
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            error("ID inválido");
            return;
        }
        String dni = elegirProveedorDni();
        if (dni == null) return;
        ctx.mantenimiento.asignarPersonal(id, dni);
        info("Proveedor asignado a la tarea " + id);
    }

    private void verTareasAdmin() {
        StringBuilder sb = new StringBuilder("=== Tareas de mantenimiento ===\n");
        List<TareaDTO> tareas = ctx.mantenimiento.listarTareas();
        if (tareas.isEmpty()) sb.append("(no hay tareas)\n");
        for (TareaDTO t : tareas) {
            sb.append("#").append(t.getId()).append("  ").append(t.getTitulo())
                    .append("  [").append(t.getEstado()).append("]  (").append(t.getTipo()).append(")");
            if (t.getDniProveedor() != null) sb.append("  proveedor DNI ").append(t.getDniProveedor());
            sb.append("\n");
        }
        mostrar(sb.toString());
    }

    private void registrarVivienda() {
        String numero = pedir("Número/identificación de la vivienda:");
        if (numero == null) return;
        ViviendaDTO v = ctx.estructura.registrarVivienda(numero);
        info("Vivienda registrada (id=" + v.getId() + ", número " + v.getNumero() + ")");
    }

    private void asignarResidenteVivienda() {
        String idStr = pedir("ID de la vivienda:");
        if (idStr == null) return;
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            error("ID inválido");
            return;
        }
        String dni = pedir("DNI del residente:");
        if (dni == null) return;
        ctx.estructura.asignarResidente(id, dni);
        info("Residente asignado a la vivienda " + id);
    }

    private void cambiarMiNotificacion() {
        String canal = elegir("¿Cómo querés recibir notificaciones?", new String[]{"EMAIL", "SMS"});
        if (canal == null) return;
        ctx.personas.cambiarCanalNotificacion(usuario.getDni(), canal);
        info("Vas a recibir notificaciones por " + canal);
    }

    /** Elige especialidad, lista los proveedores de esa especialidad y devuelve el DNI elegido (o null). */
    private String elegirProveedorDni() {
        EspecialidadProveedor[] vals = EspecialidadProveedor.values();
        String[] ops = new String[vals.length];
        for (int i = 0; i < vals.length; i++) ops[i] = vals[i].getDescripcion();
        String elegida = elegir("Especialidad del proveedor:", ops);
        if (elegida == null) return null;
        String esp = EspecialidadProveedor.OTROS.name();
        for (EspecialidadProveedor e : vals) {
            if (e.getDescripcion().equals(elegida)) esp = e.name();
        }
        List<PersonaDTO> provs = ctx.personas.listarProveedores(esp);
        if (provs.isEmpty()) {
            info("No hay proveedores de " + elegida + ". Queda sin asignar.");
            return null;
        }
        String[] nombres = new String[provs.size()];
        for (int i = 0; i < provs.size(); i++) {
            nombres[i] = provs.get(i).getNombre() + " " + provs.get(i).getApellido()
                    + " (DNI " + provs.get(i).getDni() + ")";
        }
        String sel = elegir("Proveedor:", nombres);
        if (sel == null) return null;
        for (int i = 0; i < nombres.length; i++) {
            if (nombres[i].equals(sel)) return provs.get(i).getDni();
        }
        return null;
    }

    private void verViviendas() {
        StringBuilder sb = new StringBuilder("=== Viviendas ===\n");
        List<ViviendaDTO> viviendas = ctx.estructura.listarViviendas();
        if (viviendas.isEmpty()) sb.append("(no hay viviendas)\n");
        for (ViviendaDTO v : viviendas) {
            sb.append("#").append(v.getId()).append("  Vivienda ").append(v.getNumero())
                    .append("  - residentes: ")
                    .append(v.getResidentes().isEmpty() ? "(ninguno)" : String.join(", ", v.getResidentes()))
                    .append("\n");
        }
        mostrar(sb.toString());
    }

    private void cambiarMiPassword() {
        String actual = pedir("Contraseña actual:");
        if (actual == null) return;
        String nueva = pedir("Nueva contraseña:");
        if (nueva == null) return;
        ctx.personas.cambiarPassword(usuario.getDni(), actual, nueva);
        info("Contraseña actualizada.");
    }

    private void verTodosLosReclamos() {
        StringBuilder sb = new StringBuilder("=== Reclamos ===\n");
        for (SolicitudDTO s : ctx.reclamos.listarReclamos()) {
            sb.append("#").append(s.getId()).append("  ").append(s.getTitulo())
                    .append("  [").append(s.getEstado()).append("]  DNI residente: ")
                    .append(s.getDniResidente()).append("\n");
        }
        sb.append("\n=== Incidentes ===\n");
        for (SolicitudDTO i : ctx.reclamos.listarIncidentes()) {
            sb.append("#").append(i.getId()).append("  ").append(i.getTitulo())
                    .append("  [").append(i.getEstado()).append("]\n");
        }
        mostrar(sb.toString());
    }

    private void cambiarEstadoReclamo() {
        Long id = pedirLong("ID del reclamo:");
        if (id == null) return;
        String estado = elegir("Nuevo estado:",
                new String[]{"PENDIENTE", "EN_PROCESO", "RESUELTO", "CERRADO"});
        if (estado == null) return;
        ctx.reclamos.cambiarEstado(id, estado);
        info("Estado cambiado a " + estado);
    }

    private void registrarTarea() {
        String tipo = elegir("Tipo de tarea:",
                new String[]{"Simple (asignada a un proveedor)", "Compuesta (con subtareas)"});
        if (tipo == null) return;

        if (tipo.startsWith("Simple")) {
            registrarTareaSimple();
        } else {
            registrarTareaCompuesta();
        }
    }

    private void registrarTareaSimple() {
        String titulo = pedir("Título de la tarea:");
        if (titulo == null) return;
        String desc = pedir("Descripción:");
        if (desc == null) return;
        String dni = elegirProveedorDni();
        TareaDTO dto = new TareaDTO();
        dto.setTitulo(titulo);
        dto.setDescripcion(desc);
        dto.setDniProveedor(dni);
        TareaDTO creada = ctx.mantenimiento.registrarTareaSimple(dto);
        info("Tarea simple registrada (id=" + creada.getId() + ")");
    }

    private void registrarTareaCompuesta() {
        String titulo = pedir("Título de la tarea (compuesta):");
        if (titulo == null) return;
        String desc = pedir("Descripción:");
        if (desc == null) return;
        TareaDTO dto = new TareaDTO();
        dto.setTitulo(titulo);
        dto.setDescripcion(desc);
        TareaDTO creada = ctx.mantenimiento.registrarTarea(dto);

        int n = 0;
        while (JOptionPane.showConfirmDialog(this, "¿Agregar una subtarea?", "Subtareas",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String st = pedir("Título de la subtarea:");
            if (st == null) {
                continue;
            }
            String sd = pedir("Descripción de la subtarea:");
            String dni = elegirProveedorDni();
            TareaDTO sub = new TareaDTO();
            sub.setTitulo(st);
            sub.setDescripcion(sd != null ? sd : "");
            sub.setDniProveedor(dni);
            ctx.mantenimiento.registrarSubtarea(creada.getId(), sub);
            n++;
        }
        info("Tarea compuesta creada (id=" + creada.getId() + ") con " + n + " subtarea(s).");
    }

    private void verHistorial() {
        StringBuilder sb = new StringBuilder("=== Historial de acciones ===\n");
        for (String linea : ctx.historial.listarAcciones()) sb.append(linea).append("\n");
        mostrar(sb.toString());
    }

    private void generarReporte() {
        String tipo = elegir("Tipo de reporte:", new String[]{"RECLAMOS", "ACCESOS", "INCIDENTES"});
        if (tipo == null) return;
        mostrar(ctx.reportes.generarReporte(tipo).getContenido());
    }

    // ===================== Acciones: Guardia =====================

    private void registrarIngreso() {
        String dni = pedir("DNI del actor:");
        if (dni == null) return;
        AccesoDTO reg = ctx.accesos.registrarIngreso(dni);
        info(reg.isPermitido()
                ? "Ingreso PERMITIDO y registrado (id=" + reg.getId() + ", " + reg.getTipoAcceso() + ")"
                : "Ingreso DENEGADO (la persona no tiene el acceso autorizado)");
    }

    private void registrarEgreso() {
        String dni = pedir("DNI del actor:");
        if (dni == null) return;
        ctx.accesos.registrarEgreso(dni);
        info("Egreso registrado");
    }

    private void registrarEmergencia() {
        String nombre = pedir("Nombre (emergencia):");
        if (nombre == null) return;
        String apellido = pedir("Apellido:");
        if (apellido == null) return;
        String dni = pedir("DNI:");
        if (dni == null) return;
        PersonaDTO p = ctx.accesos.registrarEmergencia(nombre, apellido, dni);
        info("Emergencia dada de alta: " + p.getNombre() + " " + p.getApellido()
                + ".\nAhora registrá su ingreso y luego su egreso.");
    }

    private void validarAcceso() {
        String dni = pedir("DNI del actor:");
        if (dni == null) return;
        boolean ok = ctx.accesos.validarAcceso(dni);
        info(ok ? "Acceso PERMITIDO" : "Acceso DENEGADO (sin autorización o no cumple el protocolo)");
    }

    private void registrarIncidente() {
        String titulo = pedir("Título del incidente:");
        if (titulo == null) return;
        String desc = pedir("Descripción:");
        if (desc == null) return;
        String prioridad = elegir("Prioridad:", new String[]{"BAJA", "MEDIA", "ALTA"});
        if (prioridad == null) return;
        boolean urgente = JOptionPane.showConfirmDialog(this, "¿Es urgente?", "Incidente",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        SolicitudDTO dto = new SolicitudDTO();
        dto.setTitulo(titulo);
        dto.setDescripcion(desc);
        dto.setPrioridad(prioridad);
        dto.setUrgencia(urgente);
        dto.setDniGuardia(usuario.getDni());
        SolicitudDTO creado = ctx.reclamos.registrarIncidente(dto);
        info("Incidente de seguridad registrado (id=" + creado.getId() + ")");
    }

    private void verIncidentes() {
        StringBuilder sb = new StringBuilder("=== Incidentes de seguridad ===\n");
        List<SolicitudDTO> incidentes = ctx.reclamos.listarIncidentes();
        if (incidentes.isEmpty()) sb.append("(no hay incidentes)\n");
        for (SolicitudDTO i : incidentes) {
            sb.append("#").append(i.getId()).append("  ").append(i.getTitulo())
                    .append("  [").append(i.getEstado()).append("]  (")
                    .append(i.getCategoria()).append("/").append(i.getPrioridad()).append(")\n");
        }
        mostrar(sb.toString());
    }

    private void autorizarIngreso() {
        int existe = JOptionPane.showConfirmDialog(this, "¿El ingresante ya existe en el sistema?",
                "Autorizar ingreso", JOptionPane.YES_NO_CANCEL_OPTION);
        PersonaDTO aut;
        if (existe == JOptionPane.YES_OPTION) {
            List<PersonaDTO> noResidentes = ctx.accesos.listarNoResidentes();
            if (noResidentes.isEmpty()) {
                info("No hay personas no residentes registradas.");
                return;
            }
            String[] ops = new String[noResidentes.size()];
            for (int i = 0; i < noResidentes.size(); i++) {
                PersonaDTO p = noResidentes.get(i);
                ops[i] = p.getNombre() + " " + p.getApellido() + " (DNI " + p.getDni() + ", " + p.getTipo() + ")";
            }
            String sel = elegir("Elegí la persona:", ops);
            if (sel == null) return;
            String dni = noResidentes.get(java.util.Arrays.asList(ops).indexOf(sel)).getDni();
            aut = ctx.accesos.autorizarExistente(dni);
        } else if (existe == JOptionPane.NO_OPTION) {
            String cat = elegir("Categoría:", new String[]{"FAMILIAR", "VISITANTE"});
            if (cat == null) return;
            String nombre = pedir("Nombre:");
            if (nombre == null) return;
            String apellido = pedir("Apellido:");
            if (apellido == null) return;
            String dni = pedir("DNI:");
            if (dni == null) return;
            aut = ctx.accesos.autorizarNuevo(cat, nombre, apellido, dni);
        } else {
            return;
        }
        info("Ingreso autorizado para " + aut.getNombre() + " " + aut.getApellido()
                + " (" + aut.getTipo() + "). El guardia ya puede validarlo.");
    }

    // ===================== Acciones: Proveedor =====================

    private void verMisTareas() {
        StringBuilder sb = new StringBuilder("=== Mis tareas asignadas ===\n");
        List<TareaDTO> mias = ctx.mantenimiento.listarTareasDeProveedor(usuario.getDni());
        if (mias.isEmpty()) sb.append("(no tenés tareas asignadas)\n");
        for (TareaDTO t : mias) {
            sb.append("#").append(t.getId()).append("  ").append(t.getTitulo())
                    .append("  [").append(t.getEstado()).append("]\n");
        }
        mostrar(sb.toString());
    }

    private void cambiarEstadoTarea() {
        Long id = pedirLong("ID de la tarea:");
        if (id == null) return;
        String estado = elegir("Nuevo estado:", new String[]{"PENDIENTE", "EN_PROCESO", "FINALIZADA"});
        if (estado == null) return;
        ctx.mantenimiento.cambiarEstado(id, estado);
        info("Estado de la tarea cambiado a " + estado);
    }

    private void verHistorialTrabajos() {
        StringBuilder sb = new StringBuilder("=== Historial de trabajos ===\n");
        List<String> lineas = ctx.historial.listarAccionesDe(usuario.getDni());
        if (lineas.isEmpty()) sb.append("(sin registros)\n");
        for (String l : lineas) sb.append(l).append("\n");
        mostrar(sb.toString());
    }

    // ===================== Helpers UI =====================

    private void agregar(String texto, Runnable accion) {
        JButton b = new JButton(texto);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        b.setFocusPainted(false);
        b.addActionListener(e -> ejecutar(accion));
        botones.add(b);
        botones.add(Box.createVerticalStrut(8));
    }

    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (Exception ex) {
            error(ex.getMessage());
        }
    }

    private void cerrarSesion() {
        ctx.autenticacion.logout();
        dispose();
        new LoginView(ctx).setVisible(true);
    }

    private void mostrar(String texto) {
        salida.setText(texto);
        salida.setCaretPosition(0);
    }

    private String pedir(String msg) {
        String v = JOptionPane.showInputDialog(this, msg);
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }

    private Integer pedirEntero(String msg) {
        String v = pedir(msg);
        if (v == null) return null;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            error("Número entero inválido");
            return null;
        }
    }

    private Long pedirLong(String msg) {
        String v = pedir(msg);
        if (v == null) return null;
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            error("ID numérico inválido");
            return null;
        }
    }

    private String elegir(String msg, String[] opciones) {
        return (String) JOptionPane.showInputDialog(this, msg, "Selección",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
