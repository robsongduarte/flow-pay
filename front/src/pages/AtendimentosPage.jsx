import { useEffect, useState, useCallback } from "react";
import {
  listAtendimentos,
  createAtendimento,
  finalizarAtendimento,
} from "../api/atendimentoApi";
import AtendimentoTable from "../components/AtendimentoTable";

const ASSUNTOS = ["PROBLEMA_CARTAO", "CONTRATACAO_EMPRESTIMO", "OUTROS"];
const STATUSES = ["AGUARDANDO", "EM_ATENDIMENTO", "FINALIZADO"];
const TIMES = ["CARTOES", "EMPRESTIMOS", "OUTROS_ASSUNTOS"];

export default function AtendimentosPage() {
  const [atendimentos, setAtendimentos] = useState([]);
  const [status, setStatus] = useState("");
  const [time, setTime] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    nomeCliente: "",
    documentoCliente: "",
    assunto: ASSUNTOS[0],
  });
  const [submitting, setSubmitting] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {};
      if (status) params.status = status;
      if (time) params.time = time;
      const data = await listAtendimentos(params);
      setAtendimentos(Array.isArray(data) ? data : data?.items ?? []);
    } catch (e) {
      setError(e?.message ?? "Erro ao carregar");
    } finally {
      setLoading(false);
    }
  }, [status, time]);

  useEffect(() => {
    load();
  }, [load]);

  const handleFinalizar = async (id) => {
    try {
      await finalizarAtendimento(id);
      load();
    } catch (e) {
      setError(e?.message ?? "Erro ao finalizar");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.nomeCliente.trim() || !form.documentoCliente.trim()) return;
    setSubmitting(true);
    try {
      await createAtendimento(form);
      setForm({
        nomeCliente: "",
        documentoCliente: "",
        assunto: ASSUNTOS[0],
      });
      setShowForm(false);
      load();
    } catch (e) {
      setError(e?.message ?? "Erro ao criar atendimento");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold text-slate-900">
            Atendimentos
          </h1>
          <p className="text-sm text-slate-500">
            Gestão da fila de atendimentos.
          </p>
        </div>
        <button
          onClick={() => setShowForm((v) => !v)}
          className="rounded-lg bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-800"
        >
          {showForm ? "Cancelar" : "Novo atendimento"}
        </button>
      </div>

      {showForm && (
        <form
          onSubmit={handleSubmit}
          className="grid grid-cols-1 gap-3 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm md:grid-cols-4"
        >
          <Input
            label="Nome do cliente"
            value={form.nomeCliente}
            onChange={(v) => setForm({ ...form, nomeCliente: v })}
          />
          <Input
            label="Documento"
            value={form.documentoCliente}
            onChange={(v) => setForm({ ...form, documentoCliente: v })}
          />
          <Select
            label="Assunto"
            value={form.assunto}
            onChange={(v) => setForm({ ...form, assunto: v })}
            options={ASSUNTOS}
          />
          <div className="flex items-end">
            <button
              type="submit"
              disabled={submitting}
              className="w-full rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white hover:bg-emerald-700 disabled:opacity-50"
            >
              {submitting ? "Criando..." : "Criar"}
            </button>
          </div>
        </form>
      )}

      <div className="flex flex-wrap items-end gap-3 rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
        <Select
          label="Status"
          value={status}
          onChange={setStatus}
          options={STATUSES}
          allowEmpty
        />
        <Select
          label="Time"
          value={time}
          onChange={setTime}
          options={TIMES}
          allowEmpty
        />
        <button
          onClick={load}
          className="rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-50"
        >
          Atualizar
        </button>
        {loading && (
          <span className="text-xs text-slate-500">Carregando...</span>
        )}
      </div>

      {error && (
        <div className="rounded-xl border border-red-200 bg-red-50 p-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <AtendimentoTable
        atendimentos={atendimentos}
        onFinalizar={handleFinalizar}
      />
    </div>
  );
}

function Input({ label, value, onChange }) {
  return (
    <label className="block">
      <span className="mb-1 block text-xs font-medium text-slate-600">
        {label}
      </span>
      <input
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm shadow-sm focus:border-slate-400 focus:outline-none"
      />
    </label>
  );
}

function Select({ label, value, onChange, options, allowEmpty }) {
  return (
    <label className="block">
      <span className="mb-1 block text-xs font-medium text-slate-600">
        {label}
      </span>
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="w-full min-w-[160px] rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm shadow-sm focus:border-slate-400 focus:outline-none"
      >
        {allowEmpty && <option value="">Todos</option>}
        {options.map((o) => (
          <option key={o} value={o}>
            {o}
          </option>
        ))}
      </select>
    </label>
  );
}
