// src/components/CategoriesModal.tsx
import React from 'react';
import { Modal, Form, Input } from 'antd';
import { Category } from '../models/Category';

interface CategoriesModalProps {
  visible: boolean;
  onOk: () => void;
  onCancel: () => void;
  editingCategory: Category | null;
  form: any;
}

const CategoriesModal: React.FC<CategoriesModalProps> = ({
  visible,
  onOk,
  onCancel,
  editingCategory,
  form,
}) => {
  return (
    <Modal
      title={editingCategory ? 'Edit Category' : 'Add Category'}
      visible={visible}
      onOk={onOk}
      onCancel={onCancel}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="Name"
          rules={[{ required: true, message: 'Please input the category name!' }]}
        >
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CategoriesModal;