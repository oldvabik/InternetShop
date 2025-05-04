import React, { useEffect } from 'react';
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
  }, [editingOrder, form]);

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
                      >
                        {products.map(product => (
                          <Select.Option key={product.id} value={product.id}>
                            {`${product.name} - ${product.price?.toFixed(2)} BYN`}
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
                      />
                    </Form.Item>
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