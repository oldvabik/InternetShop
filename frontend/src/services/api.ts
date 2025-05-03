import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

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