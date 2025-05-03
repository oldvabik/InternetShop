// src/components/ProductsModal.tsx
import React from 'react';
import { Modal, Form, Input, InputNumber, Select } from 'antd';
import { Product } from '../models/Product';
import { Category } from '../models/Category';

interface ProductsModalProps {
  visible: boolean;
  onOk: () => void;
  onCancel: () => void;
  editingProduct: Product | null;
  categories: Category[];
  form: any;
}

const ProductsModal: React.FC<ProductsModalProps> = ({
  visible,
  onOk,
  onCancel,
  editingProduct,
  categories,
  form,
}) => {
  return (
    <Modal
      title={editingProduct ? 'Edit Product' : 'Add Product'}
      visible={visible}
      onOk={onOk}
      onCancel={onCancel}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="Name"
          rules={[{ required: true, message: 'Please input the product name!' }]}
        >
          <Input />
        </Form.Item>

        <Form.Item
          name="price"
          label="Price"
          rules={[{ required: true, message: 'Please input the product price!' }]}
        >
          <InputNumber min={0} style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="quantity"
          label="Quantity"
          rules={[{ required: true, message: 'Please input the product quantity!' }]}
        >
          <InputNumber min={0} style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="category"
          label="Category"
          rules={[{ required: true, message: 'Please select a category!' }]}
        >
          <Select>
            {categories.map(category => (
              <Select.Option key={category.id} value={category.id}>
                {category.name}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default ProductsModal;