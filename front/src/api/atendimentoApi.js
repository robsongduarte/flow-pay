import axiosClient from "./axiosClient";

export const listAtendimentos = (params = {}) =>
  axiosClient.get("/atendimentos", { params }).then((r) => r.data);

export const createAtendimento = (payload) =>
  axiosClient.post("/atendimentos", payload).then((r) => r.data);

export const finalizarAtendimento = (id) =>
  axiosClient.post(`/atendimentos/${id}/finalizar`).then((r) => r.data);
