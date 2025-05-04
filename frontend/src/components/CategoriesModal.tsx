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
      title={editingCategory ? 'Редактировать категорию' : 'Добавить категорию'}
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
          rules={[{ required: true, message: 'Пожалуйста, введите название категории!' }]}
        >
          <Input />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CategoriesModal;