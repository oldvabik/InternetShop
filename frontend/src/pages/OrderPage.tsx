import React, { useState, useEffect } from 'react';
import { Button, Card, Layout, Modal, List, Typography, message, Form } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { getOrders, createOrder, updateOrder, deleteOrder, getProducts, getUsers } from '../services/api';
import OrderTable from '../components/OrderTable';
import OrderModal from '../components/OrderModal';
import { Order } from '../models/Order';
import { Product } from '../models/Product';
import { User } from '../models/User';

const { Content, Footer } = Layout;
const { Text } = Typography;

const OrdersPage: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isProductsModalVisible, setIsProductsModalVisible] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [editingOrder, setEditingOrder] = useState<Order | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [ordersRes, productsRes, usersRes] = await Promise.all([
          getOrders(),
          getProducts(),
          getUsers()
        ]);
        setOrders(ordersRes.data);
        setProducts(productsRes.data);
        setUsers(usersRes.data);
      } catch (error) {
        message.error('Ошибка при загрузке данных');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleAdd = () => {
    setEditingOrder(null);
    form.resetFields();
    setIsModalVisible(true);
  };

  const handleEdit = (order: Order) => {
    setEditingOrder(order);
    setIsModalVisible(true);
  };

  const handleDelete = async (userId: number, orderId: number) => {
    try {
      await deleteOrder(userId, orderId);
      setOrders(prev => prev.filter(o => o.id !== orderId));
      message.success('Заказ успешно удален');
    } catch (error) {
      message.error('Не удалось удалить заказ');
    }
  };

  const handleViewProducts = (order: Order) => {
    setSelectedOrder(order);
    setIsProductsModalVisible(true);
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      const orderData = {
        items: values.products.map((p: any) => ({
          productName: products.find(prod => prod.id === p.productId)?.name || '',
          quantity: p.quantity
        }))
      };

      if (editingOrder && editingOrder.user?.id) {
        await updateOrder(editingOrder.user.id, editingOrder.id, orderData);
        message.success('Заказ успешно обновлен');
      } else if (values.userId) {
        await createOrder(values.userId, orderData);
        message.success('Заказ успешно создан');
      } else {
        throw new Error('User ID is required');
      }

      const ordersRes = await getOrders();
      setOrders(ordersRes.data);
      setIsModalVisible(false);
    } catch (error: any) {
      message.error(error.message || 'Ошибка при сохранении заказа');
    }
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Content style={{ padding: '24px', maxWidth: 1400, margin: '0 auto', width: '100%' }}>
        <Card 
          title={
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Text strong style={{ fontSize: 18 }}>Управление заказами</Text>
              <Button 
                type="primary" 
                icon={<PlusOutlined />} 
                onClick={handleAdd}
              >
                Новый заказ
              </Button>
            </div>
          }
          bordered={false}
          loading={loading}
        >
          <OrderTable 
            data={orders} 
            onEdit={handleEdit}
            onDelete={handleDelete}
            onViewProducts={handleViewProducts}
            loading={loading}
          />
        </Card>

        <Modal
          title={`Детали заказа #${selectedOrder?.id || ''}`}
          visible={isProductsModalVisible}
          onCancel={() => setIsProductsModalVisible(false)}
          footer={null}
          width={600}
        >
          {selectedOrder && (
            <>
              <div style={{ marginBottom: 16 }}>
                <Text strong>Клиент: </Text>
                <Text>
                  {selectedOrder.user?.firstName || 'Неизвестно'} {selectedOrder.user?.lastName || ''}
                  {selectedOrder.user?.email && ` (${selectedOrder.user.email})`}
                </Text>
              </div>
              <div style={{ marginBottom: 16 }}>
                <Text strong>Дата заказа: </Text>
                <Text>
                  {selectedOrder.date ? new Date(selectedOrder.date).toLocaleDateString() : 'Не указана'}
                </Text>
              </div>

              <List
                dataSource={selectedOrder.orderProducts || selectedOrder.items || []}
                renderItem={(item: any) => {
                  const product = products.find(p => 
                    p.id === (item.product?.id || item.productId)
                  );
                  const quantity = item.quantity || 0;
                  const price = product?.price || 0;
                  
                  return (
                    <List.Item>
                      <div style={{ display: 'flex', justifyContent: 'space-between', width: '100%' }}>
                        <div>
                          <Text strong>{product?.name || 'Неизвестный товар'}</Text>
                          <div style={{ color: '#666', fontSize: 12 }}>
                            {price.toFixed(2)} BYN × {quantity} шт.
                          </div>
                        </div>
                        <Text strong>{(price * quantity).toFixed(2)} BYN</Text>
                      </div>
                    </List.Item>
                  );
                }}
              />
              <div style={{ marginTop: 16, paddingTop: 16, borderTop: '1px solid #f0f0f0', textAlign: 'right' }}>
                <Text strong style={{ fontSize: 16 }}>
                  Итого: {selectedOrder.totalPrice?.toFixed(2) || '0.00'} BYN
                </Text>
              </div>
            </>
          )}
        </Modal>

        <OrderModal
          visible={isModalVisible}
          onOk={handleOk}
          onCancel={() => setIsModalVisible(false)}
          editingOrder={editingOrder}
          products={products}
          users={users}
          form={form}
        />
      </Content>
      <Footer style={{
        textAlign: 'center',
        padding: '16px 0',
        flex: '0 0 auto'
      }}>
        © 2025 Складской учёт. Все права защищены.
      </Footer>
    </Layout>
  );
};

export default OrdersPage;