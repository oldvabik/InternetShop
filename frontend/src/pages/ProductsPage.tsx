import React, { useState, useEffect } from 'react';
import { Button, Card, Layout, Form, message, Grid } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getProducts, createProduct, updateProduct, deleteProduct, getCategories } from '../services/api';
import ProductsTable from '../components/ProductsTable';
import ProductsModal from '../components/ProductsModal';
import { Product } from '../models/Product';
import { Category } from '../models/Category';

const { Content, Footer } = Layout;
const { useBreakpoint } = Grid;

const ProductsPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [form] = Form.useForm();
  const screens = useBreakpoint();

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
      setProducts(prev => prev.filter(p => p.id !== id));
    } catch (error) {
      console.error('Ошибка при удалении продукта:', error);
      message.error('Не удалось удалить товар');
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
    <Layout style={{ 
      flex: 1, 
      display: 'flex', 
      flexDirection: 'column', 
      maxWidth: screens.lg ? 1000 : '100%', 
      margin: '0 auto',
      padding: screens.xs ? '0 8px' : '0 16px'
    }}>
      <Content style={{ 
        padding: screens.xs ? '8px 0' : '16px 0', 
        flex: 1 
      }}>
        <Card 
          title="Список товаров"
          extra={
            <Button 
              type="primary" 
              icon={<PlusOutlined />} 
              onClick={handleAdd}
              size={screens.xs ? 'small' : 'middle'}
            >
              {screens.xs ? '' : 'Добавить товар'}
            </Button>
          }
          bordered={false}
          bodyStyle={{ padding: screens.xs ? 8 : 16 }}
        >
          <ProductsTable 
            data={products} 
            onEdit={handleEdit}
            onDelete={handleDelete}
            isMobile={!screens.sm}
          />
        </Card>
      </Content>
      
      <Footer style={{ 
        textAlign: 'center', 
        padding: screens.xs ? '8px 0' : '16px 0',
        flex: '0 0 auto'
      }}>
        © 2025 Складской учёт. Все права защищены.
      </Footer>

      <ProductsModal
        visible={isModalVisible}
        onOk={handleOk}
        onCancel={() => setIsModalVisible(false)}
        editingProduct={editingProduct}
        categories={categories}
        form={form}
        isMobile={!screens.sm}
      />
    </Layout>
  );
};

export default ProductsPage;