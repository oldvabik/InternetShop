import React, { useEffect, useState } from 'react';
import { Modal, Form, InputNumber, Table, Button, Select, message } from 'antd';
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons';
import { Product } from '../models/Product';
import { Order } from '../models/Order';
import { User } from '../models/User';

interface OrderModalProps {
  visible: boolean;
  onOk: () => void;
  onCancel: () => void;
  editingOrder: Order | null;
  products: Product[];
  users: User[];
  form: any;
}

interface OrderProductItem {
  productId?: number;
  quantity?: number;
  key?: string; // для уникальности в Form.List
}

const OrderModal: React.FC<OrderModalProps> = ({
  visible,
  onOk,
  onCancel,
  editingOrder,
  products,
  users,
  form,
}) => {
  const [quantityErrors, setQuantityErrors] = useState<Record<string, string>>({});
  const [selectedProducts, setSelectedProducts] = useState<Set<number>>(new Set());

  useEffect(() => {
    if (editingOrder) {
      const orderProducts = editingOrder.orderProducts || [];
      const items = editingOrder.items || [];
      
      const initialProducts = [
        ...orderProducts.map(op => ({
          productId: op.product?.id,
          quantity: op.quantity,
        })),
        ...items.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
        }))
      ];
      
      form.setFieldsValue({
        userId: editingOrder.user?.id,
        products: initialProducts,
      });
      
      // Инициализируем множество выбранных продуктов
      const productIds = initialProducts.map(p => p.productId).filter(Boolean) as number[];
      setSelectedProducts(new Set(productIds));
    } else {
      form.resetFields();
      setSelectedProducts(new Set());
    }
    setQuantityErrors({});
  }, [editingOrder, form]);

  const validateQuantity = (productId: number, quantity: number): string | null => {
    const product = products.find(p => p.id === productId);
    if (product && product.quantity !== undefined && quantity > product.quantity) {
      return `Максимально доступно: ${product.quantity}`;
    }
    return null;
  };

  const handleQuantityChange = (index: number, value: number | null): void => {
    const productId = form.getFieldValue(['products', index, 'productId']);
    if (productId && value !== null) {
      const error = validateQuantity(productId, value);
      setQuantityErrors((prev: Record<string, string>) => ({
        ...prev,
        [index]: error || ''
      }));
    } else {
      setQuantityErrors((prev: Record<string, string>) => ({
        ...prev,
        [index]: ''
      }));
    }
  };

  const handleProductChange = (index: number, value: number): void => {
    const currentProducts: OrderProductItem[] = form.getFieldValue('products') || [];
    
    // Проверяем, не выбран ли уже этот продукт в другом поле
    const isDuplicate = Array.from(selectedProducts).some(
      (id, i) => id === value && i !== index
    );
    
    if (isDuplicate) {
      message.warning('Этот продукт уже добавлен в заказ');
      // Возвращаем предыдущее значение
      form.setFieldsValue({
        products: currentProducts.map((item, i) => 
          i === index ? { ...item, productId: undefined } : item
        )
      });
      return;
    }
    
    // Обновляем множество выбранных продуктов
    const newSelectedProducts = new Set(selectedProducts);
    const prevProductId = currentProducts[index]?.productId;
    
    if (prevProductId) {
      newSelectedProducts.delete(prevProductId);
    }
    
    if (value) {
      newSelectedProducts.add(value);
    }
    
    setSelectedProducts(newSelectedProducts);
    
    const quantity = form.getFieldValue(['products', index, 'quantity']);
    if (quantity) {
      const error = validateQuantity(value, quantity);
      setQuantityErrors((prev: Record<string, string>) => ({
        ...prev,
        [index]: error || ''
      }));
    }
  };

  const handleAddProduct = (add: () => void) => {
    // Проверяем, есть ли еще доступные продукты для добавления
    const availableProducts = products.filter(
      p => !selectedProducts.has(p.id) && (p.quantity === undefined || p.quantity > 0)
    );
    
    if (availableProducts.length === 0) {
      message.warning('Все доступные продукты уже добавлены в заказ');
      return;
    }
    
    add();
  };

  const handleRemoveProduct = (remove: (index: number) => void, index: number, productId?: number) => {
    remove(index);
    
    if (productId) {
      const newSelectedProducts = new Set(selectedProducts);
      newSelectedProducts.delete(productId);
      setSelectedProducts(newSelectedProducts);
    }
  };

  const getAvailableProducts = (currentIndex: number, currentProductId?: number) => {
    return products.filter(product => {
      // Продукт доступен если:
      // 1. Он не выбран в других полях ИЛИ это текущий выбранный продукт этого поля
      // 2. И он есть в наличии (quantity === undefined или quantity > 0)
      return (
        (!selectedProducts.has(product.id) || product.id === currentProductId) &&
        (product.quantity === undefined || product.quantity > 0)
      );
    });
  };

  return (
    <Modal
      title={editingOrder ? 'Редактировать заказ' : 'Создать новый заказ'}
      visible={visible}
      onOk={onOk}
      onCancel={onCancel}
      width={700}
      okText="Сохранить"
      cancelText="Отмена"
      destroyOnClose
    >
      <Form form={form} layout="vertical">
        <Form.Item
          name="userId"
          label="Клиент"
          rules={[{ required: true, message: 'Пожалуйста, выберите клиента!' }]}
        >
          <Select placeholder="Выберите клиента">
            {users.map(user => (
              <Select.Option key={user.id} value={user.id}>
                {`${user.firstName} ${user.lastName}${user.email ? ` (${user.email})` : ''}`}
              </Select.Option>
            ))}
          </Select>
        </Form.Item>

        <Form.List name="products">
          {(fields, { add, remove }) => (
            <>
              <div style={{ marginBottom: 8, fontWeight: 'bold', textAlign: 'center' }}>
                Товары в заказе:
              </div>
              <Table
                dataSource={fields}
                pagination={false}
                rowKey="key"
                scroll={{ y: 240 }}
                bordered
                footer={() => (
                  <Button
                    type="dashed"
                    onClick={() => handleAddProduct(add)}
                    icon={<PlusOutlined />}
                    block
                    disabled={products.filter(p => !selectedProducts.has(p.id) && (p.quantity === undefined || p.quantity > 0)).length === 0}
                  >
                    Добавить товар
                  </Button>
                )}
              >
                <Table.Column
                  title="Товар"
                  width="55%"
                  align="center"
                  render={(_, field, index) => {
                    const currentProductId = form.getFieldValue(['products', index, 'productId']);
                    const availableProducts = getAvailableProducts(index, currentProductId);
                    
                    return (
                      <Form.Item
                        name={[index, 'productId']}
                        rules={[{ required: true, message: 'Выберите товар!' }]}
                        style={{ marginBottom: 0, textAlign: 'center' }}
                      >
                        <Select 
                          placeholder="Выберите товар"
                          style={{ width: '100%' }}
                          onChange={(value) => handleProductChange(index, value)}
                        >
                          {availableProducts.map(product => (
                            <Select.Option 
                              key={product.id} 
                              value={product.id}
                            >
                              {`${product.name} - ${product.price?.toFixed(2)} BYN`}
                              {product.quantity !== undefined && ` (Доступно: ${product.quantity})`}
                            </Select.Option>
                          ))}
                        </Select>
                      </Form.Item>
                    );
                  }}
                />
                <Table.Column
                  title="Кол-во"
                  width="25%"
                  align="center"
                  render={(_, __, index) => (
                    <div>
                      <Form.Item
                        name={[index, 'quantity']}
                        rules={[
                          { required: true, message: 'Введите количество!' },
                          { type: 'number', min: 1, message: 'Минимум 1' },
                        ]}
                        style={{ marginBottom: 0, textAlign: 'center' }}
                      >
                        <InputNumber 
                          min={1} 
                          style={{ width: '80%' }}
                          onChange={(value) => handleQuantityChange(index, value as number | null)}
                        />
                      </Form.Item>
                      {quantityErrors[index] && (
                        <div style={{ color: 'red', fontSize: 12 }}>
                          {quantityErrors[index]}
                        </div>
                      )}
                    </div>
                  )}
                />
                <Table.Column
                  title="Действие"
                  width="20%"
                  align="center"
                  render={(_, field, index) => {
                    const currentProductId = form.getFieldValue(['products', index, 'productId']);
                    return (
                      <Form.Item style={{ marginBottom: 0, textAlign: 'center' }}>
                        <Button
                          danger
                          icon={<MinusCircleOutlined />}
                          onClick={() => handleRemoveProduct(remove, index, currentProductId)}
                        />
                      </Form.Item>
                    );
                  }}
                />
              </Table>
            </>
          )}
        </Form.List>
      </Form>
    </Modal>
  );
};

export default OrderModal;