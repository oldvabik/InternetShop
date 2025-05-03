import React, { useState, useEffect } from 'react';
import { Button, Card, Layout, Form } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getProducts, createProduct, updateProduct, deleteProduct, getCategories } from '../services/api';
import ProductsTable from '../components/ProductsTable';
import ProductsModal from '../components/ProductsModal';
import { Product } from '../models/Product';
import { Category } from '../models/Category';

const { Content, Footer } = Layout;

const ProductsPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchProducts();
    fetchCategories();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await getProducts();
      setProducts(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке продуктов:', error);
    }
  };

  const fetchCategories = async () => {
    try {
      const response = await getCategories();
      setCategories(response.data);
    } catch (error) {
      console.error('Ошибка при загрузке категорий:', error);
    }
  };

  const handleAdd = () => {
    setEditingProduct(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEdit = (product: Product) => {
    setEditingProduct(product);
    form.setFieldsValue({
      name: product.name,
      price: product.price,
      quantity: product.quantity,
      category: product.category?.id,
    });
    setIsModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await deleteProduct(id);
      fetchProducts();
    } catch (error) {
      console.error('Ошибка при удалении продукта:', error);
    }
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      const selectedCategory = categories.find(c => c.id === values.category);
      
      const productData = {
        name: values.name,
        price: values.price,
        quantity: values.quantity,
        categoryName: selectedCategory?.name || ''
      };

      if (editingProduct) {
        await updateProduct(editingProduct.id, productData);
      } else {
        await createProduct(productData);
      }

      setIsModalVisible(false);
      fetchProducts();
    } catch (error) {
      console.error('Ошибка при сохранении продукта:', error);
    }
  };

  return (
    <Layout style={{ minHeight: '100vh', maxWidth: 1000, margin: '0 auto' }}>
      <Content style={{ padding: '16px 0' }}>
        <Card 
          title="Список товаров"
          extra={
            <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
              Добавить товар
            </Button>
          }
          bordered={false}
        >
          <ProductsTable 
            data={products} 
            onEdit={handleEdit}
            onDelete={handleDelete}
          />
        </Card>
      </Content>
      
      <Footer style={{ 
        textAlign: 'center', 
        padding: '16px 0',
        marginTop: 'auto'
      }}>
        © 2023 Складской учёт. Все права защищены.
      </Footer>

      <ProductsModal
        visible={isModalVisible}
        onOk={handleOk}
        onCancel={() => setIsModalVisible(false)}
        editingProduct={editingProduct}
        categories={categories}
        form={form}
      />
    </Layout>
  );
};

export default ProductsPage;