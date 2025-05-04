import { User } from './User';
import { OrderProduct } from './OrderProduct';
import { OrderItem } from './OrderItem';

export interface Order {
  id: number;
  date?: string;
  totalPrice?: number;
  user?: User;
  orderProducts?: OrderProduct[];
  items?: OrderItem[]; 
}