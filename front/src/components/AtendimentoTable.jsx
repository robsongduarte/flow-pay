const STATUS_STYLES = {
  AGUARDANDO: "bg-amber-100 text-amber-700",
  EM_ATENDIMENTO: "bg-sky-100 text-sky-700",
  FINALIZADO: "bg-emerald-100 text-emerald-700",
};

export default function AtendimentoTable({ atendimentos, onFinalizar }) {
  if (!atendimentos?.length) {
    return (
      <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center text-slate-500">
        Nenhum atendimento encontrado.
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <table className="w-full text-sm">
        <thead className="bg-slate-50 text-left text-xs uppercase tracking-wide text-slate-500">
          <tr>
            <th className="px-4 py-3">Cliente</th>
            <th className="px-4 py-3">Documento</th>
            <th className="px-4 py-3">Assunto</th>
            <th className="px-4 py-3">Time</th>
            <th className="px-4 py-3">Status</th>
            <th className="px-4 py-3 text-right">Ações</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {atendimentos.map((a) => (
            <tr key={a.id} className="hover:bg-slate-50">
              <td className="px-4 py-3 font-medium text-slate-900">
                {a.nomeCliente}
              </td>
              <td className="px-4 py-3 text-slate-600">{a.documentoCliente}</td>
              <td className="px-4 py-3 text-slate-600">{a.assunto}</td>
              <td className="px-4 py-3 text-slate-600">{a.time ?? "—"}</td>
              <td className="px-4 py-3">
                <span
                  className={`rounded-full px-2.5 py-1 text-xs font-medium ${
                    STATUS_STYLES[a.status] ?? "bg-slate-100 text-slate-700"
                  }`}
                >
                  {a.status}
                </span>
              </td>
              <td className="px-4 py-3 text-right">
                {a.status !== "FINALIZADO" && (
                  <button
                    onClick={() => onFinalizar?.(a.id)}
                    className="rounded-lg border border-slate-200 bg-white px-3 py-1.5 text-xs font-medium text-slate-700 hover:bg-slate-50"
                  >
                    Finalizar
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
