export default function CardMetric({ label, value, tone = "default", hint }) {
  const toneClass =
    tone === "warning"
      ? "border-amber-300 bg-amber-50"
      : tone === "success"
      ? "border-emerald-300 bg-emerald-50"
      : tone === "info"
      ? "border-sky-300 bg-sky-50"
      : "border-slate-200 bg-white";

  return (
    <div
      className={`rounded-2xl border ${toneClass} p-5 shadow-sm transition hover:shadow-md`}
    >
      <div className="text-sm font-medium text-slate-500">{label}</div>
      <div className="mt-2 text-3xl font-semibold tracking-tight text-slate-900">
        {value ?? "—"}
      </div>
      {hint && <div className="mt-1 text-xs text-slate-500">{hint}</div>}
    </div>
  );
}
