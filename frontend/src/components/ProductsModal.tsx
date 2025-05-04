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
      title={editingProduct ? 'Редактировать товар' : 'Добавить товар'}
      visible={visible}
      onOk={onOk}
      onCancel={onCancel}
      okText="Сохранить"
      cancelText="Отмена"
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="Название"
          rules={[{ required: true, message: 'Пожалуйста, введите название товара!' }]}
        >
          <Input />
        </Form.Item>

        <Form.Item
          name="price"
          label="Цена"
          rules={[{ required: true, message: 'Пожалуйста, введите цену товара!' }]}
        >
          <InputNumber min={0} style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="quantity"
          label="Количество"
          rules={[{ required: true, message: 'Пожалуйста, введите количество товара!' }]}
        >
          <InputNumber min={0} style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item
          name="category"
          label="Категория"
          rules={[{ required: true, message: 'Пожалуйста, выберите категорию!' }]}
        >
          <Select placeholder="Выберите категорию">
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