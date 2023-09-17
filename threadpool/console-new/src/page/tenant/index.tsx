import { useAntdTable } from 'ahooks';
import { Button, Form, Input, Row, Space, Table, Col } from 'antd';
import React, { useState } from 'react';
import { fetchTenantList } from './service';

const baseColumns = [
  {
    title: '序号',
    dataIndex: 'num',
  },
  {
    title: '租户',
    dataIndex: 'tenant',
  },
  {
    title: '租户名称',
    dataIndex: 'tenantName',
  },
  {
    title: '负责人',
    dataIndex: 'creator',
  },
  {
    title: '修改时间',
    dataIndex: 'data',
  },
];

const Tenant: React.FC = () => {
  const [editVisible, setEditVisible] = useState(false);
  const [form] = Form.useForm();
  const { tableProps, search } = useAntdTable(fetchTenantList, { form });
  const actions = (type: string) => {
    switch (type) {
      case 'create':
        setEditVisible(true);
        break;
      case 'edit':
        setEditVisible(true);
        break;
      case 'delete':
        break;
      default:
        break;
    }
  };

  return (
    <div>
      <Form>
        <Row>
          <Col span={6}>
            <Form.Item name="note" rules={[{ required: true }]}>
              <Input placeholder="租户" />
            </Form.Item>
          </Col>
          <Col span={18}>
            <Space>
              <Button onClick={() => search.submit()}>搜索</Button>
              <Button onClick={() => setEditVisible(true)}>添加</Button>
            </Space>
          </Col>
        </Row>
      </Form>
      <Table
        {...tableProps}
        rowKey="num"
        columns={[
          ...baseColumns,
          {
            title: '操作',
            key: 'action',
            render: (text: string, record: any) => {
              return (
                <Space>
                  <Button onClick={() => actions('edit')}>编辑</Button>
                  <Button onClick={() => actions('edit')}>删除</Button>
                </Space>
              );
            },
          },
        ]}
      />
    </div>
  );
};

export default Tenant;
