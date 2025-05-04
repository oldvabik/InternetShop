import React from 'react';
import { Table, Button, Space, Popconfirm, message } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { Product } from '../models/Product';
import type { TablePaginationConfig } from 'antd/es/table';

const { Column } = Table;

interface ProductsTableProps {
  data: Product[];
  onEdit: (product: Product) => void;
  onDelete: (id: number) => void;
}

const ProductsTable: React.FC<ProductsTableProps> = ({ data, onEdit, onDelete }) => {
  const handleDelete = (id: number) => {
    onDelete(id);
    message.success('Товар успешно удален');
  };

  const paginationConfig: TablePaginationConfig | false = data.length <= 10 ? false : {
    pageSize: 10,
    position: ['bottomRight' as const],
    showSizeChanger: false,
    hideOnSinglePage: true
  };

  return (
    <Table 
      dataSource={data.map((p, index) => ({ ...p, key: index + 1 }))} 
      pagination={paginationConfig}
      bordered
      rowKey="id"
      size="middle"
    >
      <Column 
        title="#" 
        dataIndex="key" 
        key="key" 
        width={50} 
        align="center"
      />
      <Column 
        title="Название" 
        dataIndex="name" 
        key="name" 
        align="center"
        width={200}
      />
      <Column 
        title="Цена" 
        dataIndex="price" 
        key="price" 
        render={(price: number) => (
          <span style={{ fontWeight: 500, color: '#52c41a' }}>
            {price.toFixed(2)} BYN
          </span>
        )}
        width={120}
        align="center"
      />
      <Column 
        title="Количество" 
        dataIndex="quantity" 
        key="quantity" 
        render={(quantity: number) => (
          <span style={{ 
            fontWeight: 500, 
            color: quantity < 10 ? '#f5222d' : 'inherit' 
          }}>
            {quantity}
          </span>
        )}
        width={100}
        align="center"
      />
      <Column 
        title="Категория" 
        dataIndex="category" 
        key="category" 
        render={(category) => category?.name || '-'}
        width={150}
        align="center"
      />
      <Column
        title="Действия"
        key="actions"
        width={100}
        align="center"
        render={(_: any, record: Product) => (
          <Space size="small">
            <Button 
              icon={<EditOutlined />} 
              onClick={() => onEdit(record)}
              style={{ color: '#1890ff', borderColor: '#1890ff' }}
            />
            <Popconfirm
              title="Вы уверены, что хотите удалить этот товар?"
              onConfirm={() => handleDelete(record.id)}
              okText="Да"
              cancelText="Нет"
            >
              <Button 
                danger 
                icon={<DeleteOutlined />}
              />
            </Popconfirm>
          </Space>
        )}
      />
    </Table>
  );
};

export default ProductsTable;