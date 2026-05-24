import { useDashboardStream } from "../hooks/useDashboardStream";
import CardMetric from "../components/CardMetric";
import TeamStatusCard from "../components/TeamStatusCard";

export default function DashboardPage() {
  const { data, error, connected } = useDashboardStream();

  const totalAguardando = data?.totalAguardando ?? 0;
  const totalEmAtendimento = data?.totalEmAtendimento ?? 0;
  const totalFinalizados = data?.totalFinalizados ?? 0;
  const times = data?.porTime ?? data?.times ?? [];

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold text-slate-900">Dashboard</h1>
          <p className="text-sm text-slate-500">
            Acompanhamento em tempo real da central de atendimento.
          </p>
        </div>
        <div className="flex items-center gap-2 text-xs">
          <span
            className={`h-2.5 w-2.5 rounded-full ${
              connected ? "bg-emerald-500" : "bg-slate-300"
            }`}
          />
          <span className="text-slate-500">
            {connected ? "Stream conectado" : "Aguardando conexão"}
          </span>
        </div>
      </div>

      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-700">
          Erro ao carregar dados do dashboard.
        </div>
      )}

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <CardMetric
          label="Total aguardando"
          value={totalAguardando}
          tone={totalAguardando > 10 ? "warning" : "default"}
        />
        <CardMetric
          label="Total em atendimento"
          value={totalEmAtendimento}
          tone="info"
        />
        <CardMetric
          label="Total finalizados"
          value={totalFinalizados}
          tone="success"
        />
      </div>

      <div>
        <h2 className="mb-3 text-lg font-semibold text-slate-900">Times</h2>
        <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-3">
          {times.map((t) => (
            <TeamStatusCard key={t.time ?? t.nome ?? t.id} team={t} />
          ))}
          {!times.length && (
            <div className="col-span-full rounded-2xl border border-dashed border-slate-300 bg-white p-8 text-center text-slate-500">
              Sem dados de times no momento.
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
