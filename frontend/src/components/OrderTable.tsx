import React from 'react';
import { Table, Button, Popconfirm, Badge, message } from 'antd';
import { EditOutlined, DeleteOutlined, ShoppingCartOutlined, UserOutlined } from '@ant-design/icons';
import { Order } from '../models/Order';

interface OrderTableProps {
  data: Order[];
  onEdit: (order: Order) => void;
  onDelete: (userId: number, orderId: number) => void;
  onViewProducts: (order: Order) => void;
  loading?: boolean;
}

const OrderTable: React.FC<OrderTableProps> = ({ 
  data, 
  onEdit, 
  onDelete, 
  onViewProducts,
  loading = false
}) => {
  const columns = [
    {
      title: '#',
      dataIndex: 'key',
      key: 'key',
      width: 60,
      align: 'center' as const,
    },
    {
      title: 'Дата заказа',
      dataIndex: 'date',
      key: 'date',
      render: (date: string) => date ? new Date(date).toLocaleDateString() : 'Не указана',
    },
    {
      title: 'Пользователь',
      key: 'user',
      render: (_: any, record: Order) => (
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <UserOutlined style={{ marginRight: 8, color: '#1890ff' }} />
          <div>
            <span style={{ fontWeight: 500 }}>
              {record.user?.firstName || 'Неизвестно'} {record.user?.lastName || ''}
            </span>
            {record.user?.email && (
              <div style={{ fontSize: 12, color: '#666' }}>{record.user.email}</div>
            )}
          </div>
        </div>
      ),
    },
    {
      title: 'Сумма',
      dataIndex: 'totalPrice',
      key: 'totalPrice',
      render: (amount: number | undefined) => (
        <span style={{ fontWeight: 500, color: '#52c41a' }}>
          {amount?.toFixed(2) || '0.00'} BYN
        </span>
      ),
    },
    {
      title: 'Товары',
      key: 'products',
      render: (_: any, record: Order) => {
        const count = record.orderProducts?.length || record.items?.length || 0;
        return (
          <Badge count={count} style={{ backgroundColor: '#1890ff' }}>
            <Button
              type="text"
              icon={<ShoppingCartOutlined />}
              onClick={() => onViewProducts(record)}
              style={{ color: '#1890ff' }}
            />
          </Badge>
        );
      },
    },
    {
      title: 'Действия',
      key: 'actions',
      width: 120,
      render: (_: any, record: Order) => (
        <div style={{ display: 'flex', gap: 8 }}>
          <Button 
            icon={<EditOutlined />} 
            onClick={() => onEdit(record)}
          />
          <Popconfirm
            title="Удалить этот заказ?"
            onConfirm={() => {
              if (record.user?.id && record.id) {
                onDelete(record.user.id, record.id);
              } else {
                message.error('Не удалось определить заказ');
              }
            }}
            okText="Да"
            cancelText="Нет"
          >
            <Button danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </div>
      ),
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={data.map((o, index) => ({ ...o, key: index + 1 }))}
      pagination={{ pageSize: 10 }}
      rowKey="id"
      loading={loading}
    />
  );
};

export default OrderTable;