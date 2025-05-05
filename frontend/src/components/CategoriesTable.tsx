import React from 'react';
import { Table, Button, Space, Popconfirm, message } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { Category } from '../models/Category';
import type { TablePaginationConfig } from 'antd/es/table';

const { Column } = Table;

interface CategoriesTableProps {
  data: Category[];
  onEdit: (category: Category) => void;
  onDelete: (id: number) => void;
}

const CategoriesTable: React.FC<CategoriesTableProps> = ({ data, onEdit, onDelete }) => {
  const handleDelete = (id: number) => {
    onDelete(id);
    message.success('Категория успешно удалена');
  };

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

  return (
    <Table 
      dataSource={data.map((c, index) => ({ ...c, key: index + 1 }))} 
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
        width={300}
        sorter={(a, b) => a.name.localeCompare(b.name)}
      />
      <Column
        title="Действия"
        key="actions"
        width={100}
        align="center"
        render={(_: any, record: Category) => (
          <Space size="small">
            <Button 
              icon={<EditOutlined />} 
              onClick={() => onEdit(record)}
              style={{ 
                color: '#1890ff', 
                borderColor: '#1890ff',
                background: 'transparent'
              }}
            />
            <Popconfirm
              title="Вы уверены, что хотите удалить эту категорию?"
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

export default CategoriesTable;