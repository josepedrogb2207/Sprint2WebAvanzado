package com.lta.backend.services;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;

@Service
public class TopicMessageService {

    private static final Logger log = LoggerFactory.getLogger(TopicMessageService.class);
    private static final String RESPONSE_TOPIC = "str-topic-response";
    private static final Pattern SUFFIX_PATTERN = Pattern.compile("(?i)(paciente|pacientes|cita|citas|estado)\\d+$");
    private final Firestore firestore;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public TopicMessageService(Firestore firestore, KafkaTemplate<String, String> kafkaTemplate) {
        this.firestore = firestore;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void handlePacientePartition0(String message) {
        String nombre = cleanPayload(message);

        if (nombre.isBlank()) {
            sendResponse("No se pudo crear paciente: nombre vacio.");
            return;
        }

        try {
            firestore.collection("pacientes")
                    .add(Map.of("nombre", nombre, "estado", "vivo"))
                    .get();

            sendResponse("Paciente creado: " + nombre + " (estado=vivo)");
        } catch (Exception ex) {
            log.error("Error al crear paciente {}", nombre, ex);
            sendResponse("Error al crear paciente: " + nombre);
        }
    }

    public void handlePacientePartition1(String message) {
        String nombre = cleanPayload(message);

        if (nombre.isBlank()) {
            sendResponse("No se pudo actualizar paciente: nombre vacio.");
            return;
        }

        try {
            QuerySnapshot query = firestore.collection("pacientes")
                    .whereEqualTo("nombre", nombre)
                    .get()
                    .get();

            int updated = 0;
            for (QueryDocumentSnapshot doc : query.getDocuments()) {
                doc.getReference().update("estado", "muerto").get();
                updated++;
            }

            if (updated == 0) {
                sendResponse("No se encontro paciente para actualizar: " + nombre);
                return;
            }

            sendResponse("Paciente(s) actualizado(s) a muerto: " + nombre + " (" + updated + ")");
        } catch (Exception ex) {
            log.error("Error al actualizar paciente {}", nombre, ex);
            sendResponse("Error al actualizar paciente: " + nombre);
        }
    }

    public void handlePacientePartition2(String message) {
        String nombre = cleanPayload(message);

        if (nombre.isBlank()) {
            sendResponse("No se pudo eliminar paciente: nombre vacio.");
            return;
        }

        try {
            QuerySnapshot query = firestore.collection("pacientes")
                    .whereEqualTo("nombre", nombre)
                    .get()
                    .get();

            int deleted = 0;
            for (QueryDocumentSnapshot doc : query.getDocuments()) {
                doc.getReference().delete().get();
                deleted++;
            }

            if (deleted == 0) {
                sendResponse("No se encontro paciente para eliminar: " + nombre);
                return;
            }

            sendResponse("Paciente(s) eliminado(s): " + nombre + " (" + deleted + ")");
        } catch (Exception ex) {
            log.error("Error al eliminar paciente {}", nombre, ex);
            sendResponse("Error al eliminar paciente: " + nombre);
        }
    }

    public void handleCitasPartition0(String message) {
        String identificador = cleanPayload(message);

        if (identificador.isBlank()) {
            sendResponse("No se pudo crear cita: identificador vacio.");
            return;
        }

        try {
            firestore.collection("citas")
                    .add(Map.of("identificador", identificador))
                    .get();

            sendResponse("Cita creada con identificador: " + identificador);
        } catch (Exception ex) {
            log.error("Error al crear cita {}", identificador, ex);
            sendResponse("Error al crear cita: " + identificador);
        }
    }

    public void handleCitasPartition1(String message) {
        String identificador = cleanPayload(message);

        if (identificador.isBlank()) {
            sendResponse("No se pudo eliminar cita: identificador vacio.");
            return;
        }

        try {
            QuerySnapshot query = firestore.collection("citas")
                    .whereEqualTo("identificador", identificador)
                    .get()
                    .get();

            int deleted = 0;
            for (QueryDocumentSnapshot doc : query.getDocuments()) {
                doc.getReference().delete().get();
                deleted++;
            }

            if (deleted == 0) {
                sendResponse("No se encontro cita para eliminar: " + identificador);
                return;
            }

            sendResponse("Cita(s) eliminada(s): " + identificador + " (" + deleted + ")");
        } catch (Exception ex) {
            log.error("Error al eliminar cita {}", identificador, ex);
            sendResponse("Error al eliminar cita: " + identificador);
        }
    }

    public void handleCitasPartition2(String message) {
        String payload = cleanPayload(message);
        String[] values = payload.split("\\|\\|", 2);

        if (values.length < 2 || values[0].isBlank() || values[1].isBlank()) {
            sendResponse("Formato invalido para actualizar cita. Usa viejo||nuevo.");
            return;
        }

        String identificadorViejo = values[0].trim();
        String identificadorNuevo = values[1].trim();

        try {
            QuerySnapshot query = firestore.collection("citas")
                    .whereEqualTo("identificador", identificadorViejo)
                    .get()
                    .get();

            int updated = 0;
            for (QueryDocumentSnapshot doc : query.getDocuments()) {
                DocumentReference ref = doc.getReference();
                ref.update("identificador", identificadorNuevo).get();
                updated++;
            }

            if (updated == 0) {
                sendResponse("No se encontro cita para actualizar: " + identificadorViejo);
                return;
            }

            sendResponse("Cita(s) actualizada(s): " + identificadorViejo + " -> " + identificadorNuevo + " (" + updated + ")");
        } catch (Exception ex) {
            log.error("Error al actualizar cita {}", identificadorViejo, ex);
            sendResponse("Error al actualizar cita: " + identificadorViejo);
        }
    }

    public void handleEstadoPartition0(String message) {
        try {
            QuerySnapshot query = firestore.collection("pacientes").get().get();
            List<QueryDocumentSnapshot> docs = query.getDocuments();

            if (docs.isEmpty()) {
                sendResponse("Pacientes: sin registros.");
                return;
            }

            StringBuilder builder = new StringBuilder("Pacientes:\n");
            for (QueryDocumentSnapshot doc : docs) {
                String nombre = String.valueOf(doc.get("nombre"));
                String estado = String.valueOf(doc.get("estado"));
                builder.append("- ").append(nombre).append(" | estado=").append(estado).append("\n");
            }
            sendResponse(builder.toString().trim());
        } catch (Exception ex) {
            log.error("Error al consultar pacientes", ex);
            sendResponse("Error al consultar pacientes.");
        }
    }

    public void handleEstadoPartition1(String message) {
        try {
            QuerySnapshot query = firestore.collection("citas").get().get();
            List<QueryDocumentSnapshot> docs = query.getDocuments();

            if (docs.isEmpty()) {
                sendResponse("Citas: sin registros.");
                return;
            }

            StringBuilder builder = new StringBuilder("Citas:\n");
            for (QueryDocumentSnapshot doc : docs) {
                String identificador = String.valueOf(doc.get("identificador"));
                builder.append("- ").append(identificador).append("\n");
            }
            sendResponse(builder.toString().trim());
        } catch (Exception ex) {
            log.error("Error al consultar citas", ex);
            sendResponse("Error al consultar citas.");
        }
    }

    private String cleanPayload(String message) {
        String normalized = message == null ? "" : message.trim();
        return SUFFIX_PATTERN.matcher(normalized).replaceFirst("").trim();
    }

    private void sendResponse(String message) {
        kafkaTemplate.send(RESPONSE_TOPIC, message).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("No se pudo enviar respuesta al topic {}", RESPONSE_TOPIC, ex);
                return;
            }

            log.info("Respuesta enviada a {} offset={}", RESPONSE_TOPIC, result.getRecordMetadata().offset());
        });
    }
}