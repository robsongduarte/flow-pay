import { createFileRoute } from "@tanstack/react-router";
import DashboardPage from "../pages/DashboardPage";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "Dashboard — FlowPay" },
      {
        name: "description",
        content: "Acompanhamento em tempo real da central de atendimento FlowPay.",
      },
    ],
  }),
  component: DashboardPage,
});
