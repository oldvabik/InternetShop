import { Product } from './Product';

export interface OrderProduct {
  product?: Product;
  quantity: number;
}