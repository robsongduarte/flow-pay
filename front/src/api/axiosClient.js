import axios from "axios";

const rawBaseURL =
  (typeof import.meta !== "undefined" && import.meta.env?.VITE_API_BASE_URL) ||
  "http://localhost:8080/flow-pay";

const normalizedBaseURL = rawBaseURL.replace(/\/+$/, "");
const baseURL = normalizedBaseURL.endsWith("/api")
  ? normalizedBaseURL
  : `${normalizedBaseURL}/api`;


const axiosClient = axios.create({
  baseURL,
  headers: { "Content-Type": "application/json" },
});

export default axiosClient;
export const API_BASE_URL = baseURL;
