import React from 'react';
import { Table, Button, Space } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { Product } from '../models/Product';

const { Column } = Table;

interface ProductsTableProps {
  data: Product[];
  onEdit: (product: Product) => void;
  onDelete: (id: number) => void;
}

const ProductsTable: React.FC<ProductsTableProps> = ({ data, onEdit, onDelete }) => {
  return (
    <Table 
      dataSource={data.map((p, index) => ({ ...p, key: index + 1 }))} 
      pagination={{
        pageSize: 7,
        position: ['bottomRight'],
        showSizeChanger: false
      }}
      bordered
      rowKey="id"
      size="middle"
    >
      <Column 
        title="#" 
        dataIndex="key" 
        key="key" 
        width={60} 
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
        render={(price: number) => `${price.toFixed(2)} ₽`}
        width={120}
        align="center"
      />
      <Column 
        title="Кол-во" 
        dataIndex="quantity" 
        key="quantity" 
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
        width={120}
        align="center"
        render={(_: any, record: Product) => (
          <Space size="small">
            <Button 
              icon={<EditOutlined />} 
              onClick={() => onEdit(record)}
            />
            <Button 
              danger 
              icon={<DeleteOutlined />} 
              onClick={() => onDelete(record.id)}
            />
          </Space>
        )}
      />
    </Table>
  );
};

export default ProductsTable;