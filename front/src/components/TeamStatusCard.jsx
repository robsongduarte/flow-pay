export default function TeamStatusCard({ team }) {
  const aguardando = team.aguardando ?? 0;
  const emAtendimento = team.emAtendimento ?? 0;
  const finalizados = team.finalizados ?? team.finalizada ?? 0;
  const disponiveis = team.atendentesDisponiveis ?? 0;
  const ocupados = team.atendentesOcupados ?? 0;
  const capacidadeTotal = team.capacidadeTotal ?? 0;
  const capacidadeUtilizada = team.capacidadeUtilizada ?? 0;
  const timeLabel = team.time ?? team.nome ?? "-";

  const semAtendentes = disponiveis === 0;
  const filaCheia = aguardando >= 10;

  const utilizacaoPct = capacidadeTotal
    ? Math.min(100, Math.round((capacidadeUtilizada / capacidadeTotal) * 100))
    : 0;

  return (
    <div
      className={`rounded-2xl border bg-white p-5 shadow-sm ${
        semAtendentes
          ? "border-red-300 ring-2 ring-red-200"
          : filaCheia
          ? "border-amber-300"
          : "border-slate-200"
      }`}
    >
      <div className="flex items-center justify-between">
        <h3 className="text-lg font-semibold text-slate-900">{timeLabel}</h3>
        {semAtendentes && (
          <span className="rounded-full bg-red-100 px-2.5 py-1 text-xs font-medium text-red-700">
            Sem atendentes
          </span>
        )}
        {!semAtendentes && filaCheia && (
          <span className="rounded-full bg-amber-100 px-2.5 py-1 text-xs font-medium text-amber-700">
            Fila alta
          </span>
        )}
      </div>

      <div className="mt-4 grid grid-cols-3 gap-3">
        <Stat label="Aguardando" value={aguardando} highlight={filaCheia} />
        <Stat label="Em atendimento" value={emAtendimento} />
        <Stat label="Finalizados" value={finalizados} />
      </div>

      <div className="mt-4 grid grid-cols-2 gap-3 text-sm">
        <div className="rounded-lg bg-slate-50 p-3">
          <div className="text-slate-500">Disponíveis</div>
          <div className="text-lg font-semibold text-emerald-600">
            {disponiveis}
          </div>
        </div>
        <div className="rounded-lg bg-slate-50 p-3">
          <div className="text-slate-500">Ocupados</div>
          <div className="text-lg font-semibold text-sky-600">{ocupados}</div>
        </div>
      </div>

      <div className="mt-4">
        <div className="flex items-center justify-between text-xs text-slate-500">
          <span>Capacidade</span>
          <span>
            {capacidadeUtilizada} / {capacidadeTotal}
          </span>
        </div>
        <div className="mt-1 h-2 w-full overflow-hidden rounded-full bg-slate-100">
          <div
            className={`h-full rounded-full ${
              utilizacaoPct > 85
                ? "bg-red-500"
                : utilizacaoPct > 60
                ? "bg-amber-500"
                : "bg-emerald-500"
            }`}
            style={{ width: `${utilizacaoPct}%` }}
          />
        </div>
      </div>
    </div>
  );
}

function Stat({ label, value, highlight }) {
  return (
    <div
      className={`rounded-lg p-3 text-center ${
        highlight ? "bg-amber-50" : "bg-slate-50"
      }`}
    >
      <div className="text-xs text-slate-500">{label}</div>
      <div
        className={`text-xl font-semibold ${
          highlight ? "text-amber-700" : "text-slate-900"
        }`}
      >
        {value}
      </div>
    </div>
  );
}
