import { createFileRoute } from "@tanstack/react-router";
import AtendentesPage from "../pages/AtendentesPage";

export const Route = createFileRoute("/atendentes")({
  head: () => ({
    meta: [
      { title: "Atendentes — FlowPay" },
      { name: "description", content: "Cadastro e gestão de atendentes." },
    ],
  }),
  component: AtendentesPage,
});
