package com.barrio.presentacion.consola;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.barrio.aplicacion.gestores.GestorAutenticacion;
import com.barrio.dominio.personas.EspecialidadProveedor;
import com.barrio.dominio.personas.Persona;
import com.barrio.presentacion.controladores.ControladorAccesos;
import com.barrio.presentacion.controladores.ControladorEstructura;
import com.barrio.presentacion.controladores.ControladorHistorial;
import com.barrio.presentacion.controladores.ControladorMantenimiento;
import com.barrio.presentacion.controladores.ControladorNotificaciones;
import com.barrio.presentacion.controladores.ControladorPersonas;
import com.barrio.presentacion.controladores.ControladorReclamos;
import com.barrio.presentacion.controladores.ControladorReportes;
import com.barrio.presentacion.dto.AccesoDTO;
import com.barrio.presentacion.dto.NotificacionDTO;
import com.barrio.presentacion.dto.PersonaDTO;
import com.barrio.presentacion.dto.SolicitudDTO;
import com.barrio.presentacion.dto.TareaDTO;
import com.barrio.presentacion.dto.ViviendaDTO;

/**
 * Interfaz de consola con login y menús por rol.
 * Delega toda la lógica en los controladores (capa de presentación → controladores → gestores).
 */
public class ConsolaUI {

    private final GestorAutenticacion autenticacion;
    private final ControladorPersonas ctrlPersonas;
    private final ControladorReclamos ctrlReclamos;
    private final ControladorAccesos ctrlAccesos;
    private final ControladorMantenimiento ctrlMantenimiento;
    private final ControladorNotificaciones ctrlNotificaciones;
    private final ControladorReportes ctrlReportes;
    private final ControladorHistorial ctrlHistorial;
    private final ControladorEstructura ctrlEstructura;
    private final Scanner scanner;

    public ConsolaUI(GestorAutenticacion autenticacion,
                     ControladorPersonas ctrlPersonas,
                     ControladorReclamos ctrlReclamos,
                     ControladorAccesos ctrlAccesos,
                     ControladorMantenimiento ctrlMantenimiento,
                     ControladorNotificaciones ctrlNotificaciones,
                     ControladorReportes ctrlReportes,
                     ControladorHistorial ctrlHistorial,
                     ControladorEstructura ctrlEstructura) {
        this.autenticacion = autenticacion;
        this.ctrlPersonas = ctrlPersonas;
        this.ctrlReclamos = ctrlReclamos;
        this.ctrlAccesos = ctrlAccesos;
        this.ctrlMantenimiento = ctrlMantenimiento;
        this.ctrlNotificaciones = ctrlNotificaciones;
        this.ctrlReportes = ctrlReportes;
        this.ctrlHistorial = ctrlHistorial;
        this.ctrlEstructura = ctrlEstructura;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    SISTEMA GESTIÓN BARRIO CERRADO      ║");
        System.out.println("║    TPO - Programación de Sistemas      ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("\n⚠️  En desarrollo - Contraseñas sin encriptación");
        System.out.println("Cuentas de prueba:");
        System.out.println("  admin@barrio.com / admin123");
        System.out.println("  residente@barrio.com / res123");
        System.out.println("  guardia@barrio.com / guard123");
        System.out.println("  proveedor@barrio.com / prov123");

        while (scanner.hasNextLine()) {
            Persona usuario = mostrarMenuLogin();
            if (usuario == null) {
                continue;
            }
            sesion(usuario);
        }
        System.out.println("\n¡Hasta luego!");
    }

    public Persona mostrarMenuLogin() {
        System.out.println("\n=== LOGIN ===");
        String email = leer("Email: ");
        String password = leer("Contraseña: ");

        Persona persona = autenticacion.login(email, password);
        if (persona != null) {
            System.out.println("\n✓ Bienvenido " + persona.getNombreCompleto());
            return persona;
        }
        System.out.println("\n✗ Email o contraseña incorrectos");
        return null;
    }

    private void sesion(Persona usuario) {
        while (autenticacion.estaLogueado()) {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("  Usuario: " + usuario.getNombreCompleto()
                    + " (" + usuario.getClass().getSimpleName() + ")");
            System.out.println("╚════════════════════════════════════════╝");

            if (autenticacion.esResidente()) {
                mostrarOpcionesResidente();
            } else if (autenticacion.esAdministrador()) {
                mostrarOpcionesAdmin();
            } else if (autenticacion.esGuardia()) {
                mostrarOpcionesGuardia();
            } else if (autenticacion.esProveedor()) {
                mostrarOpcionesProveedor();
            }
            System.out.println("0. Logout");

            if (!scanner.hasNextLine()) {
                break;
            }
            String opcion = leer("\nOpción: ");
            if ("0".equals(opcion)) {
                autenticacion.logout();
                System.out.println("✓ Sesión cerrada");
                break;
            }

            try {
                if (autenticacion.esResidente()) {
                    ejecutarResidente(usuario, opcion);
                } else if (autenticacion.esAdministrador()) {
                    ejecutarAdmin(opcion);
                } else if (autenticacion.esGuardia()) {
                    ejecutarGuardia(opcion);
                } else if (autenticacion.esProveedor()) {
                    ejecutarProveedor(usuario, opcion);
                }
            } catch (Exception e) {
                System.out.println("✗ Error: " + e.getMessage());
            }
        }
    }

    // ===================== RESIDENTE =====================

    public void mostrarOpcionesResidente() {
        System.out.println("\n1. Registrar reclamo");
        System.out.println("2. Ver mis reclamos");
        System.out.println("3. Ver notificaciones");
        System.out.println("4. Cambiar mi contraseña");
        System.out.println("5. Cambiar mi método de notificación");
    }

    private void ejecutarResidente(Persona usuario, String opcion) {
        switch (opcion) {
            case "1": {
                SolicitudDTO dto = new SolicitudDTO();
                dto.setTitulo(leer("Título: "));
                dto.setDescripcion(leer("Descripción: "));
                dto.setCategoria(elegirCategoria());
                dto.setPrioridad(elegirPrioridad());
                dto.setDniResidente(usuario.getDni());
                SolicitudDTO creado = ctrlReclamos.registrarReclamo(dto);
                System.out.println("✓ Reclamo registrado (id=" + creado.getId() + ")");
                break;
            }
            case "2": {
                boolean alguno = false;
                for (SolicitudDTO s : ctrlReclamos.listarReclamos()) {
                    if (usuario.getDni() != null && usuario.getDni().equals(s.getDniResidente())) {
                        alguno = true;
                        System.out.println("  #" + s.getId() + " " + s.getTitulo() + " [" + s.getEstado() + "]");
                    }
                }
                if (!alguno) {
                    System.out.println("  (no tenés reclamos)");
                }
                break;
            }
            case "3": {
                List<NotificacionDTO> notis = ctrlNotificaciones.listarNotificacionesDe(usuario.getDni());
                if (notis.isEmpty()) {
                    System.out.println("  (no tenés notificaciones)");
                }
                for (NotificacionDTO n : notis) {
                    System.out.println("  - [" + n.getCanal() + "] " + n.getMensaje());
                }
                break;
            }
            case "4": {
                cambiarMiPassword(usuario);
                break;
            }
            case "5": {
                cambiarMiNotificacion(usuario);
                break;
            }
            default:
                System.out.println("✗ Opción inválida");
        }
    }

    private void cambiarMiPassword(Persona usuario) {
        String actual = leer("Contraseña actual: ");
        String nueva = leer("Nueva contraseña: ");
        ctrlPersonas.cambiarPassword(usuario.getDni(), actual, nueva);
        System.out.println("✓ Contraseña actualizada");
    }

    // ===================== ADMINISTRADOR =====================

    public void mostrarOpcionesAdmin() {
        System.out.println("\n— Residentes y viviendas (CU-01) —");
        System.out.println("1. Registrar residente");
        System.out.println("2. Registrar vivienda");
        System.out.println("3. Asignar residente a vivienda");
        System.out.println("4. Ver viviendas");
        System.out.println("— Proveedores y personal (CU-02) —");
        System.out.println("5. Registrar proveedor");
        System.out.println("6. Registrar guardia / personal de seguridad");
        System.out.println("— Accesos (CU-05) —");
        System.out.println("7. Autorizar ingreso (familiar / visitante / proveedor)");
        System.out.println("— Reclamos (CU-04) —");
        System.out.println("8. Ver todos los reclamos");
        System.out.println("9. Cambiar estado de un reclamo");
        System.out.println("— Mantenimiento (CU-07 / CU-08) —");
        System.out.println("10. Registrar tarea de mantenimiento");
        System.out.println("11. Asignar proveedor a una tarea");
        System.out.println("12. Ver tareas de mantenimiento");
        System.out.println("13. Cambiar estado de una tarea");
        System.out.println("— Notificaciones, historial y reportes (CU-09/10/11) —");
        System.out.println("14. Ver notificaciones");
        System.out.println("15. Ver historial");
        System.out.println("16. Generar reportes");
        System.out.println("— Otros —");
        System.out.println("17. Asignar contraseña a un usuario");
        System.out.println("18. Cambiar mi método de notificación");
    }

    private void ejecutarAdmin(String opcion) {
        switch (opcion) {
            case "1": {
                PersonaDTO dto = new PersonaDTO();
                dto.setNombre(leer("Nombre: "));
                dto.setApellido(leer("Apellido: "));
                dto.setDni(leer("DNI: "));
                dto.setEmail(leer("Email: "));
                dto.setPassword(leer("Contraseña: "));
                dto.setNumeroLote(leerInt("Número de lote: "));
                PersonaDTO creado = ctrlPersonas.registrarResidente(dto);
                System.out.println("✓ Residente registrado (id=" + creado.getId()
                        + "). Ya puede iniciar sesión con " + creado.getEmail());
                break;
            }
            case "2": {
                String numero = leer("Número/identificación de la vivienda: ");
                ViviendaDTO v = ctrlEstructura.registrarVivienda(numero);
                System.out.println("✓ Vivienda registrada (id=" + v.getId() + ", número " + v.getNumero() + ")");
                break;
            }
            case "3": {
                Long idViv = leerLong("ID de la vivienda: ");
                String dni = leer("DNI del residente: ");
                ctrlEstructura.asignarResidente(idViv, dni);
                System.out.println("✓ Residente asignado a la vivienda " + idViv);
                break;
            }
            case "4": {
                List<ViviendaDTO> viviendas = ctrlEstructura.listarViviendas();
                if (viviendas.isEmpty()) {
                    System.out.println("  (no hay viviendas)");
                }
                for (ViviendaDTO v : viviendas) {
                    System.out.println("  #" + v.getId() + " Vivienda " + v.getNumero()
                            + " - residentes: " + (v.getResidentes().isEmpty() ? "(ninguno)" : String.join(", ", v.getResidentes())));
                }
                break;
            }
            case "5": {
                PersonaDTO dto = new PersonaDTO();
                dto.setNombre(leer("Nombre: "));
                dto.setApellido(leer("Apellido: "));
                dto.setDni(leer("DNI: "));
                dto.setEmail(leer("Email: "));
                dto.setPassword(leer("Contraseña: "));
                dto.setEspecialidad(elegirEspecialidad());
                PersonaDTO creado = ctrlPersonas.registrarProveedor(dto);
                System.out.println("✓ Proveedor registrado (id=" + creado.getId()
                        + "). Ya puede iniciar sesión con " + creado.getEmail());
                break;
            }
            case "6": {
                PersonaDTO dto = new PersonaDTO();
                dto.setNombre(leer("Nombre: "));
                dto.setApellido(leer("Apellido: "));
                dto.setDni(leer("DNI: "));
                dto.setEmail(leer("Email: "));
                dto.setPassword(leer("Contraseña: "));
                dto.setLegajo(leer("Legajo: "));
                PersonaDTO creado = ctrlPersonas.registrarGuardia(dto);
                System.out.println("✓ Guardia registrado (id=" + creado.getId()
                        + "). Ya puede iniciar sesión con " + creado.getEmail());
                break;
            }
            case "8": {
                List<SolicitudDTO> reclamos = ctrlReclamos.listarReclamos();
                if (reclamos.isEmpty()) {
                    System.out.println("  (no hay reclamos)");
                }
                for (SolicitudDTO s : reclamos) {
                    System.out.println("  #" + s.getId() + " " + s.getTitulo() + " [" + s.getEstado()
                            + "] (" + s.getCategoria() + "/" + s.getPrioridad() + ")"
                            + " - DNI residente: " + s.getDniResidente());
                }
                for (SolicitudDTO i : ctrlReclamos.listarIncidentes()) {
                    System.out.println("  #" + i.getId() + " [INCIDENTE] " + i.getTitulo() + " [" + i.getEstado() + "]");
                }
                break;
            }
            case "9": {
                Long id = leerLong("ID del reclamo: ");
                String estado = elegirEstadoSolicitud();
                ctrlReclamos.cambiarEstado(id, estado);
                System.out.println("✓ Estado cambiado a " + estado);
                break;
            }
            case "10": {
                String tipo = leer("Tipo: 1) Simple (asignada a un proveedor)  2) Compuesta (con subtareas): ");
                if ("1".equals(tipo)) {
                    TareaDTO dto = new TareaDTO();
                    dto.setTitulo(leer("Título: "));
                    dto.setDescripcion(leer("Descripción: "));
                    dto.setDniProveedor(elegirProveedorPorEspecialidad());
                    TareaDTO creada = ctrlMantenimiento.registrarTareaSimple(dto);
                    System.out.println("✓ Tarea simple registrada (id=" + creada.getId() + ")");
                } else {
                    TareaDTO dto = new TareaDTO();
                    dto.setTitulo(leer("Título de la tarea (compuesta): "));
                    dto.setDescripcion(leer("Descripción: "));
                    TareaDTO creada = ctrlMantenimiento.registrarTarea(dto);
                    System.out.println("✓ Tarea compuesta registrada (id=" + creada.getId() + ")");
                    int n = 0;
                    while ("s".equalsIgnoreCase(leer("¿Agregar subtarea? (s/n): "))) {
                        TareaDTO sub = new TareaDTO();
                        sub.setTitulo(leer("  Título subtarea: "));
                        sub.setDescripcion(leer("  Descripción: "));
                        sub.setDniProveedor(elegirProveedorPorEspecialidad());
                        ctrlMantenimiento.registrarSubtarea(creada.getId(), sub);
                        n++;
                        System.out.println("  ✓ Subtarea agregada");
                    }
                    System.out.println("✓ Tarea compuesta con " + n + " subtarea(s)");
                }
                break;
            }
            case "11": {
                Long idTarea = leerLong("ID de la tarea a asignar: ");
                String dniProv = elegirProveedorPorEspecialidad();
                if (dniProv == null) {
                    System.out.println("✗ No se seleccionó proveedor");
                    break;
                }
                ctrlMantenimiento.asignarPersonal(idTarea, dniProv);
                System.out.println("✓ Proveedor asignado a la tarea " + idTarea);
                break;
            }
            case "12": {
                List<TareaDTO> tareas = ctrlMantenimiento.listarTareas();
                if (tareas.isEmpty()) {
                    System.out.println("  (no hay tareas)");
                }
                for (TareaDTO t : tareas) {
                    System.out.println("  #" + t.getId() + " " + t.getTitulo() + " [" + t.getEstado()
                            + "] (" + t.getTipo() + ")"
                            + (t.getDniProveedor() != null ? " - proveedor DNI " + t.getDniProveedor() : ""));
                }
                break;
            }
            case "13": {
                Long id = leerLong("ID de la tarea: ");
                String estado = elegirEstadoTarea();
                ctrlMantenimiento.cambiarEstado(id, estado);
                System.out.println("✓ Estado de la tarea cambiado a " + estado);
                break;
            }
            case "14": {
                String dni = autenticacion.getUsuarioActual().getDni();
                List<NotificacionDTO> notis = ctrlNotificaciones.listarNotificacionesDe(dni);
                if (notis.isEmpty()) {
                    System.out.println("  (no tenés notificaciones)");
                }
                for (NotificacionDTO n : notis) {
                    System.out.println("  - [" + n.getCanal() + "] " + n.getMensaje());
                }
                break;
            }
            case "15": {
                List<String> historial = ctrlHistorial.listarAcciones();
                if (historial.isEmpty()) {
                    System.out.println("  (historial vacío)");
                }
                for (String linea : historial) {
                    System.out.println("  " + linea);
                }
                break;
            }
            case "16": {
                String tipo = elegirTipoReporte();
                System.out.println("\n" + ctrlReportes.generarReporte(tipo).getContenido());
                break;
            }
            case "17": {
                String dni = leer("DNI del usuario: ");
                String pass = leer("Nueva contraseña: ");
                PersonaDTO p = ctrlPersonas.asignarPassword(dni, pass);
                System.out.println("✓ Contraseña asignada a " + p.getNombre() + " " + p.getApellido());
                break;
            }
            case "18": {
                cambiarMiNotificacion(autenticacion.getUsuarioActual());
                break;
            }
            case "7": {
                PersonaDTO aut;
                if ("s".equalsIgnoreCase(leer("¿El ingresante ya existe en el sistema? (s/n): "))) {
                    List<PersonaDTO> noResidentes = ctrlAccesos.listarNoResidentes();
                    if (noResidentes.isEmpty()) {
                        System.out.println("  (no hay personas no residentes registradas)");
                        break;
                    }
                    System.out.println("Personas no residentes:");
                    for (PersonaDTO p : noResidentes) {
                        System.out.println("  - " + p.getNombre() + " " + p.getApellido() + " (DNI " + p.getDni()
                                + ", " + p.getTipo() + ")" + (p.isAccesoAutorizado() ? " [acceso vigente]" : ""));
                    }
                    String dni = leer("DNI del ingresante: ");
                    aut = ctrlAccesos.autorizarExistente(dni);
                } else {
                    String cat = "1".equals(leer("Categoría: 1) Familiar  2) Visitante\n> ")) ? "FAMILIAR" : "VISITANTE";
                    String nombre = leer("Nombre: ");
                    String apellido = leer("Apellido: ");
                    String dni = leer("DNI: ");
                    aut = ctrlAccesos.autorizarNuevo(cat, nombre, apellido, dni);
                }
                System.out.println("✓ Ingreso autorizado para " + aut.getNombre() + " " + aut.getApellido()
                        + " (" + aut.getTipo() + ")");
                break;
            }
            default:
                System.out.println("✗ Opción inválida");
        }
    }

    // ===================== GUARDIA =====================

    public void mostrarOpcionesGuardia() {
        System.out.println("\n1. Registrar ingreso");
        System.out.println("2. Registrar egreso");
        System.out.println("3. Validar acceso");
        System.out.println("4. Registrar emergencia");
        System.out.println("5. Registrar incidente de seguridad");
        System.out.println("6. Ver incidentes de seguridad");
    }

    private void ejecutarGuardia(String opcion) {
        switch (opcion) {
            case "1": {
                String dni = leer("DNI del actor: ");
                AccesoDTO reg = ctrlAccesos.registrarIngreso(dni);
                System.out.println(reg.isPermitido()
                        ? "✓ Ingreso PERMITIDO y registrado (id=" + reg.getId() + ", " + reg.getTipoAcceso() + ")"
                        : "✗ Ingreso DENEGADO (la persona no tiene el acceso autorizado)");
                break;
            }
            case "2": {
                String dni = leer("DNI del actor: ");
                ctrlAccesos.registrarEgreso(dni);
                System.out.println("✓ Egreso registrado");
                break;
            }
            case "3": {
                String dni = leer("DNI del actor: ");
                boolean ok = ctrlAccesos.validarAcceso(dni);
                System.out.println(ok ? "✓ Acceso PERMITIDO" : "✗ Acceso DENEGADO (sin autorización o no cumple el protocolo)");
                break;
            }
            case "4": {
                String nombre = leer("Nombre (emergencia): ");
                String apellido = leer("Apellido: ");
                String dni = leer("DNI: ");
                PersonaDTO p = ctrlAccesos.registrarEmergencia(nombre, apellido, dni);
                System.out.println("✓ Emergencia dada de alta: " + p.getNombre() + " " + p.getApellido()
                        + ". Ahora registrá su ingreso (opción 1) y luego su egreso (opción 2).");
                break;
            }
            case "5": {
                SolicitudDTO dto = new SolicitudDTO();
                dto.setTitulo(leer("Título del incidente: "));
                dto.setDescripcion(leer("Descripción: "));
                dto.setPrioridad(elegirPrioridad());
                dto.setUrgencia("s".equalsIgnoreCase(leer("¿Es urgente? (s/n): ")));
                dto.setDniGuardia(autenticacion.getUsuarioActual().getDni());
                SolicitudDTO creado = ctrlReclamos.registrarIncidente(dto);
                System.out.println("✓ Incidente de seguridad registrado (id=" + creado.getId() + ")");
                break;
            }
            case "6": {
                List<SolicitudDTO> incidentes = ctrlReclamos.listarIncidentes();
                if (incidentes.isEmpty()) {
                    System.out.println("  (no hay incidentes)");
                }
                for (SolicitudDTO i : incidentes) {
                    System.out.println("  #" + i.getId() + " " + i.getTitulo() + " [" + i.getEstado()
                            + "] (" + i.getCategoria() + "/" + i.getPrioridad() + ")");
                }
                break;
            }
            default:
                System.out.println("✗ Opción inválida");
        }
    }

    // ===================== PROVEEDOR =====================

    public void mostrarOpcionesProveedor() {
        System.out.println("\n1. Ver mis tareas asignadas");
        System.out.println("2. Cambiar estado de tarea");
        System.out.println("3. Ver historial de trabajos");
        System.out.println("4. Cambiar mi contraseña");
    }

    private void ejecutarProveedor(Persona usuario, String opcion) {
        switch (opcion) {
            case "1": {
                List<TareaDTO> mias = ctrlMantenimiento.listarTareasDeProveedor(usuario.getDni());
                if (mias.isEmpty()) {
                    System.out.println("  (no tenés tareas asignadas)");
                }
                for (TareaDTO t : mias) {
                    System.out.println("  #" + t.getId() + " " + t.getTitulo() + " [" + t.getEstado() + "]");
                }
                break;
            }
            case "2": {
                Long id = leerLong("ID de la tarea: ");
                String estado = elegirEstadoTarea();
                ctrlMantenimiento.cambiarEstado(id, estado);
                System.out.println("✓ Estado de la tarea cambiado a " + estado);
                break;
            }
            case "3": {
                List<String> historial = ctrlHistorial.listarAccionesDe(usuario.getDni());
                if (historial.isEmpty()) {
                    System.out.println("  (sin registros)");
                }
                for (String linea : historial) {
                    System.out.println("  " + linea);
                }
                break;
            }
            case "4": {
                cambiarMiPassword(usuario);
                break;
            }
            default:
                System.out.println("✗ Opción inválida");
        }
    }

    // ===================== Helpers de entrada / menús =====================

    private String elegirCategoria() {
        System.out.println("Categoría: 1) MANTENIMIENTO  2) SEGURIDAD  3) ADMINISTRATIVA");
        switch (leer("> ")) {
            case "2": return "SEGURIDAD";
            case "3": return "ADMINISTRATIVA";
            default: return "MANTENIMIENTO";
        }
    }

    private String elegirPrioridad() {
        System.out.println("Prioridad: 1) BAJA  2) MEDIA  3) ALTA");
        switch (leer("> ")) {
            case "3": return "ALTA";
            case "1": return "BAJA";
            default: return "MEDIA";
        }
    }

    private String elegirEstadoSolicitud() {
        System.out.println("Nuevo estado: 1) PENDIENTE  2) EN_PROCESO  3) RESUELTO  4) CERRADO");
        switch (leer("> ")) {
            case "2": return "EN_PROCESO";
            case "3": return "RESUELTO";
            case "4": return "CERRADO";
            default: return "PENDIENTE";
        }
    }


    private String elegirEstadoTarea() {
        System.out.println("Estado: 1) PENDIENTE  2) EN_PROCESO  3) FINALIZADA");
        switch (leer("> ")) {
            case "2": return "EN_PROCESO";
            case "3": return "FINALIZADA";
            default: return "PENDIENTE";
        }
    }

    private String elegirEspecialidad() {
        EspecialidadProveedor[] vals = EspecialidadProveedor.values();
        StringBuilder sb = new StringBuilder("Especialidad:");
        for (int i = 0; i < vals.length; i++) {
            sb.append("  ").append(i + 1).append(") ").append(vals[i].getDescripcion());
        }
        System.out.println(sb.toString());
        try {
            int idx = Integer.parseInt(leer("> ")) - 1;
            if (idx >= 0 && idx < vals.length) {
                return vals[idx].name();
            }
        } catch (NumberFormatException ignored) {
            // cae en OTROS
        }
        return EspecialidadProveedor.OTROS.name();
    }

    /** Elige una especialidad, lista los proveedores de esa especialidad y devuelve el DNI elegido (o null). */
    private String elegirProveedorPorEspecialidad() {
        String esp = elegirEspecialidad();
        List<PersonaDTO> provs = ctrlPersonas.listarProveedores(esp);
        if (provs.isEmpty()) {
            System.out.println("  (no hay proveedores de " + esp + ") → queda sin asignar");
            return null;
        }
        System.out.println("Proveedores de " + esp + ":");
        for (int i = 0; i < provs.size(); i++) {
            PersonaDTO p = provs.get(i);
            System.out.println("  " + (i + 1) + ") " + p.getNombre() + " " + p.getApellido() + " (DNI " + p.getDni() + ")");
        }
        try {
            int idx = Integer.parseInt(leer("> ")) - 1;
            if (idx >= 0 && idx < provs.size()) {
                return provs.get(idx).getDni();
            }
        } catch (NumberFormatException ignored) {
            // queda sin asignar
        }
        return null;
    }

    /** El propio usuario elige por qué medio quiere recibir notificaciones. */
    private void cambiarMiNotificacion(Persona usuario) {
        String canal = "2".equals(leer("¿Cómo querés recibir notificaciones? 1) EMAIL  2) SMS: ")) ? "SMS" : "EMAIL";
        ctrlPersonas.cambiarCanalNotificacion(usuario.getDni(), canal);
        System.out.println("✓ Vas a recibir notificaciones por " + canal);
    }

    private String elegirTipoReporte() {
        System.out.println("Reporte: 1) RECLAMOS  2) ACCESOS  3) INCIDENTES");
        switch (leer("> ")) {
            case "2": return "ACCESOS";
            case "3": return "INCIDENTES";
            default: return "RECLAMOS";
        }
    }

    private String leer(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int leerInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(leer(prompt));
            } catch (NumberFormatException e) {
                System.out.println("✗ Ingresá un número entero válido");
            }
        }
    }

    private Long leerLong(String prompt) {
        while (true) {
            try {
                return Long.parseLong(leer(prompt));
            } catch (NumberFormatException e) {
                System.out.println("✗ Ingresá un ID numérico válido");
            }
        }
    }
}
