import React from 'react';
import { Table, Button, Space } from 'antd';
import { EditOutlined, DeleteOutlined } from '@ant-design/icons';
import { Category } from '../models/Category';

const { Column } = Table;

interface CategoriesTableProps {
  data: Category[];
  onEdit: (category: Category) => void;
  onDelete: (id: number) => void;
}

const CategoriesTable: React.FC<CategoriesTableProps> = ({ data, onEdit, onDelete }) => {
  return (
    <Table 
      dataSource={data.map((c, index) => ({ ...c, key: index + 1 }))} 
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
        width={300}
      />
      <Column
        title="Действия"
        key="actions"
        width={120}
        align="center"
        render={(_: any, record: Category) => (
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

export default CategoriesTable;