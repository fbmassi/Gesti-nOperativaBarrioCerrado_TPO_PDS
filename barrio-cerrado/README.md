# Sistema de Gestion de Barrio Cerrado

Fase actual: **estructura con Lombok** (esqueleto de clases, sin logica de negocio).

## Que esta hecho

- **Estructura de paquetes** completa bajo `com.barrio` (dominio, aplicacion, infraestructura, presentacion).
- **Clases de dominio** con Lombok:
  - `personas`: `Persona` (abstracta), `Residente`, `Administrador`, `Guardia`, `Proveedor`, `Visitante`.
  - `reclamos`: `Solicitud` (abstracta), `Reclamo`, `IncidenteSeguridad`, interfaz `EstadoSolicitud` con implementaciones `Pendiente`, `EnProceso`, `Resuelto`, `Cerrado`.
  - `categorias`: enums `CategoriaSolicitud`, `PrioridadSolicitud`.
  - `estructura`: `Lote`, `EspacioComun`, `Vivienda`, `Barrio`.
  - `acceso`: `RegistroAcceso`, interfaz `ProtocoloAcceso` con implementaciones `ProtocoloFamiliar`, `ProtocoloVisitante`, `ProtocoloProveedor`, `ProtocoloEmergencia`.
  - `mantenimiento`: `TareaDeMantenimiento` (abstracta, implementa Sujeto), `TareaSimple`, `TareaCompuesta`.
  - `notificaciones`: `Notificacion`, interfaz `CanalNotificacion` con implementaciones `CanalEmail`, `CanalSMS`.
  - `eventos`: `Evento`, interfaces `Sujeto`, `Observador`.
- **Capa de aplicacion**:
  - 6 gestores: `GestorReclamos`, `GestorAcceso`, `GestorMantenimiento`, `GestorNotificaciones`, `GestorReportes`, `GestorPersonas`.
  - `FabricaProtocolos` (Factory de protocolos de acceso).
  - `HistorialAcciones` (Singleton) con `EntradaHistorial`.
- **Infraestructura**: interfaz generica `Repositorio<T>`, 5 repositorios concretos vacios (`RepositorioPersonas`, `RepositorioSolicitudes`, `RepositorioAccesos`, `RepositorioNotificaciones`, `RepositorioTareas`), y `ConexionH2` (unico archivo con codigo real: pool de conexiones + schema de 9 tablas).
- **Presentacion**: 6 controladores (`ControladorReclamos`, `ControladorAcceso`, `ControladorMantenimiento`, `ControladorNotificaciones`, `ControladorReportes`, `ControladorPersonas`), 7 DTOs (`SolicitudDTO`, `ReclamoDTO`, `IncidenteSeguridadDTO`, `AccesoDTO`, `TareaDTO`, `NotificacionDTO`, `PersonaDTO`), y `ConsolaUI` con `main()`.

## Que se elimino respecto a la v1

- `ServicioAutenticacion` (no estaba en los diagramas).
- `PersonalMantenimiento` (renombrado a `Proveedor`).
- `SolicitudMantenimiento` (reemplazado por `TareaDeMantenimiento` abstracta + `TareaSimple`/`TareaCompuesta`).
- `Accion` (renombrada a `EntradaHistorial`).
- Paquete `com.barrio.comun.excepciones` completo (no estaba en los diagramas).
- `GestorAccesos` (renombrado a `GestorAcceso`).
- `ControladorAccesos` (renombrado a `ControladorAcceso`).
- `ResidenteDTO` y `SolicitudMantenimientoDTO` (reemplazados por `PersonaDTO` y `TareaDTO`).

## Que falta

- Cuerpos de metodos y logica de negocio en dominio, gestores y controladores.
- Logica de transiciones de estado en `Pendiente`, `EnProceso`, `Resuelto`, `Cerrado`.
- Logica de `FabricaProtocolos.crearProtocolo()` con el switch por tipo.
- Recursion de `TareaCompuesta.cambiarEstado()` sobre subtareas.
- Persistencia real en los repositorios (SQL sobre `ConexionH2`).
- Mapeo entidad - DTO en los controladores.
- Implementacion de la consola interactiva y la inyeccion de dependencias entre capas.

## Patrones de diseno previstos

| Patron | Donde |
|---|---|
| **State** | `EstadoSolicitud` con `Pendiente`, `EnProceso`, `Resuelto`, `Cerrado` - controlan transiciones validas de `Solicitud` y `TareaDeMantenimiento`. |
| **Strategy** | `ProtocoloAcceso` con `ProtocoloFamiliar`, `ProtocoloVisitante`, `ProtocoloProveedor`, `ProtocoloEmergencia` - protocolos intercambiables de acceso. Tambien `CanalNotificacion` con `CanalEmail`/`CanalSMS`. |
| **Observer** | `Sujeto` / `Observador` en `dominio.eventos` - `TareaDeMantenimiento` implementa `Sujeto` para notificar cambios de estado. |
| **Singleton** | `HistorialAcciones` - punto unico de registro de trazabilidad (instancia `final`). |
| **Composite** | `TareaDeMantenimiento` / `TareaSimple` / `TareaCompuesta` - permite tareas anidadas con propagacion de estado. |
| **Factory** | `FabricaProtocolos` - crea el protocolo de acceso segun el tipo recibido ("FAMILIAR", "VISITANTE", etc.). |
| **Repository** | `Repositorio<T>` + implementaciones en `infraestructura.persistencia` - desacoplan el dominio de H2. |
| **DTO** | `presentacion.dto` - la consola nunca toca entidades del dominio; `ReclamoDTO`/`IncidenteSeguridadDTO` extienden `SolicitudDTO`. |

## Como correr

```bash
mvn compile
```

Solo compila: no hay logica ejecutable en esta fase. Al implementarse la persistencia, `ConexionH2.getConnection()` creara la base en `./data/barrio.mv.db` y el schema automaticamente.
