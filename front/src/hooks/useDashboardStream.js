import { useEffect, useState } from "react";
import { getDashboard } from "../api/dashboardApi";
import { API_BASE_URL } from "../api/axiosClient";

export function useDashboardStream() {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    let active = true;
    let pollingId = null;

    const refreshDashboard = async () => {
      try {
        const dashboard = await getDashboard();
        if (active) {
          setData(dashboard);
          setError(null);
        }
      } catch (e) {
        if (active) {
          setError(e);
        }
      }
    };

    refreshDashboard();
    pollingId = setInterval(refreshDashboard, 5000);

    const url = `${API_BASE_URL}/dashboard/stream`;
    const es = new EventSource(url);

    const handleDashboardEvent = (ev) => {
      try {
        const parsed = JSON.parse(ev.data);
        setData(parsed);
      } catch {
        // ignore
      }
    };

    es.onopen = () => setConnected(true);
    es.onmessage = handleDashboardEvent;
    es.addEventListener("dashboard", handleDashboardEvent);
    es.onerror = (e) => {
      setConnected(false);
      setError(e);
    };

    return () => {
      active = false;
      if (pollingId) {
        clearInterval(pollingId);
      }
      es.removeEventListener("dashboard", handleDashboardEvent);
      es.close();
    };
  }, []);

  return { data, error, connected };
}
