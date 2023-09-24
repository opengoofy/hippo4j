import { useAntdTable } from 'ahooks';
import { Button, Form, Input, Row, Space, Table, Col } from 'antd';
import { SearchOutlined, RedoOutlined } from '@ant-design/icons';
import React, { useState } from 'react';
import { fetchLogList } from './service';
import { useUrlSet } from '@/hooks/useUrlSet';
import style from './index.module.less';
import LogDetail from './detail';

const baseColumns = [
  {
    title: '序号',
    dataIndex: 'id',
    width: 100,
    // fixed: 'left',
  },
  {
    title: '业务类型',
    dataIndex: 'category',
    width: 200,
  },
  {
    title: '业务标识',
    dataIndex: 'bizNo',
    width: 380,
  },
  {
    title: '日志内容',
    dataIndex: 'action',
    width: 380,
  },
  {
    title: '操作人',
    dataIndex: 'operator',
    width: 100,
  },
  {
    title: '操作时间',
    dataIndex: 'createTime',
    width: 200,
  },
];

const Tenant: React.FC = () => {
  const [visible, setVisible] = useState(false);
  const [form] = Form.useForm();
  const { setUrl } = useUrlSet({ form });
  const { tableProps, search } = useAntdTable(fetchLogList, { form });
  const [curItems, setCurItems] = useState({});
  const handleSearch = () => {
    setUrl();
    search.submit();
  };
  const handleDetail = (item: any) => {
    setVisible(!visible);
    setCurItems(item);
  };
  const handleClose = () => {
    setVisible(false);
  };

  return (
    <div className={style.tenant_wrapper}>
      <Form form={form} wrapperCol={{ span: 23 }}>
        <Row>
          <Col span={5}>
            <Form.Item name="category">
              <Input placeholder="业务类型" allowClear />
            </Form.Item>
          </Col>
          <Col span={5}>
            <Form.Item name="bizNo">
              <Input placeholder="业务标识" allowClear />
            </Form.Item>
          </Col>
          <Col span={5}>
            <Form.Item name="operator">
              <Input placeholder="操作人" allowClear />
            </Form.Item>
          </Col>
          <Col span={6}>
            <Space>
              <Button onClick={() => handleSearch()} type="primary" icon={<SearchOutlined />}>
                搜索
              </Button>
              <Button onClick={() => search.reset()} type="primary" icon={<RedoOutlined />}>
                重置
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
        scroll={{ x: 1000 }}
        columns={[
          ...baseColumns,
          {
            fixed: 'right',
            width: 100,
            title: '操作',
            key: 'action',
            render: (text: string, record: any) => {
              return (
                <Button onClick={() => handleDetail(record)} type="link" className={style.opreate_btn}>
                  查看
                </Button>
              );
            },
          },
        ]}
      />
      <LogDetail visible={visible} onClose={handleClose} data={curItems} />
    </div>
  );
};

export default Tenant;
