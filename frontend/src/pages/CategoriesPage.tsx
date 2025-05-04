import React, { useState, useEffect } from 'react';
import { Button, Card, Layout, Form, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getCategories, createCategory, updateCategory, deleteCategory } from '../services/api';
import CategoriesTable from '../components/CategoriesTable';
import CategoriesModal from '../components/CategoriesModal';
import { Category } from '../models/Category';

const { Content, Footer } = Layout;

const CategoriesPage: React.FC = () => {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchCategories();
  }, []);

  const fetchCategories = async () => {
    try {
      const response = await getCategories();
      setCategories(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке категорий:', error);
    }
  };

  const handleAdd = () => {
    setEditingCategory(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEdit = (category: Category) => {
    setEditingCategory(category);
    form.setFieldsValue({
      name: category.name,
    });
    setIsModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteCategory(id);
      setCategories(prev => prev.filter(c => c.id !== id)); // Локальное обновление состояния
    } catch (error) {
      console.error('Ошибка при удалении категории:', error);
      message.error('Не удалось удалить категорию');
    }
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      
      if (editingCategory) {
        await updateCategory(editingCategory.id, values);
      } else {
        await createCategory(values);
      }

      setIsModalVisible(false);
      fetchCategories();
    } catch (error) {
      console.error('Ошибка при сохранении категории:', error);
    }
  };

  return (
    <Layout style={{ flex: 1, display: 'flex', flexDirection: 'column', maxWidth: 800, margin: '0 auto' }}>
      <Content style={{ padding: '16px 0', flex: 1 }}>
        <Card 
          title="Список категорий"
          extra={
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              Добавить категорию
            </Button>
          }
          bordered={false}
        >
          <CategoriesTable 
            data={categories} 
            onEdit={handleEdit}
            onDelete={handleDelete}
          />
        </Card>
      </Content>

      <Footer style={{ 
        textAlign: 'center', 
        padding: '16px 0',
        flex: '0 0 auto'
      }}>
        © 2025 Складской учёт. Все права защищены.
      </Footer>

      <CategoriesModal
        visible={isModalVisible}
        onOk={handleOk}
        onCancel={() => setIsModalVisible(false)}
        editingCategory={editingCategory}
        form={form}
      />
    </Layout>
  );
};

export default CategoriesPage;