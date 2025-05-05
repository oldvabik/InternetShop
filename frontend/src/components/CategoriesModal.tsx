import React from 'react';
import { Modal, Form, Input } from 'antd';
import { Category } from '../models/Category';


interface CategoriesModalProps {
  visible: boolean;
  onOk: () => void;
  onCancel: () => void;
  editingCategory: Category | null;
  form: any;
  isMobile: boolean;
}

const CategoriesModal: React.FC<CategoriesModalProps> = ({
  visible,
  onOk,
  onCancel,
  editingCategory,
  form,
  isMobile,
}) => {
  return (
    <Modal
      title={editingCategory ? 'Редактировать категорию' : 'Добавить категорию'}
      visible={visible}
      onOk={onOk}
      onCancel={onCancel}
      okText="Сохранить"
      cancelText="Отмена"
      width={isMobile ? '90%' : 500}
      bodyStyle={{ padding: isMobile ? '16px 8px' : '24px' }}
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="name"
          label="Название"
          rules={[{ required: true, message: 'Пожалуйста, введите название категории!' }]}
        >
          <Input size={isMobile ? 'small' : 'middle'} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default CategoriesModal;