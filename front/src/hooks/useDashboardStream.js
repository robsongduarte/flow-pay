import { useEffect, useState } from "react";
import { getDashboard } from "../api/dashboardApi";
import { API_BASE_URL } from "../api/axiosClient";

export function useDashboardStream() {
  const [data, setData] = useState(null);
  const [error, setError] = useState(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    let active = true;

    getDashboard()
      .then((d) => active && setData(d))
      .catch((e) => active && setError(e));

    const url = `${API_BASE_URL}/dashboard/stream`;
    const es = new EventSource(url);

    es.onopen = () => setConnected(true);
    es.onmessage = (ev) => {
      try {
        const parsed = JSON.parse(ev.data);
        setData(parsed);
      } catch {
        // ignore
      }
    };
    es.onerror = (e) => {
      setConnected(false);
      setError(e);
    };

    return () => {
      active = false;
      es.close();
    };
  }, []);

  return { data, error, connected };
}
