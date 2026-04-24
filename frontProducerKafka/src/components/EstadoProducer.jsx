import { useState } from "react";

export default function EstadoProducer() {
    const [response, setResponse] = useState("");
    const [loading, setLoading] = useState(false);

    const readLatest = async () => {
        const res = await fetch("http://localhost:8000/producer/response");
        return res.json();
    };

    const sendEstadoRequest = async (suffix) => {
        setLoading(true);

        try {
            const before = await readLatest();
            const previousUpdatedAt = Number(before.updatedAt ?? 0);
            const message = `consulta${suffix}`;

            await fetch(
                `http://localhost:8000/producer/menu?message=${encodeURIComponent(message)}`
            );

            let latestResponse = "No hubo respuesta del consumidor.";

            for (let i = 0; i < 10; i += 1) {
                const current = await readLatest();
                const currentUpdatedAt = Number(current.updatedAt ?? 0);

                if (currentUpdatedAt > previousUpdatedAt && current.response) {
                    latestResponse = current.response;
                    break;
                }

                await new Promise((resolve) => setTimeout(resolve, 300));
            }

            setResponse(latestResponse);
        } catch (error) {
            setResponse("Error consultando estado por Kafka.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ padding: 20 }}>
            <h1>Estado</h1>

            <div style={{ marginBottom: "14px" }}>
                <button
                    onClick={() => sendEstadoRequest("Estado0")}
                    disabled={loading}
                    style={{
                        padding: "12px 20px",
                        fontSize: "16px",
                        backgroundColor: "#198754",
                        color: "white",
                        border: "none",
                        borderRadius: "5px",
                        cursor: "pointer",
                        marginRight: "10px"
                    }}
                >
                    Mostrar Pacientes
                </button>

                <button
                    onClick={() => sendEstadoRequest("Estado1")}
                    disabled={loading}
                    style={{
                        padding: "12px 20px",
                        fontSize: "16px",
                        backgroundColor: "#0d6efd",
                        color: "white",
                        border: "none",
                        borderRadius: "5px",
                        cursor: "pointer"
                    }}
                >
                    Mostrar Citas
                </button>
            </div>

            {loading && <p>Consultando...</p>}

            {response && (
                <pre
                    style={{
                        marginTop: "12px",
                        padding: "12px",
                        background: "#f5f5f5",
                        borderRadius: "6px",
                        whiteSpace: "pre-wrap"
                    }}
                >
                    {response}
                </pre>
            )}
        </div>
    );
}
