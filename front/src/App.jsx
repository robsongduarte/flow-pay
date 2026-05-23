import { useEffect, useState } from "react";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080/flow-pay";

export default function App() {
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    fetch(`${apiBaseUrl}/api/dashboard`)
      .then(async (response) => {
        if (!response.ok) {
          throw new Error(`HTTP ${response.status}`);
        }
        return response.json();
      })
      .then((data) => setDashboard(data))
      .catch((err) => setError(err.message));
  }, []);

  return (
    <main className="page">
      <header className="header">
        <h1>FlowPay Front</h1>
        <span className="endpoint">{apiBaseUrl}</span>
      </header>
      {error && <p className="error">Erro ao buscar dashboard: {error}</p>}
      {!error && !dashboard && <p className="loading">Carregando dashboard...</p>}
      {dashboard && (
        <section className="panel">
          <h2>Resumo</h2>
          <ul>
            <li>Aguardando: {dashboard.totalAguardando}</li>
            <li>Em atendimento: {dashboard.totalEmAtendimento}</li>
            <li>Finalizados: {dashboard.totalFinalizados}</li>
          </ul>
        </section>
      )}
    </main>
  );
}
