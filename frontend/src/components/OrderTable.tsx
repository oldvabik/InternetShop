import React from 'react';
import { Table, Button, Popconfirm, Badge, message, Space } from 'antd';
import { DeleteOutlined, ShoppingCartOutlined, UserOutlined } from '@ant-design/icons';
import { Order } from '../models/Order';
import type { TablePaginationConfig, ColumnType } from 'antd/es/table';

interface OrderTableProps {
  data: Order[];
  onDelete: (userId: number, orderId: number) => void;
  onViewProducts: (order: Order) => void;
  loading?: boolean;
}

const OrderTable: React.FC<OrderTableProps> = ({ 
  data, 
  onDelete, 
  onViewProducts,
  loading = false
}) => {
  const paginationConfig: TablePaginationConfig | false = data.length <= 10 ? false : {
    pageSize: 10,
    position: ['bottomRight'],
    showSizeChanger: false,
    hideOnSinglePage: true,
    showTotal: (total, range) => (
      <div style={{
        position: 'absolute',
        left: '4px',
        fontSize: '14px',
        color: 'rgba(0, 0, 0, 0.65)'
      }}>
        {range[0]}-{range[1]} из {total}
      </div>
    ),
    style: {
      position: 'relative'
    }
  };

  // Функция для сортировки по дате
  const dateSorter = (a: Order, b: Order) => {
    const dateA = a.date ? new Date(a.date).getTime() : 0;
    const dateB = b.date ? new Date(b.date).getTime() : 0;
    return dateA - dateB;
  };

  // Функция для сортировки по имени пользователя
  const userSorter = (a: Order, b: Order) => {
    const nameA = `${a.user?.firstName || ''} ${a.user?.lastName || ''}`.toLowerCase();
    const nameB = `${b.user?.firstName || ''} ${b.user?.lastName || ''}`.toLowerCase();
    return nameA.localeCompare(nameB);
  };

  // Функция для сортировки по сумме
  const amountSorter = (a: Order, b: Order) => (a.totalPrice || 0) - (b.totalPrice || 0);

  const columns: ColumnType<Order>[] = [
    {
      title: '#',
      dataIndex: 'key',
      key: 'key',
      width: 50,
      align: 'center',
    },
    {
      title: 'Дата заказа',
      dataIndex: 'date',
      key: 'date',
      align: 'center',
      width: 120,
      sorter: dateSorter,
      render: (date: string) => date ? new Date(date).toLocaleDateString() : 'Не указана',
    },
    {
      title: 'Пользователь',
      key: 'user',
      align: 'center',
      width: 200,
      sorter: userSorter,
      render: (_: any, record: Order) => (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
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
      align: 'center',
      width: 120,
      sorter: amountSorter,
      render: (amount: number | undefined) => (
        <span style={{ fontWeight: 500, color: '#52c41a' }}>
          {amount?.toFixed(2) || '0.00'} BYN
        </span>
      ),
    },
    {
      title: 'Товары',
      key: 'products',
      align: 'center',
      width: 100,
      render: (_: any, record: Order) => {
        const count = record.orderProducts?.length || record.items?.length || 0;
        return (
          <Badge count={count} style={{ backgroundColor: '#1890ff' }}>
            <Button
              type="default"
              icon={<ShoppingCartOutlined />}
              onClick={() => onViewProducts(record)}
              style={{ 
                color: '#1890ff',
                borderColor: '#1890ff',
              }}
            />
          </Badge>
        );
      },
    },
    {
      title: 'Действия',
      key: 'actions',
      width: 100,
      align: 'center',
      render: (_: any, record: Order) => (
        <Space size="small">
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
        </Space>
      ),
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={data.map((o, index) => ({ ...o, key: index + 1 }))}
      pagination={paginationConfig}
      bordered
      rowKey="id"
      size="middle"
      loading={loading}
    />
  );
};

export default OrderTable;