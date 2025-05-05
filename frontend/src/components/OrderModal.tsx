import React, { useEffect, useState } from 'react';
import { Modal, Form, InputNumber, Table, Button, Select } from 'antd';
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

  useEffect(() => {
    if (editingOrder) {
      const orderProducts = editingOrder.orderProducts || [];
      const items = editingOrder.items || [];
      
      form.setFieldsValue({
        userId: editingOrder.user?.id,
        products: [
          ...orderProducts.map(op => ({
            productId: op.product?.id,
            quantity: op.quantity,
          })),
          ...items.map(item => ({
            productId: item.productId,
            quantity: item.quantity,
          }))
        ],
      });
    } else {
      form.resetFields();
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
    const quantity = form.getFieldValue(['products', index, 'quantity']);
    if (quantity) {
      const error = validateQuantity(value, quantity);
      setQuantityErrors((prev: Record<string, string>) => ({
        ...prev,
        [index]: error || ''
      }));
    }
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
                    onClick={() => add()}
                    icon={<PlusOutlined />}
                    block
                  >
                    Добавить товар
                  </Button>
                )}
              >
                <Table.Column
                  title="Товар"
                  width="55%"
                  align="center"
                  render={(_, __, index) => (
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
                        {products.map(product => (
                          <Select.Option 
                            key={product.id} 
                            value={product.id}
                            disabled={product.quantity === 0}
                          >
                            {`${product.name} - ${product.price?.toFixed(2)} BYN`}
                            {product.quantity !== undefined && ` (Доступно: ${product.quantity})`}
                          </Select.Option>
                        ))}
                      </Select>
                    </Form.Item>
                  )}
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
                  render={(_, __, index) => (
                    <Form.Item style={{ marginBottom: 0, textAlign: 'center' }}>
                      <Button
                        danger
                        icon={<MinusCircleOutlined />}
                        onClick={() => remove(index)}
                      />
                    </Form.Item>
                  )}
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