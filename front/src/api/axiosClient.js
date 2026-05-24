import axios from "axios";

const baseURL =
  (typeof import.meta !== "undefined" && import.meta.env?.VITE_API_BASE_URL) ||
  "https://api-flow-pay.onrender.com/flow-pay";


const axiosClient = axios.create({
  baseURL,
  headers: { "Content-Type": "application/json" },
});

export default axiosClient;
export const API_BASE_URL = baseURL;
