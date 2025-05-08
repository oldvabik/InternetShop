import axios from 'axios';

const API_URL = 'https://warehouse-management-cimz.onrender.com/api';

export const getProducts = async () => {
  return axios.get(`${API_URL}/products`);
};

export const getProduct = async (id: number) => {
  return axios.get(`${API_URL}/products/${id}`);
};

export const createProduct = async (product: {
  name: string;
  price: number;
  quantity: number;
  categoryName: string;
}) => {
  return axios.post(`${API_URL}/products`, product);
};

export const updateProduct = async (id: number, product: {
  name?: string;
  price?: number;
  quantity?: number;
  categoryName?: string;
}) => {
  return axios.put(`${API_URL}/products/${id}`, product);
};

export const deleteProduct = async (id: number) => {
  return axios.delete(`${API_URL}/products/${id}`);
};

export const getCategories = async () => {
  return axios.get(`${API_URL}/categories`);
};

export const getCategory = async (id: number) => {
  return axios.get(`${API_URL}/categories/${id}`);
};

export const createCategory = async (category: { name: string }) => {
  return axios.post(`${API_URL}/categories`, category);
};

export const updateCategory = async (id: number, category: { name: string }) => {
  return axios.put(`${API_URL}/categories/${id}`, category);
};

export const deleteCategory = async (id: number) => {
  return axios.delete(`${API_URL}/categories/${id}`);
};

export const getOrders = async () => {
  return axios.get(`${API_URL}/orders`);
};

export const getOrder = async (id: number) => {
  return axios.get(`${API_URL}/orders/${id}`);
};

export const getUserOrders = async (userId: number) => {
  return axios.get(`${API_URL}/users/${userId}/orders`);
};

export const createOrder = async (userId: number, orderDto: {
  items: { productName: string; quantity: number }[];
}) => {
  return axios.post(`${API_URL}/users/${userId}/orders`, orderDto);
};

export const updateOrder = async (userId: number, orderId: number, orderDto: {
  items: { productName: string; quantity: number }[];
}) => {
  return axios.put(`${API_URL}/users/${userId}/orders/${orderId}`, orderDto);
};

export const deleteOrder = async (userId: number, orderId: number) => {
  return axios.delete(`${API_URL}/users/${userId}/orders/${orderId}`);
};

export const getUsers = async () => {
  return axios.get(`${API_URL}/users`);
};
