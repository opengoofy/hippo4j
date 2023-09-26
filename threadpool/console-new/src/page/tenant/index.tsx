import React, { useState } from 'react';
import { useAntdTable, useRequest } from 'ahooks';
import { Button, Form, Input, Row, Space, Table, Col, Modal, notification, message } from 'antd';
import { SearchOutlined, EditOutlined } from '@ant-design/icons';
import { useUrlSet } from '@/hooks/useUrlSet';
import { fetchDeleteTenant, fetchTenantList } from './service';
import style from './index.module.less';
import TenantCreate from './create';

const baseColumns = [
  {
    title: '序号',
    dataIndex: 'index',
  },
  {
    title: '租户',
    dataIndex: 'tenantId',
  },
  {
    title: '租户名称',
    dataIndex: 'tenantName',
  },
  {
    title: '负责人',
    dataIndex: 'owner',
  },
  {
    title: '修改时间',
    dataIndex: 'gmtModified',
  },
];

const Tenant: React.FC = () => {
  const [editVisible, setEditVisible] = useState(false);
  const [curItem, setCurItem] = useState({});
  const [type, setType] = useState('add');
  const [form] = Form.useForm();
  const { setUrl } = useUrlSet({ form });
  const { tableProps, search } = useAntdTable(fetchTenantList, { form });
  const deleteRequest = useRequest(fetchDeleteTenant, { manual: true });

  const handleSearch = () => {
    setUrl();
    search.submit();
  };
  const handleDelete = (item: any) => {
    Modal.confirm({
      title: '提示',
      content: `此操作将删除 ${item.tenantId}，是否继续？`,
      onOk: async () => {
        try {
          const res = await deleteRequest.runAsync(item.tenantId);
          if (res && res.success) {
            notification.success({ message: '删除成功' });
            search.reset();
          }
        } catch (e: any) {
          message.error(e.message || '服务器开小差啦~');
        }
      },
    });
  };
  const actions = (type: string, item?: any) => {
    switch (type) {
      case 'add':
        setType('add');
        setEditVisible(true);
        break;
      case 'edit':
        setType('edit');
        setCurItem(item);
        setEditVisible(true);
        break;
      case 'delete':
        handleDelete(item);
        break;
      default:
        break;
    }
  };
  const handleClose = () => {
    setEditVisible(false);
  };

  return (
    <div className={style.tenant_wrapper}>
      <Form form={form} wrapperCol={{ span: 23 }}>
        <Row>
          <Col span={6}>
            <Form.Item name="note">
              <Input placeholder="租户" allowClear />
            </Form.Item>
          </Col>
          <Col span={18}>
            <Space>
              <Button onClick={() => handleSearch()} type="primary" icon={<SearchOutlined />}>
                搜索
              </Button>
              <Button onClick={() => actions('add')} type="primary" icon={<EditOutlined />}>
                添加
              </Button>
            </Space>
          </Col>
        </Row>
        <Row>
          <Col span={6}></Col>
          <Col span={18}></Col>
        </Row>
      </Form>
      <Table
        {...tableProps}
        rowKey="id"
        columns={[
          ...baseColumns,
          {
            title: '操作',
            key: 'action',
            render: (text: string, record: any) => {
              return (
                <Space>
                  <Button onClick={() => actions('edit', record)} type="link" className={style.opreate_btn}>
                    编辑
                  </Button>
                  <Button onClick={() => actions('delete', record)} type="link" className={style.opreate_btn}>
                    删除
                  </Button>
                </Space>
              );
            },
          },
        ]}
      />
      {editVisible && (
        <TenantCreate
          data={curItem}
          onClose={handleClose}
          visible={editVisible}
          type={type}
          reset={() => search.reset()}
        />
      )}
    </div>
  );
};

export default Tenant;
