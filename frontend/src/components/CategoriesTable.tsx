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
  isMobile: boolean;
}

const CategoriesTable: React.FC<CategoriesTableProps> = ({ 
  data, 
  onEdit, 
  onDelete,
  isMobile 
}) => {
  const handleDelete = (id: number) => {
    onDelete(id);
    message.success('Категория успешно удалена');
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
      dataSource={data.map((c, index) => ({ ...c, key: index + 1 }))} 
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
        width={isMobile ? 200 : 300}
        sorter={(a, b) => a.name.localeCompare(b.name)}
      />
      <Column
        title="Действия"
        key="actions"
        width={isMobile ? 90 : 100}
        align="center"
        fixed={isMobile ? 'right' : undefined}
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
              size={isMobile ? 'small' : 'middle'}
            />
            <Popconfirm
              title="Удалить категорию?"
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

export default CategoriesTable;