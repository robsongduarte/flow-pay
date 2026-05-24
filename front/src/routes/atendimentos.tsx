import { createFileRoute } from "@tanstack/react-router";
import AtendimentosPage from "../pages/AtendimentosPage";

export const Route = createFileRoute("/atendimentos")({
  head: () => ({
    meta: [
      { title: "Atendimentos — FlowPay" },
      { name: "description", content: "Gestão da fila de atendimentos." },
    ],
  }),
  component: AtendimentosPage,
});
