import { useState } from "react";

export default function Producer() {
    const routes = [
        { key: "paciente0", label: "Registro Paciente", suffix: "Paciente0" },
        { key: "paciente1", label: "Actualizar Paciente", suffix: "Paciente1" },
        { key: "paciente2", label: "Eliminar Paciente", suffix: "Paciente2" },
        { key: "cita0", label: "Crear Cita", suffix: "Cita0" },
        { key: "cita1", label: "Cancelar Cita", suffix: "Cita1" }
    ];

    const cita2 = { key: "cita2", label: "Reprogramar Cita", suffix: "Cita2" };

    const [messages, setMessages] = useState(
        routes.reduce((acc, route) => {
            acc[route.key] = "";
            return acc;
        }, {})
    );
    const [responses, setResponses] = useState(
        [...routes, cita2].reduce((acc, route) => {
            acc[route.key] = "";
            return acc;
        }, {})
    );
    const [cita2Vieja, setCita2Vieja] = useState("");
    const [cita2Nueva, setCita2Nueva] = useState("");

    const sendMessage = async (route) => {
        const rawMessage = messages[route.key].trim();

        if (!rawMessage) {
            return;
        }

        const finalMessage = `${rawMessage}${route.suffix}`;

        const res = await fetch(
            `http://localhost:8000/producer/menu?message=${encodeURIComponent(finalMessage)}`
        );

        const data = await res.text();

        setResponses((prev) => ({ ...prev, [route.key]: data }));
        setMessages((prev) => ({ ...prev, [route.key]: "" }));
    };

    const sendCita2 = async () => {
        const oldValue = cita2Vieja.trim();
        const newValue = cita2Nueva.trim();

        if (!oldValue || !newValue) {
            return;
        }

        const finalMessage = `${oldValue}||${newValue}${cita2.suffix}`;

        const res = await fetch(
            `http://localhost:8000/producer/menu?message=${encodeURIComponent(finalMessage)}`
        );

        const data = await res.text();

        setResponses((prev) => ({ ...prev, [cita2.key]: data }));
        setCita2Vieja("");
        setCita2Nueva("");
    };

    return (
        <div style={{ padding: 20 }}>
            <h1>Kafka Producer</h1>

            {routes.map((route) => (
                <div key={route.key} style={{ marginBottom: "18px" }}>
                    <h3 style={{ marginBottom: "8px" }}>{route.label}</h3>

                    <input
                        value={messages[route.key]}
                        onChange={(e) =>
                            setMessages((prev) => ({ ...prev, [route.key]: e.target.value }))
                        }
                        placeholder={`Escribe mensaje para ${route.label}`}
                        style={{
                            width: "300px",
                            padding: "12px",
                            fontSize: "16px",
                            marginRight: "10px",
                            borderRadius: "5px",
                            border: "1px solid #ccc"
                        }}
                    />

                    <button
                        onClick={() => sendMessage(route)}
                        style={{
                            padding: "12px 20px",
                            fontSize: "16px",
                            backgroundColor: "#007bff",
                            color: "white",
                            border: "none",
                            borderRadius: "5px",
                            cursor: "pointer"
                        }}
                    >
                        Enviar {route.label}
                    </button>

                    {responses[route.key] && (
                        <p style={{ marginTop: "8px" }}>{responses[route.key]}</p>
                    )}
                </div>
            ))}

            <div style={{ marginBottom: "18px" }}>
                <h3 style={{ marginBottom: "8px" }}>{cita2.label}</h3>

                <input
                    value={cita2Vieja}
                    onChange={(e) => setCita2Vieja(e.target.value)}
                    placeholder="Identificador actual"
                    style={{
                        width: "220px",
                        padding: "12px",
                        fontSize: "16px",
                        marginRight: "10px",
                        borderRadius: "5px",
                        border: "1px solid #ccc"
                    }}
                />

                <input
                    value={cita2Nueva}
                    onChange={(e) => setCita2Nueva(e.target.value)}
                    placeholder="Nuevo identificador"
                    style={{
                        width: "220px",
                        padding: "12px",
                        fontSize: "16px",
                        marginRight: "10px",
                        borderRadius: "5px",
                        border: "1px solid #ccc"
                    }}
                />

                <button
                    onClick={sendCita2}
                    style={{
                        padding: "12px 20px",
                        fontSize: "16px",
                        backgroundColor: "#007bff",
                        color: "white",
                        border: "none",
                        borderRadius: "5px",
                        cursor: "pointer"
                    }}
                >
                    Enviar {cita2.label}
                </button>

                {responses[cita2.key] && (
                    <p style={{ marginTop: "8px" }}>{responses[cita2.key]}</p>
                )}
            </div>

            <div style={{ marginTop: "8px", color: "#555" }}>
                El sufijo de topico/particion se agrega automaticamente al enviar.
            </div>
        </div>
    );
}