import axiosClient from "./axiosClient";

export const listAtendentes = () =>
  axiosClient.get("/atendentes").then((r) => r.data);

export const createAtendente = (payload) =>
  axiosClient.post("/atendentes", payload).then((r) => r.data);

export const ativarAtendente = (id) =>
  axiosClient.post(`/atendentes/${id}/ativar`).then((r) => r.data);

export const desativarAtendente = (id) =>
  axiosClient.post(`/atendentes/${id}/desativar`).then((r) => r.data);
