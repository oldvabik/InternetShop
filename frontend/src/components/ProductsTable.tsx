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
  isMobile: boolean;
}

const ProductsTable: React.FC<ProductsTableProps> = ({ 
  data, 
  onEdit, 
  onDelete,
  isMobile 
}) => {
  const handleDelete = (id: number) => {
    onDelete(id);
    message.success('Товар успешно удален');
  };

  const paginationConfig: TablePaginationConfig | false = data.length <= 10 ? false : {
    pageSize: 10,
    position: ['bottomRight'],
    showSizeChanger: false,
    hideOnSinglePage: true,
    showTotal: isMobile ? undefined : (total, range) => (
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
    },
    size: isMobile ? 'small' : 'default'
  };

  return (
    <Table 
      dataSource={data.map((p, index) => ({ ...p, key: index + 1 }))} 
      pagination={paginationConfig}
      bordered
      rowKey="id"
      size={isMobile ? 'small' : 'middle'}
      scroll={isMobile ? { x: true } : undefined}
    >
      {!isMobile && (
        <Column 
          title="#" 
          dataIndex="key" 
          key="key" 
          width={50} 
          align="center"
        />
      )}
      <Column 
        title="Название" 
        dataIndex="name" 
        key="name" 
        align="center"
        width={isMobile ? 120 : 200}
        sorter={(a, b) => a.name.localeCompare(b.name)}
      />
      <Column 
        title="Цена" 
        dataIndex="price" 
        key="price" 
        sorter={(a, b) => (a.price || 0) - (b.price || 0)}
        render={(price: number) => (
          <span style={{ fontWeight: 500, color: '#52c41a' }}>
            {price.toFixed(2)} BYN
          </span>
        )}
        width={isMobile ? 100 : 120}
        align="center"
      />
      <Column 
        title={isMobile ? 'Кол-во' : 'Количество'} 
        dataIndex="quantity" 
        key="quantity" 
        sorter={(a, b) => (a.quantity || 0) - (b.quantity || 0)}
        render={(quantity: number) => (
          <span style={{ 
            fontWeight: 500, 
            color: quantity < 10 ? '#f5222d' : 'inherit' 
          }}>
            {quantity}
          </span>
        )}
        width={isMobile ? 80 : 100}
        align="center"
      />
      {!isMobile && (
        <Column 
          title="Категория" 
          dataIndex="category" 
          key="category" 
          sorter={(a, b) => (a.category?.name || '').localeCompare(b.category?.name || '')}
          render={(category) => category?.name || '-'}
          width={150}
          align="center"
        />
      )}
      <Column
        title="Действия"
        key="actions"
        width={isMobile ? 90 : 100}
        align="center"
        fixed={isMobile ? 'right' : undefined}
        render={(_: any, record: Product) => (
          <Space size="small">
            <Button 
              icon={<EditOutlined />} 
              onClick={() => onEdit(record)}
              style={{ color: '#1890ff', borderColor: '#1890ff' }}
              size={isMobile ? 'small' : 'middle'}
            />
            <Popconfirm
              title="Удалить товар?"
              onConfirm={() => handleDelete(record.id)}
              okText="Да"
              cancelText="Нет"
            >
              <Button 
                danger 
                icon={<DeleteOutlined />}
                size={isMobile ? 'small' : 'middle'}
              />
            </Popconfirm>
          </Space>
        )}
      />
    </Table>
  );
};

export default ProductsTable;